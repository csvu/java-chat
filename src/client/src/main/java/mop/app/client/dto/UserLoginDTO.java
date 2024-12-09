package mop.app.client.dto;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    private String username;
    private String displayName;
    private String email;
    private Timestamp loginAt;

    public UserLoginDTO(UserDTO user, Timestamp loginTime) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.loginAt = loginTime;
    }
}
