/*
 * I attest that the code in this file is entirely my own except for the starter
 * code provided with the assignment and the following exceptions:
 * <
 * Enter all external resources and collaborations here. Note external code may
 * reduce your score but appropriate citation is required to avoid academic
 * integrity violations. Please see the Course Syllabus as well as the
 * university code of academic integrity:
 *  https://catalog.upenn.edu/pennbook/code-of-academic-integrity/
 * >
 * Signed,
 * Author: SHIJU FRANCIS
 * Penn email: SFRANC@seas.upenn.edu
 * Date: 2026-03-03
 */
package edu.upenn.cit5940.datamanagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Scoreboard {

    private TreeMap<Integer, List<String>> scoreboard;

    public TreeMap<Integer, List<String>> getScoreboard() {
        return scoreboard;
    }

    // TODO
    public Scoreboard() {
    	scoreboard = new TreeMap<>(Collections.reverseOrder());
    }

    // TODO
    public void update(String name, Integer score) {
    	//return if the score is less than or equal to 0
    	if( score < 0)
    		return;
    	
    	remove(name);
    	
    	//add the name if the score is present. if not create a new arraylist and add the name
    	scoreboard.computeIfAbsent(score, k->new ArrayList<>()).add(name);
    }

    // TODO
    public void remove(String name) {
    	
    	Integer key = null;
    	
    	//loop through the Map to find the key and value for the given name
    	for(Map.Entry<Integer, List<String>> entry : scoreboard.entrySet())
    	{
    		List<String> names = entry.getValue();
    		
    		//remove the name is present
    		if(names.remove(name))
    		{
    			key = entry.getKey();
    			break;
    		}    		
    	}
    	
    	//if after removing the name, if the list is empty, then delete it
    	if(key != null && scoreboard.get(key).isEmpty())
    		scoreboard.remove(key);
    }

}