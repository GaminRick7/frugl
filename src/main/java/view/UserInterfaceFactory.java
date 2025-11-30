package view;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UserInterfaceFactory {

    private static final int PADDING = 10;
    /**
     * Creates a JLabel with the specified text, centered alignment, and font size.
     * Adds padding around the label using an empty border.
     *
     * @param text the text to display on the label
     * @param fontSize the font size to apply to the label text
     * @return a styled JLabel instance
     */

    public static JLabel createTitleLabel(String text, int fontSize) {
        final JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, fontSize));
        label.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        return label;
    }
    /**
     * Creates a JButton with the specified text and attaches an ActionListener.
     *
     * @param text the text to display on the button
     * @param listener the ActionListener to attach to the button
     * @return a JButton instance with the specified text and listener
     */

    public static JButton createButton(String text, ActionListener listener) {
        final JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }
}
