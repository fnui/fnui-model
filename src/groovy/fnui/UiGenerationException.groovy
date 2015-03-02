package fnui

import groovy.transform.CompileStatic


/**
 * RuntimeException for the (FN)UI generation process
 */
@CompileStatic
class UiGenerationException extends RuntimeException {
	
	public UiGenerationException() {
		super()
	}
	
	public UiGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace)
	}
	
	public UiGenerationException(String message, Throwable cause) {
		super(message, cause)
	}
	
	public UiGenerationException(String message) {
		super(message)
	}
	
	public UiGenerationException(Throwable cause) {
		super(cause)
	}
}