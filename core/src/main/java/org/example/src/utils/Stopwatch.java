package org.example.src.utils;

public class Stopwatch {
    private long startTime;
    private long endTime = -1;

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.endTime = -1;
    }

    public void stop() {
        this.endTime = System.currentTimeMillis();
    }

    /**
     * Returns the formatted elapsed time.
     * If stop() was not called yet, uses the current time.
     */
    public String format() {
        long end = (endTime >= 0) ? endTime : System.currentTimeMillis();
        return format(end - startTime);
    }

    /**
     * Formats a raw millisecond duration as a human-readable string.
     * Examples: "10seg", "10min 20seg", "1h 3min 4seg"
     */
    public static String format(long millis) {
        long total = millis / 1000;
        long seconds = total % 60;
        long minutes = (total / 60) % 60;
        long hours = total / 3600;

        if (hours > 0) return String.format("%dh %dmin %s", hours, minutes, seconds);
        if (minutes > 0) return String.format("%dmin %s", minutes, seconds);
        return String.format("%s", seconds);
    }

    /**
     * Parses a formatted duration string back to total seconds.
     * Handles: "10seg", "10min 20seg", "1h 3min 4seg", and plain integers.
     */
    public static int parseSeconds(String formatted) {
        if (formatted == null || formatted.isBlank()) return 0;
        try {
            int total = 0;
            String s = formatted.trim();
            if (s.contains("h ")) {
                String[] parts = s.split("h ", 2);
                total += Integer.parseInt(parts[0].trim()) * 3600;
                s = parts[1].trim();
            }
            if (s.contains("min ")) {
                String[] parts = s.split("min ", 2);
                total += Integer.parseInt(parts[0].trim()) * 60;
                s = parts[1].trim();
            }
            total += Integer.parseInt(s.replace("seg", "").trim());
            return total;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
