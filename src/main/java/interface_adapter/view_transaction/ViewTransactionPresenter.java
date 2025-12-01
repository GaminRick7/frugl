package interface_adapter.view_transaction;

import interface_adapter.ViewManagerModel;
import use_case.view_transactions.ViewTransactionOutputBoundary;
import use_case.view_transactions.ViewTransactionOutputData;

/**
 * Presenter for the View Transaction Use Case.
 */
public class ViewTransactionPresenter implements ViewTransactionOutputBoundary {

    private final ViewTransactionViewModel viewTransactionViewModel;
    private final ViewManagerModel viewManagerModel;

    public ViewTransactionPresenter(ViewManagerModel viewManagerModel,
                             ViewTransactionViewModel viewTransactionViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.viewTransactionViewModel = viewTransactionViewModel;
    }

    @Override
    public void prepareSuccessView(ViewTransactionOutputData result) {
        final ViewTransactionState viewTransactionState = viewTransactionViewModel.getState();
        viewTransactionState.setMonthlyTransactions(result.getMonthTransactions());
        viewTransactionState.setMonth(result.getYearMonth());
        viewTransactionState.setDataError(null);
        // Clear any old error

        this.viewTransactionViewModel.setState(viewTransactionState);

        this.viewTransactionViewModel.firePropertyChange();

        this.viewManagerModel.setState(viewTransactionViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();

    }

    @Override
    public void prepareFailView(String error) {
        final ViewTransactionState viewTransactionState = viewTransactionViewModel.getState();
        viewTransactionState.setDataError(error);

        viewTransactionState.setMonthlyTransactions(null);

        this.viewTransactionViewModel.setState(viewTransactionState);

        viewTransactionViewModel.firePropertyChange();
    }
}
