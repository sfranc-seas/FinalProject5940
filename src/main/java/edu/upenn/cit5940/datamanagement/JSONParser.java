package edu.upenn.cit5940.datamanagement;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.logging.Logger;

public class JSONParser implements Parser {

    private static class JsonArticleRecord {
        String uri;
        String date;
        String title;
        String body;
    }

    private final Logger logger;

    public JSONParser(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public List<Article> parse(String filePath) {
        List<Article> articles = new ArrayList<>();

        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();

            List<JsonArticleRecord> records = gson.fromJson(
                    reader,
                    new TypeToken<List<JsonArticleRecord>>() {}.getType()
            );

            if (records == null) {
                return articles;
            }

            for (JsonArticleRecord record : records) {
                try {
                    Article article = new Article(
                            record.uri,
                            record.date,
                            record.title,
                            record.body
                    );
                    articles.add(article);
                } catch (IllegalArgumentException e) {
                    // skip malformed JSON record
                	logger.logWarning("Skipping malformed JSON record: missing required fields");
                }
            }

        } catch (IOException e) {
        	logger.logError("Failed to parse JSON file: " + filePath + ". Reason: " + e.getMessage());
            throw new RuntimeException("Failed to parse JSON file: " + filePath, e);
        }

        return articles;
    }
}