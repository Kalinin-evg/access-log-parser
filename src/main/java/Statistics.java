import java.util.ArrayList;
import java.util.List;

public class Statistics {
    private List<LogEntry> entries = new ArrayList<>();

    private long totalTraffic = 0;
    private int googlebotCount = 0;
    private int yandexBotCount = 0;

    public void addEntry(LogEntry entry) {
        entries.add(entry);
        totalTraffic += entry.getBytes();

        String uaLower = entry.getUserAgent().toString().toLowerCase();
        if (uaLower.contains("googlebot")) {
            googlebotCount++;
        }
        if (uaLower.contains("yandexbot")) {
            yandexBotCount++;
        }
    }

    public int getTotalLines() {
        return entries.size();
    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public double getGooglebotShare() {
        if (entries.isEmpty()) return 0;
        return (double) googlebotCount / entries.size() * 100;
    }

    public double getYandexbotShare() {
        if (entries.isEmpty()) return 0;
        return (double) yandexBotCount / entries.size() * 100;
    }

    public double getTrafficRate() {
        if (entries.isEmpty()) return 0;

        LogEntry first = entries.get(0);
        LogEntry last = entries.get(entries.size() - 1);

        long seconds = java.time.Duration.between(first.getTimestamp(), last.getTimestamp()).getSeconds();
        if (seconds <= 0) return totalTraffic;

        double hours = seconds / 3600.0;
        return totalTraffic / hours;
    }
}
