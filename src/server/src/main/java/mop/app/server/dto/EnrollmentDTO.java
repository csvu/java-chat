package mop.app.server.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "enrollment", schema = "public")
@IdClass(EnrollmentId.class)
@Builder
public class EnrollmentDTO implements Serializable {
    @Id
    @Column(name = "user_id")
    private int userId;
    @Id
    @Column(name = "conversation_id")
    private int conversationId;
}
