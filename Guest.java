public class Guest extends User {

    private String fullName, adress;
    private int age, contactNumber;
    
    
    public Guest(String email, String password, int userID, String fullName, String adress, int age, int contactNumber){
        super(email, password, userID);

        this.fullName = fullName;
        this.adress = adress;
        this.age=age;
        this.contactNumber = contactNumber;

    }
}
