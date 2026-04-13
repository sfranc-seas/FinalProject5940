package edu.upenn.cit5940.datamanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StopWordLoader {

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
        }

        return stopWords;
    }
}