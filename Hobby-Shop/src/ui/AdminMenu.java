/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;
import java.util.Scanner;
/**
 *
 * @author junry
 */
public class AdminMenu {
   Scanner sc = new Scanner(System.in);

        
        System.out.println("==============================================");
        System.out.println("                                              ");
        System.out.println("          A E T H E R I O N                   ");
        System.out.println("       HOBBY SHOP & COLLECTIBLES              ");
        System.out.println("                                              ");
        System.out.println("==============================================");
        System.out.println();
        System.out.println("          WELCOME, COLLECTOR!");
        System.out.println();
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
        sc.nextLine();

        
        System.out.println();
        System.out.println("============== MAIN MENU =====================");
        System.out.println("[1] Browse Figures");
        System.out.println("[2] Browse Blind Boxes");
        System.out.println("[3] About Aetherion");
        System.out.println("[0] Exit");
        System.out.println("==============================================");
        System.out.print("Choose an option: ");

        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Opening Figures Collection...");
                break;
            case 2:
                System.out.println("Opening Blind Boxes...");
                break;
            case 3:
                System.out.println("Aetherion - Your collectible hobby shop!");
                break;
            case 0:
                System.out.println("Thanks for visiting Aetherion!");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }

        sc.close();
    }
}
