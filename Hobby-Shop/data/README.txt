run.bat:
testing the system by running it directly through the computer terminal without using an IDE.

All data files use one record per line, with fields separated by the pipe character (|).

users.txt:
username|passwordHash|role|fullName

products.txt:
productId|name|series|manufacturer|category|price|stock

transactions.txt:
transactionId|date|processedBy|customerUsernameOrWalkIn|productId|productName|quantity|unitPrice|total

collections.txt:
customerUsername|productId|quantity|averagePurchasePricePerFigure|estimatedCurrentValuePerFigure|purchaseDate|condition
Allowed conditions: New, Like New, Good, Fair, Damaged.

wishlist.txt:
customerUsername|productId

deliveries.txt:
referenceNumber|distributorName|orderDate|expectedDeliveryDate|status
Example: DEL001|Pop Mart Distributor|2026-10-20|2026-10-30|Shipped
Allowed statuses: Pending, Shipped, Delivered, Delayed. Dates use YYYY-MM-DD. Staff can record and update deliveries. Admin can review them. This is not real-time courier tracking.

data/art/:
ASCII art files use the matching product ID as the filename, for example FIG001.txt for product FIG001. Artwork is plain text, not photography. Missing files are reported with a friendly message.

Collection records belong to a username. The program checks the current customer's username before viewing or changing that customer's collection or wishlist. The initial estimated value after a purchase equals that purchase price until the customer edits it.
