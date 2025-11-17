import java.util.*;


public class Main {
    static Scanner scn = new Scanner(System.in);
    
    public static void main(String[] args) {
        
        // 1. Create the file handler
        UserFileHandler fileHandler = new UserFileHandler();

        // 2. Load all users from USERS.txt
        ArrayList<User> userAccounts = fileHandler.loadUsers();

        
        if (userAccounts.isEmpty()) {
            System.out.println("No users file found. Creating default accounts...");
            
            // Create default accounts
            Manager m = new Manager("JazMasungit@dlsl.edu.ph","password123",0001,"Justine Margaret Candelaria","Sa ilalim ng ferris wheel ng ek",1,"0986754432", 1000);
            Reception r = new Reception("LuisMatthew@dlsl.edu.ph", "password1234", 0002, "Luis Matthew Quinton", "Malapit sa bahay ni raviza", 2, "09984365", 2000);
            
            // Add to in-memory list
            userAccounts.add(m);
            userAccounts.add(r);
            
            // Save them to the file for next time
            fileHandler.saveNewUser(m);
            fileHandler.saveNewUser(r);
            System.out.println("Default accounts created in USERS.txt.");
        }

        // 4. Pass the handler to the Authenticator
        Authenticator authenticator = new Authenticator(fileHandler);
        
        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            // The authenticator will loop until a user is successfully logged in
            User loggedInUser = authenticator.authenticator(userAccounts);

            if (loggedInUser instanceof Guest){
                System.out.println("Redirecting to Guest Portal...");
                clientMenu.guestMenu((Guest) loggedInUser);
                
            } else if(loggedInUser instanceof Reception){
                // --- THIS IS THE CHANGE ---
                // We now pass the 'userAccounts' list to the menu
                receptionist_menu.receptionistMenu((Reception) loggedInUser, userAccounts);
                
            } else if(loggedInUser instanceof Manager){
                System.out.println("Welcome, Manager " + ((Manager) loggedInUser).getFullName() + "!");
                System.out.println("Manager menu is not implemented yet.");
            }

            System.out.println("Logging out... Returning to main login screen.");
        }
    }
}