public class Guest extends User {

    private String fullName, adress, contactNumber;
    private int age;
    
    
    public Guest(String email, String password, int userID, String fullName, String adress, int age, String contactNumber){
        super(email, password, userID);

        this.fullName = fullName;
        this.adress = adress;
        this.age=age;
        this.contactNumber = contactNumber;
    }
        public String getFullName(){
        return fullName;
        }

        public String getAdress() {
        return adress;
        }

        public int getAge() {
        return age;
        }

        public String getContactNumber() {
        return contactNumber;
        }

         public void setFullName(String fullName) {
        this.fullName = fullName;
        }

        public void setAdress(String adress) {
        this.adress = adress;
        }

        public void setAge(int age) {
        this.age = age;
        }

        public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
        }
}

