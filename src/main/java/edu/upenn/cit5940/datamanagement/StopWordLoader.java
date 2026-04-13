package edu.upenn.cit5940.datamanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import edu.upenn.cit5940.logging.Logger;

public class StopWordLoader {
	
	private final Logger logger;
	public StopWordLoader(Logger logger)
	{
		this.logger = logger;
	}
    public Set<String> loadStopWords(String filePath) {
        Set<String> stopWords = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();

                if (!word.isEmpty()) {
                    stopWords.add(word);
                }
            }
        } catch (IOException e) {
            //Add logs for error
        	logger.logWarning("Failed to load stop words file: " + filePath);
        }

        return stopWords;
    }
}