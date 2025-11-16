import java.io.*;
import java.util.*;

// --- CHANGE 1: Renamed class to match file name ---
public class GuestCancelReservations { // Was 'GuestCancelReservation'

    private static final String RESERVE_FILE = "RESERVE.txt";

    // MAIN FUNCTION CALLED BY clientMenu OPTION 3
    public static void requestCancellation(Guest guest) {

        System.out.println("\n===== REQUEST CANCELLATION =====");
        System.out.println("Loading your reservations...");

        File file = new File(RESERVE_FILE);
        if (!file.exists()) {
            System.out.println("No reservations file found.");
            return;
        }

        // Step 1 — Load all reservations & locate guest’s reservations
        ArrayList<String> fileLines = new ArrayList<>();
        ArrayList<Integer> reservationStartIndexes = new ArrayList<>(); // store block starting lines
        ArrayList<Integer> reservationEndIndexes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0;
            boolean isGuestBlock = false;
            int startIndex = -1;

            while ((line = br.readLine()) != null) {
                fileLines.add(line);

                // Find the START of a block
                if (line.startsWith("========================================================") && lineCount > 0) {
                    if (isGuestBlock) {
                        // This is the end of the *previous* block
                        reservationEndIndexes.add(lineCount - 1); // The line *before* the separator
                        isGuestBlock = false;
                    }
                }
                
                // Find the user's ID
                if (line.contains("Client ID: " + guest.getID())) {
                    isGuestBlock = true;
                    // The start index is the '====' line *before* the Client ID line
                    // We need to find the last '===='
                    int searchBack = lineCount - 1;
                    while(searchBack >= 0) {
                        if (fileLines.get(searchBack).startsWith("========================================================")) {
                            startIndex = searchBack;
                            break;
                        }
                        searchBack--;
                    }
                    if (startIndex == -1) startIndex = 0; // Failsafe for first block
                    
                    if (!reservationStartIndexes.contains(startIndex)) {
                         reservationStartIndexes.add(startIndex);
                    }
                }
                lineCount++;
            }
            
            // If the last block was a guest block, mark its end
            if (isGuestBlock) {
                reservationEndIndexes.add(lineCount - 1);
            }

        } catch (IOException e) {
            System.out.println("Error reading reservation file: " + e.getMessage());
            return;
        }

        // Step 2 — If no reservations:
        if (reservationStartIndexes.isEmpty()) {
            System.out.println("You have no reservations to cancel.");
            return;
        }
        
        // --- CHANGE 2: Improved display to find 'Transaction ID' ---
        // Step 3 — Display numbered list of the user's reservations
        System.out.println("\nYour Reservations:");
        for (int i = 0; i < reservationStartIndexes.size(); i++) {
            int start = reservationStartIndexes.get(i);
            System.out.println("\n[" + (i + 1) + "] Reservation:");
            
            // Try to find Transaction ID, Check-in, and Status
            String transID = "N/A";
            String checkIn = "N/A";
            String status = "Active";
            
            for (int j = start; j <= reservationEndIndexes.get(i); j++) {
                String line = fileLines.get(j);
                if (line.startsWith("Transaction ID:")) transID = line;
                if (line.startsWith("Check-in Date:")) checkIn = line;
                if (line.contains("CANCELLATION REQUESTED")) status = "Pending Cancel";
            }
            
            System.out.println("  " + transID);
            System.out.println("  " + checkIn);
            System.out.println("  Status: " + status);
        }

        // Step 4 — Let the user choose a reservation
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        while (true) {
            try {
                System.out.print("\nEnter reservation number to cancel (0 to go back): ");
                choice = sc.nextInt();

                if (choice == 0) {
                    System.out.println("Returning to guest menu.");
                    return;
                }
                if (choice < 1 || choice > reservationStartIndexes.size()) {
                    System.out.println("Invalid number.");
                    continue;
                }
                break;

            } catch (Exception e) {
                System.out.println("Invalid input.");
                sc.nextLine();
            }
        }

        int cancelIndex = choice - 1;
        int blockStart = reservationStartIndexes.get(cancelIndex);
        int blockEnd = reservationEndIndexes.get(cancelIndex);

        // Step 5 — Check if cancellation is already requested
        boolean alreadyCancelled = false;
        for (int i = blockStart; i <= blockEnd; i++) {
            if (fileLines.get(i).contains("CANCELLATION REQUESTED: YES")) {
                alreadyCancelled = true;
                break;
            }
        }
        
        if (alreadyCancelled) {
             System.out.println("\nYou already requested cancellation for this reservation.");
             return;
        }

        // Step 6 — Insert cancellation line BEFORE block end
        // The blockEnd is the '====' line, so we insert at that line index.
        fileLines.add(blockEnd + 1, "CANCELLTATION REQUESTED: YES"); // Add *after* the last content line

        // Step 7 — Rewrite file
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESERVE_FILE))) {
            for (String s : fileLines) {
                pw.println(s);
            }
        } catch (IOException e) {
            System.out.println("Error saving update: " + e.getMessage());
            return;
        }

        System.out.println("\nYour cancellation request for " + fileLines.get(blockStart + 1) + " has been submitted!");
    }
}