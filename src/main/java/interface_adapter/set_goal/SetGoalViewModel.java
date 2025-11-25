package interface_adapter.set_goal;

import interface_adapter.ViewModel;

public class SetGoalViewModel extends ViewModel<SetGoalState> {
    public SetGoalViewModel() {
        super("set goal");
        this.setState(new SetGoalState());
    }
}
