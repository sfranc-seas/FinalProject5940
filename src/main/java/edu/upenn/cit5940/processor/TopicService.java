package edu.upenn.cit5940.processor;

import java.util.*;

import edu.upenn.cit5940.common.dto.Article;

public class TopicService {

    private final List<Article> articles;
    private final TreeMap<String, Map<String, Integer>> periodWordFrequency;

    public TopicService(List<Article> articles) {
        this.articles = articles;
        this.periodWordFrequency = new TreeMap<>();
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
    
    /**
     * Returns monthly frequency of a topic across a date range (inclusive).
     * @param topic the word to track
     * @param startPeriod YYYY-MM format
     * @param endPeriod YYYY-MM format
     * @return Map from YYYY-MM period to count (all months in range are present)
     */
    public Map<String, Integer> getTrends(String topic, String startPeriod, String endPeriod) {
        Map<String, Integer> result = new TreeMap<>();

        if (topic == null || topic.isBlank()) return result;

        String normalizedTopic = topic.toLowerCase().trim();

        // Generate all months in range
        List<String> allMonths = generateMonthRange(startPeriod, endPeriod);
        for (String month : allMonths) {
            result.put(month, 0);
        }

        // Fill in counts from pre-built index
        for (Map.Entry<String, Map<String, Integer>> entry : periodWordFrequency.entrySet()) {
            String period = entry.getKey();
            if (period.compareTo(startPeriod) >= 0 && period.compareTo(endPeriod) <= 0) {
                int count = entry.getValue().getOrDefault(normalizedTopic, 0);
                result.put(period, count);
            }
        }

        return result;
    }
    
    private List<String> generateMonthRange(String start, String end) {
        List<String> months = new ArrayList<>();
        try {
            int startYear = Integer.parseInt(start.substring(0, 4));
            int startMonth = Integer.parseInt(start.substring(5, 7));
            int endYear = Integer.parseInt(end.substring(0, 4));
            int endMonth = Integer.parseInt(end.substring(5, 7));

            int year = startYear;
            int month = startMonth;

            while (year < endYear || (year == endYear && month <= endMonth)) {
                months.add(String.format("%04d-%02d", year, month));
                month++;
                if (month > 12) {
                    month = 1;
                    year++;
                }
            }
        } catch (Exception e) {
            // Return empty if parsing fails
        }
        return months;
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