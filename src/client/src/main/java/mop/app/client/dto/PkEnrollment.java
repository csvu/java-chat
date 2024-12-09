package mop.app.client.dto;

import java.io.Serial;
import java.io.Serializable;

public class PkEnrollment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long userId;
    private long conversationId;
}
