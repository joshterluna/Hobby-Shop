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

 String command;

        System.out.println("====================================");
        System.out.println("          AETHERION CLI");
        System.out.println("====================================");
        System.out.println("Type 'help' to see available commands.");

        while (true) {
            System.out.print("\n% ");
            command = sc.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    System.out.println("help      : List available commands");
                    System.out.println("products  : Display hobby shop products");
                    System.out.println("fonts     : List available font styles");
                    System.out.println("styles    : List available text styles");
                    System.out.println("clear     : Clear the console (visual separator)");
                    System.out.println("exit      : Close the program");
                    break;

                case "products":
                    System.out.println("\n--- AETHERION PRODUCTS ---");
                    System.out.println("1. Anime Figures");
                    System.out.println("2. Blind Boxes");
                    System.out.println("3. Collectible Figurines");
                    break;

                case "fonts":
                    System.out.println("\n--- FONT LIST ---");
                    System.out.println("Inter Bold");
                    System.out.println("Inter Regular");
                    System.out.println("Inter Italic");
                    System.out.println("Roboto Mono Regular");
                    System.out.println("Roboto Mono Bold");
                    break;

                case "styles":
                    System.out.println("\n--- TEXT STYLES ---");
                    System.out.println("Title Hero");
                    System.out.println("Title Page");
                    System.out.println("Subtitle");
                    System.out.println("Heading");
                    System.out.println("Subheading");
                    System.out.println("Body Base");
                    System.out.println("Body Strong");
                    System.out.println("Body Emphasis");
                    System.out.println("Body Small");
                    System.out.println("Body Code");
                    break;

                case "clear":
                    for (int i = 0; i < 20; i++) {
                        System.out.println();
                    }
                    break;

                case "exit":
                    System.out.println("Thank you for using Aetherion CLI!");
                    sc.close();
                    return;

                default:
                    System.out.println("Unknown command. Type 'help' for commands.");
            }
        }
    }
}

