package hobbyshop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class CustomerMenu {
     // Displays the Customer menu and handles available features.
 public static void showMenu() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
            

}
      boolean  inMenu = True;
     while (inMenu) {
         System.out.println("\n========================================");
         System.out.println("              CUSTOMER MENU");
         System.out.println("========================================"); 
         System.out.println("[1] Browse  Figures");
         System.out.println("[2] Search Figures");
            System.out.println("[3] View Figure Details by ID");
            System.out.println("[4] Buy Figures");
            System.out.println("[5] View Purchase History");
            System.out.println("[6] My Collection");
            System.out.println("[7] My Wishlist");
            System.out.println("[8] Price Tracking and Figure Condition");
            System.out.println("[9] ASCII Gallery");
            System.out.println("[10] Logout");
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
                    ProductModule.viewDetailsById();    
                    HobbyShop.pause();
                    break;
                case 4:
                    SalesModule.recordCustomerPurchase();
                    HobbyShop.pause();
                    break;
                case 5:
                    SalesModule.viewTransactionHistory();
                    HobbyShop.pause();
                    break;
                case 6:
                    CollectionModule.showCollectionMenu();
                    break;
                case 7:
                    CollectionModule.showWishlistMenu();
                    break;
                case 8:
                    CollectionModule.showPriceTrackingMenu();
                    break;
                case 9:
                    showAsciiGallery();
                    break;
                case 10:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 10.");
            }
        }
    }

//Let customers a catalog figure and  view its saved ASCII artwork.
 public static void showAsciiGallery() {
        if (!UserModule.isRole("CUSTOMER")) {
            System.out.println("Access denied. Customer role required.");
            return;
        }

        File artFolder = new File("data/art");
        if (!artFolder.exists() && !artFolder.mkdirs()) {
            System.out.println("The gallery folder could not be created.");
            HobbyShop.pause();
            return;
        }

        while (true) {
            ProductModule.loadProducts();
            if (ProductModule.productCount == 0) {
                System.out.println("No figures are available for the gallery.");
                HobbyShop.pause();
                return;
            }

            System.out.println("\n========== ASCII FIGURE GALLERY ==========");
            System.out.println("Choose a figure to view its text artwork.");
            System.out.printf("%-10s %s%n", "PRODUCT ID", "FIGURE NAME");
            System.out.println("------------------------------------------");
            for (int i = 0; i < ProductModule.productCount; i++) {
                System.out.printf("%-10s %s%n", ProductModule.productIds[i], ProductModule.productNames[i]);
            }
            System.out.println("[0] Back to Customer Menu");

            System.out.print("Enter a Product ID: ");
            String productId = HobbyShop.input.nextLine().trim();
            if (productId.equals("0")) {
                return;
            }
            if (productId.isEmpty()) {
                System.out.println("Product ID cannot be empty.");
                continue;
            }

            int index = ProductModule.findProduct(productId);
            if (index == -1) {
                System.out.println("Figure not found. Please enter an ID from the list.");
                HobbyShop.pause();
                continue;
            }

            String savedProductId = ProductModule.productIds[index];
            if (!isSafeArtId(savedProductId)) {
                System.out.println("Artwork cannot be opened for this product ID.");
                HobbyShop.pause();
                continue;
            }

            File artFile = new File(artFolder, savedProductId + ".txt");
            if (!artFile.exists()) {
                System.out.println("\nNo ASCII artwork is available for " + savedProductId + " yet.");
                System.out.println("This figure is still available in the product catalog.");
                HobbyShop.pause();
                continue;
            }

            System.out.println("\n==========================================");
            System.out.println(ProductModule.productNames[index]);
            System.out.println("Product ID: " + savedProductId);
            System.out.println("Series: " + ProductModule.productSeries[index]);
            System.out.println("ASCII artwork (text illustration, not a photograph):");
            System.out.println("------------------------------------------");
            displayArtFile(artFile);
            System.out.println("==========================================");
            HobbyShop.pause();
        }
    }

    // Reads and prints an artwork file one line at a time.
    public static void displayArtFile(File artFile) {
        BufferedReader reader = null;   
        boolean hasContent = false;
        try {
            reader = new BufferedReader(new FileReader(artFile));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                hasContent = true;
            }
            if (!hasContent) {
                System.out.println("This artwork file is empty.");
            }
        } catch (IOException e) {
            System.out.println("Sorry, the artwork file could not be read.");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("The artwork file could not be closed properly.");
                  }
            }
        }
    }

    // Allows only letters, numbers, hyphens, and underscores in artwork file names.
    public static boolean isSafeArtId(String productId) {
        if (productId == null || productId.isEmpty()) {
            return false;
        }
        for (int i = 0; i < productId.length(); i++) {
            char character = productId.charAt(i);
            boolean isLetter = (character >= 'A' && character <= 'Z')
                    || (character >= 'a' && character <= 'z');
            boolean isNumber = character >= '0' && character <= '9';
            if (!isLetter && !isNumber && character != '-' && character != '_') {
                return false;
            }
        }
        return true;
    }
}

    
