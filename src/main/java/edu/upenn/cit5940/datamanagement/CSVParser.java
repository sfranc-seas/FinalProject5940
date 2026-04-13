package edu.upenn.cit5940.datamanagement;

import java.io.IOException;
import java.util.*;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.logging.Logger;

public class CSVParser implements Parser {
	
	private final Logger logger;

    public CSVParser(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public List<Article> parse(String filePath) {
        List<Article> articles = new ArrayList<>();

        try (CharacterReader reader = new CharacterReader(filePath)) {
        	
            ArticleCSVParser parser = new ArticleCSVParser(reader, logger);
            articles = parser.readAllArticles();  // now returns List<Article>
        } 
        catch (IOException | CSVFormatException e) 
        {
        	logger.logError("Failed to parse CSV file: " + filePath + ". Reason: " + e.getMessage());
            throw new RuntimeException("Error parsing CSV file", e);
            
        }

        return articles;
    }
}