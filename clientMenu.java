import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;


public class clientMenu {

    private static final Scanner sc = new Scanner(System.in);
    
    // This variable was unused, so I removed it:
    // private static ArrayList<User> allUsers;


    public static void guestMenu(Guest loggedInUser) {
        System.out.println("\n=== Client Menu: Welcome " + loggedInUser.getFullName() + " ===");
        byte option = 0;
        boolean menuLoop = true; 
        while (menuLoop) {
            try {
                System.out.println("\n1. Make Reservation");
                System.out.println("2. View My Reservations");
                System.out.println("3. Request Cancellation of Reservation");
                System.out.println("4. Exit to Main Menu (Logout)");
                System.out.print("Select option: ");

                option = sc.nextByte();
                sc.nextLine(); // Clear the buffer

                if (option > 4 || option < 1) {
                    System.out.println("Please enter a valid option (1-4)!");
                    continue; 
                }

                // --- CHANGE 1: Cleaned up the 'switch' statement ---
                switch (option) {
                    case 1: { 
                        guest_reserve(loggedInUser);
                        // Removed the stray 'sc.nextLine()' that was here
                        break;
                    }
                    case 2: { 
                        // Removed call to empty local method 'view_reserve'
                        GuestReservationActions.viewReservations(loggedInUser);
                        break;
                    }
                    case 3: { 
                        // Removed call to empty local method 'request_cancellation'
                        // --- CHANGE 2: Using the *better* cancellation class ---
                        GuestCancelReservations.requestCancellation(loggedInUser);
                        break;
                    }
                    case 4: { 
                        System.out.println("Logging out...");
                        menuLoop = false; // This will exit the while loop
                        break;
                    }
                    default: {
                        System.out.println("Invalid option! Please enter a number between 1 and 4.");
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.nextLine(); // Clear the bad input
            }
        }
    }

    // ... (All reservation code: guest_reserve, paymentOption, etc. is unchanged) ...
    // ... (I'm omitting it here for brevity, but it's the same as your file) ...
    
    // Reservation details
    //private static String transID;
    private static String reserveDate;
    private static String checkOutDate;
    private static int facilityNo;
    private static int totalGuests;


    // Financial details
    private static double grandTotal;
    private static double foodTotal;
    private static double finalTotal;


    // Constants
    private static final String RESERVE_FILE = "RESERVE.txt";
    //private static final int TRANS_ID_LENGTH = 6;


    // --- REMOVED reserve_scanner, we use 'sc' ---


    // StringBuilder objects to store reservation details
    private static final StringBuilder facilityDetails = new StringBuilder();
    private static final StringBuilder paymentDetails = new StringBuilder();


    // We get these from the loggedInUser object now
    //private static String fullName;
    //private static String newUserID;


    // main method for testing (as requested)
    // --- CHANGE 3: Fixed test main method ---
    public static void main(String[] args) throws IOException {
        // Create a dummy Guest object to test with
        Guest testGuest = new Guest("test@email.com", "testpass", 101, "Test User", "123 Test St", 30, "09123456789");
        System.out.println("--- RUNNING IN TEST MODE (Making one reservation) ---");
        // This should call guest_reserve directly for testing, not the whole menu
        guest_reserve(testGuest);
        System.out.println("--- TEST MODE FINISHED ---");
    }


    public static void guest_reserve(Guest currentGuest) {
        // Reset totals for new reservation
        grandTotal = 0;
        foodTotal = 0;
        finalTotal = 0;
        totalGuests = 0;


        facilityDetails.setLength(0);
        paymentDetails.setLength(0);


        // Display header
        System.out.println("========================================================");
        System.out.println("               HOTEL RESERVATION PORTAL                 ");
        System.out.println("               User: " + currentGuest.getFullName());
        System.out.println("========================================================");
        System.out.println();




        // variable declarations
        char facility = ' ';
        int guestNo = 0;
        int maximumGuest = 0;
        double basePrice = 0;
        String facilityName = "";
        int additional = 0;
        double totalAmount = 0;


        // --- Facility Number Selection ---
        while (true) {
            try {
                facilityOptions();


                System.out.print("Enter the number of facilities to reserve: ");
                facilityNo = sc.nextInt();
                sc.nextLine();


                char choice;
                while (true) {
                    System.out.print("Are you sure you want to reserve " + facilityNo + " " + (facilityNo == 1 ? "facility?" : "facilities?") + " (Y/N): ");
                    choice = sc.next().toLowerCase().charAt(0);
                    sc.nextLine();


                    if (choice != 'y' && choice != 'n') {
                        System.out.println("Invalid option. Please try again.");
                    } else {
                        break;
                    }
                }
                if (choice == 'n') {
                    continue;
                } else if (choice == 'y') {
                    break;
                }


            } catch (Exception e) {
                System.out.println("Invalid option. Please enter a valid number.");
                sc.nextLine();
                // continue;
            }
        }


        // --- Facility Details Loop ---
        for (int i = 0; i < facilityNo; i++) {
            System.out.println("\n--- Reserving Facility #" + (i + 1) + " ---");
            facilityOptions();


            additional = 0;
            while (true) {
                System.out.print("Enter facility " + (i + 1) + " to reserve [A, B, C, D]: ");
                facility = sc.next().charAt(0);
                facility = Character.toUpperCase(facility);
                sc.nextLine(); // Clear the buffer


                if (facility == 'A' || facility == 'B' || facility == 'C' || facility == 'D') {
                    switch (facility) {
                        case 'A':
                            facilityName = "Single Room";
                            basePrice = 1500; maximumGuest = 2;
                            break;
                        case 'B':
                            facilityName = "Double";
                            basePrice = 2000;
                            maximumGuest = 3; break;
                        case 'C':
                            facilityName = "King";
                            basePrice = 3000;
                            maximumGuest = 4; break;
                        case 'D':
                            facilityName = "Suite";
                            basePrice = 4000;
                            maximumGuest = 6; break;
                    }


                    while (true) {
                        try {
                            System.out.print("Enter the number of guests for this facility: ");
                            guestNo = sc.nextInt();
                            sc.nextLine(); // Clear the buffer
                            System.out.println();


                            char choice;
                            while (true) {
                                String label;
                                if (guestNo == 1) {
                                    label = " guest";
                                } else {
                                    label = " guests";
                                }


                                System.out.print("Are you sure you want to have " + guestNo + " " + label + " (Y/N): ");
                                choice = sc.next().toLowerCase().charAt(0);
                                sc.nextLine();


                                if (choice != 'y' && choice != 'n') {
                                    System.out.println("Invalid option. Please enter a valid choice.");
                                } else {
                                    break;
                                }
                            }
                            if (choice == 'n') {
                                continue;
                            } else if (choice == 'y') {
                                break;
                            }


                            // break; // This was correct
                        } catch (Exception e) {
                            System.out.println("Please enter a valid number.");
                            sc.nextLine(); // Clear the bad input
                            // continue; // This was correct
                        }
                    }


                    if (guestNo > maximumGuest) {
                        int extra = guestNo - maximumGuest;
                        additional = extra * 500;
                    }


                    totalAmount = basePrice + additional;
                    totalGuests += guestNo; // Add to the overall total
                    grandTotal += totalAmount; // Add to the overall facility total


                    // Pass guestNo for this specific facility
                    facilityInfo(facilityName, guestNo, basePrice, additional, totalAmount, i + 1);
                    break;
                } else {
                    System.out.println("Invalid option. Please choose A, B, C, or D only.\n");
                }
            }
        }


        // --- Date Input (last step) ---
        // This 'try' block is not needed here
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        format.setLenient(false); // strictly enforce date format


        boolean isValid1 = false;
        reserveDate = ""; // Use the static class variable


        while (!isValid1) {
            System.out.print("\nEnter reservation date (MM/dd/yyyy): ");
            reserveDate = sc.nextLine(); // <-- This will now wait for input!


            try {
                Date inputDate = format.parse(reserveDate); // parse input date string to Date object
                Date today = new Date(); // get current date


                // Check if inputDate is before today (ignoring time)
                Calendar calInput = Calendar.getInstance();
                calInput.setTime(inputDate);
                Calendar calToday = Calendar.getInstance();
                calToday.setTime(today);


                // Set time to 00:00:00 for comparison
                calInput.set(Calendar.HOUR_OF_DAY, 0);
                calInput.set(Calendar.MINUTE, 0);
                calInput.set(Calendar.SECOND, 0);
                calInput.set(Calendar.MILLISECOND, 0);
                calToday.set(Calendar.HOUR_OF_DAY, 0);
                calToday.set(Calendar.MINUTE, 0);
                calToday.set(Calendar.SECOND, 0);
                calToday.set(Calendar.MILLISECOND, 0);


                if (calInput.before(calToday)) {
                    System.out.println("Date is in the past. Try again.");
                } else if (isMoreThanTwoYears(inputDate, today)) {
                    System.out.println("Check-in date cannot be more than 2 years in advance. Try again.");
                } else {
                    isValid1 = true;
                }
            } catch (Exception e) {
                System.out.println("Invalid format. Use MM/dd/yyyy.");
            }
        }


        checkOutDate = "";
        boolean isValid2 = false;
        while (!isValid2) {
            System.out.print("Enter check-out date (MM/dd/yyyy): ");
            checkOutDate = sc.nextLine();


            try {
                Date outDate = format.parse(checkOutDate);
                Date inDate = format.parse(reserveDate);
                Date today = new Date();


                if (!outDate.after(inDate)) {
                    System.out.println("Check-out date must be after check-in date.");
                } else if (isMoreThanTwoYears(outDate, today)) {
                    System.out.println("Check-out date cannot be more than 2 years in advance. Try again.");
                } else {
                    isValid2 = true;
                }
            } catch (Exception e) {
                System.out.println("Invalid format. Use MM/dd/yyyy.");
            }
        }


        // --- Payment Selection ---
        finalTotal = grandTotal + foodTotal; // This is the correct final total


        System.out.printf("\nTotal Facility Cost: ₱%.2f\n", grandTotal);
        System.out.printf("Total Meal Cost: ₱%.2f\n", foodTotal);
        System.out.printf("GRAND TOTAL: ₱%.2f\n\n", finalTotal);




        char paymentChoice;
        while (true) {
            System.out.println("Pay the 30% reservation fee or pay full amount?: ");
            System.out.println("A. Pay 30% reservation fee");
            System.out.println("B. Pay full amount reservation fee");
            System.out.print("Select an option: ");
            paymentChoice = sc.next().charAt(0);
            paymentChoice = Character.toUpperCase(paymentChoice);
            sc.nextLine(); // <-- FIX #3: Clear newline from next()


            if (paymentChoice == 'A' || paymentChoice == 'B') {
                break;
            } else {
                System.out.println("Invalid option. Please choose A or B only.\n");
                // continue; // This was correct
            }
        }


        double amountToPay;
        if (paymentChoice == 'A') {
            amountToPay = finalTotal * 0.30;
            System.out.println("You chose to pay 30% reservation fee.");
            System.out.printf("Amount to pay: ₱%.2f\n", amountToPay);
        } else {
            amountToPay = finalTotal;
            System.out.println("You chose to pay full amount.");
            System.out.printf("Amount to pay: ₱%.2f\n", amountToPay);
        }


        paymentOption(paymentChoice, finalTotal, amountToPay);


        // --- Save the file ---
        // We pass the currentGuest object to save their ID and Name
        try {
            saveReserveToFile(currentGuest);
            System.out.println("Reservation saved successfully.");
            System.out.println("Thank you for your reservation. Have a great stay!");
            System.out.println("========================================================");
            System.out.println();
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Could not save reservation to file.");
            System.err.println(e.getMessage());
        }
    }


    // choose which payment options client wants to do
    private static void paymentOption(char paymentOption, double finalTotal, double amountPaid) {
        // Process based on payment option
        if (paymentOption == 'A') {
            // 30% reservation fee option
            double reservationFee = finalTotal * 0.30;
            double balanceDue = finalTotal - reservationFee;


            // Record payment details
            paymentDetails.append("Payment Option: 30% Reservation Fee\n");
            paymentDetails.append("Total Reservation Cost: ₱").append(String.format("%.2f", finalTotal)).append("\n");
            paymentDetails.append("30% Deposit Amount: ₱").append(String.format("%.2f", reservationFee)).append("\n");
            paymentDetails.append("Balance Due Upon Check-in: ₱").append(String.format("%.2f", balanceDue)).append("\n");


            System.out.println("\nPayment Summary:");
            System.out.printf("Total Reservation Cost: ₱%.2f\n", finalTotal);
            System.out.printf("30%% Deposit Amount: ₱%.2f\n", reservationFee);
            System.out.printf("Balance Due Upon Check-in: ₱%.2f\n", balanceDue);


        } else if (paymentOption == 'B') {
            // Full payment option
            paymentDetails.append("Payment Option: Full Payment\n");
            paymentDetails.append("Total Reservation Cost: ₱").append(String.format("%.2f", finalTotal)).append("\n");
            paymentDetails.append("Amount Paid: ₱").append(String.format("%.2f", amountPaid)).append("\n");
            paymentDetails.append("Balance Due: ₱0.00 (No balance due)\n");


            System.out.println("\nPayment Summary:");
            System.out.printf("Total Reservation Cost: ₱%.2f\n", finalTotal);
            System.out.printf("Amount Paid: ₱%.2f\n", amountPaid);
            System.out.println("Balance Due: ₱0.00 (No balance due)");


        } else {
            // Should never reach here due to input validation
            paymentDetails.append("Payment Option: Unknown\n");
            paymentDetails.append("Please contact hotel staff for payment clarification.\n");


            System.out.println("\nWarning: Invalid payment option selected.");
            System.out.println("Please contact hotel staff for payment clarification.");
        }
    }


    private static boolean isMoreThanTwoYears(Date futureDate, Date baseDate) {
        Calendar futureCal = Calendar.getInstance();
        Calendar baseCal = Calendar.getInstance();
        futureCal.setTime(futureDate);
        baseCal.setTime(baseDate);


        // Add 2 years to the base date
        baseCal.add(Calendar.YEAR, 2);


        // If future date is after base date + 2 years, it's more than 2 years ahead
        return futureDate.after(baseCal.getTime());
    }




    // Displays available facilities and their details
    public static void facilityOptions() {
        System.out.println("\n--- Available Facilities ---");
        System.out.println("Note: There is an additional ₱500.00 charge for every guest exceeding the maximum capacity.\n");


        System.out.println("FACILITY        PRICE PER UNIT    MAXIMUM GUESTS");
        System.out.println("A. Single Room  ₱1,500.00          2 persons");
        System.out.println("B. Double       ₱2,000.00          3 persons");
        System.out.println("C. King         ₱3,000.00          4 persons");
        System.out.println("D. Suite        ₱4,000.00          6 persons");
        System.out.println();
    }


    private static void facilityInfo(String facilityName, int guestNo,
                                     double basePrice, double additional, double totalAmount, int facilityNo) {
        // Add facility details to the StringBuilder
        facilityDetails.append("Facility #").append(facilityNo).append(": ").append(facilityName).append("\n");
        facilityDetails.append("Guests: ").append(guestNo).append("\n");
        facilityDetails.append("Base Price: ₱").append(String.format("%.2f", basePrice)).append("\n");


        // Add additional charges if any
        if (additional > 0) {
            facilityDetails.append("Additional Charges for Extra Guests: ₱")
                    .append(String.format("%.2f", additional))
                    .append("\n");
        } else {
            facilityDetails.append("Additional Charges: ₱0.00\n");
        }


        // Add meal options
        facilityDetails.append("Meal Options:\n");


        // Get meal selection and calculate cost
        System.out.println("\nPlease select meal options for Facility #" + facilityNo + " (for " + guestNo + " guests):");
        double food = mealOptions(guestNo); // <-- FIX #4: Pass guestNo for this room
        totalAmount += food;
        foodTotal += food; // Add to overall food total


        // This line was incorrect before, it should be the subtotal
        facilityDetails.append("Sub-Total Amount: ₱")
                .append(String.format("%.2f", totalAmount))
                .append("\n\n");


        System.out.println("Facility #" + facilityNo + " details recorded successfully.");
    }


    // displays meal options and calculates meal expense
    public static double mealOptions(int guestsForThisRoom) { // <-- FIX #5: Accept guestNo
        while(true){
            System.out.println("\n--- Meal Options ---");
            System.out.println("Please select a meal package for your stay:");
            System.out.println("A. Breakfast Only: FREE");
            System.out.println("B. Breakfast + Lunch: ₱250 per person");
            System.out.println("C. Breakfast + Dinner: ₱350 per person");
            System.out.print("Enter your choice (A/B/C): ");


            char foodOption;
            try {
                String input = sc.next().toUpperCase().trim();
                sc.nextLine(); // Clear the buffer


                if (input.isEmpty()) {
                    System.out.println("Invalid option. Defaulting to Breakfast Only (FREE).");
                    facilityDetails.append("Breakfast: FREE\n");
                    return 0;
                }


                foodOption = input.charAt(0);
            } catch (Exception e) {
                System.out.println("Invalid input. Defaulting to Breakfast Only (FREE).");
                sc.nextLine(); // Clear the buffer
                facilityDetails.append("Breakfast: FREE\n");
                return 0;
            }


            // Meal cost
            int lunchCost = 250;
            int dinnerCost = 350;
            double total_foodCost = 0;


            // Calculate cost based on selection


            switch (foodOption) {
                case 'A': {
                    System.out.println("Meal availed: Breakfast (FREE)");
                    facilityDetails.append("Breakfast: FREE\n");
                    return 0;
                }
                case 'B': {
                    total_foodCost = guestsForThisRoom * lunchCost; // <-- FIX #6: Use guestsForThisRoom
                    System.out.println("Meal availed: Breakfast + Lunch");
                    System.out.printf("Lunch cost: ₱%d × %d guests = ₱%.2f\n", lunchCost, guestsForThisRoom, total_foodCost);
                    facilityDetails.append("Breakfast: FREE\n");
                    facilityDetails.append(String.format("Lunch: ₱%.2f\n", total_foodCost));
                    return total_foodCost;
                }
                case 'C': {
                    total_foodCost = guestsForThisRoom * dinnerCost; // <-- FIX #7: Use guestsForThisRoom
                    System.out.println("Meal availed: Breakfast + Dinner");
                    System.out.printf("Dinner cost: ₱%d × %d guests = ₱%.2f\n", dinnerCost, guestsForThisRoom, total_foodCost);
                    facilityDetails.append("Breakfast: FREE\n");
                    facilityDetails.append(String.format("Dinner: ₱%.2f\n", total_foodCost));
                    return total_foodCost;
                }
                default: {
                    System.out.println("Invalid option. Please try again.");
                    facilityDetails.append("Breakfast: FREE\n");
                }
            }
        }
    }




    // save reservation to RESERVE.txt
    private static void saveReserveToFile(Guest currentGuest) throws IOException { // <-- FIX #8: Accept Guest
        // Generate a new transaction ID
        String transID = "TID-" + (int)(Math.random() * 900000 + 100000);


        try {
            // Open file for appending
            File file = new File(RESERVE_FILE);
            FileWriter fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);


            // Write reservation header
            pw.println("========================================================");
            pw.println("Transaction ID: " + transID); // <-- Added Transaction ID
            pw.println("Client ID: " + currentGuest.getID()); // <-- Use Guest object
            pw.println("Name: " + currentGuest.getFullName() ); // <-- Use Guest object
            pw.println();


            // Write reservation dates
            pw.println("Check-in Date: " + reserveDate);
            pw.println("Check-out Date: " + checkOutDate);
            pw.println();


            // Write facility information
            pw.println("No. of facilities reserved: " + facilityNo);
            pw.println();
            pw.println(facilityDetails.toString());


            // Write summary
            pw.println("SUMMARY:");
            pw.println("Total Guests: " + totalGuests);
            pw.println(String.format("Facility Charges: ₱%.2f", grandTotal));
            pw.println(String.format("Meal Charges: ₱%.2f", foodTotal));
            pw.println(); // Add space before payment


            // Write payment details
            pw.println(paymentDetails.toString());


            // Write final total and closing line
            pw.println(String.format("Final Total Amount: ₱%.2f", finalTotal));
            pw.println("========================================================");
            pw.println(); // Add extra space after entry


            // Close the file
            pw.close();


            // System.out.println("\nReservation details saved successfully."); // Moved this to guest_reserve


        } catch (IOException e) {
            System.err.println("Error saving reservation: " + e.getMessage());
            throw e; // Re-throw to be handled by caller
        }
    }

    
    // --- CHANGE 4: Removed the empty local methods ---
    /*
    private static void view_reserve(Guest loggedInUser) {
    }
    private static void request_cancellation(Guest loggedInUser){
    }
    */
}