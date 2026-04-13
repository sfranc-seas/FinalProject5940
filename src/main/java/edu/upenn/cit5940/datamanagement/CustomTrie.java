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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTrie {

    // inner class
    private class Node {
        private HashMap<Character, Node> children = new HashMap<>();

        // TODO (provide starting value)
        private boolean endOfWord = false;

    }

    // root node (has no value)
    private Node root = new Node();

    // TODO
    public void insertWord(String word) {
    	
    	Node currentNode = root;
    	
    	if (word == null || word.isEmpty()) 
    		return;
    	
    	//loop until the word length
    	for(int i = 0; i < word.length(); i++ )
    	{
    		//get the character
    		char val = word.charAt(i);
    		
    		//if the character dosent exists, add the character as a child node 
    		if(!currentNode.children.containsKey(val))
    		{
    			currentNode.children.put(val, new Node());    			
    		}
    		
    		//set the current node for the loop
    		currentNode = currentNode.children.get(val);    		
    	}
    	
    	currentNode.endOfWord = true;
    }

    // this implementation is given to students in the starter code
    public void insertList(String[] wordList) {
        for (String string : wordList) {
            insertWord(string);
        }
    }

    // TODO
    public boolean findWord(String word) {
    	
    	Node currentNode = root;
    	//return if the word is null or empty
    	if(word == null || word.isEmpty())
    		return false;
    	
    	for(int i=0; i<word.length(); i++)
    	{
    		//get the character from the word
    		char val = word.charAt(i);
    		
    		//check if the character exists
    		Node next = currentNode.children.get(val);
    		
    		//if the node is null, before we hit the end of the word, 
    		//then the word dosent exist and return null 
    		if(next == null)
    			return false;
    		
    		currentNode = next;
    		
    	}
    	
    	if(currentNode.endOfWord)
    		return true;
    	else
    		return false;
    }

    // TODO
    public void deleteWord(String word) {
    	if (word == null || word.isEmpty()) 
    		return ;
    	
    	//check if the word exists
    	if(findWord(word))
    		deleteWordHelper(word,0, root);
    	
    }

    // TODO
    public boolean deleteWordHelper(String word, int index, Node curNode) {
    	
    	//hit base case
    	if(index == word.length())
    	{
    		curNode.endOfWord = false;
    		return curNode.children.isEmpty();
    	}
    	
    	char val = word.charAt(index);
    	
    	Node childNode = curNode.children.get(val);
    	
    	if(childNode == null)
    		return false;
    	
    	// call the delete function recursively
    	boolean canDeleteNode = deleteWordHelper(word, index+1, childNode); 
    	
    	//check if there are other dependent nodes
    	if(canDeleteNode)
    	{
    		curNode.children.remove(val);
    		
    		return !curNode.endOfWord && curNode.children.isEmpty();
    	}
    	
    	return false;        
    }

    // TODO
    public List<String> allWords() {
        
    	List<String> words = new ArrayList<>();
    	
        allWordsHelper(root, new StringBuilder(), words);
        
        Collections.sort(words);
        
        return words;
        
    }

    // TODO
    public void allWordsHelper(Node node, StringBuilder accumulated, List<String> myList) {
    	
    	//add the word to the list
    	if(node.endOfWord)
    	{
    		myList.add(accumulated.toString());
    	}
    	
    	//base case
    	if (node.children.isEmpty()) 
    		return;
    	
    	//loop through the nodes
    	for(Map.Entry<Character, Node> entry : node.children.entrySet())
    	{
    		char val = entry.getKey();
    		
    		Node childNode = entry.getValue();
    		
    		//accumulate the characters
    		accumulated.append(val);
    		
    		//call function recursively
    		allWordsHelper(childNode, accumulated, myList);
    		
    		//delete the last but one for back tracking
    		accumulated.deleteCharAt(accumulated.length()-1);
    	}
    }

    public List<String> getWordsWithPrefix(String prefix, int limit) {

        List<String> results = new ArrayList<>();

        if (prefix == null || prefix.isEmpty()) {
            return results;
        }

        Node current = root;

        for (char c : prefix.toLowerCase().toCharArray()) {
            Node next = current.children.get(c);
            if (next == null) {
                return results;
            }
            current = next;
        }

        collectPrefixWords(current, new StringBuilder(prefix.toLowerCase()), results, limit);

        return results;
    }
    
    private void collectPrefixWords(Node node,
            StringBuilder current,
            List<String> results,
            int limit) {

		if (results.size() >= limit) {
		return;
		}
		
		if (node.endOfWord) {
		results.add(current.toString());
		}
		
		for (Map.Entry<Character, Node> entry : node.children.entrySet()) {
		if (results.size() >= limit) {
		return;
		}
		
		current.append(entry.getKey());
		collectPrefixWords(entry.getValue(), current, results, limit);
		current.deleteCharAt(current.length() - 1);
		}
		
	}
}