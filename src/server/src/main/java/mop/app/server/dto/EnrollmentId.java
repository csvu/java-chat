package mop.app.server.dto;

import java.io.Serializable;
import java.util.Objects;

public class EnrollmentId implements Serializable {
    private int userId;
    private int conversationId;

    // Default constructor
    public EnrollmentId() {}

    public EnrollmentId(int userId, int conversationId) {
        this.userId = userId;
        this.conversationId = conversationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentId that = (EnrollmentId) o;
        return userId == that.userId && conversationId == that.conversationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, conversationId);
    }
}
