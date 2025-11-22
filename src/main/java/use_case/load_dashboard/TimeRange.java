package use_case.load_dashboard;

public enum TimeRange {
    ONE_MONTH("Last 30 Days"),
    THREE_MONTHS("3 Months"),
    SIX_MONTHS("6 Months"),
    TWELVE_MONTHS("12 Months");

    private final String displayName;

    TimeRange(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
