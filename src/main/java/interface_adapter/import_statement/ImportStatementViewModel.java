package interface_adapter.import_statement;

import interface_adapter.ViewModel;

/**
 * The Import Bank Statement ViewModel.
 */
public class ImportStatementViewModel extends ViewModel<String> {

    public static final String VIEW_NAME = "import statement";

    public ImportStatementViewModel() {
        super(VIEW_NAME);
        this.setState("");
    }
}
