package interface_adapter.view_transaction;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The state for the View Transaction View Model.
 */
public class ViewTransactionState {
    private String yearMonthDisplay = "2025-11";
    private YearMonth selectedMonth;
    private String noDataError;
    private List<Map<String, Object>> monthlyTransactions = new ArrayList<>();

    public List<Map<String, Object>> getMonthlyTransactions() {
        return monthlyTransactions;
    }

    public void setMonthlyTransactions(List<Map<String, Object>> monthlyTransactions) {
        this.monthlyTransactions = monthlyTransactions;
    }

    public String getDataError() {
        return noDataError;
    }

    public void setDataError(String msg) {
        this.noDataError = msg;
    }

    /**
     * Set month for transaction.
     * @param monthToDisplay is the year displayed
     */
    public void setMonth(String monthToDisplay) {
        this.yearMonthDisplay = monthToDisplay;
        this.selectedMonth = YearMonth.parse(monthToDisplay);
    }

}
