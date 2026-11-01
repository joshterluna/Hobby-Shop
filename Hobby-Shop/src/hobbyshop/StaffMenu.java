package hobbyshop;

public class StaffMenu {

    // Displays the Staff menu and handles available features.
    public static void showMenu() {
        if (!UserModule.isRole("STAFF")) {
            System.out.println("Access denied. Staff role required.");
            return;
        }
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========================================");
            System.out.println("                STAFF MENU");
            System.out.println("========================================");
            System.out.println("[1] View Figure Catalog");
            System.out.println("[2] Search Figures");
            System.out.println("[3] Manage Stock");
            System.out.println("[4] View Inventory Summary");
            System.out.println("[5] Record Walk-in Sale");
            System.out.println("[6] View Sales History");
            System.out.println("[7] Manage Deliveries");
            System.out.println("[8] Logout");
            int choice = HobbyShop.readChoice("Enter your choice: ");
            switch (choice) {
                case 1:
                    ProductModule.viewAllProducts();
                    HobbyShop.pause();
                    break;
                case 2:
                    ProductModule.searchProducts();
                    HobbyShop.pause();
                    break;
                case 3:
                    ProductModule.manageStock();
                    HobbyShop.pause();
                    break;
                case 4:
                    ProductModule.viewInventorySummary();
                    HobbyShop.pause();
                    break;
                case 5:
                    SalesModule.recordWalkInSale();
                    HobbyShop.pause();
                    break;
                case 6:
                    SalesModule.viewTransactionHistory();
                    HobbyShop.pause();
                    break;
                case 7:
                    DeliveryModule.showMenu();
                    break;
                case 8:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 8.");
            }
        }
    }
}
