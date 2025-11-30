package Main.domain;

public class User {
    private Integer id;
    private String email;
    private String password;
    private String name;
    private String role;

    // Конструкторы
    public User() {}

    public User(String email, String password, String name, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public User(Integer id, String email, String password, String name, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // Геттеры и сеттеры
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return String.format("User{id=%d, email='%s', name='%s', role='%s'}",
                id, email, name, role);
    }

    // Вспомогательные методы для проверки ролей
    public boolean isClient() {
        return "CLIENT".equalsIgnoreCase(role);
    }

    public boolean isPharmacist() {
        return "PHARMACIST".equalsIgnoreCase(role);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}