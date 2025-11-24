package app;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import data_access.TransactionDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.autosave.AutosaveController;
import interface_adapter.autosave.AutosavePresenter;
import interface_adapter.autosave.AutosaveViewModel;
import interface_adapter.import_statement.ImportStatementController;
import interface_adapter.import_statement.ImportStatementPresenter;
import use_case.autosave.AutosaveInputBoundary;
import use_case.autosave.AutosaveInteractor;
import use_case.autosave.AutosaveOutputBoundary;
import view.AutosaveView;
import view.ImportStatementView;

public class AppBuilder {

    private final JPanel cardPanel = new JPanel();

    private final CardLayout cardLayout = new CardLayout();

    private final TransactionDataAccessObject transactionDataAccessObject = new TransactionDataAccessObject();

    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    private AutosaveView autosaveView;
    private AutosaveViewModel autosaveViewModel;

    private ImportStatementView importStatementView;
    private ImportStatementViewModel importStatementViewModel;


    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addAutosaveView() {
        autosaveViewModel = new AutosaveViewModel();
        autosaveView = new AutosaveView(autosaveViewModel);

        cardPanel.add(autosaveView, getAutosaveViewName());
        return this;
    }

    public  AppBuilder addAutosaveUseCase() {
        final AutosaveOutputBoundary autosaveOutputBoundary = new AutosavePresenter(autosaveViewModel);
        final AutosaveInputBoundary autosaveInputBoundary = new AutosaveInteractor(transactionDataAccessObject, autosaveOutputBoundary);
        AutosaveController controller = new AutosaveController(autosaveInputBoundary);

        autosaveView.setupAutosaveConntroller(controller);
        return this;
    }

    public AppBuilder addImportStatementView() {
        importStatementViewModel = new ImportStatementViewModel();
        importStatementView = new ImportStatementView(importStatementViewModel);

        cardPanel.add(importStatementView, importStatementView.getViewName());
        return this;
    }

    public AppBuilder addImportStatementUseCase() {
        final ImportStatementOutputBoundary importStatementOutputBoundary = new ImportStatementPresenter(viewManagerModel,
                importStatementViewModel, dashboardViewModel, transactionsViewModel, goalsViewModel);
        final ImportStatementInputBoundary importStatementInputBoundary = new ImportStatementInteractor(transactionDataAccessObject, importStatementOutputBoundary);
        ImportStatementController importStatementController = new ImportStatementController(importStatementInputBoundary, viewManagerModel);

        importStatementView.setImportStatementController(importStatementController);
        return this;

    }

    public JFrame build() {
        if (autosaveView == null) {
            throw new IllegalStateException("Call addAutosaveFeature() before build().");
        }
        JFrame frame = new JFrame("Frugl");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(cardPanel);
        cardLayout.show(cardPanel, getAutosaveViewName());
        frame.pack();
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private String getAutosaveViewName() {
        return autosaveView.getClass().getSimpleName();
    }
}


