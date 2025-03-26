package models;

public class Admin extends User {
    private String role;
    private String department;

    public Admin(int id, String name, String username, String password, String email, 
                String phoneNumber, String role, String department) {
        super(id, name, username, password, email, phoneNumber);
        this.role = role;
        this.department = department;
    }

    public String getRole() { return role; }
    public String getDepartment() { return department; }

    @Override
    public void displayInfo() {
        System.out.println("Admin: " + getName() + " (" + role + " - " + department + ")");
    }
}