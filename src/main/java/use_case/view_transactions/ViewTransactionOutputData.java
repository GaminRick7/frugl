package use_case.view_transactions;

import java.util.List;
import java.util.Map;

public class ViewTransactionOutputData {

    // Convert list into string
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

    /**
     * This is a function to extract each tile (transaction object) from the String.
     * @param tileNumber Number of times.
     * @return String to object map.
     */
    public Map<String, Object> getTransactionByIndex(int tileNumber) {
        Map<String, Object> result = null;

        if (!monthlyTransactions.isEmpty() && tileNumber < monthlyTransactions.size()) {
            result = monthlyTransactions.get(tileNumber);
        }

        return result;
    }
}
