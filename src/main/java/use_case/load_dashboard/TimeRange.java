package use_case.load_dashboard;

/**
 * Enum to specify timeranges for time chart.
 */
public enum TimeRange {
    ONE_MONTH("Last 30 Days", 1),
    THREE_MONTHS("3 Months", 3),
    SIX_MONTHS("6 Months", 6),
    TWELVE_MONTHS("12 Months", 12);

    private final String displayName;
    private final int value;

    TimeRange(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public int getValue() {
        return value;
    }
}
