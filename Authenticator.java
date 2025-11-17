import java.util.*;
import java.util.regex.*;
import java.io.*; // Import for file handling

public class Authenticator {
    static private Scanner scn = new Scanner(System.in);

    // --- NEW ---
    // A private variable to hold the file handler
    private UserFileHandler fileHandler;

    // --- NEW CONSTRUCTOR ---
    // This receives the file handler from Main.java
    public Authenticator(UserFileHandler handler) {
        this.fileHandler = handler;
    }
    // --- END NEW ---

    public User authenticator(ArrayList<User> allUsers) {
        while (true) {
            System.out.println("--- Welcome to the Hotel ---");
            System.out.println("1. Log In");
            System.out.println("2. Register (Guests only)");
            System.out.print("Please choose an option: ");

            String decision = scn.nextLine();

            if (decision.equals("1")) {
                User loggedInUser = logIn(allUsers);
                if (loggedInUser != null) {
                    return loggedInUser;
                }                
            } else if (decision.equals("2")) {
                signUp(allUsers);
                
            } else {
                System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private User logIn(ArrayList<User> allUsers) {
        System.out.println("--- User Log In ---");
        System.out.print("Email: ");
        String email = scn.nextLine();
        System.out.print("Password: ");
        String password = scn.nextLine();

        
        for (User user : allUsers) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                System.out.println("Login successful! Welcome, " + user.getEmail());
                return user; 
            }
        }

        System.out.println("Error: Invalid email or password.");
        return null; 
    }

    private void signUp(ArrayList<User> allUsers) {
        System.out.println("--- New Guest Registration ---");

        Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        Pattern passwordPattern = Pattern.compile("^.{8,}$");

        String email;
        while (true) {
            System.out.print("Enter your Email: ");
            email = scn.nextLine();
            
            if (!emailPattern.matcher(email).matches()) {
                System.out.println("Invalid email format. Please try again.");
                continue;
            }

            boolean emailExists = false;
            for (User user : allUsers) {
                if (user.getEmail().equals(email)) {
                    emailExists = true;
                    break;
                }
            }

            if (emailExists) {
                System.out.println("This email is already registered. Please try logging in.");
                return; 
            } else {
                break; 
            }
        }

        String password;
        while (true) {
            System.out.println("Password must be at least 8 characters long.");
            System.out.print("Enter your Password: ");
            password = scn.nextLine();
            if (passwordPattern.matcher(password).matches()) {
                break;
            } else {
                System.out.println("Password is too short.");
            }
        }

        System.out.print("Enter Full Name: ");
        String fullName = scn.nextLine();
        System.out.print("Enter Address: ");
        String address = scn.nextLine();
        System.out.print("Enter Age: ");
        int age = scn.nextInt();
        scn.nextLine(); 
        System.out.print("Enter Contact Number: ");
        String contactNumber = scn.nextLine();

        int newUserID = (int)(Math.random() * 90000 + 10000); 

        Guest newGuest = new Guest(email, password, newUserID, fullName, address, age, contactNumber);

        // 1. Add to the *in-memory* list for this session
        allUsers.add(newGuest);

        // 2. --- THIS IS THE SAVE ---
        // Save to the *physical file* for next time
        fileHandler.saveNewUser(newGuest);

        System.out.println("Registration successful! Your new UserID is " + newUserID);
        System.out.println("Please log in to continue.");
    }
}