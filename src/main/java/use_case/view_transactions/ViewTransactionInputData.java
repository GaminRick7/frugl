package use_case.view_transactions;

import java.time.LocalDate;

/**
 * Input Data for View Transaction Use Case.
 */
public class ViewTransactionInputData {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public ViewTransactionInputData(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
