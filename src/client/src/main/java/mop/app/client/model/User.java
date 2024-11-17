package mop.app.client.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    private String username;
    private String email;
    private String displayName;
    private String gender;
    private String birthday;
    private String address;
    private String createdAt;
    private boolean admin;

    public User(String username, String email, String displayName, String birthday) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.birthday = birthday;
    }

    public User(String username, String email, String displayName, String birthday, String createdAt) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.birthday = birthday;
        this.createdAt = createdAt;
    }

    public User(String username, String email, String displayName, String birthday, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.birthday = birthday;
        this.admin = isAdmin;
    }

    public User(String username, String email, String displayName, String birthday, String gender, String address) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.birthday = birthday;
        this.gender = gender;
        this.address = address;
    }

    public User(int id, String username, String email, String displayName, String birthday, String gender,
                String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.birthday = birthday;
        this.gender = gender;
        this.address = address;
    }

    public User(String username, String email, String displayName) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
    }
}