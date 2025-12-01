package interface_adapter;

/**
 * Model for the View Manager for FRUGL. Its state is the name of the View which
 * is currently active. An initial state of "" is used.
 */
public class ViewManagerModel extends ViewModel<String> {

    public ViewManagerModel() {
        super("view manager");
        this.setState("");
    }

    /**
     * Shows the popup.
     * @param message Popup message
     */
    public void showPopup(String message) {
        this.firePropertyChange("popup", message);
    }

}
