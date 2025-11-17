import java.io.*;
import java.util.*;

public class receptionist_menu {

    private static final Scanner sc = new Scanner(System.in);
    private static final String RESERVE_FILE = "RESERVE.txt";
    private static final String CHECK_IN_FILE = "CHECKED-IN.txt";


    public static void main(String[] args) {
        receptionistMenu(null);
    }

    //  RECEPTIONIST MAIN MENU

    public static void receptionistMenu(Reception loggedInUser) {

        System.out.println("\n=== Receptionist Menu ===");

        System.out.println("Welcome " + loggedInUser.getFullName() + "!");

        int option = 0;  // changed from byte to int
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.println("1. View Client Information"); //hindi p nag aappear ang client informations
                System.out.println("2. View Transaction Records"); // working now with queue
                System.out.println("3. Sort Reservation Details by Date"); // need ayusin since sa year nagkakamali (queue applied)
                System.out.println("4. Filter Reservations by Payment Status"); // both not working (inaayos na ni nomis, queue applied)
                System.out.println("5. Check-In Guest");//fix code flow
                System.out.println("6. Exit to Main Menu");
                System.out.print("Select option: ");

                option = sc.nextInt();
                sc.nextLine(); // clear buffer

                if (option < 1 || option > 6) {
                    System.out.println("Please enter a valid option (1-5)!");
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
                viewClientInformation();
                receptionistMenu(loggedInUser);
                break;
            }
            case 2: { // View Transaction Records
                viewTransactionRecords();
                receptionistMenu(loggedInUser);
                break;
            }
            case 3: { // Sort Reservations by Date
                sortReservationsByDate();
                receptionistMenu(loggedInUser);
                break;
            }
            case 4: { // Filter Reservations by Payment
                filterReservationsByPayment();
                receptionistMenu(loggedInUser);
                break;
            }
            case 5: { //Check-in Guest
                checkInGuest();
                receptionistMenu(loggedInUser);
                break;
            }
            case 6:{ // Exit to Main Menu
                System.out.println("Returning to Main Menu...");
                return;
            }
            default: { // Safety catch
                System.out.println("Invalid option! Please try again.");
                receptionistMenu(loggedInUser);
                break;
            }
        }
    }

    // 1. VIEW CLIENT INFORMATION (may issue pa here)

    public static void viewClientInformation() {
        System.out.println("\n=== Client Information ===");

        List<String> blocks = readReservations();

        if (blocks.isEmpty()) {
            System.out.println("No client records found.");
            return;
        }

        printBlocks(blocks);
    }

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
        Queue<String> queue = new LinkedList<>();

        if (list.isEmpty()) {
            System.out.println("No reservation records found.");
            return;
        }

        Collections.sort(list); // simple sort
        queue.addAll(list);      // queue for FIFO

        while (!queue.isEmpty()) {
            System.out.println(queue.poll()); // FIFO output
        }
    }

    // 4. FILTER RESERVATIONS BY PAYMENT

    public static void filterReservationsByPayment() {
        int filterOption = 0; // changed from byte to int
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
        Queue<String> queue = new LinkedList<>();

        for (String block : blocks) {
            if (filterOption == 1 && block.contains("Remaining Balance: Php") && !block.contains("Remaining Balance: Php 0")) {
                queue.add(block);
            }
            if (filterOption == 2 && block.contains("Remaining Balance: Php 0")) {
                queue.add(block);
            }
        }

        if (queue.isEmpty()) {
            System.out.println("No matching reservations found.");
        } else {
            while (!queue.isEmpty()) {
                System.out.println(queue.poll()); // FIFO output
            }
        }
    }

    // HELPER FUNCTIONS

    public static List<String> readReservations() {
        List<String> list = new ArrayList<>();

        try {
            File file = new File(RESERVE_FILE);
            if (!file.exists()) return list;

            Scanner reader = new Scanner(file);
            StringBuilder block = new StringBuilder();

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                block.append(line).append("\n");

                if (line.contains("========================================================")) {
                    list.add(block.toString());
                    block.setLength(0);
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

    // 5. CHECK-IN GUEST

    //a. verify client using transaction id or client name
    //b. tags verified reservation as "Guest Checked-in
    //c. All checked-in guests saved in CHECKED-IN.txt.

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

        if (matchResult.size() > 1) {
            System.out.println("\nMultiple matches found:");
            for (int i = 0; i < matchResult.size(); i++) {
                System.out.println("[" + (i + 1) + "]\n" + matchResult.get(i));
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

            String sel = matchResult.get(choice - 1);
            updateCheckInFile(sel, listOfReservations);

        } else {
            String sel = matchResult.get(0);
            updateCheckInFile(sel, listOfReservations);
        }
    }

    private static void updateCheckInFile(String current, List<String> all) {

        if (current.contains("Guest Checked-In")) {
            System.out.println("Guest already checked in.");
            return;
        }

        System.out.println("\nChecking in:");
        System.out.println(current);

        String updated = current + "Guest Checked-InNNNNNN\n";

        int index = all.indexOf(current);
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

        System.out.println("Guest Checked-Innnn");
    }
}