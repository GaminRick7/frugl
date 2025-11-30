package interface_adapter.autosave;

import interface_adapter.ViewModel;

public class AutosaveViewModel extends ViewModel<AutosaveState> {
    public AutosaveViewModel() {
        super("autosave");
        this.setState(new AutosaveState());
    }
}
