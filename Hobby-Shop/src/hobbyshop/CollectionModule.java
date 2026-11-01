package hobbyshop;

// Handles each customer's collection, wishlist, price tracking, and figure condition.
public class CollectionModule {

    public static final String COLLECTION_FILE = "data/collections.txt";
    public static final String WISHLIST_FILE = "data/wishlist.txt";
    public static final int MAX_RECORDS = FileHelper.MAX_RECORDS;

    // Displays options for managing the logged-in customer's collection.
    public static void showCollectionMenu() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }

        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========== MY COLLECTION ==========");
            System.out.println("[1] View My Collection");
            System.out.println("[2] Add Owned Figure");
            System.out.println("[3] Update Collection Record");
            System.out.println("[4] Remove Figure from Collection");
            System.out.println("[5] Back to Customer Menu");
            int choice = HobbyShop.readChoice("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewMyCollection();
                    HobbyShop.pause();
                    break;
                case 2:
                    addOwnedFigure();
                    HobbyShop.pause();
                    break;
                case 3:
                    updateCollectionRecord();
                    HobbyShop.pause();
                    break;
                case 4:
                    removeOwnedFigure();
                    HobbyShop.pause();
                    break;
                case 5:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 5.");
            }
        }
    }

    // Displays wishlist options for the logged-in customer.
    public static void showWishlistMenu() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }

        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========== MY WISHLIST ==========");
            System.out.println("[1] View My Wishlist");
            System.out.println("[2] Add Figure to Wishlist");
            System.out.println("[3] Remove Figure from Wishlist");
            System.out.println("[4] Back to Customer Menu");
            int choice = HobbyShop.readChoice("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewWishlist();
                    HobbyShop.pause();
                    break;
                case 2:
                    addWishlistItem();
                    HobbyShop.pause();
                    break;
                case 3:
                    removeWishlistItem();
                    HobbyShop.pause();
                    break;
                case 4:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 4.");
            }
        }
    }

    // Displays price tracking and figure-condition options.
    public static void showPriceTrackingMenu() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }

        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n====== PRICE TRACKING AND CONDITION ======");
            System.out.println("[1] View Estimated Values");
            System.out.println("[2] Update Estimated Value");
            System.out.println("[3] Update Figure Condition");
            System.out.println("[4] Back to Customer Menu");
            int choice = HobbyShop.readChoice("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewPriceTracking();
                    HobbyShop.pause();
                    break;
                case 2:
                    updateEstimatedValue();
                    HobbyShop.pause();
                    break;
                case 3:
                    updateFigureCondition();
                    HobbyShop.pause();
                    break;
                case 4:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 4.");
            }
        }
    }

    // Adds a successful customer purchase to that customer's collection automatically.
    // For an existing entry, quantity and average purchase price are updated.
    public static boolean recordCustomerPurchase(String username, String productId,
            int quantity, double unitPrice, String purchaseDate) {
        if (!UserModule.isRole("CUSTOMER")
                || !UserModule.currentUsername.equalsIgnoreCase(username)
                || quantity <= 0 || unitPrice < 0 || !SalesModule.validDateFormat(purchaseDate)) {
            return false;
        }

        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(COLLECTION_FILE, lines, MAX_RECORDS);
        int index = findOwnCollectionIndex(lines, count, productId);

        if (index >= 0) {
            String[] fields = lines[index].split("\\|", -1);
            if (!validCollectionFields(fields)) {
                return false;
            }
            int oldQuantity = Integer.parseInt(fields[2]);
            if (quantity > Integer.MAX_VALUE - oldQuantity) {
                return false;
            }
            double oldPurchasePrice = Double.parseDouble(fields[3]);
            int newQuantity = oldQuantity + quantity;
            double averagePrice = ((oldPurchasePrice * oldQuantity) + (unitPrice * quantity)) / newQuantity;
            fields[2] = String.valueOf(newQuantity);
            fields[3] = ProductModule.money(averagePrice);
            fields[5] = purchaseDate;
            lines[index] = joinFields(fields);
            return FileHelper.writeLines(COLLECTION_FILE, lines, count);
        }

        if (count >= MAX_RECORDS) {
            return false;
        }
        // The purchase price is the initial estimate until the customer changes it.
        lines[count] = username + "|" + ProductModule.cleanField(productId) + "|" + quantity + "|"
                + ProductModule.money(unitPrice) + "|" + ProductModule.money(unitPrice) + "|"
                + purchaseDate + "|New";
        return FileHelper.writeLines(COLLECTION_FILE, lines, count + 1);
    }

    // Shows only records owned by the logged-in customer.
    public static void viewMyCollection() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }

        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(COLLECTION_FILE, lines, MAX_RECORDS);
        ProductModule.loadProducts();
        boolean found = false;

        System.out.println("\n==================== MY COLLECTION ====================");
        for (int i = 0; i < count; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (fields.length != 7) {
                System.out.println("Warning: Skipping malformed collection record on line " + (i + 1) + ".");
                continue;
            }
            if (!fields[0].equalsIgnoreCase(UserModule.currentUsername)) {
                continue;
            }
            if (!validCollectionFields(fields)) {
                System.out.println("Warning: Skipping invalid collection record on line " + (i + 1) + ".");
                continue;
            }

            int productIndex = ProductModule.findProduct(fields[1]);
            String productName = productIndex >= 0 ? ProductModule.productNames[productIndex] : "Unavailable product";
            int quantity = Integer.parseInt(fields[2]);
            double purchasePrice = Double.parseDouble(fields[3]);
            double estimatedValue = Double.parseDouble(fields[4]);
            double difference = (estimatedValue - purchasePrice) * quantity;

            System.out.println("Product ID: " + fields[1]);
            System.out.println("Figure: " + productName);
            System.out.println("Quantity Owned: " + quantity);
            System.out.printf("Purchase Price per Figure: PHP %.2f%n", purchasePrice);
            System.out.printf("Your Estimated Value per Figure: PHP %.2f%n", estimatedValue);
            System.out.println("Purchase Date: " + fields[5]);
            System.out.println("Condition: " + fields[6]);
            System.out.printf("Estimated Difference for All Owned: %sPHP %.2f%n",
                    difference >= 0 ? "+" : "-", Math.abs(difference));
            System.out.println("-------------------------------------------------------");
            found = true;
        }

        if (!found) {
            System.out.println("Your collection is empty. Add a figure you own to get started.");
        }
        System.out.println("Note: estimated values are your own estimates, not verified market prices.");
    }

    // Adds a figure and the customer's purchase information to their collection.
    public static void addOwnedFigure() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }

        ProductModule.loadProducts();
        ProductModule.viewAllProducts();
        String productId = ProductModule.readRequiredField("Enter Product ID to add (0 to cancel): ");
        if (productId.equals("0")) {
            System.out.println("Operation cancelled.");
            return;
        }
        int productIndex = ProductModule.findProduct(productId);
        if (productIndex < 0) {
            System.out.println("Product ID was not found. Nothing was added.");
            return;
        }

        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(COLLECTION_FILE, lines, MAX_RECORDS);
        if (count >= MAX_RECORDS) {
            System.out.println("Collection file is full. No record was added.");
            return;
        }
        if (findOwnCollectionIndex(lines, count, ProductModule.productIds[productIndex]) >= 0) {
            System.out.println("This figure is already in your collection. Use Update Collection Record instead.");
            return;
        }

        int quantity = ProductModule.readPositiveInt("Quantity owned: ");
        double purchasePrice = ProductModule.readNonNegativeDouble("Purchase price per figure (PHP): ");
        double estimatedValue = ProductModule.readNonNegativeDouble("Your estimated current value per figure (PHP): ");
        String purchaseDate = readPurchaseDate();
        if (purchaseDate == null) {
            System.out.println("Operation cancelled.");
            return;
        }
        String condition = readCondition();

        System.out.println("\n========== COLLECTION ENTRY SUMMARY ==========");
        System.out.println("Figure: " + ProductModule.productNames[productIndex]);
        System.out.println("Quantity: " + quantity);
        System.out.printf("Purchase price per figure: PHP %.2f%n", purchasePrice);
        System.out.printf("Your estimated value per figure: PHP %.2f%n", estimatedValue);
        System.out.println("Purchase date: " + purchaseDate);
        System.out.println("Condition: " + condition);
        if (!confirm("Save this collection entry? (Y/N): ")) {
            System.out.println("Operation cancelled. No changes were made.");
            return;
        }

        lines[count] = UserModule.currentUsername + "|" + ProductModule.productIds[productIndex] + "|"
                + quantity + "|" + ProductModule.money(purchasePrice) + "|"
                + ProductModule.money(estimatedValue) + "|" + purchaseDate + "|" + condition;
        if (FileHelper.writeLines(COLLECTION_FILE, lines, count + 1)) {
            System.out.println("Figure added to your collection successfully.");
        } else {
            System.out.println("Could not save your collection. Please try again.");
        }
    }

    // Updates quantity and purchase/value details for one of the customer's figures.
    public static void updateCollectionRecord() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }

        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(COLLECTION_FILE, lines, MAX_RECORDS);
        viewMyCollection();
        String productId = ProductModule.readRequiredField("Enter Product ID to update (0 to cancel): ");
        if (productId.equals("0")) {
            System.out.println("Operation cancelled.");
            return;
        }
        int index = findOwnCollectionIndex(lines, count, productId);
        if (index < 0) {
            System.out.println("That figure was not found in your collection.");
            return;
        }

        String[] fields = lines[index].split("\\|", -1);
        if (!validCollectionFields(fields)) {
            System.out.println("This collection record is invalid and cannot be updated.");
            return;
        }

        System.out.println("Press Enter to keep the current value.");
        fields[2] = readOptionalPositiveInt("Quantity owned [" + fields[2] + "]: ", fields[2]);
        fields[3] = readOptionalNonNegativeDouble("Purchase price per figure [" + fields[3] + "]: ", fields[3]);
        fields[4] = readOptionalNonNegativeDouble("Your estimated value per figure [" + fields[4] + "]: ", fields[4]);
        fields[5] = readOptionalDate("Purchase date [" + fields[5] + "]: ", fields[5]);
        fields[6] = readOptionalCondition("Condition [" + fields[6] + "]: ", fields[6]);

        if (!confirm("Save these changes? (Y/N): ")) {
            System.out.println("Update cancelled. No changes were made.");
            return;
        }
        lines[index] = joinFields(fields);
        if (FileHelper.writeLines(COLLECTION_FILE, lines, count)) {
            System.out.println("Collection record updated successfully.");
        } else {
            System.out.println("Could not save the update.");
        }
    }

    // Removes a figure after the customer confirms the deletion.
    public static void removeOwnedFigure() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }

        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(COLLECTION_FILE, lines, MAX_RECORDS);
        viewMyCollection();
        String productId = ProductModule.readRequiredField("Enter Product ID to remove (0 to cancel): ");
        if (productId.equals("0")) {
            System.out.println("Operation cancelled.");
            return;
        }
        int index = findOwnCollectionIndex(lines, count, productId);
        if (index < 0) {
            System.out.println("That figure was not found in your collection.");
            return;
        }
        if (!confirm("Remove this figure from your collection? (Y/N): ")) {
            System.out.println("Removal cancelled.");
            return;
        }
        for (int i = index; i < count - 1; i++) {
            lines[i] = lines[i + 1];
        }
        lines[count - 1] = null;
        if (FileHelper.writeLines(COLLECTION_FILE, lines, count - 1)) {
            System.out.println("Figure removed from your collection.");
        } else {
            System.out.println("Could not save the removal.");
        }
    }

    // Shows each collection item's estimated value and gain or loss.
    public static void viewPriceTracking() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }
        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(COLLECTION_FILE, lines, MAX_RECORDS);
        ProductModule.loadProducts();
        boolean found = false;

        System.out.println("\n==================== PRICE TRACKING ====================");
        for (int i = 0; i < count; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (fields.length != 7 || !fields[0].equalsIgnoreCase(UserModule.currentUsername)) {
                continue;
            }
            if (!validCollectionFields(fields)) {
                System.out.println("Warning: Skipping invalid collection record on line " + (i + 1) + ".");
                continue;
            }
            int productIndex = ProductModule.findProduct(fields[1]);
            String name = productIndex >= 0 ? ProductModule.productNames[productIndex] : "Unavailable product";
            int quantity = Integer.parseInt(fields[2]);
            double paid = Double.parseDouble(fields[3]);
            double estimate = Double.parseDouble(fields[4]);
            double difference = (estimate - paid) * quantity;

            System.out.println(fields[1] + " - " + name);
            System.out.println("Quantity owned: " + quantity);
            System.out.printf("Total purchase cost: PHP %.2f%n", paid * quantity);
            System.out.printf("Your estimated total value: PHP %.2f%n", estimate * quantity);
            System.out.printf("Estimated difference: %sPHP %.2f%n", difference >= 0 ? "+" : "-", Math.abs(difference));
            System.out.println("Condition: " + fields[6]);
            System.out.println("---------------------------------------------------------");
            found = true;
        }
        if (!found) {
            System.out.println("No price records found. Add a figure to My Collection first.");
        }
        System.out.println("Values are manual estimates, not verified market prices.");
    }

    // Changes only the estimated current value for one of the customer's owned figures.
    public static void updateEstimatedValue() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }
        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(COLLECTION_FILE, lines, MAX_RECORDS);
        viewMyCollection();
        String productId = ProductModule.readRequiredField("Enter Product ID to update estimate (0 to cancel): ");
        if (productId.equals("0")) {
            System.out.println("Operation cancelled.");
            return;
        }
        int index = findOwnCollectionIndex(lines, count, productId);
        if (index < 0) {
            System.out.println("That figure was not found in your collection.");
            return;
        }
        String[] fields = lines[index].split("\\|", -1);
        if (!validCollectionFields(fields)) {
            System.out.println("This collection record is invalid and cannot be updated.");
            return;
        }
        double value = ProductModule.readNonNegativeDouble("Enter your new estimated value per figure (PHP): ");
        fields[4] = ProductModule.money(value);
        if (!confirm("Save this estimate? (Y/N): ")) {
            System.out.println("Update cancelled.");
            return;
        }
        lines[index] = joinFields(fields);
        if (FileHelper.writeLines(COLLECTION_FILE, lines, count)) {
            System.out.println("Your estimate was updated successfully.");
        } else {
            System.out.println("Could not save the estimated value.");
        }
    }

    // Updates the condition of one figure owned by the logged-in customer.
    public static void updateFigureCondition() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }
        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(COLLECTION_FILE, lines, MAX_RECORDS);
        viewMyCollection();
        String productId = ProductModule.readRequiredField("Enter Product ID to update condition (0 to cancel): ");
        if (productId.equals("0")) {
            System.out.println("Operation cancelled.");
            return;
        }
        int index = findOwnCollectionIndex(lines, count, productId);
        if (index < 0) {
            System.out.println("That figure was not found in your collection.");
            return;
        }
        String[] fields = lines[index].split("\\|", -1);
        if (!validCollectionFields(fields)) {
            System.out.println("This collection record is invalid and cannot be updated.");
            return;
        }
        fields[6] = readCondition();
        if (!confirm("Save this condition? (Y/N): ")) {
            System.out.println("Update cancelled.");
            return;
        }
        lines[index] = joinFields(fields);
        if (FileHelper.writeLines(COLLECTION_FILE, lines, count)) {
            System.out.println("Figure condition updated successfully.");
        } else {
            System.out.println("Could not save the figure condition.");
        }
    }

    // Displays the customer's wishlist items.
    public static void viewWishlist() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }
        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(WISHLIST_FILE, lines, MAX_RECORDS);
        ProductModule.loadProducts();
        boolean found = false;

        System.out.println("\n==================== MY WISHLIST ====================");
        for (int i = 0; i < count; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (fields.length != 2) {
                System.out.println("Warning: Skipping malformed wishlist record on line " + (i + 1) + ".");
                continue;
            }
            if (!fields[0].equalsIgnoreCase(UserModule.currentUsername)) {
                continue;
            }
            int productIndex = ProductModule.findProduct(fields[1]);
            if (productIndex >= 0) {
                System.out.println(fields[1] + " - " + ProductModule.productNames[productIndex]
                        + " | PHP " + ProductModule.money(ProductModule.productPrices[productIndex])
                        + " | Stock: " + ProductModule.productStocks[productIndex]);
            } else {
                System.out.println(fields[1] + " - Product is no longer in the catalog.");
            }
            found = true;
        }
        if (!found) {
            System.out.println("Your wishlist is empty.");
        }
    }

    // Adds a catalog figure to the current customer's wishlist, without duplicates.
    public static void addWishlistItem() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }
        ProductModule.loadProducts();
        ProductModule.viewAllProducts();
        String productId = ProductModule.readRequiredField("Enter Product ID to add (0 to cancel): ");
        if (productId.equals("0")) {
            System.out.println("Operation cancelled.");
            return;
        }
        int productIndex = ProductModule.findProduct(productId);
        if (productIndex < 0) {
            System.out.println("Product ID was not found. Nothing was added.");
            return;
        }

        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(WISHLIST_FILE, lines, MAX_RECORDS);
        if (count >= MAX_RECORDS) {
            System.out.println("Wishlist file is full. No item was added.");
            return;
        }
        if (findWishlistIndex(lines, count, ProductModule.productIds[productIndex]) >= 0) {
            System.out.println("This figure is already in your wishlist.");
            return;
        }
        lines[count] = UserModule.currentUsername + "|" + ProductModule.productIds[productIndex];
        if (FileHelper.writeLines(WISHLIST_FILE, lines, count + 1)) {
            System.out.println("Figure added to your wishlist successfully.");
        } else {
            System.out.println("Could not save your wishlist.");
        }
    }

    // Removes an item only from the logged-in customer's wishlist.
    public static void removeWishlistItem() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }
        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(WISHLIST_FILE, lines, MAX_RECORDS);
        viewWishlist();
        String productId = ProductModule.readRequiredField("Enter Product ID to remove (0 to cancel): ");
        if (productId.equals("0")) {
            System.out.println("Operation cancelled.");
            return;
        }
        int index = findWishlistIndex(lines, count, productId);
        if (index < 0) {
            System.out.println("That figure was not found in your wishlist.");
            return;
        }
        if (!confirm("Remove this figure from your wishlist? (Y/N): ")) {
            System.out.println("Removal cancelled.");
            return;
        }
        for (int i = index; i < count - 1; i++) {
            lines[i] = lines[i + 1];
        }
        lines[count - 1] = null;
        if (FileHelper.writeLines(WISHLIST_FILE, lines, count - 1)) {
            System.out.println("Figure removed from your wishlist.");
        } else {
            System.out.println("Could not save the wishlist removal.");
        }
    }

    // Finds a collection entry only when it belongs to the logged-in customer.
    public static int findOwnCollectionIndex(String[] lines, int count, String productId) {
        for (int i = 0; i < count; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (fields.length == 7 && fields[0].equalsIgnoreCase(UserModule.currentUsername)
                    && fields[1].equalsIgnoreCase(productId.trim())) {
                return i;
            }
        }
        return -1;
    }

    // Finds a wishlist entry only when it belongs to the logged-in customer.
    public static int findWishlistIndex(String[] lines, int count, String productId) {
        for (int i = 0; i < count; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (fields.length == 2 && fields[0].equalsIgnoreCase(UserModule.currentUsername)
                    && fields[1].equalsIgnoreCase(productId.trim())) {
                return i;
            }
        }
        return -1;
    }

    // Checks that a saved collection record has valid values.
    public static boolean validCollectionFields(String[] fields) {
        if (fields.length != 7 || fields[0].trim().isEmpty() || fields[1].trim().isEmpty()) {
            return false;
        }
        try {
            int quantity = Integer.parseInt(fields[2]);
            double purchasePrice = Double.parseDouble(fields[3]);
            double estimatedValue = Double.parseDouble(fields[4]);
            return quantity > 0 && purchasePrice >= 0 && estimatedValue >= 0
                    && !Double.isInfinite(purchasePrice) && !Double.isNaN(purchasePrice)
                    && !Double.isInfinite(estimatedValue) && !Double.isNaN(estimatedValue)
                    && SalesModule.validDateFormat(fields[5]) && validCondition(fields[6]);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Requests a purchase date in YYYY-MM-DD format.
    public static String readPurchaseDate() {
        while (true) {
            String date = ProductModule.readRequiredField("Purchase date (YYYY-MM-DD, or 0 to cancel): ");
            if (date.equals("0")) {
                return null;
            }
            if (SalesModule.validDateFormat(date)) {
                return date;
            }
            System.out.println("Invalid date. Use YYYY-MM-DD, for example 2026-10-29.");
        }
    }

    // Lets the customer select one of the allowed figure conditions.
    public static String readCondition() {
        while (true) {
            System.out.println("Select figure condition:");
            System.out.println("[1] New");
            System.out.println("[2] Like New");
            System.out.println("[3] Good");
            System.out.println("[4] Fair");
            System.out.println("[5] Damaged");
            int choice = HobbyShop.readChoice("Enter condition: ");
            switch (choice) {
                case 1:
                    return "New";
                case 2:
                    return "Like New";
                case 3:
                    return "Good";
                case 4:
                    return "Fair";
                case 5:
                    return "Damaged";
                default:
                    System.out.println("Invalid choice. Please enter 1 to 5.");
            }
        }
    }

    // Checks whether a saved condition is in the allowed list.
    public static boolean validCondition(String condition) {
        return condition.equals("New") || condition.equals("Like New") || condition.equals("Good")
                || condition.equals("Fair") || condition.equals("Damaged");
    }

    // Reads a new optional quantity while allowing Enter to keep the old value.
    public static String readOptionalPositiveInt(String prompt, String oldValue) {
        while (true) {
            System.out.print(prompt);
            String value = HobbyShop.input.nextLine().trim();
            if (value.isEmpty()) {
                return oldValue;
            }
            try {
                int number = Integer.parseInt(value);
                if (number > 0) {
                    return String.valueOf(number);
                }
            } catch (NumberFormatException e) {
                // Show the validation message below.
            }
            System.out.println("Enter a whole number greater than zero, or press Enter to keep the current value.");
        }
    }

    // Reads a new optional amount while allowing Enter to keep the old value.
    public static String readOptionalNonNegativeDouble(String prompt, String oldValue) {
        while (true) {
            System.out.print(prompt);
            String value = HobbyShop.input.nextLine().trim();
            if (value.isEmpty()) {
                return oldValue;
            }
            try {
                double number = Double.parseDouble(value);
                if (number >= 0 && !Double.isInfinite(number) && !Double.isNaN(number)) {
                    return ProductModule.money(number);
                }
            } catch (NumberFormatException e) {
                // Show the validation message below.
            }
            System.out.println("Enter a valid amount of zero or more, or press Enter to keep the current value.");
        }
    }

    // Reads a new optional date and validates its format.
    public static String readOptionalDate(String prompt, String oldValue) {
        while (true) {
            System.out.print(prompt);
            String value = HobbyShop.input.nextLine().trim();
            if (value.isEmpty()) {
                return oldValue;
            }
            if (SalesModule.validDateFormat(value)) {
                return value;
            }
            System.out.println("Enter a valid date in YYYY-MM-DD format, or press Enter to keep the current value.");
        }
    }

    // Reads a condition while allowing Enter to keep the current one.
    public static String readOptionalCondition(String prompt, String oldValue) {
        while (true) {
            System.out.println(prompt + " Press Enter to keep it, or enter 1-New, 2-Like New, 3-Good, 4-Fair, 5-Damaged.");
            String value = HobbyShop.input.nextLine().trim();
            if (value.isEmpty()) {
                return oldValue;
            }
            switch (value) {
                case "1":
                    return "New";
                case "2":
                    return "Like New";
                case "3":
                    return "Good";
                case "4":
                    return "Fair";
                case "5":
                    return "Damaged";
                default:
                    System.out.println("Invalid condition. Please enter 1 to 5 or press Enter to keep the current value.");
            }
        }
    }

    // Reads a yes/no confirmation.
    public static boolean confirm(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = HobbyShop.input.nextLine().trim();
            if (answer.equalsIgnoreCase("Y")) {
                return true;
            }
            if (answer.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("Please enter Y or N.");
        }
    }

    // Removes every collection and wishlist record that belongs to a deleted customer.
    public static boolean removeRecordsForCustomer(String username) {
        boolean collectionOk = removeLinesForUser(COLLECTION_FILE, username);
        boolean wishlistOk = removeLinesForUser(WISHLIST_FILE, username);
        return collectionOk && wishlistOk;
    }

    // Rewrites one file without the lines whose first field is this username.
    public static boolean removeLinesForUser(String filePath, String username) {
        String[] lines = new String[MAX_RECORDS];
        int count = FileHelper.readLines(filePath, lines, MAX_RECORDS);
        String[] kept = new String[MAX_RECORDS];
        int keptCount = 0;
        for (int i = 0; i < count; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (!fields[0].equalsIgnoreCase(username)) {
                kept[keptCount] = lines[i];
                keptCount++;
            }
        }
        if (keptCount == count) {
            return true;
        }
        return FileHelper.writeLines(filePath, kept, keptCount);
    }

    // Joins collection fields using the file's pipe delimiter.
    public static String joinFields(String[] fields) {
        String line = "";
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                line += "|";
            }
            line += ProductModule.cleanField(fields[i]);
        }
        return line;
    }
}
