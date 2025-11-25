package use_case.view_transactions;

import entity.Transaction;
import java.time.YearMonth;
import java.util.ArrayList;

public class ViewTransactionInputData {

        private final YearMonth yearMonth;


    public ViewTransactionInputData(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    public YearMonth getYearMonth(){
        return yearMonth;
    }

}
