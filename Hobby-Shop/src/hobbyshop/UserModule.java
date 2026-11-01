package hobbyshop;

public class UserModule {

    public static String currentUsername = "";
    public static String currentName = "";
    public static String currentRole = "";

    static String[] usernames = new String[FileHelper.MAX_RECORDS];
    static String[] passwordHashes = new String[FileHelper.MAX_RECORDS];
    static String[] roles = new String[FileHelper.MAX_RECORDS];
    static String[] fullNames = new String[FileHelper.MAX_RECORDS];
    static int userCount = 0;
    static int malformedUserCount = 0;

    // Loads valid account records from users.txt into parallel arrays.
    public static void loadUsers() {
        String[] lines = new String[FileHelper.MAX_RECORDS];
        userCount = 0;
        malformedUserCount = 0;
        int lineCount = FileHelper.readLines(FileHelper.USER_FILE, lines, lines.length);

        for (int i = 0; i < lineCount; i++) {
            String[] parts = lines[i].split("\\|", -1);
            if (parts.length != 4) {
                malformedUserCount++;
                System.out.println("Warning: Skipping malformed user record on line " + (i + 1) + ".");
                continue;
            }
            if (parts[0].trim().isEmpty() || parts[1].trim().isEmpty()
                    || !validRole(parts[2]) || parts[3].trim().isEmpty()
                    || findUser(parts[0]) >= 0) {
                malformedUserCount++;
                System.out.println("Warning: Skipping incomplete or duplicate user record on line " + (i + 1) + ".");
                continue;
            }
            if (userCount >= usernames.length) {
                System.out.println("Warning: User limit reached.");
                break;
            }
            usernames[userCount] = parts[0];
            passwordHashes[userCount] = parts[1];
            roles[userCount] = parts[2];
            fullNames[userCount] = parts[3];
            userCount++;
        }
    }

    // Saves the current parallel arrays to users.txt.
    public static boolean saveUsers() {
        if (malformedUserCount > 0) {
            System.out.println("Save cancelled because users.txt contains malformed or duplicate records.");
            System.out.println("Correct the reported lines in users.txt before changing accounts.");
            return false;
        }
        String[] lines = new String[userCount];
        for (int i = 0; i < userCount; i++) {
            lines[i] = usernames[i] + "|" + passwordHashes[i] + "|" + roles[i] + "|" + fullNames[i];
        }
        return FileHelper.writeLines(FileHelper.USER_FILE, lines, userCount);
    }

    // Registers a public account with the CUSTOMER role only.
    public static void registerCustomer() {
        loadUsers();
        System.out.println("\n========== CUSTOMER REGISTRATION ==========");
        String fullName = readSafeText("Full name: ");
        if (fullName == null) {
            return;
        }

        String username = readUsername();
        if (username == null) {
            return;
        }
        if (findUser(username) >= 0) {
            System.out.println("That username already exists. Please try registering again.");
            return;
        }
        if (userCount >= usernames.length) {
            System.out.println("The user account limit has been reached.");
            return;
        }

        String password = readPassword("Password (at least 4 characters): ");
        if (password == null) {
            return;
        }
        if (password.length() < 4) {
            System.out.println("Password must contain at least 4 characters.");
            return;
        }

        System.out.print("Confirm password: ");
        String confirm = HobbyShop.input.nextLine();
        if (!password.equals(confirm)) {
            System.out.println("Passwords do not match. Account was not created.");
            return;
        }

        usernames[userCount] = username;
        passwordHashes[userCount] = hashPassword(password);
        roles[userCount] = "CUSTOMER";
        fullNames[userCount] = fullName;
        userCount++;

        if (saveUsers()) {
            System.out.println("Registration successful! Your account role is CUSTOMER.");
            System.out.println("You can now log in.");
        } else {
            userCount--;
            System.out.println("Registration failed because the account could not be saved.");
        }
    }

    // Checks a user's credentials and starts the login session.
    public static boolean login() {
        loadUsers();
        System.out.println("\n========== LOGIN ==========");
        System.out.print("Username: ");
        String username = HobbyShop.input.nextLine().trim();
        System.out.print("Password: ");
        String password = HobbyShop.input.nextLine();

        int index = findUser(username);
        if (index < 0 || !passwordHashes[index].equals(hashPassword(password))) {
            System.out.println("Invalid username or password.");
            return false;
        }

        currentUsername = usernames[index];
        currentName = fullNames[index];
        currentRole = roles[index];
        System.out.println("\nLogin successful! Welcome, " + currentName + ".");
        System.out.println("Role: " + currentRole);
        return true;
    }

    // Clears the current account information after logout.
    public static void logout() {
        currentUsername = "";
        currentName = "";
        currentRole = "";
    }

    // Displays the Admin's user account management submenu.
    public static void manageUsers() {
        if (!isRole("ADMIN")) {
            System.out.println("Access denied. Only Admin can manage user accounts.");
            return;
        }

        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========== USER MANAGEMENT ==========");
            System.out.println("[1] View User Accounts");
            System.out.println("[2] Add User Account");
            System.out.println("[3] Update User Account");
            System.out.println("[4] Delete User Account");
            System.out.println("[5] Back to Admin Menu");
            int choice = HobbyShop.readChoice("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewUsers();
                    HobbyShop.pause();
                    break;
                case 2:
                    addUser();
                    HobbyShop.pause();
                    break;
                case 3:
                    updateUser();
                    HobbyShop.pause();
                    break;
                case 4:
                    deleteUser();
                    HobbyShop.pause();
                    break;
                case 5:
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 5.");
            }
        }
    }

    // Displays all user account details except their password hashes.
    public static void viewUsers() {
        if (!isRole("ADMIN")) {
            System.out.println("Access denied.");
            return;
        }
        loadUsers();
        System.out.println("\n---------------- USER ACCOUNTS ----------------");
        if (userCount == 0) {
            System.out.println("No user accounts found.");
            return;
        }
        for (int i = 0; i < userCount; i++) {
            System.out.println((i + 1) + ". " + usernames[i] + " | " + fullNames[i] + " | " + roles[i]);
        }
    }

    // Lets Admin create an account with an assigned role.
    public static void addUser() {
        if (!isRole("ADMIN")) {
            System.out.println("Access denied.");
            return;
        }
        loadUsers();
        System.out.println("\n========== ADD USER ACCOUNT ==========");
        String fullName = readSafeText("Full name: ");
        if (fullName == null) {
            return;
        }
        String username = readUsername();
        if (username == null) {
            return;
        }
        if (findUser(username) >= 0) {
            System.out.println("That username already exists.");
            return;
        }
        if (userCount >= usernames.length) {
            System.out.println("The user account limit has been reached.");
            return;
        }

        System.out.println("[1] ADMIN  [2] STAFF  [3] CUSTOMER");
        int roleChoice = HobbyShop.readChoice("Choose role: ");
        String role;
        if (roleChoice == 1) {
            role = "ADMIN";
        } else if (roleChoice == 2) {
            role = "STAFF";
        } else if (roleChoice == 3) {
            role = "CUSTOMER";
        } else {
            System.out.println("Invalid role choice.");
            return;
        }

        String password = readPassword("Password (at least 4 characters): ");
        if (password == null) {
            return;
        }
        if (password.length() < 4) {
            System.out.println("Password must contain at least 4 characters.");
            return;
        }
        System.out.print("Confirm password: ");
        if (!password.equals(HobbyShop.input.nextLine())) {
            System.out.println("Passwords do not match.");
            return;
        }

        usernames[userCount] = username;
        passwordHashes[userCount] = hashPassword(password);
        roles[userCount] = role;
        fullNames[userCount] = fullName;
        userCount++;
        if (saveUsers()) {
            System.out.println("User account created successfully.");
        } else {
            userCount--;
            System.out.println("Could not save the new user account.");
        }
    }

    // Allows Admin to update a user's full name or role.
    public static void updateUser() {
        if (!isRole("ADMIN")) {
            System.out.println("Access denied.");
            return;
        }
        loadUsers();
        viewUsers();
        System.out.print("Enter the username to update: ");
        String username = HobbyShop.input.nextLine().trim();
        int index = findUser(username);
        if (index < 0) {
            System.out.println("User account not found.");
            return;
        }

        System.out.println("[1] Update Full Name");
        System.out.println("[2] Update Role");
        int choice = HobbyShop.readChoice("Choose what to update: ");
        if (choice == 1) {
            String newName = readSafeText("New full name: ");
            if (newName == null) {
                return;
            }
            fullNames[index] = newName;
        } else if (choice == 2) {
            if (usernames[index].equalsIgnoreCase(currentUsername)) {
                System.out.println("You cannot change the role of the account you are currently using.");
                return;
            }
            System.out.println("[1] ADMIN  [2] STAFF  [3] CUSTOMER");
            int roleChoice = HobbyShop.readChoice("New role: ");
            String newRole;
            if (roleChoice == 1) {
                newRole = "ADMIN";
            } else if (roleChoice == 2) {
                newRole = "STAFF";
            } else if (roleChoice == 3) {
                newRole = "CUSTOMER";
            } else {
                System.out.println("Invalid role choice.");
                return;
            }
            if (roles[index].equals("ADMIN") && !newRole.equals("ADMIN") && countAdmins() <= 1) {
                System.out.println("You cannot remove the role of the last Admin.");
                return;
            }
            roles[index] = newRole;
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (saveUsers()) {
            System.out.println("User account updated successfully.");
        } else {
            System.out.println("Could not save the update.");
        }
    }

    // Deletes an account after confirmation, while protecting the last Admin.
    public static void deleteUser() {
        if (!isRole("ADMIN")) {
            System.out.println("Access denied.");
            return;
        }
        loadUsers();
        viewUsers();
        System.out.print("Enter the username to delete: ");
        String username = HobbyShop.input.nextLine().trim();
        int index = findUser(username);
        if (index < 0) {
            System.out.println("User account not found.");
            return;
        }
        if (usernames[index].equalsIgnoreCase(currentUsername)) {
            System.out.println("You cannot delete the account you are currently using.");
            return;
        }
        if (roles[index].equals("ADMIN") && countAdmins() <= 1) {
            System.out.println("You cannot delete the last Admin account.");
            return;
        }
        if (roles[index].equals("CUSTOMER")) {
            System.out.println("Note: this also deletes the customer's collection and wishlist.");
            System.out.println("Their sales records are kept for the shop's history.");
        }
        System.out.print("Delete " + usernames[index] + "? (Y/N): ");
        String confirmation = HobbyShop.input.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        String deletedName = usernames[index];
        boolean deletedCustomer = roles[index].equals("CUSTOMER");
        for (int i = index; i < userCount - 1; i++) {
            usernames[i] = usernames[i + 1];
            passwordHashes[i] = passwordHashes[i + 1];
            roles[i] = roles[i + 1];
            fullNames[i] = fullNames[i + 1];
        }
        userCount--;
        if (saveUsers()) {
            System.out.println("User account deleted successfully.");
            if (deletedCustomer && !CollectionModule.removeRecordsForCustomer(deletedName)) {
                System.out.println("Warning: the customer's collection or wishlist could not be fully cleaned.");
            }
        } else {
            System.out.println("Could not save the deletion. Please check users.txt.");
            loadUsers();
        }
    }

    // Checks whether the current session has a specific role.
    public static boolean isRole(String role) {
        return currentRole.equals(role);
    }

    // Finds a username without treating uppercase and lowercase as different.
    static int findUser(String username) {
        for (int i = 0; i < userCount; i++) {
            if (usernames[i].equalsIgnoreCase(username)) {
                return i;
            }
        }
        return -1;
    }

    // Counts Admin accounts to protect the last Admin from removal.
    static int countAdmins() {
        int count = 0;
        for (int i = 0; i < userCount; i++) {
            if (roles[i].equals("ADMIN")) {
                count++;
            }
        }
        return count;
    }

    // Checks that a role name is one of the supported roles.
    static boolean validRole(String role) {
        return role.equals("ADMIN") || role.equals("STAFF") || role.equals("CUSTOMER");
    }

    // Converts a password to the classroom-level hash stored in the text file.
    static String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }

    // Reads a required name and rejects the record delimiter.
    static String readSafeText(String prompt) {
        System.out.print(prompt);
        String value = HobbyShop.input.nextLine().trim();
        if (value.isEmpty()) {
            System.out.println("This field is required.");
            return null;
        }
        if (value.contains("|")) {
            System.out.println("The character | is not allowed.");
            return null;
        }
        return value;
    }

    // Reads and checks a username.
    static String readUsername() {
        System.out.print("Username (no spaces): ");
        String username = HobbyShop.input.nextLine().trim();
        if (username.isEmpty() || username.contains("|") || username.contains(" ")) {
            System.out.println("Username is required and must not contain spaces or |.");
            return null;
        }
        return username;
    }

    // Reads a password without storing the original password in the file.
    static String readPassword(String prompt) {
        System.out.print(prompt);
        String password = HobbyShop.input.nextLine();
        if (password.contains("|")) {
            System.out.println("The character | is not allowed in a password.");
            return null;
        }
        return password;
    }
}
