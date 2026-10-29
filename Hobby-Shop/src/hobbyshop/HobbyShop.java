package hobbyshop;
import java.util.*; 
public class HobbyShop {

    static Scanner input = new Scanner (System.in);
    
    public static void main(String[] args) {
        // Aetherion // - JAVA CLI: HOBBY SHOP CLI
    boolean running = true;
    
    while (running) {
        displayWelcomeMenu();
        int choice = readChoice();
        
        switch (choice) {
            case 1:
                showLoginMessage();
                break;
            case 2:
                showRegisterMessage();
                break;
            case 3:
                System.out.println();
                System.out.println("Thank you for visiting Aetherion!");
                System.out.println("Goodbye!");
                running = false;
                break;
            default:
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
        }       
    }
    
        
    }
    
    public static void displayWelcomeMenu(){
         System.out.println();
        System.out.println("    _        _   _               _             ");
        System.out.println("   / \\   ___| |_| |__   ___ _ __(_) ___  _ __  ");
        System.out.println("  / _ \\ / _ \\ __| '_ \\ / _ \\ '__| |/ _ \\| '_ \\ ");
        System.out.println(" / ___ \\  __/ |_| | | |  __/ |  | | (_) | | | |");
        System.out.println("/_/   \\_\\___|\\__|_| |_|\\___|_|  |_|\\___/|_| |_|");
        System.out.println("----------------------------------------------");
        System.out.println("              DEVELOPED BY");
        System.out.println("----------------------------------------------");
        System.out.println("  1. [Iederf Sean Halik]");
        System.out.println("  2. [Kent Felix Guma]");
        System.out.println("  3. [Mark Lampano]");
        System.out.println("  4. [Dean Andrew Bustamante]");
        System.out.println("----------------------------------------------");
        System.out.println();
        System.out.println("       Press ENTER to continue...");
        input.nextLine();
        
        System.out.println();
        System.out.println("============== MAIN MENU =====================");
        System.out.println("[1] Login");
        System.out.println("[2] Register");
        System.out.println("[3] Exit");
        System.out.println("==============================================");
        System.out.print("Choose an option: ");
    }
    
    public static int readChoice(){
        while (!input.hasNext()) {
            System.out.println("Invalid input. Enter a number:");
            input.nextLine();
        }
        int choice = input.nextInt();
        input.nextLine();
        return choice;
    }
    public static void showLoginMessage() {
        System.out.println();
        System.out.println("Login--");
        pause();
    }
    public static void showRegisterMessage() {
        System.out.println();
        System.out.println("Register--");
        pause();
    }
    public static void pause(){
        System.out.println("Please Enter to return to the welcome menu");
        input.nextLine();
    }
}
