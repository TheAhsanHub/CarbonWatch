package server;

import common.CO2Reading;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.locks.ReentrantLock;

public class DataLogger {
    private final String csvFilePath;
    private final ReentrantLock lock;

    public DataLogger(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        this.lock = new ReentrantLock();
        initializeCSVFile();
    }

    private void initializeCSVFile() {
        lock.lock();
        try {
            File file = new File(csvFilePath);
            if (!file.exists()) {
                // Create directory if it doesn't exist
                file.getParentFile().mkdirs();

                // Write CSV header
                try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                    writer.println("Timestamp,UserID,Postcode,CO2_ppm");
                }
                System.out.println("Created new CSV file: " + csvFilePath);
            }
        } catch (IOException e) {
            System.err.println("Error initializing CSV file: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public boolean logReading(CO2Reading reading) {
        lock.lock();
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath, true))) {
            writer.println(reading.toCSVFormat());
            writer.flush();
            System.out.println("Logged reading: " + reading);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }
    }

    public String getFilePath() {
        return csvFilePath;
    }
}