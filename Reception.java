public class Reception extends User {

    private String fullName, adress, contactNumber;
    private int age, employeeID;

    public Reception(String email, String password, int userID, String fullName, String adress, int age, String contactNumber, int employeeID){
        super(email, password, userID);

        this.fullName = fullName;
        this.adress = adress;
        this.age=age;
        this.contactNumber = contactNumber;
        this.employeeID = employeeID;

    }
    
}
