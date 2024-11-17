package mop.app.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spam {
    private long id;
    private String userReported;
    private String reportedBy;
    private String reason;
    private String reportDate;
}
