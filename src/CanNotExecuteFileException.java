

public class CanNotExecuteFileException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CanNotExecuteFileException(String message) {
		super(message);
	}
	
	public CanNotExecuteFileException(String message, Throwable cause) {
		super(message, cause);
	}

}
