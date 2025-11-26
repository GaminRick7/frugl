package use_case.view_transactions;



import interface_adapter.ViewManagerModel;
import interface_adapter.view_transaction.ViewTransactionController;
import interface_adapter.view_transaction.ViewTransactionPresenter;
import interface_adapter.view_transaction.ViewTransactionState;
import interface_adapter.view_transaction.ViewTransactionViewModel;
import view.TransactionsView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import view.TransactionsView;


public class TransactionsViewTest {

    public static void main(String[] args) {
        // 1. Create the Main Frame (Parent)
        JFrame app = new JFrame("Test Transaction View");
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setSize(850, 500);

        // 2. Create the View Model
        ViewTransactionViewModel viewModel = new ViewTransactionViewModel();

        // 3. Create Initial Mock Data (The "Fake Database" for default view)
        List<HashMap<String, Object>> initialTransactions = new ArrayList<>();

        HashMap<String, Object> t1 = new HashMap<>();
        t1.put("date", "2025-11-01");
        t1.put("source", "Starbucks");
        t1.put("category", "Food");
        t1.put("amount", 15.50);

        HashMap<String, Object> t2 = new HashMap<>();
        t2.put("date", "2025-11-05");
        t2.put("source", "Uber");
        t2.put("category", "Transportation");
        t2.put("amount", 45.20);

        initialTransactions.add(t1);
        initialTransactions.add(t2);

        // 4. Load Initial State
        ViewTransactionState state = new ViewTransactionState();
        state.setMonthlyTransactions(initialTransactions);
        state.setMonth("2025-11");



////        state.setMonth("2025-11");
////        viewModel.setState(state);


        viewModel.setState(state);

        // 5. Create the View
        TransactionsView view = new TransactionsView(viewModel, app);

        // 6. Create Dynamic Mock Controller
        // This simulates the Interactor + Presenter logic
        ViewTransactionController mockController = new ViewTransactionController(null) {
            @Override
            public void execute(String monthString) {
                System.out.println("Controller Executed with: " + monthString);

                // Get current state to update
                ViewTransactionState currentState = viewModel.getState();
                List<HashMap<String, Object>> newData = new ArrayList<>();

                if ("2025-01".equals(monthString)) {
                    // SIMULATE FAILURE (Empty Data)
                    System.out.println("Simulating No Data for 2025-01...");
                    // In your real app logic, the Presenter would set error and clear transactions
                    // Assuming you added an 'error' field to state, or just empty list implies error in your view logic
                    currentState.setMonthlyTransactions(new ArrayList<>());
                    // If you have an error field: currentState.setError("No data found");

                } else if ("2024-06".equals(monthString)) {
                    // SIMULATE DIFFERENT DATA
                    System.out.println("Simulating June Data...");
                    HashMap<String, Object> juneTrans = new HashMap<>();
                    juneTrans.put("date", "2024-06-15");
                    juneTrans.put("source", "Summer Job");
                    juneTrans.put("category", "Income");
                    juneTrans.put("amount", 500.00);
                    newData.add(juneTrans);

                    currentState.setMonthlyTransactions(newData);

                } else {
                    // DEFAULT / SUCCESS (e.g. November)
                    System.out.println("Simulating Default Data...");
                    currentState.setMonthlyTransactions(initialTransactions);
                }

                // UPDATE STATE & FIRE EVENT
                // Ideally update the month display too
                currentState.setMonth(monthString);

                viewModel.setState(currentState);
                viewModel.firePropertyChange(); // Triggers View update!
            }
        };

        view.setViewTransactionController(mockController);

        // 7. Add View to App and Display
        app.add(view);
        app.setVisible(true);

        // 8. Fire Property Change to trigger the initial draw
        viewModel.firePropertyChange();
    }
}