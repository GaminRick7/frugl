package use_case.view_transactions;
import java.util.HashMap;
import java.util.ArrayList;


public class ViewTransactionOutputData {

    /// Convert list into string

    private final String yearMonthStr;
    private ArrayList<HashMap<String, Object>> monthlyTransactions;

    public ViewTransactionOutputData(String yearMonthStr, ArrayList<HashMap<String, Object>> monthlyTransactions) {
        this.yearMonthStr = yearMonthStr;
        this.monthlyTransactions = monthlyTransactions;
    }

    public ArrayList<HashMap<String, Object>> getMonthTransactions() {
        return monthlyTransactions;
    }

    public String getYearMonth() { return yearMonthStr; }


    /**
     * This is a function to extract each tile (transaction object) from the String
     * @param tileNum
     * @return
     */
    public HashMap<String, Object> getTransactionByIndex(int tileNum) {
        if (!monthlyTransactions.isEmpty() && tileNum < monthlyTransactions.size()) {
            return monthlyTransactions.get(tileNum);
        }
        return null;

    }
}
