package com.quackinduckstries.gamesdonequack.exceptions;

public class ExistingPermissionDoesNotExistException extends RuntimeException  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExistingPermissionDoesNotExistException(String message) {
        super(message);
    }

}
