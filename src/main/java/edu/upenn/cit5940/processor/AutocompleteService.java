package edu.upenn.cit5940.processor;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.datamanagement.CustomTrie;

public class AutocompleteService {

    private final CustomTrie trie;

    public AutocompleteService(CustomTrie trie) {
        this.trie = trie;
    }

    public void indexArticles(List<Article> articles) {
        if (articles == null) {
            return;
        }

        for (Article article : articles) {
            indexTitleWords(article.getTitle());
        }
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

        return trie.getWordsWithPrefix(prefix, 10);
    }
}