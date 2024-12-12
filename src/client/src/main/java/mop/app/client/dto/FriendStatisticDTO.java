package mop.app.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendStatisticDTO {
    private String username;
    private String email;
    private String displayName;
    private long friendCount;
    private long friendsOfFriendsCount;
}
