package mop.app.client.dto;

import java.io.Serial;
import java.io.Serializable;

public class PkRelationship implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long userId1;
    private long userId2;
}
