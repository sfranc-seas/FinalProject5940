package edu.upenn.cit5940.processor;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.datamanagement.CustomTrie;
import edu.upenn.cit5940.logging.Logger;

public class AutocompleteService {

    private final CustomTrie trie;
    private final Logger logger;

    public AutocompleteService(CustomTrie trie, Logger logger) {
        this.trie = trie;
        this.logger = logger;
    }

    public void indexArticles(List<Article> articles) {
        if (articles == null) {
            return;
        }

        logger.logInfo("Autocomplete: indexing titles for " + articles.size() + " article(s)");

        for (Article article : articles) {
            indexTitleWords(article.getTitle());
        }

        logger.logInfo("Autocomplete: title indexing complete");
    }

    private void indexTitleWords(String title) {
        if (title == null || title.isEmpty()) {
            return;
        }

        String[] words = title.toLowerCase().split("\\W+");
        for (String word : words) {
            if (!word.isEmpty()) {
                trie.insertWord(word);
            }
        }
    }

    public List<String> getSuggestions(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>();
        }

        logger.logInfo("Autocomplete prefix query: \"" + prefix + "\"");

        List<String> suggestions = trie.getWordsWithPrefix(prefix, 10);

        if (suggestions.isEmpty()) {
            logger.logWarning("Autocomplete prefix \"" + prefix + "\" returned no suggestions");
        } else {
            logger.logInfo("Autocomplete prefix \"" + prefix + "\" returned " + suggestions.size() + " suggestion(s)");
        }

        return suggestions;
    }
}