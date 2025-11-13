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
        public String getFullName(){
        return fullName;
        }

        public String getAdress() {
        return adress;
        }

        public int getAge() {
        return age;
        }

        public int getContactNumber() {
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

        public void setContactNumber(int contactNumber) {
        this.contactNumber = contactNumber;
        }
}

