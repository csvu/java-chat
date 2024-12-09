package mop.app.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import mop.app.server.dao.AuthDAO;
import mop.app.server.util.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int PORT = 8888;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    public static List<ClientHandler> clients;

    public static String getThisIP() {
        String ip = null;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("1.1.1.1", 53));
            ip = socket.getLocalAddress().getHostAddress();
            socket.close();
        } catch (IOException e) {
            logger.error("Failed to get IP address: {}", e.getMessage());
        }
        return ip;
    }

    public static void main(String[] args) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.close();
            AuthDAO authDAO = new AuthDAO();
            logger.info("User: {}", authDAO.getUerById(1L));

        } catch (Exception e) {
            logger.error("Error creating SessionFactory: {}", e.getMessage());
            throw new ExceptionInInitializerError(e);
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server Socket: {}", serverSocket);
            clients = new ArrayList<>();
            logger.info("Server started on IP: {}, Port: {}", getThisIP(), PORT);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();

                Future<String> future = threadPool.submit(() -> {
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    threadPool.execute(clientHandler);
                }, "Client connected: " + clientSocket.getInetAddress());

                try {
                    String threadInfo = future.get();
                    logger.info("Thread info: {}", threadInfo);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error getting thread info", e);
                }

                // Future<?> future = threadPool.submit(new ClientHandler(clientSocket), null);
            }

//            new Thread(() -> {
//                while (!serverSocket.isClosed()) {
//                    try {
//                        Socket clientSocket = serverSocket.accept();
//                        logger.info("New client connected: " + clientSocket.getInetAddress());
//                        ClientHandler clientHandler = new ClientHandler(clientSocket);
//                        clientHandler.start();
//                    } catch (IOException e) {
//                        logger.error("Error accepting client connection: {}", e.getMessage());
//                    }
//                }
//            }).start();

        } catch (IOException e) {
            logger.error("Server error: {}", e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}