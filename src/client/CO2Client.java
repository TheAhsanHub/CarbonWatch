package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class CO2Client {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;

    private String host;
    private int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public CO2Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.scanner = new Scanner(System.in);
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to CO2 server at " + host + ":" + port);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            return false;
        }
    }

    public void start() {
        if (!connect()) {
            return;
        }

        try {
            // Read server messages and send responses
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("Server: " + serverMessage);

                // Check if we should end the session
                if (serverMessage.contains("Thank you") || serverMessage.contains("Connection closed")) {
                    break;
                }

                // Check if this is a request for input
                if (serverMessage.contains("Please enter")) {
                    String userInput = scanner.nextLine();
                    out.println(userInput);

                    // Allow user to quit
                    if ("QUIT".equalsIgnoreCase(userInput)) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Communication error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (scanner != null) scanner.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Parse command line arguments
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length >= 1) {
            host = args[0];
        }

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default: " + DEFAULT_PORT);
            }
        }

        // Validate port range
        if (port < 1 || port > 65535) {
            System.err.println("Port must be between 1 and 65535. Using default: " + DEFAULT_PORT);
            port = DEFAULT_PORT;
        }

        System.out.println("CO2 Client Configuration:");
        System.out.println("  Server: " + host);
        System.out.println("  Port: " + port);
        System.out.println("Type 'QUIT' at any prompt to disconnect\n");

        CO2Client client = new CO2Client(host, port);
        client.start();
    }
}