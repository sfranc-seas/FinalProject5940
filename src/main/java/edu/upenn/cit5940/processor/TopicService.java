package edu.upenn.cit5940.processor;

import java.util.*;

import edu.upenn.cit5940.common.dto.Article;

public class TopicService {

    private final List<Article> articles;

    public TopicService(List<Article> articles) {
        this.articles = articles;
    }

    public List<String> getTopTopics(String period) {

        if (period == null || !period.matches("\\d{4}-\\d{2}")) {
            return new ArrayList<>();
        }

        Map<String, Integer> frequencyMap = new HashMap<>();

        for (Article article : articles) {
            String date = article.getDate();

            if (date != null && date.startsWith(period)) {
                processText(article.getTitle(), frequencyMap);
                processText(article.getContent(), frequencyMap);
            }
        }

        // Heap (priority queue) → highest frequency first
        PriorityQueue<Map.Entry<String, Integer>> heap =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        heap.addAll(frequencyMap.entrySet());

        List<String> result = new ArrayList<>();

        int count = 0;
        while (!heap.isEmpty() && count < 10) {
            result.add(heap.poll().getKey());
            count++;
        }

        return result;
    }

    private void processText(String text, Map<String, Integer> map) {
        if (text == null || text.isBlank()) {
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