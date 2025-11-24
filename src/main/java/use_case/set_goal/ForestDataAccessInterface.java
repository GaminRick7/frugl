package use_case.set_goal;

import java.util.List;

import entity.Transaction;


public interface ForestDataAccessInterface {

    List<Transaction> getAll();
}
