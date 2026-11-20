package server;

import common.CO2Reading;
import common.Protocol;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final DataLogger dataLogger;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, DataLogger dataLogger) {
        this.clientSocket = socket;
        this.dataLogger = dataLogger;
    }

    @Override
    public void run() {
        try {
            // Set up communication streams
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

            // Send welcome message
            out.println(Protocol.WELCOME_MESSAGE);

            // Collect data from client
            CO2Reading reading = collectData();
            if (reading != null) {
                // Log the reading
                boolean success = dataLogger.logReading(reading);
                if (success) {
                    out.println(Protocol.SUCCESS_MESSAGE);
                } else {
                    out.println(Protocol.ERROR_MESSAGE + ": Failed to save data");
                }
            }

            // Send goodbye message
            out.println(Protocol.GOODBYE_MESSAGE);

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private CO2Reading collectData() throws IOException {
        try {
            // Get User ID
            out.println(Protocol.REQUEST_USER_ID);
            String userId = in.readLine();
            if (userId == null || userId.equalsIgnoreCase(Protocol.DISCONNECT_COMMAND)) {
                return null;
            }

            // Get Postcode
            out.println(Protocol.REQUEST_POSTCODE);
            String postcode = in.readLine();
            if (postcode == null || postcode.equalsIgnoreCase(Protocol.DISCONNECT_COMMAND)) {
                return null;
            }

            // Get CO2 concentration
            out.println(Protocol.REQUEST_CO2);
            String co2Input = in.readLine();
            if (co2Input == null || co2Input.equalsIgnoreCase(Protocol.DISCONNECT_COMMAND)) {
                return null;
            }

            // Validate and create reading
            double co2Value = Double.parseDouble(co2Input);
            CO2Reading reading = new CO2Reading(userId, postcode, co2Value);
            return reading;

        } catch (NumberFormatException e) {
            out.println(Protocol.ERROR_MESSAGE + ": Invalid CO2 concentration format");
            return null;
        } catch (IllegalArgumentException e) {
            out.println(Protocol.ERROR_MESSAGE + ": " + e.getMessage());
            return null;
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }
}