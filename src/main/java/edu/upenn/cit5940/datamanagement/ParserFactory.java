package edu.upenn.cit5940.datamanagement;

public class ParserFactory {

    public Parser getParser(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Data file path cannot be null or empty.");
        }

        String lower = filePath.toLowerCase();

        if (lower.endsWith(".csv")) {
            return new CSVParser();
        }

        if (lower.endsWith(".json")) {
            return new JSONParser();
        }

        throw new IllegalArgumentException("Unsupported file format. Use .csv or .json");
    }
}