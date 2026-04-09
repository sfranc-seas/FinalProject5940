/*
 * I attest that the code in this file is entirely my own except for the starter
 * code provided with the assignment and the following exceptions:
 * <Enter all external resources and collaborations here. Note external code may
 * reduce your score but appropriate citation is required to avoid academic
 * integrity violations. Please see the Course Syllabus as well as the
 * university code of academic integrity:
 *  https://catalog.upenn.edu/pennbook/code-of-academic-integrity/ >
 * Signed,
 * Author: SHIJU FRANCIS
 * Penn email: SFRANC@SEAS.UPENN.EDU
 * Date: 2026-03-30
 */
package edu.upenn.cit5940.datamanagement;

import java.io.*;
import java.util.*;
import edu.upenn.cit5940.common.dto.Article;

public class ArticleCSVParser {
    private final CharacterReader reader;
    private int iLine = 1;
    private int iRecord = 1;

    public ArticleCSVParser(CharacterReader reader) {
        this.reader = reader;
    }

    // The states for the Finite State Machine (FSM).
    private enum STATES {
        // Add states here
    	START_FIELD,
	    IN_UNQUOTED,
    	IN_QUOTED,
    	AFTER_QUOTE
    }

    /**
     * Reads the entire CSV stream and parses it into a map of Articles.
     *
     * @return A map where the key is the article's URI (String) and the value
     * is the fully populated Article object.
     * @throws IOException when the underlying reader encounters an error.
     * @throws CSVFormatException when the CSV file is formatted incorrectly.
     */
    public List<Article> readAllArticles() throws IOException, CSVFormatException {
        // TODO: Add code here
    	
    	List<Article> articles = new ArrayList<>();
    	 List<String> record = new ArrayList<>(16);
    	 StringBuilder field = new StringBuilder();
    	 
    	 STATES state = STATES.START_FIELD;
    	 
    	 while(true)
    	 {
    		 int ch = reader.read();
    		 
    		 if(ch == -1)
    		 {
    			 switch(state)
    			 {
    			 case IN_QUOTED:
    				 throw new CSVFormatException();
    				 
    			 case IN_UNQUOTED:
    				 record.add(field.toString());
    				 break;
    				 
    			 case AFTER_QUOTE:
    				 record.add(field.toString());
    				 break;
    			 case START_FIELD:
    				 if (!record.isEmpty())
    					 record.add("");
    				 break;
    			 }
    			  
    			 if (!record.isEmpty()) {
    				 processRecord(record, articles);
    	         }
    			 
    			 return articles;
    		 }
    		 
    		 char c = (char)ch;
    		 
    		 switch(state)
    		 {
    		 case START_FIELD:
    			 
    			 switch(c)
    			 {
    			 case ',':
    				 record.add("");
    				 state = STATES.START_FIELD;
    				 break;
    				 
    			 case '"':
    				 state = STATES.IN_QUOTED;
    				 break;
    				 
    			 case '\n':
    				 record.add("");
    				 processRecord(record, articles);
    				 record = new ArrayList<>(16);
    				 field.setLength(0);
    				 state = STATES.START_FIELD;
    				 break;
    				 
    			 case '\r':
    				 int nextch = reader.read();
    				 
    				 if (nextch != '\n') {
                         throw new CSVFormatException();
                     }

                     record.add("");
                     processRecord(record, articles);
                     record = new ArrayList<>(16);
                     field.setLength(0);
                     state = STATES.START_FIELD;
    				 break;
    				 
				 default:
					 field.append(c);
					 state = STATES.IN_UNQUOTED;
					 break;
    			 }
    			 
    			 break;
    			 
    		 case IN_QUOTED:
    			 switch(c)
    			 {
    			 case '"':
    				 state = STATES.AFTER_QUOTE;
    				 break;
    				 
				 default:
					 field.append(c);
					 break;
    			 }
    			 
    			 break;
    			 
    		 case IN_UNQUOTED:
    			 switch(c)
    			 {
    			 case ',':
    				 record.add(field.toString());
    				 field.setLength(0);
    				 state = STATES.START_FIELD;
    				 break;
    			 case '\n':
    				 record.add(field.toString());
    				 processRecord(record, articles);
    				 record = new ArrayList<>(16);
    				 field.setLength(0);
    				 state = STATES.START_FIELD;
    				 break;
    			 case '\r':
    				 int nextch = reader.read();
    				 if (nextch != '\n') {
    					 throw new CSVFormatException();
    				 }
    				 
    				 record.add(field.toString());
    				 processRecord(record, articles);
    				 record = new ArrayList<>(16);
    				 field.setLength(0);
    				 state = STATES.START_FIELD;
    				 break;
    			 case '"':
    				 throw new CSVFormatException();
    				 
				 default:
					 field.append(c);
					 break;
    			 }
    			 break;
    			 
    		 case AFTER_QUOTE:
    			 switch(c)
    			 {
    			 case '"':
    				 field.append('"');
    				 state = STATES.IN_QUOTED;
    				 break;
    			 case ',':
    				 record.add(field.toString());
    				 field.setLength(0);
    				 state = STATES.START_FIELD;
    				 break;
    			 case '\n':
    				 record.add(field.toString());
    				 processRecord(record, articles);
    				 record = new ArrayList<>(16);
    				 field.setLength(0);
    				 state = STATES.START_FIELD;
    				 break;
    			 case '\r':
    				 int nextch = reader.read();
    				 if (nextch != '\n') {
    					 throw new CSVFormatException();
    				 }
    				 
    				 record.add(field.toString());
    				 processRecord(record, articles);
    				 record = new ArrayList<>(16);
    				 field.setLength(0);
    				 state = STATES.START_FIELD;
    				 break;
    				 
    			 default:
    				 throw new CSVFormatException();
    			 }
    			 break;
    		
    			 default:
    				 throw new CSVFormatException();
			 }
    	 }
    }

    /**
     * Helper method to convert a parsed record (list of strings) into an Article
     * and add it to the map.
     */
    private void processRecord(List<String> rec, List<Article> articles) {
        // TODO: Add code here
    	if (rec == null || rec.size() != 16) {
    		
    		throw new IllegalArgumentException();   		 
    	}
    	
        // Skip header row
        if ("uri".equals(rec.get(0))) {
            return;
        }

        if (rec.get(0) == null || rec.get(0).isBlank()) {
            throw new IllegalArgumentException("URI cannot be null or empty.");
        }
        
        try {
        	Article article = new Article(
        		    rec.get(0),  // uri
        		    rec.get(1),  // date
        		    rec.get(4),  // title
        		    rec.get(5)   // body
        		);

        		articles.add(article);
        } catch (Exception e) {
            // Convert to required exception type
            
        }
    }
}
