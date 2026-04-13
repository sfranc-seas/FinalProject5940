package edu.upenn.cit5940.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.datamanagement.ArticleRepository;
import edu.upenn.cit5940.datamanagement.InvertedIndex;

public class SearchService {

    private final InvertedIndex invertedIndex;
    private final ArticleRepository articleRepository;

    public SearchService(InvertedIndex invertedIndex, ArticleRepository articleRepository) {
        this.invertedIndex = invertedIndex;
        this.articleRepository = articleRepository;
    }

    public List<String> searchTitles(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Article> results = invertedIndex.searchAll(query);
        List<Article> sortedArticles = new ArrayList<>(results);

        Collections.sort(sortedArticles, Comparator.comparing(Article::getTitle, String.CASE_INSENSITIVE_ORDER));

        List<String> titles = new ArrayList<>();
        for (Article article : sortedArticles) {
            titles.add(article.getTitle());
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