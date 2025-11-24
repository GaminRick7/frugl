package use_case.import_statement;

/**
 * The input data for the Import Bank Statement Use Case.
 */
public class ImportStatementInputData {

    private final String filePath;

    public ImportStatementInputData(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
