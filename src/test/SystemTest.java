package test;

import common.CO2Reading;
import server.CO2Server;
import client.CO2Client;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SystemTest {

    public static void testCO2ReadingValidation() {
        System.out.println("=== Testing CO2Reading Validation ===");

        // Test valid reading
        try {
            CO2Reading valid = new CO2Reading("RES001", "SW1A 1AA", 415.5);
            System.out.println("✓ Valid reading created: " + valid);
        } catch (Exception e) {
            System.out.println("✗ Valid reading failed: " + e.getMessage());
        }

        // Test invalid user ID
        try {
            CO2Reading invalid = new CO2Reading("", "SW1A 1AA", 415.5);
            System.out.println("✗ Empty user ID should have failed");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Empty user ID correctly rejected: " + e.getMessage());
        }

        // Test invalid postcode
        try {
            CO2Reading invalid = new CO2Reading("RES001", "INVALID", 415.5);
            System.out.println("✗ Invalid postcode should have failed");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Invalid postcode correctly rejected: " + e.getMessage());
        }

        // Test invalid CO2 value
        try {
            CO2Reading invalid = new CO2Reading("RES001", "SW1A 1AA", 100.0);
            System.out.println("✗ Low CO2 value should have failed");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Low CO2 value correctly rejected: " + e.getMessage());
        }

        System.out.println();
    }

    public static void testCSVFunctionality() {
        System.out.println("=== Testing CSV Functionality ===");

        try {
            // Test toCSVFormat
            CO2Reading reading = new CO2Reading("RES001", "SW1A 1AA", 415.5);
            String csv = reading.toCSVFormat();
            System.out.println("✓ CSV format: " + csv);

            // Test fromCSV (this would need a properly formatted timestamp)
            // CO2Reading fromCSV = CO2Reading.fromCSV(csv);
            // System.out.println("✓ From CSV: " + fromCSV);

        } catch (Exception e) {
            System.out.println("✗ CSV test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("CO2 Monitoring System Tests\n");

        testCO2ReadingValidation();
        testCSVFunctionality();

        System.out.println("Note: For full system testing, run the server and multiple clients separately.");
        System.out.println("To test the system:");
        System.out.println("1. Run: java server.CO2Server [port] [csvfile]");
        System.out.println("2. Run in separate terminals: java client.CO2Client [host] [port]");
    }
}