package use_case.view_transactions;

import java.util.List;
import java.util.Map;

public class ViewTransactionOutputData {

    private final String yearMonthStr;
    private final List<Map<String, Object>> monthlyTransactions;

    public ViewTransactionOutputData(String yearMonthStr, List<Map<String, Object>> monthlyTransactions) {
        this.yearMonthStr = yearMonthStr;
        this.monthlyTransactions = monthlyTransactions;
    }

    public List<Map<String, Object>> getMonthTransactions() {
        return monthlyTransactions;
    }

    public String getYearMonth() {
        return yearMonthStr;
    }
}
