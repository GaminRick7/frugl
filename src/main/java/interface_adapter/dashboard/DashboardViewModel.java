package interface_adapter.dashboard;

import interface_adapter.ViewModel;

public class DashboardViewModel extends ViewModel<DashboardState> {

    public DashboardViewModel() {
        super("Dashboard");
        this.setState(new DashboardState());
    }
}
