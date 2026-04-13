package edu.upenn.cit5940.processor;

import java.util.*;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.logging.Logger;

public class TopicService {

    private final List<Article> articles;
    private final TreeMap<String, Map<String, Integer>> periodWordFrequency;
    private final Logger logger;

    public TopicService(List<Article> articles, Logger logger) {
        this.articles = articles;
        this.periodWordFrequency = new TreeMap<>();
        this.logger = logger;
    }

    public List<String> getTopTopics(String period) {

        if (period == null || !period.matches("\\d{4}-\\d{2}")) {
            logger.logWarning("Topics query received invalid period: \"" + period + "\"");
            return new ArrayList<>();
        }

        logger.logInfo("Topics query for period: \"" + period + "\"");

        Map<String, Integer> frequencyMap = new HashMap<>();

        for (Article article : articles) {
            String date = article.getDate();

            if (date != null && date.startsWith(period)) {
                processText(article.getTitle(), frequencyMap);
                processText(article.getContent(), frequencyMap);
            }
        }

        PriorityQueue<Map.Entry<String, Integer>> heap =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        heap.addAll(frequencyMap.entrySet());

        List<String> result = new ArrayList<>();

        int count = 0;
        while (!heap.isEmpty() && count < 10) {
            result.add(heap.poll().getKey());
            count++;
        }

        if (result.isEmpty()) {
            logger.logWarning("Topics query for period \"" + period + "\" returned no results");
        } else {
            logger.logInfo("Topics query for period \"" + period + "\" returned " + result.size() + " topic(s)");
        }

        return result;
    }

    private void processText(String text, Map<String, Integer> map) {
        if (text == null || text.isEmpty()) {
            return;
        }

        String[] tokens = text.toLowerCase().split("\\W+");

        for (String token : tokens) {
            if (token.length() <= 1) {
                continue;
            }

            map.put(token, map.getOrDefault(token, 0) + 1);
        }
    }
}