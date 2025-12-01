package interface_adapter.dashboard;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Dashboard Use Case.
 */
public class DashboardViewModel extends ViewModel<DashboardState> {

    public static final String VIEW_NAME = "dashboard";

    public DashboardViewModel() {
        super(VIEW_NAME);
        this.setState(new DashboardState());
    }
}
