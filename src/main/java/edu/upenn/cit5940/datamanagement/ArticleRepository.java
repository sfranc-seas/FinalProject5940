package edu.upenn.cit5940.datamanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.cit5940.common.dto.Article;

public class ArticleRepository {

    private final List<Article> articles;
    private final Map<String, Article> articlesById;

    public ArticleRepository() {
        this.articles = new ArrayList<>();
        this.articlesById = new HashMap<>();
    }

    public void addArticle(Article article) {
        if (article == null) {
            return;
        }

        articles.add(article);
        articlesById.put(article.getId(), article);
    }

    public void addAllArticles(List<Article> articleList) {
        if (articleList == null) {
            return;
        }

        for (Article article : articleList) {
            addArticle(article);
        }
    }

    public List<Article> getAllArticles() {
        return new ArrayList<>(articles);
    }

    public Article getArticleById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        return articlesById.get(id);
    }

    public int size() {
        return articles.size();
    }
}
