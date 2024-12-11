package mop.app.client.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"user\"", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    private String username;
    private String email;
    private String password;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "birth_date")
    private Date birthDate;
    private String gender;
    private String address;
    private String avatar;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_banned")
    private boolean isBanned;

    @Column(name = "role_id")
    private long roleID;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public UserDTO(UserDTO userDTO) {
        this.userId = userDTO.userId;
        this.username = userDTO.username;
        this.email = userDTO.email;
        this.password = userDTO.password;
        this.displayName = userDTO.displayName;
        this.birthDate = userDTO.birthDate;
        this.gender = userDTO.getGender();
        this.address = userDTO.address;
        this.avatar = userDTO.avatar;
        this.isActive = userDTO.isActive;
        this.isBanned = userDTO.isBanned;
        this.roleID = userDTO.roleID;
        this.createdAt = userDTO.createdAt;
    }

    public UserDTO(String username, String email, String displayName) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
    }

    public boolean getIsBanned() {
        return isBanned;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setIsBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    @Transient
    private String status;

    public String getStatus() {
        if (status != null) return status;
        return "Deleted User".equals(getDisplayName())
            ? "Deleted"
            : "Active";
    }
}