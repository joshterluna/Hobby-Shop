package hobbyshop;

public class SalesModule {

    public static final String TRANSACTION_FILE = "data/transactions.txt";
    public static final int MAX_TRANSACTIONS = FileHelper.MAX_RECORDS;

    // Records a purchase made by the currently logged-in Customer.
    public static void recordCustomerPurchase() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }
        processSale(true);
    }

    // Records a walk-in sale processed by Shop Staff.
    public static void recordWalkInSale() {
        if (!UserModule.isRole("STAFF")) {
            System.out.println("Access denied. Staff role required.");
            return;
        }
        processSale(false);
    }

    // Checks the product and quantity, updates stock, then saves the transaction.
    public static void processSale(boolean customerPurchase) {
        if (customerPurchase && !UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }
        if (!customerPurchase && !UserModule.isRole("STAFF")) {
            System.out.println("Access denied. Staff role required.");
            return;
        }
        ProductModule.loadProducts();
        if (ProductModule.productCount == 0) {
            System.out.println("There are no products available for sale.");
            return;
        }

        System.out.println("\n========== " + (customerPurchase ? "CUSTOMER PURCHASE" : "WALK-IN SALE") + " ==========");
        ProductModule.viewAllProducts();

        String productId = ProductModule.readRequiredField("Enter Product ID (or 0 to cancel): ");
        if (productId.equals("0")) {
            System.out.println("Sale cancelled.");
            return;
        }

        int productIndex = ProductModule.findProduct(productId);
        if (productIndex < 0) {
            System.out.println("Product ID was not found. No sale was recorded.");
            return;
        }

        if (ProductModule.productStocks[productIndex] <= 0) {
            System.out.println("This product is out of stock.");
            return;
        }

        int quantity = ProductModule.readNonNegativeInt("Enter quantity (0 to cancel): ");
        if (quantity == 0) {
            System.out.println("Sale cancelled.");
            return;
        }
        if (quantity > ProductModule.productStocks[productIndex]) {
            System.out.println("Not enough stock. Available quantity: "
                    + ProductModule.productStocks[productIndex]);
            return;
        }

        String date = readTransactionDate();
        if (date == null) {
            System.out.println("Sale cancelled.");
            return;
        }

        double unitPrice = ProductModule.productPrices[productIndex];
        double total = unitPrice * quantity;
        String buyer;
        String processedBy = UserModule.currentUsername;
        if (customerPurchase) {
            buyer = UserModule.currentUsername;
        } else {
            buyer = "WALK-IN";
        }

        System.out.println("\n========== SALE SUMMARY ==========");
        System.out.println("Product: " + ProductModule.productNames[productIndex]);
        System.out.println("Quantity: " + quantity);
        System.out.printf("Unit Price: PHP %.2f%n", unitPrice);
        System.out.printf("Total Amount: PHP %.2f%n", total);
        System.out.println("Date: " + date);
        System.out.print("Confirm sale? (Y/N): ");
        String confirm = HobbyShop.input.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Sale cancelled. No changes were made.");
            return;
        }

        String[] oldTransactions = new String[MAX_TRANSACTIONS];
        int transactionCount = FileHelper.readLines(TRANSACTION_FILE, oldTransactions, MAX_TRANSACTIONS);
        if (transactionCount >= MAX_TRANSACTIONS) {
            System.out.println("Transaction file is full. Sale was not recorded to avoid losing records.");
            return;
        }

        String transactionId = createTransactionId(oldTransactions, transactionCount);
        String transaction = transactionId + "|" + date + "|" + processedBy + "|" + buyer
                + "|" + ProductModule.productIds[productIndex]
                + "|" + ProductModule.cleanField(ProductModule.productNames[productIndex])
                + "|" + quantity + "|" + ProductModule.money(unitPrice) + "|" + ProductModule.money(total);

        int oldStock = ProductModule.productStocks[productIndex];
        ProductModule.productStocks[productIndex] = oldStock - quantity;
        if (!ProductModule.saveProducts()) {
            ProductModule.productStocks[productIndex] = oldStock;
            System.out.println("Could not update product stock. Sale was not recorded.");
            return;
        }

        oldTransactions[transactionCount] = transaction;
        if (!FileHelper.writeLines(TRANSACTION_FILE, oldTransactions, transactionCount + 1)) {
            // Attempt to restore stock if the transaction could not be saved.
            ProductModule.productStocks[productIndex] = oldStock;
            if (!ProductModule.saveProducts()) {
                System.out.println("WARNING: Stock rollback failed. Please check products.txt manually.");
            }
            System.out.println("Could not save the transaction. The sale was cancelled.");
            return;
        }

        boolean collectionUpdated = true;
        if (customerPurchase) {
            collectionUpdated = CollectionModule.recordCustomerPurchase(
                    UserModule.currentUsername, ProductModule.productIds[productIndex],
                    quantity, unitPrice, date);
        }

        printReceipt(transactionId, date, processedBy, buyer,
                ProductModule.productIds[productIndex], ProductModule.productNames[productIndex],
                quantity, unitPrice, total);
        System.out.println("Remaining stock: " + ProductModule.productStocks[productIndex]);
        System.out.println("Sale recorded successfully.");
        if (customerPurchase && collectionUpdated) {
            System.out.println("Your purchased figure was also added to My Collection.");
            System.out.println("The initial estimated value is the purchase price; you can change it later.");
        } else if (customerPurchase) {
            System.out.println("WARNING: The sale was saved, but My Collection could not be updated.");
            System.out.println("Please add the figure manually in My Collection.");
        }
    }

    // Requests a date in a simple YYYY-MM-DD format.
    public static String readTransactionDate() {
        while (true) {
            System.out.print("Transaction date (YYYY-MM-DD, or 0 to cancel): ");
            String date = HobbyShop.input.nextLine().trim();
            if (date.equals("0")) {
                return null;
            }
            if (validDateFormat(date)) {
                return date;
            }
            System.out.println("Please use the format YYYY-MM-DD, for example 2026-10-29.");
        }
    }

    // Checks the basic shape of a date without using advanced date classes.
    public static boolean validDateFormat(String date) {
        if (date.length() != 10 || date.charAt(4) != '-' || date.charAt(7) != '-') {
            return false;
        }
        for (int i = 0; i < date.length(); i++) {
            if (i == 4 || i == 7) {
                continue;
            }
            if (date.charAt(i) < '0' || date.charAt(i) > '9') {
                return false;
            }
        }

        try {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(5, 7));
            int day = Integer.parseInt(date.substring(8, 10));
            if (year < 1 || month < 1 || month > 12 || day < 1) {
                return false;
            }

            int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            boolean leapYear = (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0);
            if (leapYear) {
                daysInMonth[1] = 29;
            }
            return day <= daysInMonth[month - 1];
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Creates a transaction ID using the highest existing numeric suffix.
    public static String createTransactionId(String[] lines, int count) {
        int highestNumber = 0;
        for (int i = 0; i < count; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (fields.length > 0 && fields[0].startsWith("TXN")) {
                try {
                    int number = Integer.parseInt(fields[0].substring(3));
                    if (number > highestNumber) {
                        highestNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignore malformed IDs when selecting the next number.
                }
            }
        }
        highestNumber++;
        if (highestNumber < 1000) {
            return String.format("TXN%03d", highestNumber);
        }
        return "TXN" + highestNumber;
    }

    // Displays a receipt after the product stock and transaction file are saved.
    public static void printReceipt(String id, String date, String processedBy, String buyer,
            String productId, String productName, int quantity, double unitPrice, double total) {
        System.out.println("\n========================================");
        System.out.println("             AETHERION RECEIPT");
        System.out.println("========================================");
        System.out.println("Transaction ID: " + id);
        System.out.println("Date: " + date);
        System.out.println("Processed By: " + processedBy);
        System.out.println("Customer: " + buyer);
        System.out.println("Product ID: " + productId);
        System.out.println("Product: " + productName);
        System.out.println("Quantity: " + quantity);
        System.out.printf("Unit Price: PHP %.2f%n", unitPrice);
        System.out.printf("TOTAL: PHP %.2f%n", total);
        System.out.println("========================================");
    }

    // Shows all sales to Admin/Staff and only the logged-in Customer's sales to Customers.
    public static void viewTransactionHistory() {
        if (!UserModule.isRole("ADMIN") && !UserModule.isRole("STAFF")
                && !UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Please log in first.");
            return;
        }

        String[] lines = new String[MAX_TRANSACTIONS];
        int count = FileHelper.readLines(TRANSACTION_FILE, lines, MAX_TRANSACTIONS);
        boolean found = false;

        System.out.println("\n==================== SALES HISTORY ====================");
        for (int i = 0; i < count; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (fields.length != 9) {
                System.out.println("Warning: Skipping malformed transaction on line " + (i + 1) + ".");
                continue;
            }

            if (UserModule.isRole("CUSTOMER")
                    && !fields[3].equalsIgnoreCase(UserModule.currentUsername)) {
                continue;
            }

            try {
                int quantity = Integer.parseInt(fields[6]);
                double unitPrice = Double.parseDouble(fields[7]);
                double total = Double.parseDouble(fields[8]);
                if (quantity <= 0 || unitPrice < 0 || total < 0) {
                    System.out.println("Warning: Skipping invalid transaction on line " + (i + 1) + ".");
                    continue;
                }
                System.out.println("Transaction ID: " + fields[0]);
                System.out.println("Date: " + fields[1]);
                System.out.println("Processed By: " + fields[2]);
                System.out.println("Customer: " + fields[3]);
                System.out.println("Product: " + fields[4] + " - " + fields[5]);
                System.out.println("Quantity: " + quantity);
                System.out.printf("Unit Price: PHP %.2f%n", unitPrice);
                System.out.printf("Total: PHP %.2f%n", total);
                System.out.println("-------------------------------------------------------");
                found = true;
            } catch (NumberFormatException e) {
                System.out.println("Warning: Skipping transaction with invalid numbers on line " + (i + 1) + ".");
            }
        }

        if (!found) {
            if (UserModule.isRole("CUSTOMER")) {
                System.out.println("You do not have any recorded purchases yet.");
            } else {
                System.out.println("No valid sales records were found.");
            }
        }
        System.out.println("=======================================================");
    }
}
