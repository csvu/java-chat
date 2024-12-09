package mop.app.client.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"enrollment\"", schema = "public")
@IdClass(PkEnrollment.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id")
    private long userId;

    @Id
    @Column(name = "conversation_id")
    private long conversationId;

    @Column(name = "role_id")
    private long roleId;
}