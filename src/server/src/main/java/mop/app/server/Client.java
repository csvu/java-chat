package mop.app.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@NoArgsConstructor
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private Socket clientSocket;
    private int port;
    private int userId;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Client(Socket clientSocket, int port, ObjectInputStream in, ObjectOutputStream out) {
        this.clientSocket = clientSocket;
        this.port = port;
        this.in = in;
        this.out = out;
    }

    public Client(Socket socket, int port) {
        this.clientSocket = socket;
        this.port = port;
    }

    public void close() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            logger.error("Error closing client socket: {}", e.getMessage());
        }
    }
}
