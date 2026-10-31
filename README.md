# Hobby-Shop
A Java CLI-based management system for a hobby shop specializing in blind boxes and collectible figurines.

## Introduction

Aetherion helps a hobby shop manage its inventory, sales, and customer collections from the command line. Admins manage users and products, shop staff handle stock and walk-in sales, and customers can browse the catalog, buy items, and track their own collection. All data is saved in text files, so it persists between program runs.

```text
 Hobby-Shop/                          
├── .gitignore
├── README.md
└── Hobby-Shop/                    
    ├── build.xml, manifest.mf, nbproject/
    ├── data/
    │   ├── users.txt
    │   ├── products.txt
    │   ├── transactions.txt
    │   ├── collections.txt
    │   ├── wishlist.txt
    │   ├── deliveries.txt
    │   └── art/                     
    └── src/hobbyshop/               
        ├── HobbyShop.java          
        ├── FileHelper.java          
        ├── UserModule.java        
        ├── ProductModule.java      
        ├── DeliveryModule.java     
        ├── SalesModule.java         
        ├── CollectionModule.java    
        ├── AdminMenu.java
        ├── StaffMenu.java
        └── CustomerMenu.java
```
