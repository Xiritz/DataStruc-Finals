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

    public String getFullName(){
        return this.fullName;
    }
    
    // Added getters for new fields so CSV can read them
    public String getAdress() {
        return adress;
    }
    public int getAge() {
        return age;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public int getEmployeeID() {
        return employeeID;
    }

    // --- NEW METHOD IMPLEMENTATION ---
    @Override
    public String toCSVString() {
        // Format: ROLE,userID,email,password,fullName,address,age,contactNumber,employeeID
        return "RECEPTION" + "," + getID() + "," + getEmail() + "," + getPassword() + "," +
               getFullName() + "," + getAdress() + "," + getAge() + "," + 
               getContactNumber() + "," + getEmployeeID();
    }
}