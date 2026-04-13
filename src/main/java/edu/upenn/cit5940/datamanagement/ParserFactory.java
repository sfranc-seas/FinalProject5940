package edu.upenn.cit5940.datamanagement;

import edu.upenn.cit5940.logging.Logger;

public class ParserFactory {

	private final Logger logger;
	
	public ParserFactory(Logger logger)
	{
		this.logger = logger;
	}
    public Parser getParser(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Data file path cannot be null or empty.");
        }

        String lower = filePath.toLowerCase();

        if (lower.endsWith(".csv")) {
            return new CSVParser(logger);
        }

        if (lower.endsWith(".json")) {
            return new JSONParser(logger);
        }

        throw new IllegalArgumentException("Unsupported file format. Use .csv or .json");
    }
}