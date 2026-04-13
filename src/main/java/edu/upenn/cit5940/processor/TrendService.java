package edu.upenn.cit5940.processor;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.upenn.cit5940.common.dto.Article;

public class TrendService {

    private final List<Article> articles;

    public TrendService(List<Article> articles) {
        this.articles = articles;
    }

    public boolean isValidPeriod(String period) {
        try {
            YearMonth.parse(period);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public List<String> getTrendData(String topic, String startPeriod, String endPeriod) {
        List<String> results = new ArrayList<>();

        if (topic == null || topic.isEmpty()) {
            return results;
        }

        if (!isValidPeriod(startPeriod) || !isValidPeriod(endPeriod)) {
            return results;
        }

        YearMonth start = YearMonth.parse(startPeriod);
        YearMonth end = YearMonth.parse(endPeriod);

        if (start.isAfter(end)) {
            return results;
        }

        TreeMap<YearMonth, Integer> countsByMonth = new TreeMap<>();

        YearMonth current = start;
        while (!current.isAfter(end)) {
            countsByMonth.put(current, 0);
            current = current.plusMonths(1);
        }

        String normalizedTopic = topic.toLowerCase();

        for (Article article : articles) {
            try {
                YearMonth articleMonth = YearMonth.from(article.getPublishDate());

                if (articleMonth.isBefore(start) || articleMonth.isAfter(end)) {
                    continue;
                }

                int count = countOccurrences(article.getTitle(), normalizedTopic)
                        + countOccurrences(article.getContent(), normalizedTopic);

                countsByMonth.put(articleMonth, countsByMonth.get(articleMonth) + count);

            } catch (Exception e) {
                // skip malformed articlates
            }
        }

        for (YearMonth month : countsByMonth.keySet()) {
            results.add(month + ": " + countsByMonth.get(month));
        }

        return results;
    }

    private int countOccurrences(String text, String topic) {
        if (text == null || text.isEmpty() || topic == null || topic.isEmpty()) {
            return 0;
        }

        int count = 0;
        String[] tokens = text.toLowerCase().split("\\W+");

        for (String token : tokens) {
            if (token.equals(topic)) {
                count++;
            }
        }

        return count;
    }
}
