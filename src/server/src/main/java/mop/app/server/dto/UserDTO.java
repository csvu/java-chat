package mop.app.server.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
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
    private int roleID;

    @Column(name = "created_at")
    private Date createdAt;
}
