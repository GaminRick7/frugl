package interface_adapter.set_goal;

import interface_adapter.ViewModel;

public class SetGoalViewModel extends ViewModel<SetGoalState> {

    public static final String VIEW_NAME = "view goals";

    public SetGoalViewModel() {
        super(VIEW_NAME);
        this.setState(new SetGoalState());
    }
}
