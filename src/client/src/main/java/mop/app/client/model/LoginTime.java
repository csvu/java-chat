package mop.app.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginTime {
    private String username;
    private String email;
    private String displayName;
    private String loginDate;
}
