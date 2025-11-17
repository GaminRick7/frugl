package app;

import data_access.TransactionDataAccessObject;
import interface_adapter.autosave.AutosaveController;
import interface_adapter.autosave.AutosavePresenter;
import interface_adapter.autosave.AutosaveViewModel;
import use_case.autosave.AutosaveInputBoundary;
import use_case.autosave.AutosaveInteractor;
import use_case.autosave.AutosaveOutputBoundary;
import view.AutosaveStatusView;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.CardLayout;

public class AppBuilder {

    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();

    private AutosaveStatusView autosaveStatusView;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addAutosaveFeature() {
        AutosaveViewModel viewModel = new AutosaveViewModel();
        AutosaveOutputBoundary presenter = new AutosavePresenter(viewModel);
        TransactionDataAccessObject autosaveDao = new TransactionDataAccessObject();
        AutosaveInputBoundary interactor = new AutosaveInteractor(autosaveDao, presenter);
        AutosaveController controller = new AutosaveController(interactor);
        autosaveStatusView = new AutosaveStatusView(controller, viewModel);

        cardPanel.add(autosaveStatusView, getAutosaveViewName());
        return this;
    }

    public JFrame build() {
        if (autosaveStatusView == null) {
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
        return autosaveStatusView.getClass().getSimpleName();
    }
}


