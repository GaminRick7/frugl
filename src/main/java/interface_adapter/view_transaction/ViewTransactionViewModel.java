package interface_adapter.view_transaction;

import interface_adapter.ViewModel;

/**
 * View Model for View Transaction Use Case.
 */
public class ViewTransactionViewModel extends ViewModel<ViewTransactionState> {

    public static final String VIEW_NAME = "view transactions";

    public ViewTransactionViewModel() {
        super(VIEW_NAME);
        setState(new ViewTransactionState());

    }
}
