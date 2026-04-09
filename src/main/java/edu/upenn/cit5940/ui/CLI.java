package edu.upenn.cit5940.ui;

import java.util.List;
import java.util.Scanner;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.processor.SearchService;
import edu.upenn.cit5940.processor.TopicService;
import edu.upenn.cit5940.processor.ArticleDateService;
import edu.upenn.cit5940.processor.AutocompleteService;
import edu.upenn.cit5940.logging.Logger;

public class CLI {

    private final SearchService searchService;
    private final AutocompleteService autocompleteService;
    private final TopicService topicService;
    private final ArticleDateService articleDateService;
    private final Logger logger;
    
    private final Scanner scanner;
    
    public CLI(SearchService searchService, AutocompleteService autocompleteService,
            TopicService topicService, ArticleDateService articleDateService,
            Logger logger) {
        this.searchService = searchService;
        this.autocompleteService = autocompleteService;
        this.topicService = topicService;
        this.articleDateService = articleDateService;
        this.logger = logger;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMainMenu();
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Please enter a choice.");
                continue;
            }

            switch (input) {
	            case "1":
	                logger.logInfo("User selected Interactive Mode");
	                runInteractiveMode();
	                break;
	            case "2":
	                logger.logInfo("User selected Command Mode");
	                runCommandMode();
	                break;
	            case "3":
	                logger.logInfo("User selected Help");
	                printHelpMenu();
	                break;
	            case "4":
	                logger.logInfo("User selected Exit");
	                System.out.println("Thank you for using the Tech News Search Engine!");
	                System.out.println("Goodbye!");
	                running = false;
	                break;
                default:
                	logger.logWarning("Invalid main menu input: " + input);
                	if (input.matches("\\d+")) {
                        System.out.println("Invalid choice. Please enter 1-4.");
                    } else {
                        System.out.println("Please enter a valid number (1-4).");
                    }
            }
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("                 MAIN MENU");
        System.out.println("==================================================");
        System.out.println("1. Interactive Mode");
        System.out.println("2. Command Mode");
        System.out.println("3. Help");
        System.out.println("4. Exit");
        System.out.print("Please select an option (1-4): ");
    }

    private void runInteractiveMode() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("              INTERACTIVE MODE");
        System.out.println("==================================================");
        System.out.println("Interactive mode is not implemented yet.");
        System.out.println("Press Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void runCommandMode() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("                COMMAND MODE");
        System.out.println("==================================================");
        System.out.println("Enter commands directly. Type 'help' for available commands.");
        System.out.println("Type 'menu' to return to the main menu.");

        boolean inCommandMode = true;

        while (inCommandMode) {
            System.out.print("> ");
            String commandLine = scanner.nextLine().trim();
            logger.logInfo("User command: " + commandLine);
            
            if (commandLine.isEmpty()) {
            	logger.logWarning("Empty command entered in command mode");
                System.out.println("Unknown command. Type 'help' for available commands.");
                continue;
            }

            if (commandLine.equalsIgnoreCase("menu")) {
                inCommandMode = false;
            } else if (commandLine.equalsIgnoreCase("help")) {
                printCommandHelp();
            } else if (commandLine.toLowerCase().startsWith("search ")) {
                handleSearch(commandLine);
            } else if (commandLine.toLowerCase().startsWith("autocomplete ")) {
                handleAutocomplete(commandLine);
            } else if (commandLine.toLowerCase().startsWith("article ")) {
                handleArticle(commandLine);
            } else if (commandLine.equalsIgnoreCase("stats")) {
                handleStats();
            } else if (commandLine.toLowerCase().startsWith("topics ")) {
                handleTopics(commandLine);            
            } else if (commandLine.toLowerCase().startsWith("articles ")) {
                handleArticles(commandLine);            
            } else {
                System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
    }
    
    private void handleAutocomplete(String commandLine) {
        String prefix = commandLine.substring(13).trim();
        logger.logInfo("Autocomplete prefix: " + prefix);
        
        if (prefix.isEmpty()) {
            System.out.println("Usage: autocomplete <prefix>");
            return;
        }

        List<String> suggestions = autocompleteService.getSuggestions(prefix);

        if (suggestions.isEmpty()) {
            System.out.println("No suggestions found.");
            return;
        }

        for (String suggestion : suggestions) {
            System.out.println(suggestion);
        }
    }
    private void handleSearch(String commandLine) {
        String query = commandLine.substring(7).trim();
        
        logger.logInfo("Search query: " + query);
        
        if (query.isEmpty()) {
        	logger.logInfo("Search query: " + query);
            System.out.println("Usage: search <keyword>");
            return;
        }

        List<String> titles = searchService.searchTitles(query);

        if (titles.isEmpty()) {
            System.out.println("No articles found.");
            return;
        }

        for (String title : titles) {
            System.out.println(title);
        }
    }

    private void handleArticle(String commandLine) {
        String id = commandLine.substring(8).trim();
        
        logger.logInfo("Article lookup by id: " + id);
        
        if (id.isEmpty()) {
            System.out.println("Usage: article <id>");
            return;
        }

        Article article = searchService.getArticleById(id);

        if (article == null) {
            System.out.println("No articles found.");
            return;
        }

        System.out.println("ID: " + article.getId());
        System.out.println("Date: " + article.getDate());
        System.out.println("Title: " + article.getTitle());
        System.out.println("Body: " + article.getBody());
    }

    private void handleTopics(String commandLine) {

        String period = commandLine.substring(7).trim();
        logger.logInfo("Topics query for period: " + period);
        
        if (!period.matches("\\d{4}-\\d{2}")) {
            System.out.println("Invalid period format. Use YYYY-MM.");
            return;
        }

        List<String> topics = topicService.getTopTopics(period);

        if (topics.isEmpty()) {
            System.out.println("No topics found.");
            return;
        }

        for (String topic : topics) {
            System.out.println(topic);
        }
    }
    
    private void handleArticles(String commandLine) {
        String args = commandLine.substring(9).trim();
        String[] parts = args.split("\\s+");
        
        if (parts.length != 2) {
            System.out.println("Usage: articles <start_date> <end_date>");
            return;
        }

        String startDate = parts[0];
        String endDate = parts[1];

        logger.logInfo("Articles query from " + startDate + " to " + endDate);
        
        if (!articleDateService.isValidDate(startDate) || !articleDateService.isValidDate(endDate)) {
            System.out.println("Error: Invalid date provided. Please use the YYYY-MM-DD format with valid values.");
            return;
        }

        if (java.time.LocalDate.parse(startDate).isAfter(java.time.LocalDate.parse(endDate))) {
            System.out.println("Error: Invalid date provided. Start date cannot be after end date.");
            return;
        }

        List<String> titles = articleDateService.getArticleTitlesInRange(startDate, endDate);

        if (titles.isEmpty()) {
            System.out.println("No articles found.");
            return;
        }

        for (String title : titles) {
            System.out.println(title);
        }
    }
    
    private void handleStats() {
        System.out.println("Total number of articles: " + searchService.getTotalArticleCount());
    }

    private void printHelpMenu() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("            HELP & DOCUMENTATION");
        System.out.println("==================================================");
        System.out.println("INTERACTIVE MODE:");
        System.out.println("  Guided step-by-step interface.");
        System.out.println();
        System.out.println("COMMAND MODE:");
        System.out.println("  Enter commands directly.");
        System.out.println("  Type 'help' for available commands.");
        System.out.println();
        System.out.println("AVAILABLE SERVICES:");
        System.out.println("  Search Articles");
        System.out.println("  Autocomplete");
        System.out.println("  View Article");
        System.out.println("  Statistics");
        System.out.println();
        System.out.println("Press Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void printCommandHelp() {
        System.out.println("Available commands:");
        System.out.println("search <keywords>");
        System.out.println("autocomplete <prefix>");
        System.out.println("articles <start_date> <end_date>");
        System.out.println("article <id>");
        System.out.println("stats");
        System.out.println("topics <period>");
        System.out.println("help");
        System.out.println("menu");        
    }
}