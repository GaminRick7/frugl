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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import entity.GoalTree;
import interface_adapter.set_goal.SetGoalController;
import interface_adapter.set_goal.SetGoalState;
import interface_adapter.set_goal.SetGoalViewModel;

public class GoalView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final int TITLE_FONT_SIZE = 24;
    private static final Color FOREST_BACKGROUND_COLOR = new Color(191, 246, 191);
    private static final int VERTICAL_STRUT = 10;

    private final SetGoalViewModel viewModel;
    private SetGoalController controller;

    private final ForestPanel forestPanel;
    private final JButton setGoalButton;

    public GoalView(SetGoalViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout());

        final JLabel title = UserInterfaceFactory.createTitleLabel(SetGoalState.TITLE_LABEL, TITLE_FONT_SIZE);
        this.add(title, BorderLayout.NORTH);

        forestPanel = new ForestPanel();
        this.add(forestPanel, BorderLayout.CENTER);

        setGoalButton = UserInterfaceFactory.createButton(SetGoalState.SET_GOAL_BUTTON_LABEL, this);
        final JPanel buttonPanel = new JPanel();
        buttonPanel.add(setGoalButton);
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
            forestPanel.repaint();
            final SetGoalState state = viewModel.getState();
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
        private BufferedImage saplingImage;
        private BufferedImage healthyImage;
        private BufferedImage deadImage;

        ForestPanel() {
            this.setBackground(FOREST_BACKGROUND_COLOR);
            loadImages();
        }

        private void loadImages() {
            try {
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
}
