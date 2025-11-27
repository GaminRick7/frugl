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
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ImportStatementView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "import statement";
    private final ImportStatementViewModel importStatementViewModel;
    private final ViewManagerModel viewManagerModel;
    private ImportStatementController importStatementController = null;

    private final JTextField filePathField;
    private final JButton chooseFileButton;
    private final JButton importButton;
    private final JButton backButton;
    private JDialog analyzingDialog;

    public ImportStatementView(ImportStatementViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.importStatementViewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.importStatementViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Import Bank Statement");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.X_AXIS));

        JLabel filePathLabel = new JLabel("Selected file: ");
        filePathField = new JTextField(20);
        filePathField.setEditable(false);

        chooseFileButton = new JButton("Choose File");
        chooseFileButton.addActionListener(e -> openFileChooser());

        pathPanel.add(filePathLabel);
        pathPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        pathPanel.add(filePathField);
        pathPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        pathPanel.add(chooseFileButton);

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
            if (importStatementController != null) {
                if (filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please choose a statement file first.", "No File Selected",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                startAnalyzingAnimation();
                importButton.setEnabled(false);
                chooseFileButton.setEnabled(false);
                backButton.setEnabled(false);
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        importStatementController.execute(filePath);
                        return null;
                    }

                    @Override
                    protected void done() {
                        stopAnalyzingAnimation();
                        importButton.setEnabled(true);
                        chooseFileButton.setEnabled(true);
                        backButton.setEnabled(true);
                        try {
                            get();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        } catch (ExecutionException ex) {
                            JOptionPane.showMessageDialog(ImportStatementView.this,
                                    "Import failed: " + ex.getCause().getMessage(),
                                    "Import Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
            }
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

    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startAnalyzingAnimation() {
        if (analyzingDialog == null) {
            Window owner = SwingUtilities.getWindowAncestor(this);
            analyzingDialog = new JDialog(owner, "Analyzing Transactions", Dialog.ModalityType.MODELESS);
            analyzingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            analyzingDialog.setResizable(false);

            JPanel content = new JPanel(new BorderLayout(10, 10));
            content.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
            JLabel label = new JLabel("Analyzing transactions...");
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);

            content.add(label, BorderLayout.NORTH);
            content.add(progressBar, BorderLayout.CENTER);
            analyzingDialog.setContentPane(content);
            analyzingDialog.pack();

            if (owner != null) {
                analyzingDialog.setLocationRelativeTo(owner);
            }
        }
        analyzingDialog.setVisible(true);
    }

    private void stopAnalyzingAnimation() {
        if (analyzingDialog != null) {
            analyzingDialog.setVisible(false);
        }
    }

}
