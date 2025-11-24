package use_case.import_statement;

import java.time.YearMonth;

/**
 * Output Data for the Import Bank Statement Use Case.
 */

public class ImportStatementOutputData {
    private final YearMonth statementMonth;

    public ImportStatementOutputData(YearMonth statementMonth) {
        this.statementMonth = statementMonth;
    }

    public YearMonth getStatementMonth() {
        return statementMonth;
    }
}
