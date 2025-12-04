package com.quackinduckstries.gamesdonequack.exceptions;

public class DuplicateUsernameException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3365387813363885432L;

	public  DuplicateUsernameException(String message) {
		super(message);
	}
}
