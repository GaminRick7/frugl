package use_case.import_statement;

/**
 * Exception type for errors encountered while categorizing sources with Gemini.
 */
public class GeminiCategorizerException extends Exception {

    public GeminiCategorizerException(String message) {
        super(message);
    }

    public GeminiCategorizerException(String message, Throwable cause) {
        super(message, cause);
    }
}
