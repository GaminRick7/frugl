package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import entity.Category;
import entity.GoalTree;
import interface_adapter.set_goal.SetGoalController;
import interface_adapter.set_goal.SetGoalState;
import interface_adapter.set_goal.SetGoalViewModel;

public class GoalView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "goal view";


    private final SetGoalViewModel viewModel;

    private SetGoalController controller;


    // Components
    private final ForestPanel forestPanel;

    private final JButton setGoalButton;

    // Pixel art tree images
    private BufferedImage saplingImage;

    private BufferedImage healthyImage;

    private BufferedImage deadImage;

    public GoalView(SetGoalViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        loadImages();

        this.setLayout(new BorderLayout());

        JLabel title = new JLabel(SetGoalState.TITLE_LABEL, SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(title, BorderLayout.NORTH);

        // Add the forest
        forestPanel = new ForestPanel();
        this.add(forestPanel, BorderLayout.CENTER);

        // Add the button for new goals
        JPanel buttons = new JPanel();
        setGoalButton = new JButton(SetGoalState.SET_GOAL_BUTTON_LABEL);
        setGoalButton.addActionListener(this);
        buttons.add(setGoalButton);
        this.add(buttons, BorderLayout.SOUTH);
    }

    private void loadImages() {
        try {
            saplingImage = ImageIO.read(new File("images/sapling.png"));
            healthyImage = ImageIO.read(new File("images/healthy.png"));
            deadImage = ImageIO.read(new File("images/dead.png"));
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private class ForestPanel extends JPanel {
        public ForestPanel() {
            this.setBackground(new Color(191, 246, 191));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            SetGoalState state = viewModel.getState();
            List<GoalTree> forest = state.getForest();

            // Iterate through each tree to render all of them
            if (forest != null) {
                for (GoalTree tree : forest) {
                    drawTree(g2d, tree);
                }
            }
        }

        private void drawTree(Graphics2D g2, GoalTree tree) {
            int x = tree.getXCoordinate();
            int y = tree.getYCoordinate();
            String status = tree.getStatus();

            BufferedImage imgToDraw = null;

            // Select appropriate image
            switch (status) {
                case "healthy":
                    imgToDraw = healthyImage;
                    break;
                case "dead":
                    imgToDraw = deadImage;
                    break;
                case "sapling":
                default:
                    imgToDraw = saplingImage;
                    break;
            }
            // Draw the image
            if (imgToDraw != null) {
                int width = imgToDraw.getWidth();
                int height = imgToDraw.getHeight();
                g2.drawImage(imgToDraw, x - width / 2, y - height, null);
            }
        }

    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(setGoalButton)) {
            showAddGoalDialog();
        }
    }

    private void showAddGoalDialog() {
        JTextField monthField = new JTextField(10);
        JTextField amountField = new JTextField(10);
        JTextField categoriesField = new JTextField(10);

        monthField.setText(YearMonth.now().toString());

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
        myPanel.add(new JLabel("Month (YYYY-MM):"));
        myPanel.add(monthField);
        myPanel.add(Box.createVerticalStrut(10));
        myPanel.add(new JLabel("Goal Amount ($):"));
        myPanel.add(amountField);
        myPanel.add(Box.createVerticalStrut(10));
        myPanel.add(new JLabel("Categories (comma separated):"));
        myPanel.add(categoriesField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Plant a New Goal Tree", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                YearMonth yearMonth = YearMonth.parse(monthField.getText());
                float amount = Float.parseFloat(amountField.getText());
                String[] catNames = categoriesField.getText().split(",");
                List<Category> categories = new ArrayList<>();
                for (String name : catNames) {
                    if (!name.trim().isEmpty()) {
                        categories.add(new Category(name.trim(), ""));
                    }
                }
                controller.setGoal(yearMonth, amount, categories);

            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid Date. Please Use YYYY-MM.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Goal Amount. Please enter a valid number.");
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            forestPanel.repaint();

            SetGoalState state = viewModel.getState();
            if (state.getErrorMessage() != null) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (state.getSuccessMessage() != null) {
                JOptionPane.showMessageDialog(this, state.getSuccessMessage(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void setGoalController(SetGoalController setGoalController) {
        this.controller = setGoalController;
    }
}
