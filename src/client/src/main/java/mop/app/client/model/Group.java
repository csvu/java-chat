package mop.app.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    private long id;
    private String name;
    private String creationDate;
    private int memberCount;
    private int adminCount;
}