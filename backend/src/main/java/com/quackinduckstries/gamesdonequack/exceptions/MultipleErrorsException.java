package com.quackinduckstries.gamesdonequack.exceptions;

import java.util.List;

import lombok.Getter;

@Getter
public class MultipleErrorsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4734120362264035532L;
	private final List<String> errors;
	
	public MultipleErrorsException(List<String> errors) {
		super("Multiple errors");
		this.errors = errors;
	}
}
