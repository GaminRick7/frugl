package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import entity.GoalTree;
import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.set_goal.SetGoalController;
import interface_adapter.set_goal.SetGoalState;
import interface_adapter.set_goal.SetGoalViewModel;

public class GoalView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final Color FOREST_BACKGROUND_COLOR = new Color(191, 246, 191);
    private static final int VERTICAL_STRUT = 10;
    private static final int COLUMN_MONTH = 0;
    private static final int COLUMN_AMOUNT = 1;
    private static final int COLUMN_CATEGORIES = 2;
    private static final int COLUMN_STATUS = 3;

    private final transient SetGoalViewModel viewModel;
    private transient SetGoalController controller;

    private final ForestPanel forestPanel;
    private final JButton setGoalButton;

    private final JTable goalTable;
    private final GoalTableModel tableModel;

    public GoalView(SetGoalViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout());

        this.add(UserInterfaceFactory.createHeader(SetGoalState.TITLE_LABEL), BorderLayout.NORTH);

        forestPanel = new ForestPanel();
        this.add(forestPanel, BorderLayout.CENTER);

        tableModel = new GoalTableModel();
        goalTable = new JTable(tableModel);
        goalTable.setAutoCreateRowSorter(true);

        final JScrollPane tableScrollPane = new JScrollPane(goalTable);
        this.add(tableScrollPane, BorderLayout.EAST);
        // ---------------------------------

        setGoalButton = UserInterfaceFactory.createButton(SetGoalState.SET_GOAL_BUTTON_LABEL, this);
        UserInterfaceFactory.stylePrimaryButton(setGoalButton);
        final JPanel buttonPanel = new JPanel();
        buttonPanel.add(setGoalButton);

        final JButton backButton = UserInterfaceFactory.createButton("Back", evt -> {
            viewManagerModel.setState(DashboardViewModel.VIEW_NAME);
            viewManagerModel.firePropertyChange();
        });
        UserInterfaceFactory.styleSecondaryButton(backButton);
        buttonPanel.add(backButton);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(setGoalButton)) {
            AddGoalDialog.show(this, controller);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            final SetGoalState state = viewModel.getState();

            // NEW: Update the table data
            tableModel.setGoals(state.getForest());

            forestPanel.repaint();

            if (state.getErrorMessage() != null) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (state.getSuccessMessage() != null) {
                JOptionPane.showMessageDialog(this, state.getSuccessMessage(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void setGoalController(SetGoalController newController) {
        this.controller = newController;
    }

    // Inner class for rendering the forest
    private class ForestPanel extends JPanel {
        private transient BufferedImage saplingImage;
        private transient BufferedImage healthyImage;
        private transient BufferedImage deadImage;

        ForestPanel() {
            this.setBackground(FOREST_BACKGROUND_COLOR);
            loadImages();
        }

        private void loadImages() {
            try {
                // NOTE: Using 'new File()' may cause issues when packaged as a JAR.
                // Consider using getClass().getResource() for production code.
                saplingImage = javax.imageio.ImageIO.read(new File("images/sapling.png"));
                healthyImage = javax.imageio.ImageIO.read(new File("images/healthy.png"));
                deadImage = javax.imageio.ImageIO.read(new File("images/dead.png"));
            }
            catch (IOException error) {
                System.err.println("Error loading images: " + error.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            final List<GoalTree> forest = viewModel.getState().getForest();
            if (forest != null) {
                for (GoalTree tree : forest) {
                    drawTree(g2d, tree);
                }
            }
        }

        private void drawTree(Graphics2D g2d, GoalTree tree) {
            final int x = tree.getxCoordinate();
            final int y = tree.getyCoordinate();
            final BufferedImage img;
            switch (tree.getStatus()) {
                case "healthy" -> img = healthyImage;
                case "dead" -> img = deadImage;
                default -> img = saplingImage;
            }
            if (img != null) {
                g2d.drawImage(img, x - img.getWidth() / 2, y - img.getHeight(), null);
            }
        }
    }

    private final class GoalTableModel extends AbstractTableModel {

        private List<GoalTree> forest = new ArrayList<>();
        private final String[] columnNames = {"Month", "Amount", "Categories", "Status"};

        @Override
        public int getRowCount() {
            return forest.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final Object value;

            if (rowIndex < 0 || rowIndex >= forest.size()) {
                value = null;
            }
            else {
                final GoalTree tree = forest.get(rowIndex);

                switch (columnIndex) {
                    case COLUMN_MONTH:
                        value = tree.getGoal().getMonth().toString();
                        break;
                    case COLUMN_AMOUNT:
                        value = "$" + String.format("%.2f", tree.getGoal().getGoalAmount());
                        break;
                    case COLUMN_CATEGORIES:
                        value = tree.getGoal().getCategories().stream()
                                .map(category -> category.getName())
                                .reduce((stringa, stringb) -> stringa + ", " + stringb)
                                .orElse("");
                        break;
                    case COLUMN_STATUS:
                        value = tree.getStatus();
                        break;
                    default:
                        value = null;
                        break;
                }
            }
            return value;
        }
        /**
         * Updates the data in the table and notifies the JTable to redraw.
         * @param newForest The list of GoalTree entities to display.
         */

        public void setGoals(List<GoalTree> newForest) {
            if (newForest == null) {
                this.forest = new ArrayList<>();
            }
            else {
                this.forest = newForest;
            }
            fireTableDataChanged();
        }
    }

}
