package com.quackinduckstries.gamesdonequack.exceptions;

public class EmptyRoleNameException extends RuntimeException{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6354386488523267998L;

	public EmptyRoleNameException(String message) {
        super(message);
    }
}
