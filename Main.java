import java.util.*;
import java.io.*;

public class Main {
        static Scanner scn = new Scanner(System.in);
    public static void main(String[] args) {
        
        ArrayList<User> allUsers = new ArrayList<>();
        allUsers.add(new Manager("JazMasungit@dlsl.edu.ph","password123",0001,"Justine Margaret Candelaria","Sa ilalim ng ferris wheel ng ek",1,"0986754432", 1000));
        allUsers.add(new Reception("LuisPogi@dlsl.edu.ph", "password1234", 0002, "Luis Matthew Quinton", "Malapit sa bahay ni raviza", 2, "09984365", 2000));
        Authenticator authenticator = new Authenticator();

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            // The authenticator will loop until a user is successfully logged in
            User loggedInUser = authenticator.authenticator(allUsers);

            // --- Filled in the 'if/else if' block ---
            // This now directs the user to the correct menu based on their object type
            if (loggedInUser instanceof Guest){
                System.out.println("Redirecting to Guest Portal...");
                // We "cast" the User object to a Guest object to pass it
                clientMenu.guestMenu((Guest) loggedInUser);
                
            } else if(loggedInUser instanceof Reception){
                receptionist_menu.receptionistMenu((Reception) loggedInUser);
                
            } else if(loggedInUser instanceof Manager){
                // Placeholder for Manager Menu
                System.out.println("Welcome, Manager !");
                System.out.println("Manager menu is not implemented yet.");
            }

            // After the menu methods finish (i.e., user logs out), we print this
            System.out.println("Logging out... Returning to main login screen.");
        }
    }
}