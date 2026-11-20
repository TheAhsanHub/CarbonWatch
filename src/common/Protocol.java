package common;

public class Protocol {
    // Server messages
    public static final String WELCOME_MESSAGE = "Welcome to CO2 Monitoring System";
    public static final String REQUEST_USER_ID = "Please enter your User ID:";
    public static final String REQUEST_POSTCODE = "Please enter the postcode:";
    public static final String REQUEST_CO2 = "Please enter CO2 concentration (ppm):";
    public static final String SUCCESS_MESSAGE = "Data successfully recorded";
    public static final String ERROR_MESSAGE = "ERROR";
    public static final String GOODBYE_MESSAGE = "Thank you for your submission. Connection closed.";

    // Client responses
    public static final String DISCONNECT_COMMAND = "QUIT";

    // Validation patterns
    public static final String POSTCODE_PATTERN = "^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$";
}