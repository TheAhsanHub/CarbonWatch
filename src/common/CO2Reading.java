package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CO2Reading implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String postcode;
    private double co2Concentration;
    private LocalDateTime timestamp;

    // Validation constants
    private static final double MIN_CO2 = 250.0;
    private static final double MAX_CO2 = 5000.0;
    private static final String POSTCODE_REGEX = "^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$";

    public CO2Reading(String userId, String postcode, double co2Concentration) {
        setUserId(userId);
        setPostcode(postcode);
        setCo2Concentration(co2Concentration);
        this.timestamp = LocalDateTime.now();
    }

    public CO2Reading(String userId, String postcode, double co2Concentration, LocalDateTime timestamp) {
        this.userId = userId;
        this.postcode = postcode;
        this.co2Concentration = co2Concentration;
        this.timestamp = timestamp;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getPostcode() { return postcode; }
    public double getCo2Concentration() { return co2Concentration; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Setters with validation
    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        this.userId = userId.trim();
    }

    public void setPostcode(String postcode) {
        if (postcode == null || postcode.trim().isEmpty()) {
            throw new IllegalArgumentException("Postcode cannot be null or empty");
        }
        String cleanedPostcode = postcode.trim().toUpperCase();
        if (!cleanedPostcode.matches(POSTCODE_REGEX)) {
            throw new IllegalArgumentException("Invalid postcode format: " + postcode);
        }
        this.postcode = cleanedPostcode;
    }

    public void setCo2Concentration(double co2Concentration) {
        if (co2Concentration < MIN_CO2 || co2Concentration > MAX_CO2) {
            throw new IllegalArgumentException(
                    String.format("CO2 concentration must be between %.1f and %.1f ppm", MIN_CO2, MAX_CO2)
            );
        }
        this.co2Concentration = co2Concentration;
    }

    public String toCSVFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s,%s,%s,%.2f",
                timestamp.format(formatter),
                userId,
                postcode,
                co2Concentration
        );
    }

    public static CO2Reading fromCSV(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid CSV format");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime timestamp = LocalDateTime.parse(parts[0], formatter);
            String userId = parts[1];
            String postcode = parts[2];
            double co2 = Double.parseDouble(parts[3]);

            return new CO2Reading(userId, postcode, co2, timestamp);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse CSV line: " + csvLine, e);
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("CO2Reading{timestamp=%s, userId='%s', postcode='%s', co2=%.2fppm}",
                timestamp.format(formatter), userId, postcode, co2Concentration);
    }
}