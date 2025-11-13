public class User {
    private String email;
    private String password;
    private int  ID;
    private Boolean isReception;
    private Boolean isManager;
    
    public void User(String email, String password, int ID){
        this.email = email;
        this.password = password;
        this.ID = ID;
        
    }
}
