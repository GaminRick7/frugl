package use_case.load_dashboard;

/**
 * Input Boundary for the Load Dashboard use case.
 * This  defines the input for the interactor.
 */
public interface LoadDashboardInputBoundary {
    /**
     * Executes the load dashboard logic.
     * @param inputData The input data required to load the dashboard.
     */
    void execute(LoadDashboardInputData inputData);
}
