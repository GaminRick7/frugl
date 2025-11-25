package use_case.view_transactions;

import entity.Transaction;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.time.YearMonth;


import java.util.List;

public class ViewTransactionInteractor implements ViewTransactionInputBoundary {
    private final ViewTransactionDataAccessInterface viewDataAccessObject;
    private final ViewTransactionOutputBoundary viewTransactionPresenter;

    public ViewTransactionInteractor(ViewTransactionDataAccessInterface viewDataAccessObject,
                                     ViewTransactionOutputBoundary viewTransactionPresenter) {
        this.viewDataAccessObject = viewDataAccessObject;
        this.viewTransactionPresenter = viewTransactionPresenter;
    }


        private ArrayList<HashMap<String, Object>> convert_transaction_toString(List<Transaction> trans) {

            ArrayList<HashMap<String, Object>> proccessed_transactions = new ArrayList<>();

            for (int i = 0; i< trans.size() ; i++) {

                Transaction transac = trans.get(i);
                HashMap<String, Object> t1 = new HashMap<>();

                t1.put("date", transac.getDate());
                t1.put("source", transac.getSource().getName());
                t1.put("amount", String.valueOf(transac.getAmount()));
                t1.put("category", transac.getCategory().getName());

                proccessed_transactions.add(t1);

            }
            return proccessed_transactions;
        }


    public void execute(ViewTransactionInputData transactionInputData) {
        final YearMonth yearMonth = transactionInputData.getYearMonth();
        final ArrayList<Transaction> trans = viewDataAccessObject.chooseMontlyTransactions(yearMonth);
        ArrayList<HashMap<String, Object>> proccessed_transactions = convert_transaction_toString(trans);


        if (!proccessed_transactions.isEmpty()){
            final ViewTransactionOutputData viewTransactionOutputData= new ViewTransactionOutputData(yearMonth.toString(),proccessed_transactions );
            viewTransactionPresenter.prepareSuccessView(  viewTransactionOutputData);
        }
        else{
            viewTransactionPresenter.prepareFailView(yearMonth.toString() + " has no data availiable");


        }


    }

}









    // convert everthing to string in outputData




