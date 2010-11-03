package com.famundo;

/**
 * A simple catch all exception
 * @author dudi
 *
 */
public class FamundoException extends Exception {
	private static final long serialVersionUID = 7855079846335008347L;

	public FamundoException(String message) {
		super(message);
	}
}
