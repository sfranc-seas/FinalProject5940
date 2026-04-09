package edu.upenn.cit5940.processor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.upenn.cit5940.common.dto.Article;

public class ArticleDateService {

    private final TreeMap<LocalDate, List<Article>> articlesByDate;

    public ArticleDateService(List<Article> articles) {
        this.articlesByDate = new TreeMap<>();
        indexArticles(articles);
    }

    private void indexArticles(List<Article> articles) {
        if (articles == null) {
            return;
        }

        for (Article article : articles) {
            try {
                LocalDate date = article.getPublishDate();
                articlesByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(article);
            } catch (Exception e) {
                // skip bad article dates
            }
        }
    }

    public boolean isValidDate(String dateText) {
        try {
            LocalDate.parse(dateText);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public List<String> getArticleTitlesInRange(String startDateText, String endDateText) {
        List<String> result = new ArrayList<>();

        if (!isValidDate(startDateText) || !isValidDate(endDateText)) {
            return result;
        }

        LocalDate startDate = LocalDate.parse(startDateText);
        LocalDate endDate = LocalDate.parse(endDateText);

        if (startDate.isAfter(endDate)) {
            return result;
        }

        Map<LocalDate, List<Article>> range = articlesByDate.subMap(startDate, true, endDate, true);

        for (Map.Entry<LocalDate, List<Article>> entry : range.entrySet()) {
            for (Article article : entry.getValue()) {
                result.add(article.getTitle());
            }
        }

        return result;
    }
}