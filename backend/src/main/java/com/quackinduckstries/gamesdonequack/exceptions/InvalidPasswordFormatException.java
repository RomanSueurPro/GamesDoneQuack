package com.quackinduckstries.gamesdonequack.exceptions;

public class InvalidPasswordFormatException extends RuntimeException{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5385731294326041401L;

	public InvalidPasswordFormatException(String message) {
		super(message);
	}
}
