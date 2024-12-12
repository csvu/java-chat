package mop.app.client.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Relationship { //null->group, status="N/A"->not friends
    private int id;
    private String userDisplayName;
    private String status;
}


