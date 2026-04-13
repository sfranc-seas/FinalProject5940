package edu.upenn.cit5940.processor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.logging.Logger;

public class ArticleDateService {

    private final TreeMap<LocalDate, List<Article>> articlesByDate;
    private final Logger logger;

    public ArticleDateService(List<Article> articles, Logger logger) {
        this.articlesByDate = new TreeMap<>();
        this.logger = logger;
        indexArticles(articles);
    }

    private void indexArticles(List<Article> articles) {
        if (articles == null) {
            return;
        }

        int skipped = 0;
        for (Article article : articles) {
            try {
                LocalDate date = article.getPublishDate();
                articlesByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(article);
            } catch (Exception e) {
                skipped++;
            }
        }

        if (skipped > 0) {
            logger.logWarning("ArticleDateService: skipped " + skipped + " article(s) with unparseable dates during indexing");
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
            logger.logWarning("Date range query rejected: invalid date(s) — start=\"" + startDateText + "\" end=\"" + endDateText + "\"");
            return result;
        }

        LocalDate startDate = LocalDate.parse(startDateText);
        LocalDate endDate = LocalDate.parse(endDateText);

        if (startDate.isAfter(endDate)) {
            logger.logWarning("Date range query rejected: start date " + startDateText + " is after end date " + endDateText);
            return result;
        }

        logger.logInfo("Date range query: " + startDateText + " to " + endDateText);

        Map<LocalDate, List<Article>> range = articlesByDate.subMap(startDate, true, endDate, true);

        for (Map.Entry<LocalDate, List<Article>> entry : range.entrySet()) {
            for (Article article : entry.getValue()) {
                result.add(article.getTitle());
            }
        }

        if (result.isEmpty()) {
            logger.logWarning("Date range query [" + startDateText + " to " + endDateText + "] returned no results");
        } else {
            logger.logInfo("Date range query [" + startDateText + " to " + endDateText + "] returned " + result.size() + " article(s)");
        }

        return result;
    }
}