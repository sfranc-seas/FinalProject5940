package edu.upenn.cit5940;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import edu.upenn.cit5940.datamanagement.StopWordLoader;
import edu.upenn.cit5940.common.dto.*;
import edu.upenn.cit5940.datamanagement.*;
import edu.upenn.cit5940.processor.*;
import edu.upenn.cit5940.ui.*;
import edu.upenn.cit5940.logging.Logger;
import edu.upenn.cit5940.processor.TrendService;
import edu.upenn.cit5940.datamanagement.ParserFactory;

public class Main {
	public static void main(String[] args) {
		String dataFile = args.length > 0 ? args[0] : "articles_small.csv";
		String logFile = args.length > 1 ? args[1] : "tech_news_search.log";
		String stopWordFile = "stop_words.txt";

		Logger logger = null;

		try {

			logger = Logger.getInstance(logFile);

			System.out.println("=== Tech News Search Engine ===");
			System.out.println("Initializing n-tier architecture...");
			System.out.println("Loading articles from: " + dataFile);

			logger.logInfo("Application starting");
			logger.logInfo("Loading articles from: " + dataFile);

			ParserFactory parserFactory = new ParserFactory();
			Parser parser = parserFactory.getParser(dataFile);
			
			logger.logInfo("Selected parser based on file extension: " + dataFile);
			
			List<Article> articles = parser.parse(dataFile);

			if (articles.isEmpty()) {
				System.out.println("Fatal error: no valid articles loaded.");
				logger.logError("No valid articles loaded from " + dataFile);
				return;
			}

			StopWordLoader stopWordLoader = new StopWordLoader();
			Set<String> stopWords = stopWordLoader.loadStopWords(stopWordFile);
			logger.logInfo("Loaded " + stopWords.size() + " stop words from " + stopWordFile);

			ArticleRepository repository = new ArticleRepository();
			repository.addAllArticles(articles);

			InvertedIndex index = new InvertedIndex();
			index.setStopWords(stopWords);
			index.addAllArticles(articles);

			CustomTrie trie = new CustomTrie();
			AutocompleteService autocompleteService = new AutocompleteService(trie);
			autocompleteService.indexArticles(articles);

			SearchService searchService = new SearchService(index, repository);
			TopicService topicService = new TopicService(articles);
			ArticleDateService articleDateService = new ArticleDateService(articles);
			TrendService trendService = new TrendService(articles);

			System.out.println(repository.size() + " articles loaded");
			System.out.println("Architecture initialization complete!");

			logger.logInfo("Loaded " + repository.size() + " articles from " + dataFile);
			logger.logInfo("Architecture initialization complete");

			CLI cli = new CLI(searchService, autocompleteService, topicService, articleDateService, trendService,
					logger);
			cli.start();

		} catch (IOException e) {
			System.out.println("Fatal error: unable to initialize log file.");
			System.out.println("Goodbye!");
			
		} catch (RuntimeException e) {
			System.out.println("Fatal error: " + e.getMessage());
			
			if (logger != null) {
				logger.logError("Fatal startup error: " + e.getMessage());
			}
			
		} finally {
			if (logger != null) {
				logger.logInfo("Application exiting");
				logger.close();
			}
		}
	}
}