package edu.upenn.cit5940.datamanagement;

import java.util.*;
import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.logging.Logger;

public class InvertedIndex {

    private Map<String, Set<Article>> index;
    private Set<String> stopWords;
    private final Logger logger;

    public InvertedIndex(Logger logger) {
    	  this.index = new HashMap<>();
    	  this.stopWords = new HashSet<>();
    	  this.logger = logger;
    }

    public void addArticle(Article article) {
        if (article == null) {
            return;
        }

        indexText(article, article.getTitle());
        indexText(article, article.getContent());
    }
    
    public void addAllArticles(List<Article> articles) {
        if (articles == null) {
            return;
        }

        for (Article article : articles) {
            addArticle(article);
        }

        logger.logInfo("InvertedIndex: index built from " + articles.size() + " article(s). Total unique terms: " + index.size());
    }
    
    public Set<Article> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new HashSet<>();
        }

        return index.getOrDefault(keyword.toLowerCase(), new HashSet<>());
    }
    
    public Set<Article> searchAll(String query) {
        if (query == null || query.isEmpty()) {
            return new HashSet<>();
        }

        String[] tokens = query.toLowerCase().split("\\W+");
        Set<Article> result = null;
        boolean hasUsableToken = false;

        for (String token : tokens) {
            if (token.length() <= 1 || stopWords.contains(token)) {
                continue;
            }

            hasUsableToken = true;

            Set<Article> current = index.get(token);
            if (current == null) {
                return new HashSet<>();
            }

            if (result == null) {
                result = new HashSet<>(current);
            } else {
                result.retainAll(current);
            }
        }

        if (!hasUsableToken) {
            return new HashSet<>();
        }

        return result == null ? new HashSet<>() : result;
    }
    
    public void setStopWords(Set<String> stopWords) {
        if (stopWords != null) {
            this.stopWords = stopWords;
        }
    }
    
    public int size() {
        return index.size();
    }

    private void indexText(Article article, String text) {
    	
        if (text == null || text.isEmpty()) {
            return;
        }

        String[] tokens = text.toLowerCase().split("\\W+");

        for (String token : tokens) {
        	if (token.length() <= 1 || stopWords.contains(token)) {
        	    continue;
        	}

            index.putIfAbsent(token, new HashSet<>());
            index.get(token).add(article);
        }
    }
}