package interface_adapter.view_transaction;

import interface_adapter.ViewModel;

public class ViewTransactionViewModel extends ViewModel<ViewTransactionState> {

    public ViewTransactionViewModel() {
        super("view transaction");
        setState(new ViewTransactionState());

    }
}
