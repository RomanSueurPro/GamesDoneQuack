package com.quackinduckstries.gamesdonequack.exceptions;

public class AlreadyExistingRoleNameException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7263119471413796804L;

	public AlreadyExistingRoleNameException(String message){
		super(message);
	}

}
