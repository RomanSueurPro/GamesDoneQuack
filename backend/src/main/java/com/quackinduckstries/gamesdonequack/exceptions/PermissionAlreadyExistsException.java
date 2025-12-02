package com.quackinduckstries.gamesdonequack.exceptions;

public class PermissionAlreadyExistsException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4273074501557115117L;

	public PermissionAlreadyExistsException(String message) {
        super(message);
    }
}
