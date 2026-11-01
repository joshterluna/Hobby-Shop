package hobbyshop;
//import java.io.*;

import java.util.*;

public class HobbyShop {

    //Scanner is shared by the menus so inputs remains the simple
    public static Scanner input = new Scanner(System.in);

// Starts the application and returns to the welcome menu after logout.
    public static void main(String[] args) {
        // Aetherion // - JAVA CLI: HOBBY SHOP CLI
        FileHelper.preparedDataFiles();
        boolean running = true;

        while (running) {
            displayWelcomeMenu();
            int choice = readChoice("Enter your choice: ");

            switch (choice) {
                case 1:
                    if (UserModule.login()) {
                        openRoleMenu();
                        UserModule.logout();
                        System.out.println("\nYou have logged out sucessfully");
                    } else {
                        pause();
                    }
                    break;
                case 2:
                    UserModule.registerCustomer();
                    pause();
                    break;
                case 3:
                    System.out.println("\nThank you for visiting Aetherion!");
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }

    }

    public static void displayWelcomeMenu() {
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
        System.out.println("  3. [Mark Allen Lampano]");
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

    public static int readChoice(String message) {
        while (true) {
            System.out.println(message);
            String value = input.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    //Routes the signed-in user to the correct rule menu.

    public static void openRoleMenu() {
        if (UserModule.currentRole.equals("ADMIN")) {
            AdminMenu.showMenu();
        } else if (UserModule.currentRole.equals("STAFF")) {
            StaffMenu.showMenu();
        } else if (UserModule.currentRole.equals("CUSTOMER")) {
            CustomerMenu.showMenu();
        } else {
            System.out.println("This account has an invalid role. Logging out.");
        }
    }

    //Waits for Enter before returning to the previous menu
    public static void pause() {
        System.out.println("Press Enter to continue...");
        input.nextLine();
    }
}
