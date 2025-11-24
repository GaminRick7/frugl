package view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import interface_adapter.autosave.AutosaveController;
import interface_adapter.autosave.AutosaveViewModel;

public class AutosaveView extends JPanel implements PropertyChangeListener {

    private AutosaveController controller;

    private final AutosaveViewModel viewModel;

    private final JLabel statusLabel = new JLabel();

    private final JButton saveNowButton = new JButton("Save Now");

    private final Timer autosaveTimer;

    public AutosaveView(AutosaveViewModel viewModel) {
        this.viewModel = viewModel;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(statusLabel);
        add(Box.createHorizontalStrut(8));
        add(saveNowButton);

        statusLabel.setText(labelText(this.viewModel.getStatusMessage(), this.viewModel.getLastSavedAt(),
                this.viewModel.getErrorMessage()));

        this.viewModel.addPropertyChangeListener(this);
        saveNowButton.addActionListener(e -> controller.autosaveNow());

        autosaveTimer = new Timer(5_000, e -> this.controller.autosaveNow());
        autosaveTimer.setRepeats(true);
        autosaveTimer.start();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"autosaveState".equals(evt.getPropertyName())) {
            return;
        }

        AutosaveViewModel state = (AutosaveViewModel) evt.getNewValue();
        statusLabel.setText(labelText(state.getStatusMessage(), state.getLastSavedAt(), state.getErrorMessage()));
    }

    private String labelText(String statusMessage, LocalDateTime timestamp, String errorMessage) {
        if (errorMessage != null) {
            return "Autosave failed: " + errorMessage;
        }
        if (timestamp != null) {
            return statusMessage + " (" + timestamp + ")";
        }
        return statusMessage;
    }

    public void setupAutosaveConntroller(AutosaveController controller) {
        this.controller = controller;
    }
}


