public class User {
    private String email;
    private String password;
    private int  userID;
    
    
    
    public User(String email, String password, int userID){
        this.email = email;
        this.password = password;
        this.userID = userID;
    }

    public String getMail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public int getID(){
        return userID;
    }
}
