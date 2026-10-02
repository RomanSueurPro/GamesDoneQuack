package com.quackinduckstries.gamesdonequack.exceptions;

public class BanNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4230346040622105499L;

	public BanNotFoundException(String message) {
		super(message);
	}
}
