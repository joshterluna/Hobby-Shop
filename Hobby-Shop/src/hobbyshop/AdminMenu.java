package hobbyshop;

public class AdminMenu {

    // Displays the Admin menu and handles available features.
    public static void showMenu() {
        if (!UserModule.isRole("ADMIN")) {
            System.out.println("Access denied. Admin role required.");
            return;
        }
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========================================");
            System.out.println("               ADMIN MENU");
            System.out.println("========================================");
            System.out.println("[1] Manage User Accounts");
            System.out.println("[2] Manage Figure Catalog");
            System.out.println("[3] View Inventory Summary");
            System.out.println("[4] Review Deliveries");
            System.out.println("[5] View Sales History");
            System.out.println("[6] Logout");
            int choice = HobbyShop.readChoice("Enter your choice: ");
            switch (choice) {
                case 1:
                    UserModule.manageUsers();
                    break;
                case 2:
                    manageCatalog();
                    break;
                case 3:
                    ProductModule.viewInventorySummary();
                    HobbyShop.pause();
                    break;
                case 4:
                    DeliveryModule.viewDeliveries();
                    HobbyShop.pause();
                    break;
                case 5:
                    SalesModule.viewTransactionHistory();
                    HobbyShop.pause();
                    break;
                case 6:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 6.");
            }
        }
    }

    // Shows the catalog actions available to Admin.
    public static void manageCatalog() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========== MANAGE FIGURE CATALOG ==========");
            System.out.println("[1] View All Figures");
            System.out.println("[2] Add Figure");
            System.out.println("[3] Search Figures");
            System.out.println("[4] Update Figure");
            System.out.println("[5] Delete Figure");
            System.out.println("[6] Back to Admin Menu");
            int choice = HobbyShop.readChoice("Enter your choice: ");
            switch (choice) {
                case 1:
                    ProductModule.viewAllProducts();
                    HobbyShop.pause();
                    break;
                case 2:
                    ProductModule.addProduct();
                    HobbyShop.pause();
                    break;
                case 3:
                    ProductModule.searchProducts();
                    HobbyShop.pause();
                    break;
                case 4:
                    ProductModule.updateProduct();
                    HobbyShop.pause();
                    break;
                case 5:
                    ProductModule.deleteProduct();
                    HobbyShop.pause();
                    break;
                case 6:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 6.");
            }
        }
    }
}
