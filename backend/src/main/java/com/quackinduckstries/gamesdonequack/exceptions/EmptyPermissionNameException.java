package com.quackinduckstries.gamesdonequack.exceptions;

public class EmptyPermissionNameException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4074868677429315527L;

	public EmptyPermissionNameException(String message) {
		super(message);
	}
}
