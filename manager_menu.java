import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date; // Used for logging timestamps

/**
 * Manages all menu actions available to a Manager.
 * This class provides functionality identical to the Receptionist (viewing info,
 * sorting, filtering, checking-in) but adds the exclusive ability to
 * approve and process guest-requested cancellations.
 */
public class manager_menu {

    // Shared scanner for all methods in this class
    private static final Scanner sc = new Scanner(System.in);
    
    // File constants for database files
    private static final String RESERVE_FILE = "RESERVE.txt";
    private static final String CHECK_IN_FILE = "CHECKED-IN.txt";
    private static final String CANCELLED_FILE = "CANCELLED.txt"; 

    /**
     * The main entry point and menu loop for the logged-in Manager.
     * @param loggedInUser The Manager object who is currently logged in.
     * @param allUsers A list of all user accounts, passed from Main.java.
     */
    public static void managerMenu(Manager loggedInUser, ArrayList<User> allUsers) {

        System.out.println("\n=== Manager Menu ===");
        System.out.println("Welcome " + loggedInUser.getFullName() + "!");

        boolean keepMenuOpen = true;
        while (keepMenuOpen) {
            int option = 0;
            boolean validInput = false;

            // Input validation loop
            while (!validInput) {
                try {
                    System.out.println("\n1. View Client Information");
                    System.out.println("2. View Transaction Records (Unsorted)");
                    System.out.println("3. Sort Reservation Details by Date");
                    System.out.println("4. Filter Reservations by Payment Status");
                    System.out.println("5. Check-In Guest");
                    System.out.println("6. Approve/Cancel Reservations");
                    System.out.println("7. Exit to Main Menu");
                    System.out.print("Select option: ");

                    option = sc.nextInt();
                    sc.nextLine(); // clear buffer

                    if (option < 1 || option > 7) {
                        System.out.println("Please enter a valid option (1-7)!");
                        continue;
                    }

                    validInput = true;
                } catch (Exception e) {
                    System.out.println("Invalid input! Please enter a number (1-7).");
                    sc.nextLine(); // clear buffer
                }
            }

            // Process the manager's menu selection
            switch (option) {
                case 1: {
                    viewClientInformation(allUsers);
                    break;
                }
                case 2: {
                    viewTransactionRecords();
                    break;
                }
                case 3: {
                    sortReservationsByDate();
                    break;
                }
                case 4: {
                    filterReservationsByPayment();
                    break;
                }
                case 5: {
                    checkInGuest();
                    break;
                }
                case 6: {
                    // Pass the manager object to log who approved the cancellation
                    approveCancellation(loggedInUser); 
                    break;
                }
                case 7:{
                    System.out.println("Returning to Main Menu...");
                    keepMenuOpen = false; // This will exit the while loop
                    break;
                }
                default: {
                    System.out.println("Invalid option! Please try again.");
                    break;
                }
            }
        }
    }

    /**
     * 1. Displays all registered guests (clients) from the in-memory user list.
     * @param allUsers The list of all User objects loaded by Main.java.
     */
    public static void viewClientInformation(ArrayList<User> allUsers) {
        System.out.println("\n=== All Registered Client Information ===");

        if (allUsers.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        boolean foundGuests = false;
        System.out.println("-------------------------------------------------");
        // Loop through all users
        for (User user : allUsers) {
            // Check if the user is a Guest
            if (user instanceof Guest) {
                foundGuests = true;
                // Cast the User to a Guest to access guest-specific methods
                Guest guest = (Guest) user;
                System.out.println("Client ID: " + guest.getID());
                System.out.println("Name:      " + guest.getFullName());
                System.out.println("Email:     " + guest.getEmail());
                System.out.println("Address:   " + guest.getAdress());
                System.out.println("Contact:   " + guest.getContactNumber());
                System.out.println("-------------------------------------------------");
            }
        }

        if (!foundGuests) {
            System.out.println("No guests are registered in the system.");
        }
    }

    /**
     * 2. Displays all transaction records in their natural (unsorted) order
     * by reading them into a Queue and printing in FIFO order.
     */
    public static void viewTransactionRecords() {
        Queue<String> queue = new LinkedList<>(readReservations());

        if (queue.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        while (!queue.isEmpty()) {
            System.out.println(queue.poll()); // FIFO queue output
        }
    }

    /**
     * 3. Reads all reservations, sorts them by check-in date (earliest to latest),
     * and prints them in order.
     */
    public static void sortReservationsByDate() {
        System.out.println("\n=== Sorted Reservation Details by Date ===");
        List<String> list = readReservations();

        if (list.isEmpty()) {
            System.out.println("No reservation records found.");
            return;
        }

        // Sort the list using a custom comparator (lambda function)
        list.sort((block1, block2) -> {
            Date date1 = extractCheckInDate(block1); // Helper method
            Date date2 = extractCheckInDate(block2); // Helper method
            
            // Handle cases where dates might be missing
            if (date1 == null && date2 == null) return 0;
            if (date1 == null) return 1; // Put nulls at the end
            if (date2 == null) return -1; // Put nulls at the end
            
            return date1.compareTo(date2); // Chronological sort
        });

        // Add to queue just to print in FIFO order
        Queue<String> queue = new LinkedList<>(list);
        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }

    /**
     * 4. Asks the user if they want to see "Paid in Full" or "Balance Due"
     * reservations, then filters and displays the matching records,
     * sorted by date.
     */
    public static void filterReservationsByPayment() {
        int filterOption = 0;
        boolean validFilter = false;

        // Get user's filter choice (1 or 2)
        while (!validFilter) {
            try {
                System.out.println("\nFilter Options:");
                System.out.println("1. Reservations WITH Remaining Balance");
                System.out.println("2. Reservations Paid in Full");
                System.out.print("Choose: ");

                filterOption = sc.nextInt();
                sc.nextLine(); // clear buffer

                if (filterOption < 1 || filterOption > 2) {
                    System.out.println("Please enter 1 or 2!");
                    continue;
                }
                validFilter = true;
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter 1 or 2.");
                sc.nextLine();
            }
        }

        List<String> blocks = readReservations();
        List<String> filtered = new ArrayList<>();

        // Loop through all blocks and check their balance
        for (String block : blocks) {
            double balance = extractBalanceFromBlock(block);
            if (balance < 0) continue; // Skip blocks without balance info

            if (filterOption == 1 && balance > 0) {
                filtered.add(block); // Add if balance is due
            } else if (filterOption == 2 && balance == 0) {
                filtered.add(block); // Add if paid in full
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("No matching reservations found.");
            return;
        }

        // Sort the *filtered* list by date for readability
        filtered.sort((b1, b2) -> {
            Date d1 = extractCheckInDate(b1);
            Date d2 = extractCheckInDate(b2);
            if (d1 == null || d2 == null) return 0;
            return d1.compareTo(d2);
        });

        for (String block : filtered) {
            System.out.println(block);
        }
    }
    
    /**
     * 5. Allows the manager to check in a guest by searching for their
     * reservation via Transaction ID or Name. This updates both
     * RESERVE.txt and CHECKED-IN.txt.
     */
    public static void checkInGuest() {
        System.out.println("\n=== Guest Check-In ===");

        List<String> listOfReservations = readReservations();
        if (listOfReservations.isEmpty()) {
            System.out.println("No reservation records found.");
            return;
        }

        System.out.print("Enter Transaction ID or Name: ");
        String searchKey = sc.nextLine().trim();

        // Find all blocks that contain the search key
        List<String> matchResult = new ArrayList<>();
        for (String block : listOfReservations) {
            if (block.toLowerCase().contains(searchKey.toLowerCase())) {
                matchResult.add(block);
            }
        }

        if (matchResult.isEmpty()) {
            System.out.println("No matching reservation found.");
            return;
        }

        String sel; // The selected reservation block
        
        // If more than one match, let the manager choose
        if (matchResult.size() > 1) {
            System.out.println("\nMultiple matches found:");
            for (int i = 0; i < matchResult.size(); i++) {
                System.out.println("[Reservation #" + (i + 1) + "]\n" + matchResult.get(i));
            }

            int choice = 0;
            while (true) {
                try {
                    System.out.print("Select number: ");
                    choice = sc.nextInt();
                    sc.nextLine();

                    if (choice < 1 || choice > matchResult.size()) {
                        System.out.println("Invalid.");
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("Please enter a numeric value.");
                    sc.nextLine();
                }
            }
            sel = matchResult.get(choice - 1);
        } else {
            // Only one match found
            sel = matchResult.get(0);
        }

        // Pass the selected block and the full list to be updated
        updateCheckInFile(sel, listOfReservations);
    }

    /**
     * 6. Finds all reservations with a "CANCELLTATION REQUESTED: YES" flag,
     * displays them to the manager, and allows the manager to select one
     * to permanently remove and log.
     * @param manager The currently logged-in manager (for logging purposes).
     */
    public static void approveCancellation(Manager manager) {
        System.out.println("\n=== Approve Pending Cancellations ===");
        
        List<String> allBlocks = readReservations();
        List<String> pendingCancellations = new ArrayList<>();
        List<String> otherReservations = new ArrayList<>();

        // Sort all blocks into two lists
        for (String block : allBlocks) {
            // This typo must match the one in GuestCancelReservations.java
            if (block.contains("CANCELLTATION REQUESTED: YES")) {
                pendingCancellations.add(block);
            } else {
                otherReservations.add(block);
            }
        }

        if (pendingCancellations.isEmpty()) {
            System.out.println("No pending cancellation requests found.");
            return;
        }

        // Display a summarized list of all pending requests
        System.out.println("Pending Cancellations:");
        for (int i = 0; i < pendingCancellations.size(); i++) {
            System.out.println("\n[" + (i + 1) + "] Reservation:");
            String block = pendingCancellations.get(i);
            
            // Extract key details for easy viewing
            String tid = "N/A";
            String name = "N/A";
            String checkIn = "N/A";
            for (String line : block.split("\n")) {
                if (line.startsWith("Transaction ID:")) tid = line;
                if (line.startsWith("Name:")) name = line;
                if (line.startsWith("Check-in Date:")) checkIn = line;
            }
            System.out.println("  " + tid);
            System.out.println("  " + name);
            System.out.println("  " + checkIn);
        }

        // Get the manager's selection
        int choice = 0;
        while (true) {
            try {
                System.out.print("\nEnter reservation number to APPROVE CANCELLATION (0 to go back): ");
                choice = sc.nextInt();
                sc.nextLine(); // clear buffer

                if (choice == 0) {
                    System.out.println("Returning to manager menu.");
                    return;
                }
                if (choice < 1 || choice > pendingCancellations.size()) {
                    System.out.println("Invalid number. Please try again.");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        }

        // Remove the selected block from the 'pending' list
        int cancelIndex = choice - 1;
        String cancelledBlock = pendingCancellations.remove(cancelIndex); 

        // Log the cancelled block to a separate file for auditing
        try {
            logCancellationToFile(cancelledBlock, manager);
        } catch (IOException e) {
            System.out.println("WARNING: Could not log cancellation to " + CANCELLED_FILE + ": " + e.getMessage());
        }

        // Rewrite the RESERVE.txt file with *only* the blocks we want to keep
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVE_FILE, false))) { // 'false' = OVERWRITE
            
            // Write all the reservations that were never pending
            for (String block : otherReservations) {
                writer.println(block);
            }
            // Write the *remaining* pending reservations that were *not* cancelled
            for (String block : pendingCancellations) {
                writer.println(block);
            }
            
        } catch (IOException e) {
            System.out.println("CRITICAL ERROR: Could not rewrite RESERVE.txt file: " + e.getMessage());
            return;
        }

        // Find the TID for a nice confirmation message
        String tid = "N/A";
        for (String line : cancelledBlock.split("\n")) {
            if (line.startsWith("Transaction ID:")) {
                tid = line.split(" ")[1];
                break;
            }
        }
        System.out.println("\nSuccessfully logged and removed reservation " + tid + ".");
        System.out.println("The RESERVE.txt file has been updated.");
    }
    
    /**
     * Appends a cancelled reservation block to the CANCELLED.txt file
     * for logging and auditing purposes.
     * @param block The reservation block to log
     * @param manager The manager who approved the cancellation
     * @throws IOException If the file cannot be written to
     */
    private static void logCancellationToFile(String block, Manager manager) throws IOException {
        // 'true' means append to the file
        try (FileWriter fw = new FileWriter(CANCELLED_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            
            pw.println(block); // Write the entire reservation block
            
            // Add the manager's approval info and a timestamp
            String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
            pw.println("CANCELLATION APPROVED BY: " + manager.getFullName() + 
                       " (EID: " + manager.getEmployeeID() + ")");
            pw.println("APPROVAL DATE: " + timeStamp);
            pw.println("========================================================"); // Add a separator
            pw.println(); // Add extra space for readability
        }
    }


    // --- HELPER METHODS (Identical to Receptionist) ---

    /**
     * Helper method to parse the balance due from a reservation block.
     * @param block The reservation text block.
     * @return The balance due as a double, or -1 if not found.
     */
    private static double extractBalanceFromBlock(String block) {
        try {
            String[] lines = block.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("Balance Due") || line.startsWith("Balance Due Upon Check-in")) {
                    // Remove all non-numeric characters except the decimal point
                    String num = line.replaceAll("[^0-9.]", "");
                    if (!num.isEmpty()) return Double.parseDouble(num);
                }
            }
        } catch (Exception e) { /* ignore parse errors */ }
        return -1; // balance not found
    }

    /**
     * Helper method to parse the check-in date from a reservation block.
     * @param block The reservation text block.
     * @return The check-in date as a Date object, or null if not found.
     */
    private static Date extractCheckInDate(String block) {
        try {
            String[] lines = block.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("Check-in Date:")) {
                    String dateStr = line.substring("Check-in Date:".length()).trim();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    return sdf.parse(dateStr);
                }
            }
        } catch (Exception e) {
            return null; // Handle parse exceptions
        }
        return null;
    }

    /**
     * Reads the entire RESERVE.txt file and parses it into a List of Strings,
     * where each String is one full reservation block.
     * @return A List<String> of reservation blocks.
     */
    public static List<String> readReservations() {
        List<String> list = new ArrayList<>();
        try {
            File file = new File(RESERVE_FILE);
            if (!file.exists()) return list; // Return empty list if no file

            Scanner reader = new Scanner(file);
            StringBuilder block = new StringBuilder();
            boolean inBlock = false;

            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                // "Transaction ID:" marks the start of a *new* block
                if (line.startsWith("Transaction ID:")) {
                    // If we were already in a block, save the previous one
                    if (inBlock && block.length() > 0) {
                        list.add(block.toString());
                    }
                    block.setLength(0); // Clear the builder for the new block
                    block.append(line).append("\n");
                    inBlock = true;
                
                // "====...====" marks the end of a block
                } else if (line.startsWith("========================================================")) {
                    if (inBlock) {
                        block.append(line).append("\n");
                        list.add(block.toString()); // Add the completed block
                        inBlock = false;
                        block.setLength(0);
                    }
                // Any other line is added to the current block
                } else if (inBlock) {
                    block.append(line).append("\n");
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error reading reservation file: " + e.getMessage());
        }
        return list;
    }

    /**
     * A simple utility method to print a list of reservation blocks.
     * @param blocks The list of blocks to print.
     */
    public static void printBlocks(List<String> blocks) {
        for (String b : blocks) {
            System.out.println(b);
        }
    }

    /**
     * Updates a reservation block to "Checked-In" status.
     * This method rewrites the entire RESERVE.txt file with the updated block
     * and appends the updated block to the CHECKED-IN.txt log.
     * @param current The original reservation block string.
     * @param all The complete list of all reservation blocks.
     */
    private static void updateCheckInFile(String current, List<String> all) {

        // Do not check in a guest twice
        if (current.contains("Guest Checked-In")) {
            System.out.println("Guest already checked in.");
            return;
        }

        System.out.println("\nChecking in:");
        System.out.println(current);

        // Find the exact index of the reservation block to replace it
        int index = -1;
        for(int i = 0; i < all.size(); i++) {
            if(all.get(i).equals(current)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("Error: Could not find reservation to update.");
            return;
        }

        // Create the new block with the "Checked-In" flag
        String updated = current.replace(
                "========================================================",
                "Guest Checked-In\n========================================================"
        );

        // Replace the old block with the new one in the master list
        all.set(index, updated);

        // Overwrite the RESERVE.txt file with the updated list
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVE_FILE))) {
            for (String block : all) {
                writer.println(block);
            }
        } catch (IOException e) {
            System.out.println("Error writing to RESERVE.txt file.");
        }

        // Append the checked-in record to the CHECKED-IN.txt log
        try (PrintWriter writer = new PrintWriter(new FileWriter(CHECK_IN_FILE, true))) {
            writer.println(updated);
            // We add an extra separator just in case CHECKED-IN.txt is read
            // using the same block logic as RESERVE.txt
            writer.println("========================================================");
        } catch (IOException e) {
            System.out.println("Error writing to CHECKED-IN.txt file.");
        }

        System.out.println("Guest Checked-In Succssfully");
    }
}