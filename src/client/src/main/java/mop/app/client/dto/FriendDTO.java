package mop.app.client.dto;


import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDTO {
    private String username;
    private String email;
    private String displayName;
    private Timestamp createdAt;
}
