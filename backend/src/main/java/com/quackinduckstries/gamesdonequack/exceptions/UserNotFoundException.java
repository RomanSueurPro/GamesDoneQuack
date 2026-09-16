package com.quackinduckstries.gamesdonequack.exceptions;

public class UserNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5418188345993534699L;

	public UserNotFoundException(String message) {
		super(message);
	}
}
