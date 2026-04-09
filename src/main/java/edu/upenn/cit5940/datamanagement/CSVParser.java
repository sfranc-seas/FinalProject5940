package edu.upenn.cit5940.datamanagement;

import java.io.IOException;
import java.util.*;

import edu.upenn.cit5940.common.dto.Article;

public class CSVParser implements Parser {

    @Override
    public List<Article> parse(String filePath) {
        List<Article> articles = new ArrayList<>();

        try (CharacterReader reader = new CharacterReader(filePath)) {
            ArticleCSVParser parser = new ArticleCSVParser(reader);
            articles = parser.readAllArticles();  // now returns List<Article>
        } catch (IOException | CSVFormatException e) {
            throw new RuntimeException("Error parsing CSV file", e);
        }

        return articles;
    }
}