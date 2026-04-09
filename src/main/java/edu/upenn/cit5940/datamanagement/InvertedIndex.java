package edu.upenn.cit5940.datamanagement;

import java.util.*;
import edu.upenn.cit5940.common.dto.Article;

public class InvertedIndex {

    private Map<String, Set<Article>> index;
    private Set<String> stopWords;

    public InvertedIndex() {
    	  this.index = new HashMap<>();
    	  this.stopWords = new HashSet<>();
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
    }
    
    public Set<Article> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new HashSet<>();
        }

        return index.getOrDefault(keyword.toLowerCase(), new HashSet<>());
    }
    
    public Set<Article> searchAll(String query) {
        if (query == null || query.isBlank()) {
            return new HashSet<>();
        }

        String[] tokens = query.toLowerCase().split("\\W+");
        Set<Article> result = null;

        for (String token : tokens) {
            if (token.length() <= 1) {
                continue;
            }

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
    	
        if (text == null || text.isBlank()) {
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