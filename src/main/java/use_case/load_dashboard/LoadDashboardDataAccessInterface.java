package use_case.load_dashboard;

import entity.Category;
import entity.Source;
import entity.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface LoadDashboardDataAccessInterface {
    List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate);
    Category getSourceCategory(Source source);
}
