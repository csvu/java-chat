package mop.app.client.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GeneratedColumn;

@Entity
@Table(name = "\"report\"", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private long reportId;

    @Column(name = "user_id1")
    private long userId1;

    @Column(name = "user_id2")
    private long userId2;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public ReportDTO(ReportDTO reportDTO) {
        this.reportId = reportDTO.reportId;
        this.userId1 = reportDTO.userId1;
        this.userId2 = reportDTO.userId2;
        this.createdAt = reportDTO.createdAt;
    }
}
