package edu.upenn.cit5940.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.datamanagement.ArticleRepository;
import edu.upenn.cit5940.datamanagement.InvertedIndex;
import edu.upenn.cit5940.logging.Logger;

public class SearchService {

    private final InvertedIndex invertedIndex;
    private final ArticleRepository articleRepository;
    private final Logger logger;

    public SearchService(InvertedIndex invertedIndex, ArticleRepository articleRepository, Logger logger) {
        this.invertedIndex = invertedIndex;
        this.articleRepository = articleRepository;
        this.logger = logger;
    }

    public List<String> searchTitles(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        logger.logInfo("Search query: \"" + query + "\"");

        Set<Article> results = invertedIndex.searchAll(query);
        List<Article> sortedArticles = new ArrayList<>(results);

        Collections.sort(sortedArticles, Comparator.comparing(Article::getTitle, String.CASE_INSENSITIVE_ORDER));

        List<String> titles = new ArrayList<>();
        for (Article article : sortedArticles) {
            titles.add(article.getTitle());
        }

        if (titles.isEmpty()) {
            logger.logWarning("Search query \"" + query + "\" returned no results");
        } else {
            logger.logInfo("Search query \"" + query + "\" returned " + titles.size() + " result(s)");
        }

        return titles;
    }

    public Article getArticleById(String id) {
        return articleRepository.getArticleById(id);
    }

    public int getTotalArticleCount() {
        return articleRepository.size();
    }
}