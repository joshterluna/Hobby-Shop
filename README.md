<div align="center">

# Aetherion

**A Java command-line management system for a blind box and collectible figurine shop**

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white)
![IDE](https://img.shields.io/badge/IDE-Apache%20NetBeans-1B6AC6?logo=apachenetbeanside&logoColor=white)
![Storage](https://img.shields.io/badge/Storage-Text%20files-lightgrey)
![Status](https://img.shields.io/badge/Status-Academic%20project-blueviolet)

</div>

Aetherion helps a hobby shop run its day-to-day work from the command line. Administrators manage accounts and the catalog, staff handle stock, deliveries, and walk-in sales, and customers browse, buy, and keep a personal collection and wishlist. Everything is saved in plain text files, so data survives between runs. There is no database and no external library.

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
- [Default Accounts](#default-accounts)
- [Project Structure](#project-structure)
- [How It Works](#how-it-works)
- [Data Files](#data-files)
- [Screenshots](#screenshots)
- [Limitations](#limitations)
- [Team](#team)
- [Contributing](#contributing)

## Features

Every role has its own menu, and each sensitive action also checks the role inside the program, not only in the menu.

| Area | Admin | Staff | Customer |
|---|:---:|:---:|:---:|
| Register, log in, log out | ✔ | ✔ | ✔ |
| Manage user accounts (add, view, update, delete) | ✔ | | |
| Manage the figure catalog (add, update, delete) | ✔ | | |
| Browse, search, and view figure details | ✔ | ✔ | ✔ |
| Manage stock | ✔ | ✔ | |
| Inventory summary with low-stock alert | ✔ | ✔ | |
| Buy figures and print a receipt | | | ✔ |
| Record walk-in sales | | ✔ | |
| View sales history | ✔ all | ✔ all | ✔ own |
| Record and update distributor deliveries | view only | ✔ | |
| My Collection, Wishlist, price tracking, figure condition | | | ✔ |
| ASCII figure gallery | | | ✔ |

**Highlights**

- **Role-based access** for Admin, Staff, and Customer, with passwords stored as hashes, not plain text.
- **Validated input everywhere.** Menus and prompts re-ask on invalid entries and never crash on letters where a number is expected.
- **Safe saving.** Files are written to a temporary file first, and the old file is kept as a backup until the new one is in place.
- **Confirmation before destructive actions** such as deleting a user, a figure, or a collection entry.
- **Automatic collection updates.** A customer purchase adds the figure to that customer's collection.
- **Price tracking.** Customers record what they paid and their own estimate of the current value, and the system shows the difference.
- **Figure condition tracking** with five fixed values: New, Like New, Good, Fair, and Damaged.
- **Delivery tracker** for distributor shipments with the statuses Pending, Shipped, Delivered, and Delayed.
- **ASCII gallery.** Text artwork is loaded from `data/art/`, with a friendly message when a figure has no artwork.

## Getting Started

### Requirements

- JDK 17 or newer, installed and available on your `PATH` (the NetBeans project is configured for Java 21)
- Apache NetBeans (optional)

### Quick start with the run scripts

1. Clone the repository: `git clone https://github.com/joshterluna/Hobby-Shop.git`
2. Open the inner `Hobby-Shop` folder, the one that contains `run.bat`, `run.sh`, and `data/`.
3. Start the program:
   - **Windows:** double-click `run.bat`, or run `run.bat` in Command Prompt.
   - **macOS and Linux:** run `chmod +x run.sh` once, then `./run.sh`.

Starter data files are created automatically on the first run if they are missing.

### Run in NetBeans

1. In NetBeans, choose **File, then Open Project**, and select the inner `Hobby-Shop` folder (the one that contains `build.xml` and `nbproject`).
2. If your JDK is not 21, open **Properties, then Libraries** and **Sources**, and set the Java Platform and Source/Binary Format to your version.
3. Press **F6** to run. The main class is `hobbyshop.HobbyShop`.

### Build a JAR (optional)

Choose **Clean and Build** in NetBeans to create `dist/Hobby-Shop.jar`, then run this from the inner `Hobby-Shop` folder so the program finds `data/`:

```
java -jar dist/Hobby-Shop.jar
```

> **Tip:** the NetBeans Output window can't hide typed passwords or clear the screen. A real terminal (or the run scripts) is better for demos and screenshots.

## Default Accounts

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Staff | `staff` | `staff123` |
| Customer | `customer` | `customer123` |

These are demonstration accounts. Change or remove them before sharing the project publicly. New customers can register from the welcome screen.

## Project Structure

```text
Hobby-Shop/                          repository root
├── README.md
├── .gitignore
└── Hobby-Shop/                      project folder (open this in NetBeans)
    ├── build.xml
    ├── manifest.mf
    ├── run.bat                      start script for Windows
    ├── run.sh                       start script for macOS and Linux
    ├── data/                        text-file storage
    │   ├── README.txt
    │   ├── users.txt
    │   ├── products.txt
    │   ├── transactions.txt
    │   ├── collections.txt
    │   ├── wishlist.txt
    │   ├── deliveries.txt
    │   └── art/                     ASCII artwork, one file per product ID
    │       └── FIG001.txt ... FIG006.txt
    ├── nbproject/
    │   ├── genfiles.properties
    │   ├── project.properties
    │   └── project.xml
    └── src/
        └── hobbyshop/
            ├── HobbyShop.java       entry point, welcome menu, role routing
            ├── FileHelper.java      safe reading and writing of text files
            ├── UserModule.java      registration, login, account management
            ├── ProductModule.java   catalog, search, stock, inventory summary
            ├── SalesModule.java     purchases, walk-in sales, receipts, history
            ├── DeliveryModule.java  distributor delivery tracker
            ├── CollectionModule.java  collection, wishlist, price, condition
            ├── AdminMenu.java       Admin menu
            ├── StaffMenu.java       Staff menu
            └── CustomerMenu.java    Customer menu and ASCII gallery
```

The `build/`, `dist/`, and `nbproject/private/` folders are created on your computer when you build the project, and Git ignores them.

## How It Works

The code is intentionally written at a beginner level: static methods, arrays, loops, `Scanner`, `if` and `switch`, and `java.io` file handling. There are no objects, collections, or frameworks.

```
HobbyShop  ->  role menu  ->  module (feature logic)  ->  FileHelper  ->  data/*.txt
```

- The **menus** only print options and call the modules.
- The **modules** check the user's role, validate input, and change the data held in arrays.
- **FileHelper** is the only class that reads and writes files, and it saves every change right away.
- Each file has one record per line, with fields separated by `|`. Entered text has the `|` character removed so a record can't be corrupted.

## Data Files

| File | Purpose | Format |
|---|---|---|
| `users.txt` | Accounts and roles | `username\|passwordHash\|role\|fullName` |
| `products.txt` | Figures and stock | `productId\|name\|series\|manufacturer\|category\|price\|stock` |
| `transactions.txt` | Completed sales | `transactionId\|date\|processedBy\|buyer\|productId\|productName\|quantity\|unitPrice\|total` |
| `collections.txt` | Figures owned by each customer | `customer\|productId\|quantity\|purchasePrice\|estimatedValue\|purchaseDate\|condition` |
| `wishlist.txt` | Figures customers want | `customer\|productId` |
| `deliveries.txt` | Distributor deliveries | `reference\|distributor\|orderDate\|expectedDate\|status` |
| `art/<productId>.txt` | ASCII artwork | plain text, printed line by line |

<details>
<summary>Example records</summary>

```
users.txt          customer|-1772107372|CUSTOMER|Sample Customer
products.txt       FIG001|Labubu - The Monsters|Exciting Macaron|Pop Mart|Blind Box|750.00|15
collections.txt    customer|FIG001|2|750.00|750.00|2026-10-29|New
wishlist.txt       customer|FIG002
deliveries.txt     DEL001|Pop Mart Distributor|2026-10-20|2026-10-30|Shipped
```

Dates use `YYYY-MM-DD`. If `products.txt` contains a line that cannot be read, the program reports it and blocks saving until the line is fixed, so the line is never erased by accident.

</details>

## Screenshots

<!--
Save screenshots in a docs/screenshots/ folder at the repository root, then uncomment:

| Welcome menu | Customer purchase |
|---|---|
| ![Welcome menu](docs/screenshots/welcome.png) | ![Receipt](docs/screenshots/receipt.png) |
-->

Screenshots of the running program will be added here.

## Limitations

This is a classroom CLI project, not a real payment or inventory platform.

- The password hash (`String.hashCode()`) is not salted and is not secure for real accounts. Typed passwords are visible on screen.
- Dates are typed in manually, and the system does not select the current date.
- Estimated values are entered by the customer and are not verified market prices.
- Most files are limited to 500 records.
- Each collection entry stores one combined quantity, one average purchase price, and one estimate per product, not a record for every individual item.
- Deleting a customer also deletes their collection and wishlist, but keeps their sales records.
- The delivery tracker records status only. It does not provide courier tracking and does not change stock automatically.
- The ASCII gallery contains a small set of sample drawings (FIG001 to FIG006) and does not show photographs.

## Team

Academic project for IT001.23, College of Computer Studies, University of the Immaculate Conception, Davao City.

| Member |
|---|
| Bustamante, Dean Andrew O. |
| Guma, Kent Felix G. | 
| Halik, Iederf Sean B. | 
| Lampano, Mark Allen T. | 

