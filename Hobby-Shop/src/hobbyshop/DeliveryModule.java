package hobbyshop;

public class DeliveryModule {

    public static final int MAX_DELIVERIES = FileHelper.MAX_RECORDS;
    public static final int FIELD_COUNT = 5;
    public static final String DELIVERY_FILE = "data/deliveries.txt";
    public static boolean hasMalformedRecords = false;

    // Shows the delivery actions available to Staff.
    public static void showMenu() {
        if (!UserModule.isRole("STAFF")) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========== DELIVERY TRACKER ==========");
            System.out.println("[1] View Deliveries");
            System.out.println("[2] Record New Delivery");
            System.out.println("[3] Update Delivery Status");
            System.out.println("[4] Back to Staff Menu");
            int choice = HobbyShop.readChoice("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewDeliveries();
                    HobbyShop.pause();
                    break;
                case 2:
                    recordDelivery();
                    HobbyShop.pause();
                    break;
                case 3:
                    updateDeliveryStatus();
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

    // Displays deliveries for Admin or Staff.
    public static void viewDeliveries() {
        if (!UserModule.isRole("ADMIN") && !UserModule.isRole("STAFF")) {
            System.out.println("Access denied. Admin or Staff role required.");
            return;
        }

        String[][] deliveries = new String[MAX_DELIVERIES][FIELD_COUNT];
        int count = loadDeliveries(deliveries);

        System.out.println("\n================ DELIVERY RECORDS ================");
        if (count == 0) {
            System.out.println("No delivery records found.");
            return;
        }

        for (int i = 0; i < count; i++) {
            System.out.println("Reference: " + deliveries[i][0]);
            System.out.println("Distributor: " + deliveries[i][1]);
            System.out.println("Order Date: " + deliveries[i][2]);
            System.out.println("Expected Delivery: " + deliveries[i][3]);
            System.out.println("Status: " + deliveries[i][4]);
            System.out.println("---------------------------------------------------");
        }
        System.out.println("Total deliveries: " + count);
    }

    // Adds a delivery record. Only Staff can create delivery records.
    public static void recordDelivery() {
        if (!UserModule.isRole("STAFF")) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        String[][] deliveries = new String[MAX_DELIVERIES][FIELD_COUNT];
        int count = loadDeliveries(deliveries);
        if (hasMalformedRecords) {
            System.out.println("Cannot add deliveries because the file has malformed records.");
            System.out.println("Correct data/deliveries.txt first to avoid losing existing data.");
            return;
        }
        if (count >= MAX_DELIVERIES) {
            System.out.println("The delivery file is full. No more records can be added.");
            return;
        }

        String reference;
        while (true) {
            reference = readRequiredField("Delivery reference number: ");
            if (findDelivery(deliveries, count, reference) != -1) {
                System.out.println("That reference number already exists. Enter a unique reference.");
            } else {
                break;
            }
        }

        String distributor = readRequiredField("Distributor name: ");
        String orderDate = readDate("Order date (YYYY-MM-DD): ");
        String expectedDate = readDate("Expected delivery date (YYYY-MM-DD): ");
        while (expectedDate.compareTo(orderDate) < 0) {
            System.out.println("Expected delivery date cannot be before the order date.");
            expectedDate = readDate("Expected delivery date (YYYY-MM-DD): ");
        }

        deliveries[count][0] = reference;
        deliveries[count][1] = distributor;
        deliveries[count][2] = orderDate;
        deliveries[count][3] = expectedDate;
        deliveries[count][4] = "Pending";

        if (saveDeliveries(deliveries, count + 1)) {
            System.out.println("Delivery recorded successfully.");
            System.out.println("Reference: " + reference);
            System.out.println("Status: Pending");
        } else {
            System.out.println("Could not save the delivery. No record was added.");
        }
    }

    // Updates a delivery status. Only Staff can update delivery records.
    public static void updateDeliveryStatus() {
        if (!UserModule.isRole("STAFF")) {
            System.out.println("Access denied. Staff role required.");
            return;
        }

        String[][] deliveries = new String[MAX_DELIVERIES][FIELD_COUNT];
        int count = loadDeliveries(deliveries);
        if (hasMalformedRecords) {
            System.out.println("Cannot update deliveries because the file has malformed records.");
            System.out.println("Correct data/deliveries.txt first to avoid losing existing data.");
            return;
        }
        if (count == 0) {
            System.out.println("There are no delivery records to update.");
            return;
        }

        String reference = readRequiredField("Enter the delivery reference number: ");
        int index = findDelivery(deliveries, count, reference);
        if (index == -1) {
            System.out.println("Delivery reference was not found.");
            return;
        }

        System.out.println("Current status: " + deliveries[index][4]);
        System.out.println("[1] Pending");
        System.out.println("[2] Shipped");
        System.out.println("[3] Delivered");
        System.out.println("[4] Delayed");
        int choice = HobbyShop.readChoice("Choose the new status: ");
        String newStatus = getStatus(choice);

        if (newStatus.isEmpty()) {
            System.out.println("Invalid status choice. No changes were saved.");
            return;
        }

        String oldStatus = deliveries[index][4];
        deliveries[index][4] = newStatus;
        if (saveDeliveries(deliveries, count)) {
            System.out.println("Delivery status updated successfully.");
            System.out.println("Reference: " + reference);
            System.out.println("Status: " + oldStatus + " -> " + newStatus);
        } else {
            System.out.println("Could not save the status update. The file was not changed.");
        }
    }

    // Loads and checks delivery records from the text file.
    public static int loadDeliveries(String[][] deliveries) {
        hasMalformedRecords = false;
        String[] lines = new String[MAX_DELIVERIES];
        int lineCount = FileHelper.readLines(DELIVERY_FILE, lines, MAX_DELIVERIES);
        int count = 0;
        int skipped = 0;

        for (int i = 0; i < lineCount; i++) {
            String[] fields = lines[i].split("\\|", -1);
            if (fields.length != FIELD_COUNT || !isValidStatus(fields[4])
                    || fields[0].trim().isEmpty() || fields[1].trim().isEmpty()
                    || !isValidDate(fields[2]) || !isValidDate(fields[3])
                    || fields[3].compareTo(fields[2]) < 0) {
                skipped++;
                continue;
            }
            boolean duplicateReference = false;
            for (int j = 0; j < count; j++) {
                if (deliveries[j][0].equalsIgnoreCase(fields[0].trim())) {
                    duplicateReference = true;
                    break;
                }
            }
            if (duplicateReference) {
                skipped++;
                continue;
            }
            if (count >= MAX_DELIVERIES) {
                skipped++;
                continue;
            }
            for (int j = 0; j < FIELD_COUNT; j++) {
                deliveries[count][j] = fields[j].trim();
            }
            count++;
        }

        if (skipped > 0) {
            hasMalformedRecords = true;
            System.out.println("Warning: skipped " + skipped + " malformed delivery record(s).");
        }
        return count;
    }

    // Saves the delivery array to the text file.
    public static boolean saveDeliveries(String[][] deliveries, int count) {
        String[] lines = new String[count];
        for (int i = 0; i < count; i++) {
            lines[i] = deliveries[i][0] + "|" + deliveries[i][1] + "|"
                    + deliveries[i][2] + "|" + deliveries[i][3] + "|" + deliveries[i][4];
        }
        return FileHelper.writeLines(DELIVERY_FILE, lines, count);
    }

    // Finds a delivery by its reference number.
    public static int findDelivery(String[][] deliveries, int count, String reference) {
        for (int i = 0; i < count; i++) {
            if (deliveries[i][0].equalsIgnoreCase(reference.trim())) {
                return i;
            }
        }
        return -1;
    }

    // Returns the delivery status that matches a menu choice.
    public static String getStatus(int choice) {
        switch (choice) {
            case 1:
                return "Pending";
            case 2:
                return "Shipped";
            case 3:
                return "Delivered";
            case 4:
                return "Delayed";
            default:
                return "";
        }
    }

    // Checks whether a status is one of the allowed values.
    public static boolean isValidStatus(String status) {
        return status.equals("Pending") || status.equals("Shipped")
                || status.equals("Delivered") || status.equals("Delayed");
    }

    // Asks for a required field and replaces the file delimiter if entered.
    public static String readRequiredField(String message) {
        while (true) {
            System.out.print(message);
            String value = HobbyShop.input.nextLine().trim();
            value = cleanField(value);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field is required.");
        }
    }

    // Keeps the pipe character from breaking a saved record.
    public static String cleanField(String value) {
        return value.replace('|', '/').trim();
    }

    // Reads a date in YYYY-MM-DD format and validates its basic parts.
    public static String readDate(String message) {
        while (true) {
            String value = readRequiredField(message);
            if (isValidDate(value)) {
                return value;
            }
            System.out.println("Invalid date. Use a real date in YYYY-MM-DD format.");
        }
    }

    // Checks the date format and whether the month and day are possible.
    public static boolean isValidDate(String value) {
        if (value == null || value.length() != 10
                || value.charAt(4) != '-' || value.charAt(7) != '-') {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (i != 4 && i != 7 && !Character.isDigit(value.charAt(i))) {
                return false;
            }
        }

        try {
            int year = Integer.parseInt(value.substring(0, 4));
            int month = Integer.parseInt(value.substring(5, 7));
            int day = Integer.parseInt(value.substring(8, 10));
            if (year < 1 || month < 1 || month > 12 || day < 1) {
                return false;
            }
            int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            if (isLeapYear(year)) {
                daysInMonth[1] = 29;
            }
            return day <= daysInMonth[month - 1];
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks whether a year is a leap year for February validation.
    public static boolean isLeapYear(int year) {
        return (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0);
    }
}
