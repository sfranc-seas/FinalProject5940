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
 * Penn email: SFRANC@seas.upenn.edu
 * Date: 2026-03-03
 */
package edu.upenn.cit5940.datamanagement;

import java.util.Random;

public class CustomHeap {

    private int[] numArray;

    private int size = 0;

    // TODO constructor
    public CustomHeap(int capacity) {
    	numArray = new int[capacity];
    }

    // TODO
    public boolean addNum(int number) {
    	
    	//if the size is same as array length return false
    	if(size == numArray.length)
    		return false;
    	else
    	{
    		//add the number to the array
    		numArray[size] = number;
    		//reorder the array
    		bubbleUp(size);
    		//increment the size
    		size++;
    		return true;
    	}        
    }

    // this implementation is given to students in the starter code
    public boolean addList(int[] myList) {
        for (int i = 0; i < myList.length; i++) {
            if (!addNum(myList[i]))
                return false;
        }
        return true;
    }

    // TODO
    public int getParentIndex(int index) {
    	
    	if(index <=0)
    		return 0;
    	else
    		return (index -1)/2; //(k-1)/2
    }

    // TODO
    public void swap(int i1, int i2) {
    	
    	int val1 = numArray[i1];
    	int val2 = numArray[i2];
    	
    	numArray[i1] = val2;
    	numArray[i2] = val1;
    	
    }

    // TODO
    public void bubbleUp(int index) {
    	
    	//hit the base case
    	if(index == 0)
    		return;
    	
		int parent = getParentIndex(index);
		
		//check if the value is less than the parent.
		if(numArray[index] < numArray[parent])
		{
			swap(index, parent);
			bubbleUp(parent);//call the function recursively
		}		
    }

    // this implementation is given to students in the starter code
    public int[] getArray() {
        return this.numArray;
    }

    // main can be used for quick testing
    public static void main(String[] args) {

        int testCapacity = 15;

        Random rnd = new Random();

        // create and populate array w/distinct values
        int[] mainArr = new int[testCapacity];
        mainArr = rnd.ints(1, 20).distinct().limit(testCapacity).toArray();

        // create CustomHeap class, and then add array
        CustomHeap myHeap = new CustomHeap(testCapacity);
        myHeap.addList(mainArr);
    }
}
