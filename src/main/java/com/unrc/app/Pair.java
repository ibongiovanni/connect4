package com.unrc.app;
/**
 * Clase par. 
 * @author Bongiovanni, Bentolila, Ducculi.
 */
 class Pair {

 	private boolean first;	//first element.
	private int 	second;	//second element.

	/**
	 * Constructor. 
	 */
	public Pair(){
				
	}
	/**
	 * Constructor con parametros.
	 * @param first element
	 * @param second element 
	 */
   	public Pair(boolean first, int second){
   		this.first = first;
   		this.second = second;
   	}
   	/**
	 * Get and Set. 
	 */
   	public boolean getFirst(){ return first; }
   	
   	public int getSecond(){ return second; }

   	public void setFirst(boolean newValue) { first = newValue; }
   	
   	public void setSecond(int newValue) { second = newValue; }
   	/**
	 * Retorna un String que representa un par de elementos. 
	 */
   	public String toString(){
   		return "("+first+", "+second+")";
   	}
}