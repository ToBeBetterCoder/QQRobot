package org.cool.qqrobot.exception;

public class RobotException extends RuntimeException {

	private static final long serialVersionUID = -5536914896540424869L;

	public RobotException(String message, Throwable cause) {
		super(message, cause);
	}

	public RobotException(String message) {
		super(message);
	}

	public RobotException(Throwable cause) {
		super(cause);
	}

}
