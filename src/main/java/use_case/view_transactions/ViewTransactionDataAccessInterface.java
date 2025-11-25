package use_case.view_transactions;
import entity.Transaction;

import java.time.YearMonth;
import java.util.ArrayList;


public interface ViewTransactionDataAccessInterface {

    /**
     * Give the
     * @param yearMonth
     * @return the trnsations in the given year and month
     */
    ArrayList<Transaction> chooseMontlyTransactions(YearMonth yearMonth);


}
