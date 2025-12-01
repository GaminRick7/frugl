package interface_adapter.dashboard;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Dashboard Use Case.
 */
public class DashboardViewModel extends ViewModel<DashboardState> {
    public DashboardViewModel() {
        super("dashboard");
        this.setState(new DashboardState());
    }
}
