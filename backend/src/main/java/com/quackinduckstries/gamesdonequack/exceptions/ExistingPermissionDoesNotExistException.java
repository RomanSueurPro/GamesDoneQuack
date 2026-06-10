package com.quackinduckstries.gamesdonequack.exceptions;

public class ExistingPermissionDoesNotExistException extends RuntimeException  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6731276279217013721L;

	public ExistingPermissionDoesNotExistException(String message) {
        super(message);
    }

}
