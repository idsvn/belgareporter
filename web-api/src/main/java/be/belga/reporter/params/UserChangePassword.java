package be.belga.reporter.params;

import java.io.Serializable;

public class UserChangePassword implements Serializable {

    private static final long serialVersionUID = 4045757495351353417L;
    private String username;
    private String oldPassword;
    private String newPassword;

    public UserChangePassword() {
        super();
    }

    public UserChangePassword(String username, String oldPassword, String newPassword) {
        super();
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "UserChangePassword [username=" + username + ", oldPassword=" + oldPassword + ", newPassword=" + newPassword + "]";
    }
}
