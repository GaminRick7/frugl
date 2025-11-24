package interface_adapter.autosave;

import interface_adapter.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;

public class AutosaveViewModel extends ViewModel<AutosaveState> {
    public AutosaveViewModel() {
        super("autosave");
        this.setState(new AutosaveState());
    }
}


