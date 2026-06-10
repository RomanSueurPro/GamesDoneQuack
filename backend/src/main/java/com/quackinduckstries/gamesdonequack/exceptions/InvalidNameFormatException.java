package com.quackinduckstries.gamesdonequack.exceptions;

public class InvalidNameFormatException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7343561950534202565L;

	public InvalidNameFormatException(String message) {
		super(message);
	}
}
