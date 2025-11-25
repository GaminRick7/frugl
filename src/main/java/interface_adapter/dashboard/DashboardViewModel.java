package interface_adapter.dashboard;

import interface_adapter.ViewModel;
import interface_adapter.autosave.AutosaveState;

import java.awt.*;
import java.util.List;

public class DashboardViewModel extends ViewModel<DashboardState> {
    public DashboardViewModel() {
        super("dashboard");
        this.setState(new DashboardState());
    }
}
