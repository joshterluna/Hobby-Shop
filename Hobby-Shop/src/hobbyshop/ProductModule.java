package hobbyshop;

import java.util.Locale;

public class ProductModule {

    public static final int MAX_PRODUCTS = 500;
    public static final String PRODUCT_FILE = "data/products.txt";

    // A product with this many units or fewer is shown as low stock.
    public static final int LOW_STOCK_LIMIT = 3;

    // Product information is stored in parallel arrays to keep the code beginner-friendly.
    public static String[] productIds = new String[MAX_PRODUCTS];
    public static String[] productNames = new String[MAX_PRODUCTS];
    public static String[] productSeries = new String[MAX_PRODUCTS];
    public static String[] productManufacturers = new String[MAX_PRODUCTS];
    public static String[] productCategories = new String[MAX_PRODUCTS];
    public static double[] productPrices = new double[MAX_PRODUCTS];
    public static int[] productStocks = new int[MAX_PRODUCTS];
    public static int productCount = 0;

    // Counts bad lines found while loading, so a save cannot erase them.
    public static int malformedProductCount = 0;

    // Loads valid product records from products.txt.
    public static void loadProducts() {
        String[] lines = new String[MAX_PRODUCTS];
        int lineCount = FileHelper.readLines(PRODUCT_FILE, lines, MAX_PRODUCTS);
        productCount = 0;
        malformedProductCount = 0;

        for (int i = 0; i < lineCount; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (fields.length != 7) {
                malformedProductCount++;
                System.out.println("Warning: Skipping malformed product record on line " + (i + 1) + ".");
                continue;
            }

            try {
                String id = fields[0].trim();
                String name = fields[1].trim();
                String series = fields[2].trim();
                String manufacturer = fields[3].trim();
                String category = fields[4].trim();
                double price = Double.parseDouble(fields[5].trim());
                int stock = Integer.parseInt(fields[6].trim());

                if (id.isEmpty() || name.isEmpty() || series.isEmpty() || manufacturer.isEmpty()
                        || category.isEmpty() || price < 0 || stock < 0 || idExists(id)) {
                    malformedProductCount++;
                    System.out.println("Warning: Skipping invalid or duplicate product on line " + (i + 1) + ".");
                    continue;
                }
                if (productCount >= MAX_PRODUCTS) {
                    System.out.println("Warning: Product limit reached. Some records were not loaded.");
                    break;
                }

                productIds[productCount] = id;
                productNames[productCount] = name;
                productSeries[productCount] = series;
                productManufacturers[productCount] = manufacturer;
                productCategories[productCount] = category;
                productPrices[productCount] = price;
                productStocks[productCount] = stock;
                productCount++;
            } catch (NumberFormatException e) {
                malformedProductCount++;
                System.out.println("Warning: Skipping product with an invalid price or stock on line " + (i + 1) + ".");
            }
        }
    }

    // Saves all current product arrays to products.txt.
    public static boolean saveProducts() {
        // Saving now would erase the bad lines, so stop and tell the user.
        if (malformedProductCount > 0) {
            System.out.println("Save cancelled because products.txt contains malformed or duplicate records.");
            System.out.println("Correct the reported lines in data/products.txt before changing products.");
            return false;
        }
        String[] lines = new String[productCount];
        for (int i = 0; i < productCount; i++) {
            lines[i] = productIds[i] + "|" + productNames[i] + "|" + productSeries[i]
                    + "|" + productManufacturers[i] + "|" + productCategories[i]
                    + "|" + money(productPrices[i]) + "|" + productStocks[i];
        }
        return FileHelper.writeLines(PRODUCT_FILE, lines, productCount);
    }

    // Shows the figure catalog. All logged-in roles may view it.
    public static void viewAllProducts() {
        if (!isAllowedToView()) {
            System.out.println("Access denied. Please log in first.");
            return;
        }
        loadProducts();
        if (productCount == 0) {
            System.out.println("No figures are currently in the catalog.");
            return;
        }

        System.out.println("\n==================== FIGURE CATALOG ====================");
        System.out.printf("%-10s %-26s %-19s %-10s %-8s%n", "ID", "NAME", "SERIES", "PRICE", "STOCK");
        System.out.println("--------------------------------------------------------");
        for (int i = 0; i < productCount; i++) {
            System.out.printf("%-10s %-26s %-19s PHP %-5.2f %-8d%n",
                    productIds[i], shorten(productNames[i], 25), shorten(productSeries[i], 18),
                    productPrices[i], productStocks[i]);
        }
        System.out.println("========================================================");
    }

    // Shows the search menu and displays matching products.
    public static void searchProducts() {
        if (!isAllowedToView()) {
            System.out.println("Access denied. Please log in first.");
            return;
        }
        loadProducts();
        System.out.println("\n========== SEARCH FIGURES ==========");
        System.out.println("[1] Search by Product ID");
        System.out.println("[2] Search by Name");
        System.out.println("[3] Search by Series");
        System.out.println("[4] Search by Category");
        System.out.println("[5] Back");
        int choice = HobbyShop.readChoice("Enter your choice: ");
        if (choice == 5) {
            return;
        }
        if (choice < 1 || choice > 4) {
            System.out.println("Invalid search option.");
            return;
        }

        String keyword = readText("Enter search keyword: ").toLowerCase(Locale.ROOT);
        while (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            keyword = readText("Enter search keyword: ").toLowerCase(Locale.ROOT);
        }
        int matches = 0;
        int[] matchIndexes = new int[MAX_PRODUCTS];
        for (int i = 0; i < productCount; i++) {
            String value = "";
            if (choice == 1) {
                value = productIds[i];
            } else if (choice == 2) {
                value = productNames[i];
            } else if (choice == 3) {
                value = productSeries[i];
            } else if (choice == 4) {
                value = productCategories[i];
            }

            if (value.toLowerCase(Locale.ROOT).contains(keyword)) {
                matchIndexes[matches] = i;
                matches++;
            }
        }

        if (matches == 0) {
            System.out.println("No matching figures found.");
            return;
        }

        System.out.println("\nSearch results:");
        for (int i = 0; i < matches; i++) {
            int index = matchIndexes[i];
            System.out.println("[" + (i + 1) + "] " + productIds[index] + " - "
                    + productNames[index] + " | PHP " + money(productPrices[index])
                    + " | Stock: " + productStocks[index]);
        }
        System.out.println("[0] Back");
        int selected = HobbyShop.readChoice("Select a figure to view details: ");
        if (selected == 0) {
            return;
        }
        if (selected < 1 || selected > matches) {
            System.out.println("Invalid result number.");
            return;
        }
        showProductDetails(matchIndexes[selected - 1]);
    }

    // Adds a new figure. Only Admin may create catalog records.
    public static void addProduct() {
        if (!UserModule.isRole("ADMIN")) {
            System.out.println("Access denied. Admin role required to add catalog figures.");
            return;
        }
        loadProducts();
        if (productCount >= MAX_PRODUCTS) {
            System.out.println("The catalog is full.");
            return;
        }

        System.out.println("\n========== ADD NEW FIGURE ==========");
        String id;
        while (true) {
            id = readText("Product ID: ");
            if (id.isEmpty()) {
                System.out.println("Product ID is required.");
            } else if (idExists(id)) {
                System.out.println("That Product ID already exists. Enter a different ID.");
            } else {
                break;
            }
        }
        String name = readRequiredField("Product name: ");
        String series = readRequiredField("Series/collection: ");
        String manufacturer = readRequiredField("Manufacturer: ");
        String category = readRequiredField("Category: ");
        double price = readNonNegativeDouble("Price (PHP): ");
        int stock = readNonNegativeInt("Starting stock quantity: ");

        productIds[productCount] = cleanField(id);
        productNames[productCount] = cleanField(name);
        productSeries[productCount] = cleanField(series);
        productManufacturers[productCount] = cleanField(manufacturer);
        productCategories[productCount] = cleanField(category);
        productPrices[productCount] = price;
        productStocks[productCount] = stock;
        productCount++;

        if (saveProducts()) {
            System.out.println("Figure added successfully.");
        } else {
            productCount--;
            System.out.println("Could not save the figure. The change was canceled in memory.");
        }
    }

    // Updates all fields of an existing figure. Only Admin may edit catalog details.
    public static void updateProduct() {
        if (!UserModule.isRole("ADMIN")) {
            System.out.println("Access denied. Admin role required to update catalog details.");
            return;
        }
        loadProducts();
        String id = readText("Enter the Product ID to update: ");
        int index = findProduct(id);
        if (index == -1) {
            System.out.println("Product ID was not found.");
            return;
        }

        System.out.println("Enter the new details. Leave a field blank to keep its current value.");
        String value = readText("Name [" + productNames[index] + "]: ");
        if (!value.isEmpty()) productNames[index] = cleanField(value);
        value = readText("Series [" + productSeries[index] + "]: ");
        if (!value.isEmpty()) productSeries[index] = cleanField(value);
        value = readText("Manufacturer [" + productManufacturers[index] + "]: ");
        if (!value.isEmpty()) productManufacturers[index] = cleanField(value);
        value = readText("Category [" + productCategories[index] + "]: ");
        if (!value.isEmpty()) productCategories[index] = cleanField(value);
        value = readText("Price in PHP [" + money(productPrices[index]) + "]: ");
        while (!value.isEmpty()) {
            try {
                double newPrice = Double.parseDouble(value);
                if (newPrice >= 0 && !Double.isInfinite(newPrice)) {
                    productPrices[index] = newPrice;
                    break;
                }
            } catch (NumberFormatException e) {
                // Ask again if the price is not a valid number.
            }
            System.out.println("Enter a valid price that is zero or greater, or leave blank to keep the old price.");
            value = readText("Price in PHP [" + money(productPrices[index]) + "]: ");
        }
        value = readText("Stock quantity [" + productStocks[index] + "]: ");
        while (!value.isEmpty()) {
            try {
                int newStock = Integer.parseInt(value);
                if (newStock >= 0) {
                    productStocks[index] = newStock;
                    break;
                }
            } catch (NumberFormatException e) {
                // Ask again if the stock is not a whole number.
            }
            System.out.println("Enter a whole number that is zero or greater, or leave blank to keep the old stock.");
            value = readText("Stock quantity [" + productStocks[index] + "]: ");
        }

        if (saveProducts()) {
            System.out.println("Figure updated successfully.");
        } else {
            System.out.println("Could not save the updated figure. Reload the catalog before continuing.");
            loadProducts();
        }
    }

    // Deletes a figure after confirmation. Only Admin may delete catalog records.
    public static void deleteProduct() {
        if (!UserModule.isRole("ADMIN")) {
            System.out.println("Access denied. Admin role required to delete catalog figures.");
            return;
        }
        loadProducts();
        String id = readText("Enter the Product ID to delete: ");
        int index = findProduct(id);
        if (index == -1) {
            System.out.println("Product ID was not found.");
            return;
        }
        showProductDetails(index);
        String answer = readText("Delete this figure? (Y/N): ");
        if (!answer.equalsIgnoreCase("Y")) {
            System.out.println("Deletion canceled.");
            return;
        }

        for (int i = index; i < productCount - 1; i++) {
            productIds[i] = productIds[i + 1];
            productNames[i] = productNames[i + 1];
            productSeries[i] = productSeries[i + 1];
            productManufacturers[i] = productManufacturers[i + 1];
            productCategories[i] = productCategories[i + 1];
            productPrices[i] = productPrices[i + 1];
            productStocks[i] = productStocks[i + 1];
        }
        productCount--;
        if (saveProducts()) {
            System.out.println("Figure deleted successfully.");
        } else {
            System.out.println("Could not save the deletion. Reload the catalog before continuing.");
            loadProducts();
        }
    }

    // Lets Admin and Staff update stock without changing product details.
    public static void manageStock() {
        if (!UserModule.isRole("STAFF") && !UserModule.isRole("ADMIN")) {
            System.out.println("Access denied. Staff or Admin role required.");
            return;
        }
        loadProducts();
        String id = readText("Enter Product ID: ");
        int index = findProduct(id);
        if (index == -1) {
            System.out.println("Product ID was not found.");
            return;
        }
        System.out.println("Product: " + productNames[index]);
        System.out.println("Current stock: " + productStocks[index]);
        System.out.println("[1] Add stock");
        System.out.println("[2] Set stock quantity");
        System.out.println("[3] Back");
        int choice = HobbyShop.readChoice("Enter your choice: ");
        if (choice == 3) return;
        int quantity;
        if (choice == 1) {
            quantity = readPositiveInt("Quantity to add: ");
            if (productStocks[index] > Integer.MAX_VALUE - quantity) {
                System.out.println("The stock quantity is too large.");
                return;
            }
            productStocks[index] += quantity;
        } else if (choice == 2) {
            quantity = readNonNegativeInt("New stock quantity: ");
            productStocks[index] = quantity;
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (saveProducts()) {
            System.out.println("Stock updated successfully. New stock: " + productStocks[index]);
        } else {
            System.out.println("Could not save the stock change. Reload the catalog before continuing.");
            loadProducts();
        }
    }

    // Displays a simple stock summary. Admin and Staff may view it.
    public static void viewInventorySummary() {
        if (!UserModule.isRole("ADMIN") && !UserModule.isRole("STAFF")) {
            System.out.println("Access denied. Staff or Admin role required.");
            return;
        }
        loadProducts();
        int totalUnits = 0;
        int lowStock = 0;
        double totalValue = 0;
        for (int i = 0; i < productCount; i++) {
            totalUnits += productStocks[i];
            totalValue += productPrices[i] * productStocks[i];
            if (productStocks[i] <= LOW_STOCK_LIMIT) lowStock++;
        }
        System.out.println("\n========== INVENTORY SUMMARY ==========");
        System.out.println("Different products: " + productCount);
        System.out.println("Total units in stock: " + totalUnits);
        System.out.println("Products with " + LOW_STOCK_LIMIT + " or fewer units: " + lowStock);
        System.out.println("Estimated stock value: PHP " + money(totalValue));
        System.out.println("Note: Stock value uses catalog prices and is only a summary.");
        showLowStockList();
    }

    // Lists every product that is low on stock, so staff know what to reorder.
    public static void showLowStockList() {
        System.out.println("\n---------- LOW-STOCK ALERT ----------");
        boolean found = false;
        for (int i = 0; i < productCount; i++) {
            if (productStocks[i] <= LOW_STOCK_LIMIT) {
                String label = productStocks[i] == 0 ? "OUT OF STOCK" : productStocks[i] + " left";
                System.out.println(productIds[i] + " - " + productNames[i] + " (" + label + ")");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No products are low on stock.");
        }
    }

    // Asks for a Product ID and shows that figure's full details.
    public static void viewDetailsById() {
        if (!isAllowedToView()) {
            System.out.println("Access denied. Please log in first.");
            return;
        }
        loadProducts();
        String id = readText("Enter the Product ID (or 0 to cancel): ");
        if (id.equals("0")) {
            return;
        }
        int index = findProduct(id);
        if (index == -1) {
            System.out.println("Product ID was not found.");
            return;
        }
        showProductDetails(index);
    }

    // Shows all available information for one product.
    public static void showProductDetails(int index) {
        System.out.println("\n========== FIGURE DETAILS ==========");
        System.out.println("Product ID: " + productIds[index]);
        System.out.println("Name: " + productNames[index]);
        System.out.println("Series/collection: " + productSeries[index]);
        System.out.println("Manufacturer: " + productManufacturers[index]);
        System.out.println("Category: " + productCategories[index]);
        System.out.println("Price: PHP " + money(productPrices[index]));
        System.out.println("Available stock: " + productStocks[index]);
        System.out.println("====================================");
    }

    // Checks whether the current role may view the catalog.
    public static boolean isAllowedToView() {
        return UserModule.isRole("ADMIN") || UserModule.isRole("STAFF") || UserModule.isRole("CUSTOMER");
    }

    // Finds a product by ID and returns its array position.
    public static int findProduct(String id) {
        for (int i = 0; i < productCount; i++) {
            if (productIds[i].equalsIgnoreCase(id.trim())) return i;
        }
        return -1;
    }

    // Checks whether a product ID already exists.
    public static boolean idExists(String id) {
        for (int i = 0; i < productCount; i++) {
            if (productIds[i] != null && productIds[i].equalsIgnoreCase(id.trim())) return true;
        }
        return false;
    }

    // Reads and cleans a text field so it cannot corrupt the file delimiter format.
    public static String readText(String message) {
        System.out.print(message);
        return cleanField(HobbyShop.input.nextLine().trim());
    }

    // Removes the pipe delimiter and line breaks from a field.
    public static String cleanField(String value) {
        return value.replace("|", "/").replace("\n", " ").replace("\r", " ").trim();
    }

    // Repeats until the user enters a non-empty field.
    public static String readRequiredField(String message) {
        while (true) {
            String value = readText(message);
            if (!value.isEmpty()) return value;
            System.out.println("This field is required.");
        }
    }

    // Reads a number that may be zero but cannot be negative.
    public static double readNonNegativeDouble(String message) {
        while (true) {
            String value = readText(message);
            try {
                double number = Double.parseDouble(value);
                if (number >= 0 && !Double.isInfinite(number) && !Double.isNaN(number)) return number;
            } catch (NumberFormatException e) {
                // Show the validation message below.
            }
            System.out.println("Please enter a valid number that is zero or greater.");
        }
    }

    // Reads an integer that may be zero but cannot be negative.
    public static int readNonNegativeInt(String message) {
        while (true) {
            String value = readText(message);
            try {
                int number = Integer.parseInt(value);
                if (number >= 0) return number;
            } catch (NumberFormatException e) {
                // Show the validation message below.
            }
            System.out.println("Please enter a whole number that is zero or greater.");
        }
    }

    // Reads a whole number greater than zero.
    public static int readPositiveInt(String message) {
        while (true) {
            int number = readNonNegativeInt(message);
            if (number > 0) return number;
            System.out.println("Quantity must be greater than zero.");
        }
    }

    // Formats a number to two decimal places.
    public static String money(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    // Shortens long text to keep catalog columns aligned.
    public static String shorten(String value, int maxLength) {
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength - 3) + "...";
    }
}
