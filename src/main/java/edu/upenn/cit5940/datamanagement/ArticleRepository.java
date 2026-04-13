package edu.upenn.cit5940.datamanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.logging.Logger;

public class ArticleRepository {

    private final List<Article> articles;
    private final Map<String, Article> articlesById;
    private final Logger logger;

    public ArticleRepository(Logger logger) {
        this.articles = new ArrayList<>();
        this.articlesById = new HashMap<>();
        this.logger = logger;
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

        logger.logInfo("ArticleRepository: " + articles.size() + " article(s) loaded into repository");
    }

    public List<Article> getAllArticles() {
        return new ArrayList<>(articles);
    }

    public Article getArticleById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        return articlesById.get(id);
    }

    public int size() {
        return articles.size();
    }
}