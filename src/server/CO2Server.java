package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CO2Server {
    private static final int DEFAULT_PORT = 8888;
    private static final String DEFAULT_CSV_FILE = "data/co2_readings.csv";
    private static final int MAX_CLIENTS = 4;

    private ServerSocket serverSocket;
    private DataLogger dataLogger;
    private ExecutorService clientThreadPool;
    private boolean isRunning;

    public CO2Server(int port, String csvFilePath) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.dataLogger = new DataLogger(csvFilePath);
        this.clientThreadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        this.isRunning = false;

        System.out.println("CO2 Server initialized:");
        System.out.println("  Port: " + port);
        System.out.println("  Data file: " + csvFilePath);
        System.out.println("  Max clients: " + MAX_CLIENTS);
    }

    public void start() {
        isRunning = true;
        System.out.println("CO2 Server started on port " + serverSocket.getLocalPort());

        try {
            while (isRunning) {
                // Accept client connection
                Socket clientSocket = serverSocket.accept();

                // Create and submit client handler to thread pool
                ClientHandler clientHandler = new ClientHandler(clientSocket, dataLogger);
                clientThreadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            if (isRunning) {
                System.err.println("Server error: " + e.getMessage());
            }
        } finally {
            stop();
        }
    }

    public void stop() {
        isRunning = false;
        clientThreadPool.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
        System.out.println("CO2 Server stopped");
    }

    public static void main(String[] args) {
        // Parse command line arguments
        int port = DEFAULT_PORT;
        String csvFile = DEFAULT_CSV_FILE;

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default: " + DEFAULT_PORT);
            }
        }

        if (args.length >= 2) {
            csvFile = args[1];
        }

        // Validate port range
        if (port < 1 || port > 65535) {
            System.err.println("Port must be between 1 and 65535. Using default: " + DEFAULT_PORT);
            port = DEFAULT_PORT;
        }

        try {
            CO2Server server = new CO2Server(port, csvFile);

            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down server...");
                server.stop();
            }));

            // Start the server
            server.start();

        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        }
    }
}