package interface_adapter.view_transaction;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The State for the View Transaction Use Case.
 */
public class ViewTransactionState {
    private String currentYearMonth = LocalDate.now().toString();
    private YearMonth selectedMonth;
    private String noDataError;
    private List<Map<String, Object>> monthlyTransactions = new ArrayList<>();

    public String getYearMonthDisplay() {
        return currentYearMonth;
    }

    public YearMonth getSelectedMonth() {
        return selectedMonth;
    }

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
     * Set the month.
     * @param newCurrentYearMonth Current year and month as a string.
     */
    public void setMonth(String newCurrentYearMonth) {
        this.currentYearMonth = newCurrentYearMonth;
        this.selectedMonth = YearMonth.parse(newCurrentYearMonth);
    }

}
