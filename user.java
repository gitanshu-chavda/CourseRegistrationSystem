public abstract class user {
    private final String id;
    private String name;
    private String email;
    private String password;
    user(String id, String name, String email, String password){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }
    public String toString() {
        return String.format("[%s] %s (%s) — %s", getRole(), name, id, email);
    
}
}
