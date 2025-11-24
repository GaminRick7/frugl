package interface_adapter.import_statement;

import interface_adapter.ViewModel;

public class ImportStatementViewModel extends ViewModel<String> {

    public ImportStatementViewModel() {
        super("import statement");
        this.setState("");
    }
}