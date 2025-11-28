package use_case.view_transactions;

import entity.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.YearMonth;


public class ViewTransactionInteractor implements ViewTransactionInputBoundary {
    private final ViewTransactionDataAccessInterface viewDataAccessObject;
    private final ViewTransactionOutputBoundary viewTransactionPresenter;

    public ViewTransactionInteractor(ViewTransactionDataAccessInterface viewDataAccessObject,
                                     ViewTransactionOutputBoundary viewTransactionPresenter) {
        this.viewDataAccessObject = viewDataAccessObject;
        this.viewTransactionPresenter = viewTransactionPresenter;
    }


        private List<HashMap<String, Object>> convert_transaction_toString(List<Transaction> trans) {

            List<HashMap<String, Object>> proccessed_transactions = new ArrayList<>();

            for (int i = 0; i< trans.size() ; i++) {

                Transaction transac = trans.get(i);
                HashMap<String, Object> t1 = new HashMap<>();

                t1.put("date", transac.getDate());
                t1.put("source", transac.getSource().getName());
                t1.put("amount", String.valueOf(transac.getAmount()));
                t1.put("category", viewDataAccessObject.getSourceCategory(transac.getSource()).getName());

                proccessed_transactions.add(t1);

            }
            return proccessed_transactions;
        }


    public void execute(ViewTransactionInputData transactionInputData) {

        LocalDate start = transactionInputData.getStartDate();
        LocalDate end = transactionInputData.getEndDate();
        final List<Transaction> trans =viewDataAccessObject.getByDateRange(start, end);
        List<HashMap<String, Object>> proccessed_transactions = convert_transaction_toString(trans);

        YearMonth yearMonth = YearMonth.from(start);


        if (!proccessed_transactions.isEmpty()){
            final ViewTransactionOutputData viewTransactionOutputData= new ViewTransactionOutputData(yearMonth.toString(),proccessed_transactions );
            viewTransactionPresenter.prepareSuccessView(  viewTransactionOutputData);
        }
        else{
            viewTransactionPresenter.prepareFailView( "No data available.");



        }


    }

}









    // convert everthing to string in outputData




