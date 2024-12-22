package mop.app.client.dto;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityDTO {
    private String username;
    private String displayName;
    private Timestamp createdAt;
    private long amountOpenTime;
    private long amountChatWithFriend;
    private long amountChatWithGroup;
}
