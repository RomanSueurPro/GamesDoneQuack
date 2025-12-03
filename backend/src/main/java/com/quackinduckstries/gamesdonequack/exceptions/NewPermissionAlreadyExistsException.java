package com.quackinduckstries.gamesdonequack.exceptions;

public class NewPermissionAlreadyExistsException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4273074501557115117L;

	public NewPermissionAlreadyExistsException(String message) {
        super(message);
    }
}
