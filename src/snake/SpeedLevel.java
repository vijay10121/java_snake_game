package snake;

public enum SpeedLevel {
    VERY_SLOW("Very Slow", 160),
    SLOW("Slow", 130),
    MEDIUM("Medium (Normal)", 100),
    FAST("Fast", 70),
    INSANE("Insane", 45);

    public final String label;
    public final int delayMs;

    SpeedLevel(String label, int delayMs) {
        this.label = label;
        this.delayMs = delayMs;
    }

    @Override
    public String toString() {
        return label;
    }
}
