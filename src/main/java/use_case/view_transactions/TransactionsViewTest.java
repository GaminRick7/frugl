package use_case.view_transactions;

import interface_adapter.ViewManagerModel; // IMPORT THIS
import interface_adapter.view_transaction.ViewTransactionController;
import interface_adapter.view_transaction.ViewTransactionPresenter;
import interface_adapter.view_transaction.ViewTransactionState;
import interface_adapter.view_transaction.ViewTransactionViewModel;
import view.TransactionsView;

import javax.swing.*;
import java.util.ArrayList;

public class TransactionsViewTest {

    public static void main(String[] args) {

        // 1. SETUP DATA LAYER (The Mock DAO)
        // Make sure this matches the class name you created earlier
        MockTransactionDAO userDataAccessObject = new MockTransactionDAO();

        // 2. SETUP PRESENTATION LAYER
        ViewTransactionViewModel viewModel = new ViewTransactionViewModel();

        // --- FIX START ---
        // You need to create this because your Presenter requires it
        ViewManagerModel viewManagerModel = new ViewManagerModel();

        // Pass BOTH models to the Presenter
        ViewTransactionPresenter presenter = new ViewTransactionPresenter(viewManagerModel, viewModel);
        // --- FIX END ---

        // 3. SETUP USE CASE LAYER
        ViewTransactionInteractor interactor = new ViewTransactionInteractor(userDataAccessObject, presenter);

        // 4. SETUP CONTROLLER
        ViewTransactionController controller = new ViewTransactionController(interactor);

        // 5. SETUP VIEW
        JFrame app = new JFrame("Test Transaction View");
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setSize(850, 500);

        // Create the View
        TransactionsView view = new TransactionsView(viewModel, app);
        view.setViewTransactionController(controller);

        // 6. INITIALIZATION
        // Set an initial state so the view isn't blank
        ViewTransactionState state = new ViewTransactionState();
        state.setMonth("2025-02");
        viewModel.setState(state);

        app.add(view);
        app.setVisible(true);

        // 7. TRIGGER TEST
        // This simulates the user asking for data immediately
        System.out.println("Test: Requesting data for Feb 2025...");
        controller.execute("2025-02");
    }
}