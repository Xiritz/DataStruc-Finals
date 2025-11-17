public class User {
    private String email;
    private String password;
    private int  userID;
    private String fullName;
    
    
    
    public User(String email, String password, int userID){
        this.email = email;
        this.password = password;
        this.userID = userID;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public int getID(){
        return userID;
    }
    
    public String getFullName(){
        return fullName;
    }
}
