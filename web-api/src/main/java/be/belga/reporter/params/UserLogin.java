package be.belga.reporter.params;

import java.io.Serializable;

public class UserLogin implements Serializable {
    private static final long serialVersionUID = -5673149932253855049L;

    private String username;
    private String password;
    private String email;

    public UserLogin() {
        super();
    }

    public UserLogin(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLogin [username=" + username + ", password=" + password + ", email=" + email + "]";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
