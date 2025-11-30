package interface_adapter.view_transaction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A helper class to handle the layout of a single transaction row.
 */
public class TransactionRowPanel extends JPanel {
    private static final int NUM_COLUMNS = 4;
    private static final int ROW_HEIGHT = 40;

    /**
     * Constructs a panel for a single transaction row.
     * @param date     The date of the transaction
     * @param source   The source of the transaction
     * @param category The category of the transaction
     * @param amount   The amount of the transaction
     */
    public TransactionRowPanel(String date, String source, String category, String amount) {
        this.setLayout(new GridLayout(1, NUM_COLUMNS));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));

        this.add(new JLabel(date));
        this.add(new JLabel(source));
        this.add(new JLabel(category));
        this.add(new JLabel(amount));
    }
}

