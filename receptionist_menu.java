import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class receptionist_menu {

    private static final Scanner sc = new Scanner(System.in);
    private static final String RESERVE_FILE = "RESERVE.txt";
    private static final String CHECK_IN_FILE = "CHECKED-IN.txt";


    public static void main(String[] args) {
        // This is a test main, but it needs a valid object
        // receptionistMenu(null); // This would cause a NullPointerException
        System.out.println("Running test main... please launch from Main.java");
    }

    //  RECEPTIONIST MAIN MENU
    // --- CHANGE 1: This method now accepts the 'allUsers' list from Main ---
    public static void receptionistMenu(Reception loggedInUser, ArrayList<User> allUsers) {

        System.out.println("\n=== Receptionist Menu ===");
        System.out.println("Welcome " + loggedInUser.getFullName() + "!");

        boolean keepMenuOpen = true;
        while (keepMenuOpen) {
            int option = 0;
            boolean validInput = false;

            while (!validInput) {
                try {
                    System.out.println("\n1. View Client Information");
                    System.out.println("2. View Transaction Records (Unsorted)");
                    System.out.println("3. Sort Reservation Details by Date");
                    System.out.println("4. Filter Reservations by Payment Status");
                    System.out.println("5. Check-In Guest");
                    System.out.println("6. Log Out");
                    System.out.print("Select option: ");

                    option = sc.nextInt();
                    sc.nextLine(); // clear buffer

                    if (option < 1 || option > 6) {
                        System.out.println("Please enter a valid option (1-6)!");
                        continue;
                    }

                    validInput = true;
                } catch (Exception e) {
                    System.out.println("Invalid input! Please enter a number (1-6).");
                    sc.nextLine(); // clear buffer
                }
            }

            //  PROCESS USER SELECTION

            switch (option) {
                case 1: { // View Client Information
                    // --- CHANGE 2: We pass the 'allUsers' list down ---
                    viewClientInformation(allUsers);
                    break;
                }
                case 2: { // View Transaction Records
                    viewTransactionRecords();
                    break;
                }
                case 3: { // Sort Reservations by Date
                    sortReservationsByDate();
                    break;
                }
                case 4: { // Filter Reservations by Payment
                    filterReservationsByPayment();
                    break;
                }
                case 5: { //Check-in Guest
                    checkInGuest();
                    break;
                }
                case 6:{ // Exit to Main Menu
                    System.out.println("Logging Out");
                    keepMenuOpen = false; // This will exit the while loop
                    break;
                }
                default: { // Safety catch
                    System.out.println("Invalid option! Please try again.");
                    break;
                }
            }
        }
    }

    // 1. VIEW CLIENT INFORMATION
    // --- CHANGE 3: This method now uses the 'allUsers' list ---
    public static void viewClientInformation(ArrayList<User> allUsers) {
        System.out.println("\n=== All Registered Client Information ===");

        // We REMOVED the UserFileHandler and loadUsers() call
        // We just use the list that was passed in!

        if (allUsers.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        // This is the "for loop" you asked for
        boolean foundGuests = false;
        System.out.println("-------------------------------------------------");
        for (User user : allUsers) {
            // Use 'instanceof' to check if the user is a Guest
            if (user instanceof Guest) {
                foundGuests = true;
                // "Cast" the User object to a Guest object to get Guest-specific methods
                Guest guest = (Guest) user;

                // Print the details
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
    // --- END OF FIX ---

    // 2. VIEW TRANSACTION RECORDS
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

    // 3. SORT RESERVATIONS BY DATE
    public static void sortReservationsByDate() {
        System.out.println("\n=== Sorted Reservation Details by Date ===");

        List<String> list = readReservations();

        if (list.isEmpty()) {
            System.out.println("No reservation records found.");
            return;
        }

        list.sort((block1, block2) -> {
            Date date1 = extractCheckInDate(block1);
            Date date2 = extractCheckInDate(block2);

            if (date1 == null && date2 == null) return 0;
            if (date1 == null) return 1;
            if (date2 == null) return -1;

            return date1.compareTo(date2);
        });

        Queue<String> queue = new LinkedList<>();
        queue.addAll(list);

        while (!queue.isEmpty()) {
            System.out.println(queue.poll()); // FIFO output
        }
    }



    // 4. FILTER RESERVATIONS BY PAYMENT WITH DATE SORTING
    public static void filterReservationsByPayment() {
        int filterOption = 0; // 1 = remaining balance, 2 = fully paid
        boolean validFilter = false;

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

        for (String block : blocks) {
            double balance = extractBalanceFromBlock(block);
            if (balance < 0) continue; // skip if balance line not found

            if (filterOption == 1 && balance > 0) {
                filtered.add(block);
            } else if (filterOption == 2 && balance == 0) {
                filtered.add(block);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("No matching reservations found.");
            return;
        }

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

    // This helper method is fine
    private static double extractBalanceFromBlock(String block) {
        try {
            String[] lines = block.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("Balance Due") || line.startsWith("Balance Due Upon Check-in")) {
                    String num = line.replaceAll("[^0-9.]", "");
                    if (!num.isEmpty()) return Double.parseDouble(num);
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return -1; // balance not found
    }

    // This helper method is also fine
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
            return null;
        }
        return null;
    }


    // HELPER FUNCTIONS

    // This method is fine
    public static List<String> readReservations() {
        List<String> list = new ArrayList<>();

        try {
            File file = new File(RESERVE_FILE);
            if (!file.exists()) return list;

            Scanner reader = new Scanner(file);
            StringBuilder block = new StringBuilder();
            boolean inBlock = false;

            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                if (line.startsWith("Transaction ID:")) {
                    if (inBlock && block.length() > 0) {
                        list.add(block.toString());
                    }
                    block.setLength(0);
                    block.append(line).append("\n");
                    inBlock = true;

                } else if (line.startsWith("========================================================")) {
                    if (inBlock) {
                        block.append(line).append("\n");
                        list.add(block.toString());
                        inBlock = false;
                        block.setLength(0);
                    }

                } else if (inBlock) {
                    block.append(line).append("\n");
                }
            }

            reader.close();
        } catch (Exception e) {
            System.out.println("Error reading reservation file.");
        }

        return list;
    }

    public static void printBlocks(List<String> blocks) {
        for (String b : blocks) {
            System.out.println(b);
        }
    }

    // 5. CHECK-IN GUEST (This method looks fine)
    public static void checkInGuest() {
        System.out.println("\n=== Guest Check-In ===");

        List<String> listOfReservations = readReservations();
        if (listOfReservations.isEmpty()) {
            System.out.println("No reservation records found.");
            return;
        }

        System.out.print("Enter Transaction ID or Name: ");
        String searchKey = sc.nextLine().trim();

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

        String sel; // The selected block

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
            sel = matchResult.get(0);
        }

        updateCheckInFile(sel, listOfReservations);
    }

    private static void updateCheckInFile(String current, List<String> all) {

        if (current.contains("Guest Checked-In")) {
            System.out.println("Guest already checked in.");
            return;
        }

        System.out.println("\nChecking in:");
        System.out.println(current);

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

        String updated = current.replace(
                "========================================================",
                "Guest Checked-In\n========================================================"
        );

        all.set(index, updated);

        try (PrintWriter writer = new PrintWriter(new FileWriter(RESERVE_FILE))) {
            for (String block : all) {
                writer.println(block);
            }
        } catch (IOException e) {
            System.out.println("Error writing RESERVE file.");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(CHECK_IN_FILE, true))) {
            writer.println(updated);
            writer.println("========================================================");
        } catch (IOException e) {
            System.out.println("Error writing CHECKED-IN file.");
        }

        System.out.println("Guest Checked-In Succssfully");
    }
}