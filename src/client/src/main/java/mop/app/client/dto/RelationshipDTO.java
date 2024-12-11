package mop.app.client.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"relationship\"", schema = "public")
@IdClass(PkRelationship.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelationshipDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id1")
    private long userId1;

    @Id
    @Column(name = "user_id2")
    private long userId2;

    @Column(name = "created_at")
    private Timestamp createdAt;

    private long status;
}
