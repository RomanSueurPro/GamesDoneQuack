package com.quackinduckstries.gamesdonequack.exceptions;

public class NewRoleAlreadyExistsException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4449713519901396898L;

	public NewRoleAlreadyExistsException(String message) {
		super(message);
	}
}
