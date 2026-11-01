package hobbyshop;

import java.io.*;

public class FileHelper {

    public static final int MAX_RECORDS = 500;
    public static final String USER_FILE = "data/users.txt";

    public static void preparedDataFiles() { //creates data and the starter account if needed
        File folder = new File("data");
        if (!folder.exists() && !folder.mkdirs()) {
            System.out.println("Warning: Could not create the data folder");
        }
        File userFile = new File(USER_FILE);
        if (!userFile.exists()) {
            String[] starterUsers = new String[3];
            starterUsers[0] = "admin|-969161597|ADMIN|Aetherion Administration"; // user | password hash | role | NAME
            starterUsers[1] = "staff|130525166|STAFF|Shop Staff";
            starterUsers[2] = "customer|-1772107372|CUSTOMER|Sample Customer";
            if (!writeLines(USER_FILE, starterUsers, 3)) {
                System.out.println("Warning: Could not create user.txt");
            }
        }

        String[] otherFiles = {"data/transactions.txt", "data/collecttions.txt", "data/wishlist.txt", "data/deliveries.txt"};
        for (int i = 0; i < otherFiles.length; i++) {
            File otherFile = new File(otherFiles[i]);
            if (!otherFile.exists()) {
                try {
                    otherFile.createNewFile();
                } catch (IOException e) {
                    System.out.println("WARNING: Could not create " + otherFiles[i] + ".");
                }
            }
        }
    }

    //READ lines from a text into an array
    public static int readLines(String filePath, String[] lines, int maxRecords) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                if (!file.createNewFile()) {
                    System.out.println("Could not create file: " + filePath);
                    return 0;
                }
            } catch (IOException e) {
                System.out.println("Could not create file: " + filePath);
            }
        }
        int count = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (count >= maxRecords) {
                    System.out.println("Warning: File has more records than the program can load");
                    break;
                }
                lines[count] = line;
                count++;
            }
        } catch (IOException e) {
            System.out.println("Could not read file: " + filePath);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Could not close file: ");
                }
            }
        }
        return count;
    }

    // Writes all supplied records using a temporary file before replacing the original.
    public static boolean writeLines(String filePath, String[] lines, int count) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            System.out.println("Could not create folder for: " + filePath);
            return false;
        }

        File tempFile = new File(filePath + ".tmp");
        File backupFile = new File(filePath + ".bak");
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(tempFile, false));
            for (int i = 0; i < count; i++) {
                writer.write(lines[i]);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not write temporary file: " + filePath);
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Could not close temporary file.");
                }
            }
        }

        if (backupFile.exists() && !backupFile.delete()) {
            tempFile.delete();
            System.out.println("Could not clear the previous backup file.");
            return false;
        }

        boolean movedOldFile = false;
        if (file.exists()) {
            if (!file.renameTo(backupFile)) {
                tempFile.delete();
                System.out.println("Could not protect the old file before saving.");
                return false;
            }
            movedOldFile = true;
        }

        if (!tempFile.renameTo(file)) {
            if (movedOldFile) {
                backupFile.renameTo(file);
            }
            tempFile.delete();
            System.out.println("Could not replace the old file. Previous data was preserved when possible.");
            return false;
        }

        if (movedOldFile) {
            backupFile.delete();
        }
        return true;
    }
}
