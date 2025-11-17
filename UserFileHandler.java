import java.io.*;
import java.util.ArrayList;

public class UserFileHandler {

    private static final String USERS_FILE = "USERS.txt";

    /**
     * Loads all user accounts from USERS.txt into an ArrayList.
     * This is called once when the program starts.
     */
    public ArrayList<User> loadUsers() {
        ArrayList<User> userList = new ArrayList<>();
        File file = new File(USERS_FILE);

        // If file doesn't exist, return an empty list.
        if (!file.exists()) {
            return userList; 
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue; // Skip epmty lines

                String[] data = line.split(",");
                if (data.length < 8) continue; // Skip corrupted lines
                
                String role = data[0];
                
                // Use the 'role' to decide which object to create
                switch (role) {
                    case "GUEST":
                        // ROLE,userID,email,password,fullName,address,age,contactNumber
                        userList.add(new Guest(
                                data[2], // email
                                data[3], // password
                                Integer.parseInt(data[1]), // userID
                                data[4], // fullName
                                data[5], // address
                                Integer.parseInt(data[6]), // age
                                data[7]  // contactNumber
                        ));
                        break;
                    case "MANAGER":
                        // ROLE,userID,email,password,fullName,address,age,contactNumber,employeeID
                         if (data.length < 9) continue; // Skip corrupted manager line
                        userList.add(new Manager(
                                data[2], // email
                                data[3], // password
                                Integer.parseInt(data[1]), // userID
                                data[4], // fullName
                                data[5], // address
                                Integer.parseInt(data[6]), // age
                                data[7], // contactNumber
                                Integer.parseInt(data[8]) // employeeID
                        ));
                        break;
                    case "RECEPTION":
                        // ROLE,userID,email,password,fullName,address,age,contactNumber,employeeID
                         if (data.length < 9) continue; // Skip corrupted reception line
                        userList.add(new Reception(
                                data[2], // email
                                data[3], // password
                                Integer.parseInt(data[1]), // userID
                                data[4], // fullName
                                data[5], // address
                                Integer.parseInt(data[6]), // age
                                data[7], // contactNumber
                                Integer.parseInt(data[8]) // employeeID
                        ));
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing data from USERS.txt. Check file for corruption.");
        }
        
        return userList;
    }

    /**
     * Appends a single new user to the end of USERS.txt.
     * This is called by the Authenticator during sign-up.
     * @param user The user object (Guest, Manager, etc.) to save.
     */
    public void saveNewUser(User user) {
        // We use 'true' to append to the file
        try (FileWriter fw = new FileWriter(USERS_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            
            // This calls the 'toCSVString()' method of the *specific* object
            pw.println(user.toCSVString());
            
        } catch (IOException e) {
            System.err.println("Error saving new user: " + e.getMessage());
        }
    }
}