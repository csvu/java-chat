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

    public static final String FRIEND = "FRIEND";
    public static final String PENDING = "PENDING";
    public static final String BLOCK = "BLOCK";
    public static final String NA = "N/A";
}


