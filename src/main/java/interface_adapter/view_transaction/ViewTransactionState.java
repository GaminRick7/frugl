package interface_adapter.view_transaction;


import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The state for the View Transaction View Model
 */
public class ViewTransactionState {
    private String yearMonthDisplay = "2025-11";
    private YearMonth selectedMonth;
    private String noDataError;
    private List<HashMap<String, Object>> monthlyTransactions = new ArrayList<>();
    public String getYearMonthDisplay() {return yearMonthDisplay;}

    public YearMonth getSelectedMonth() {return selectedMonth;}

    public List<HashMap<String, Object>>  getMonthlyTransactions() {return monthlyTransactions;}

    public void  setMonthlyTransactions(List<HashMap<String, Object>> monthlyTransactions) {this.monthlyTransactions = monthlyTransactions;}


    public String getDataError() {return noDataError;}

    public void setDataError(String msg) { this.noDataError = msg;}

    public void setMonth(String yearMonthDisplay) {
        this.yearMonthDisplay = yearMonthDisplay;
        this.selectedMonth = YearMonth.parse(yearMonthDisplay);
    }

}
