package view;

import interface_adapter.import_statement.ImportStatementController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class ImportStatementView extends JPanel implements ActionListener {

    private final ImportStatementController controller;

    private final JTextField filePathField;
    private final JButton importButton;

    public ImportStatementView(ImportStatementController controller) {
        this.controller = controller;

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


        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(pathPanel);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(importButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == importButton) {
            String filePath = filePathField.getText().trim();
            controller.execute(filePath);
        }
    }


}
