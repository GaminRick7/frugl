package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import interface_adapter.ViewManagerModel;
import interface_adapter.import_statement.ImportStatementController;
import interface_adapter.import_statement.ImportStatementViewModel;

/**
 * The Import Bank Statement View.
 */
public class ImportStatementView extends JPanel implements ActionListener, PropertyChangeListener {
    private static final int BORDER_PADDING = 15;
    private static final int TITLE_FONT_SIZE = 16;
    private static final int PATH_FIELD_COLUMNS = 20;
    private static final int HORIZONTAL_GAP = 10;
    private static final int VERTICAL_GAP_LARGE = 15;
    private static final int VERTICAL_GAP_SMALL = 10;

    private final String viewName = "import statement";

    private final ImportStatementViewModel importStatementViewModel;

    private final ViewManagerModel viewManagerModel;

    private ImportStatementController importStatementController;

    private final JTextField filePathField;

    private final JButton importButton;

    private final JButton backButton;

    private final JButton browseButton;

    public ImportStatementView(ImportStatementViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.importStatementViewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        importStatementViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(
                BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));

        final JLabel title = new JLabel("Import Bank Statement");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, TITLE_FONT_SIZE));

        filePathField = new JTextField(PATH_FIELD_COLUMNS);
        filePathField.setEditable(false);

        browseButton = new JButton("Browse");
        browseButton.addActionListener(this);

        importButton = new JButton("Import");
        importButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        importButton.addActionListener(this);

        backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(this);

        final JPanel pathPanel = createPathPanel();

        addComponents(title, pathPanel);
    }

    private JPanel createPathPanel() {
        final JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.X_AXIS));

        final JLabel filePathLabel = new JLabel("File path: ");

        pathPanel.add(filePathLabel);
        pathPanel.add(Box.createRigidArea(new Dimension(HORIZONTAL_GAP, 0)));
        pathPanel.add(filePathField);
        pathPanel.add(Box.createRigidArea(new Dimension(HORIZONTAL_GAP, 0)));
        pathPanel.add(browseButton);

        return pathPanel;
    }

    private void addComponents(JLabel title, JPanel pathPanel) {
        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, VERTICAL_GAP_LARGE)));
        this.add(pathPanel);
        this.add(Box.createRigidArea(new Dimension(0, VERTICAL_GAP_LARGE)));
        this.add(importButton);
        this.add(Box.createRigidArea(new Dimension(0, VERTICAL_GAP_SMALL)));
        this.add(backButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseButton) {
            final JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Bank Statement JSON File");

            final int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = chooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        }
        else if (e.getSource() == importButton) {
            final String filePath = filePathField.getText().trim();
            importStatementController.execute(filePath);
        }
        else if (e.getSource() == backButton) {
            viewManagerModel.setState("dashboard");
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

}
