package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.import_statement.ImportStatementController;
import interface_adapter.import_statement.ImportStatementViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ImportStatementView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "import statement";
    private final ImportStatementViewModel importStatementViewModel;
    private final ViewManagerModel viewManagerModel;
    private ImportStatementController importStatementController = null;

    private final JTextField filePathField;
    private final JButton importButton;
    private final JButton backButton;

    public ImportStatementView(ImportStatementViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.importStatementViewModel = viewModel;
        this.viewManagerModel = viewManagerModel;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Import Bank Statement");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.X_AXIS));

        JLabel filePathLabel = new JLabel("File path: ");
        filePathField = new JTextField(20);

        pathPanel.add(filePathLabel);
        pathPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        pathPanel.add(filePathField);

        importButton = new JButton("Import");
        importButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        importButton.addActionListener(this);

        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(this);

        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(pathPanel);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(importButton);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == importButton) {
            String filePath = filePathField.getText().trim();
            importStatementController.execute(filePath);
        }
        else if (e.getSource() == backButton) {
            viewManagerModel.setState("autosave");
            viewManagerModel.firePropertyChange();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("filePath".equals(evt.getPropertyName())) {
            filePathField.setText((String) evt.getNewValue());
        }
    }

    public void setImportStatementController(ImportStatementController controller) {
        importStatementController = controller;
    }

    public String getViewName() {
        return viewName;
    }

}
