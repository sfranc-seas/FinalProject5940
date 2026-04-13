package edu.upenn.cit5940.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.upenn.cit5940.common.dto.Article;
import edu.upenn.cit5940.processor.SearchService;
import edu.upenn.cit5940.processor.TopicService;
import edu.upenn.cit5940.processor.ArticleDateService;
import edu.upenn.cit5940.processor.AutocompleteService;
import edu.upenn.cit5940.logging.Logger;
import edu.upenn.cit5940.processor.TrendService;

public class CLI {

    private final SearchService searchService;
    private final AutocompleteService autocompleteService;
    private final TopicService topicService;
    private final ArticleDateService articleDateService;
    private final TrendService trendService;
    
    private final Logger logger;
    
    private final Scanner scanner;
    
    public CLI(SearchService searchService, AutocompleteService autocompleteService,
            TopicService topicService, ArticleDateService articleDateService,
            TrendService trendService,Logger logger) {
        this.searchService = searchService;
        this.autocompleteService = autocompleteService;
        this.topicService = topicService;
        this.articleDateService = articleDateService;
        this.trendService = trendService;
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
        boolean inInteractive = true;
        while (inInteractive) {
            printInteractiveMenu();
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Please enter a choice.");
                continue;
            }

            switch (input) {
                case "1": interactiveSearch(); break;
                case "2": interactiveAutocomplete(); break;
                case "3": interactiveTopics(); break;
                case "4": interactiveTrends(); break;
                case "5": interactiveArticlesByDate(); break;
                case "6": interactiveArticleById(); break;
                case "7": interactiveStats(); break;
                case "8": inInteractive = false; break;
                default:
                    if (input.matches("\\d+")) {
                        System.out.println("Invalid choice. Please enter 1-8.");
                    } else {
                        System.out.println("Please enter a valid number (1-8).");
                    }
            }
        }
    }
    
    private void printInteractiveMenu() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("               INTERACTIVE MODE");
        System.out.println("==================================================");
        System.out.println("This mode will guide you through each operation step by step.");
        System.out.println("----------------------------------------");
        System.out.println("             AVAILABLE SERVICES");
        System.out.println("----------------------------------------");
        System.out.println("1. Search Articles");
        System.out.println("2. Get Autocomplete Suggestions");
        System.out.println("3. View Top Topics");
        System.out.println("4. Analyze Topic Trends");
        System.out.println("5. Browse Articles by Date");
        System.out.println("6. View Specific Article by ID");
        System.out.println("7. Show Statistics");
        System.out.println("8. Back to Main Menu");
        System.out.println("----------------------------------------");
        System.out.print("Select a service (1-8): ");
    }
    
    private void interactiveSearch() {
        System.out.print("Enter search keyword(s): ");
        String query = scanner.nextLine().trim();
        logger.logInfo("Interactive search query: " + query);
        
        if (query.isEmpty()) {
            System.out.println("Usage: search <keyword>");
        } 
        else 
        {
            List<String> titles = searchService.searchTitles(query);
            if (titles.isEmpty()) {
                System.out.println("No articles found.");
            } else {
                for (String title : titles) System.out.println(title);
            }
        }
        returnToMenu();
    }

    private void interactiveAutocomplete() {
        System.out.print("Enter prefix for autocomplete: ");
        String prefix = scanner.nextLine().trim();
        logger.logInfo("Interactive autocomplete prefix: " + prefix);
        
        if (prefix.isEmpty()) {
            System.out.println("Usage: autocomplete <prefix>");
        } else {
            List<String> suggestions = autocompleteService.getSuggestions(prefix);
            if (suggestions.isEmpty()) {
                System.out.println("No suggestions found.");
            } else {
                for (String s : suggestions) System.out.println(s);
            }
        }
        returnToMenu();
    }

    private void interactiveTopics() {
        System.out.print("Enter period (YYYY-MM): ");
        String period = scanner.nextLine().trim();
        logger.logInfo("Interactive topics period: " + period);
        
        while(true)
        {
        	if (!isValidPeriod(period)) {
        		System.out.println("Invalid date. Please use the YYYY-MM format with valid values.");
        		logger.logWarning("Interactive topics invalid period: " + period);
        		System.out.print("Enter period (YYYY-MM): ");
                period = scanner.nextLine().trim();
        	}
        	else
        		break;
        }
        
        List<String> topics = topicService.getTopTopics(period);
        if (topics.isEmpty()) {
            System.out.println("No topics found.");
        } else {
            for (String t : topics) System.out.println(t);
        }
        
        returnToMenu();
    }

    private void interactiveTrends() {
        System.out.print("Enter topic word: ");
        String topic = scanner.nextLine().trim();
        
        System.out.print("Enter start period (YYYY-MM): ");
        String start = scanner.nextLine().trim();
        
        while (true)
        {
	        if (!trendService.isValidPeriod(start)) {
	            System.out.println("Invalid period format. Use YYYY-MM.");
	            logger.logWarning("Invalid trends start period format: " + start);
	            System.out.print("Enter start period (YYYY-MM): ");
	            start = scanner.nextLine().trim();	            
	        }
	        else
	        	break;	        
        }
        
        java.time.YearMonth startMonth = java.time.YearMonth.parse(start);
        
        System.out.print("Enter end period (YYYY-MM): ");
        String end = scanner.nextLine().trim();
        
        while (true)
        {
	        if (!trendService.isValidPeriod(end)) {
	            System.out.println("Invalid period format. Use YYYY-MM.");
	            logger.logWarning("Invalid trends start period format: " + end);
	            System.out.print("Enter end period (YYYY-MM): ");
	            end = scanner.nextLine().trim();	            
	        }
	        else
	        {
	        	java.time.YearMonth endMonth = java.time.YearMonth.parse(end);
	        	
	        	if (startMonth.isAfter(endMonth)) {
	                System.out.println("Invalid period range. Start period cannot be after end period.");
	                logger.logWarning("Invalid trends period range: " + end);
	                System.out.print("Enter end period (YYYY-MM): ");
	                end = scanner.nextLine().trim();	                
	            }
	        	else
	        		break;
	        }
        }
        
        logger.logInfo("Interactive trends: topic=" + topic + " start=" + start + " end=" + end);

         List<String> trendLines = trendService.getTrendData(topic, start, end);

        if (trendLines.isEmpty()) {
            System.out.println("No trend data found.");
            return;
        }

        for (String line : trendLines) {
            System.out.println(line);
        }
        
        returnToMenu();
    }

    private void interactiveArticlesByDate() {
    	
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine().trim();
        
        while (true)
        {
        	if (!isValidDate(startDate)) {
                System.out.println("Error: Invalid date provided. Please use the YYYY-MM-DD format with valid values.");
                System.out.print("Enter start date (YYYY-MM-DD): ");
                startDate = scanner.nextLine().trim();
            }
        	else
        		break;
        }
        
        LocalDate start = LocalDate.parse(startDate);
        
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine().trim();
        
        while(true)
        {
        	if (!isValidDate(endDate)) {
                System.out.println("Error: Invalid date provided. Please use the YYYY-MM-DD format with valid values.");
                System.out.print("Enter start date (YYYY-MM-DD): ");
                endDate = scanner.nextLine().trim();
                
        	}
        	else
        	{
        		LocalDate end = LocalDate.parse(endDate);
        		
        		if (start.isAfter(end)) {
        			System.out.println("Error: Invalid date provided. Start date cannot be after end date.");
        			System.out.print("Enter end date (YYYY-MM-DD): ");
        			endDate = scanner.nextLine().trim();
        			
        		}
        		
        		else
        			break;
        	}
        }
        
        logger.logInfo("Interactive articles range: " + startDate + " to " + endDate);

        List<String> titles = articleDateService.getArticleTitlesInRange(startDate, endDate);
        if (titles.isEmpty()) {
            System.out.println("No articles found.");
        } else {
            for (String t : titles) System.out.println(t);
        }
        
        returnToMenu();
    }

    private void interactiveArticleById() {
        System.out.print("Enter article ID (URI): ");
        String id = scanner.nextLine().trim();
        logger.logInfo("Interactive article lookup: " + id);
        if (id.isEmpty()) {
            System.out.println("Usage: article <id>");
        } else {
            Article article = searchService.getArticleById(id);
            if (article == null) {
                System.out.println("No articles found.");
            } else {
                printArticle(article);
            }
        }
        returnToMenu();
    }

    private void interactiveStats() {
        System.out.println("Total number of articles: " + searchService.getTotalArticleCount());
        returnToMenu();
    }
    
    private void returnToMenu() {
        System.out.println();
        System.out.print("Press Enter to return to the menu...");
        scanner.nextLine();
    }
    
    private boolean isValidDate(String dateText) {
        if (dateText == null || !dateText.matches("\\d{4}-\\d{2}-\\d{2}")) return false;
        try {
            LocalDate.parse(dateText);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    private boolean isValidPeriod(String period) {
        if (period == null || !period.matches("\\d{4}-\\d{2}")) return false;
        try {
            int month = Integer.parseInt(period.substring(5, 7));
            return month >= 1 && month <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void printArticle(Article article) {
        System.out.println("ID: " + article.getId());
        System.out.println("Date: " + article.getDate());
        System.out.println("Title: " + article.getTitle());
        System.out.println("Body: " + article.getBody());
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
            } else if (commandLine.equalsIgnoreCase("stats")) {
                handleStats();
            } else if (commandLine.toLowerCase().startsWith("search")) {
                handleSearch(commandLine);
            } else if (commandLine.toLowerCase().startsWith("autocomplete")) {
                handleAutocomplete(commandLine);
            }  else if (commandLine.toLowerCase().startsWith("trends")) {
                handleTrends(commandLine);            
            } else if (commandLine.toLowerCase().startsWith("article")) {
                handleArticle(commandLine);
            } else if (commandLine.toLowerCase().startsWith("topics")) {
                handleTopics(commandLine);            
            } else if (commandLine.toLowerCase().startsWith("articles")) {
                handleArticles(commandLine);            
            } else {
                System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
    }
    
    private void handleTrends(String commandLine) {
    	
    	if(commandLine.length() <= 6 )
    	{
    		logger.logWarning("Invalid command.Usage: trends <topic> <start-YYYY-MM> <end-YYYY-MM>");
    		System.out.println("Usage: trends <topic> <start-YYYY-MM> <end-YYYY-MM>");
            return;
    	}
    	String args = commandLine.substring(7).trim();
        String[] parts = args.split("\\s+");
        if (parts.length != 3) {
            System.out.println("Usage: trends <topic> <start-YYYY-MM> <end-YYYY-MM>");
            return;
        }
        String topic = parts[0];
        String start = parts[1];
        String end = parts[2];
        logger.logInfo("Trends query: topic=" + topic + " start=" + start + " end=" + end);

        if (!trendService.isValidPeriod(start) || !trendService.isValidPeriod(end)) {
            System.out.println("Invalid period format. Use YYYY-MM.");
            logger.logWarning("Invalid trends period format: " + commandLine);
            return;
        }

        java.time.YearMonth startMonth = java.time.YearMonth.parse(start);
        java.time.YearMonth endMonth = java.time.YearMonth.parse(end);

        if (startMonth.isAfter(endMonth)) {
            System.out.println("Invalid period range. Start period cannot be after end period.");
            logger.logWarning("Invalid trends period range: " + commandLine);
            return;
        }

        List<String> trendLines = trendService.getTrendData(topic, start, end);

        if (trendLines.isEmpty()) {
            System.out.println("No trend data found.");
            return;
        }

        for (String line : trendLines) {
            System.out.println(line);
        }
    }
    
    private void handleAutocomplete(String commandLine) {
    	if(commandLine.length() <= 12 )
    	{
    		logger.logWarning("Invalid command.Usage: autocomplete <prefix>");
    		System.out.println("Invalid Command. Usage: autocomplete <prefix>");
            return;
    	}
    	
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
    	if(commandLine.length() <= 6 )
    	{
    		logger.logWarning("Invalid command.Usage: search <keyword>");
    		System.out.println("Invalid Command. Usage: search <keyword>");
            return;
    	}
    	
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
    	if(commandLine.length() <= 7 )
    	{
    		logger.logWarning("Invalid command.Usage: article <id>");
    		System.out.println("Invalid Command. Usage: article <id>");
            return;
    	}
    	
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
    	
    	if(commandLine.length() <= 6 )
    	{
    		logger.logWarning("Invalid command.Usage: topics <period>");
    		System.out.println("Invalid Command. Usage: topics <period>");
            return;
    	}
    	
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
    	if(commandLine.length() <= 8 )
    	{
    		logger.logWarning("Invalid command.Usage: articles <start_date> <end_date>");
    		System.out.println("Invalid Command. Usage: articles <start_date> <end_date>");
            return;
    	}
    	
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
    	logger.logInfo("Stats command");
        System.out.println("Total number of articles: " + searchService.getTotalArticleCount());
    }

    private void printHelpMenu() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("             HELP & DOCUMENTATION");
        System.out.println("============================================================");
        System.out.println("INTERACTIVE MODE:");
        System.out.println("  * Guided step-by-step interface");
        System.out.println("  * Prompts for all required inputs");
        System.out.println("  * Perfect for beginners");
        System.out.println();
        System.out.println("COMMAND MODE:");
        System.out.println("  * Direct command entry");
        System.out.println("  * Faster for experienced users");
        System.out.println("  * Type 'help' for command list");
        System.out.println();
        System.out.println("AVAILABLE SERVICES:");
        System.out.println("  1. Search Articles   - Find articles by keywords");
        System.out.println("  2. Autocomplete      - Get search suggestions");
        System.out.println("  3. Top Topics        - View trending topics by period");
        System.out.println("  4. Topic Trends      - Analyze topic popularity over time");
        System.out.println("  5. Browse Articles   - Filter articles by date range");
        System.out.println("  6. View Article      - Get detailed article information");
        System.out.println("  7. Statistics        - View database statistics");
        System.out.println();
        System.out.println("DATE FORMATS:");
        System.out.println("  * Period: YYYY-MM (e.g., 2023-12)");
        System.out.println("  * Date:   YYYY-MM-DD (e.g., 2023-12-01)");
        System.out.println();
        System.out.print("Press Enter to return to the main menu...");
        scanner.nextLine();
    }

    private void printCommandHelp() {
        System.out.println("Available commands:");
        System.out.println("  search <keywords>              - Search articles by keyword(s)");
        System.out.println("  autocomplete <prefix>          - Get word suggestions from titles");
        System.out.println("  topics <YYYY-MM>               - Top 10 suggested words for a month");
        System.out.println("  trends <topic> <start> <end>   - Monthly frequency of a topic");
        System.out.println("  articles <start_date> <end>    - List articles in a date range");
        System.out.println("  article <id>                   - View a specific article by URI");
        System.out.println("  stats                          - Show total article count");
        System.out.println("  help                           - Show this help message");
        System.out.println("  menu                           - Return to main menu");
    }
}