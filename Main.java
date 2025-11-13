import java.util.*;
import java.io.*;

public class Main {
        static Scanner scn = new Scanner(System.in);
    public static void main(String[] args) {
        
        ArrayList<User> userAccounts = new ArrayList<>();

        Authenticator authenticator = new Authenticator();
        
        User loggedInUser = authenticator.authenticator(userAccounts);

        if (loggedInUser instanceof Guest){
            //lagay here side ng guest
        } else if(loggedInUser instanceof Reception){
            //lagay here side ng receptionist
        } else if(loggedInUser instanceof Manager){
            //lagay here side ng manager
        }
    }
}