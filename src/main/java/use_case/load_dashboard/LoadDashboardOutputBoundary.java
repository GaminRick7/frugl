package use_case.load_dashboard;

/**
 * Output Boundary for the Load Dashboard use case.
 * Defines how the interactor communicates results to the presenter.
 */
public interface LoadDashboardOutputBoundary {
    /**
     * Presents the successfully loaded dashboard data.
     * @param outputData The data to be displayed.
     */
    void present(LoadDashboardOutputData outputData);

    /**
     * Prepares the failure view in case of an error.
     * @param errorMessage A description of the error.
     */
    void prepareFailView(String errorMessage);
}
