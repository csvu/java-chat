package mop.app.client.dto;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendsOfFriendsDTO {
    private String username;
    private String email;
    private String displayName;
    private String directFriend;
    private Timestamp createdAt;
}
