import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Statistics {
    private List<LogEntry> entries = new ArrayList<>();
    private long totalTraffic = 0;
    private int googlebotCount = 0;
    private int yandexBotCount = 0;
    private HashSet<String> pages = new HashSet<>();  // Для уникальных страниц с кодом 200
    private HashMap<String, Integer> osStats = new HashMap<>();  // Для подсчёта частоты ОС

    public void addEntry(LogEntry entry) {
        entries.add(entry);
        totalTraffic += entry.getBytes();

        String uaLower = entry.getUserAgent().toLowerCase();
        if (uaLower.contains("googlebot")) {
            googlebotCount++;
        }
        if (uaLower.contains("yandexbot")) {
            yandexBotCount++;
        }

        if (entry.getResponseCode() == 200) {
            String request = entry.getRequest();
            String url = extractUrlFromRequest(request);
            if (url != null) {
                pages.add(url);
            }
        }

        String os = extractOsFromUserAgent(entry.getUserAgent());
        osStats.put(os, osStats.getOrDefault(os, 0) + 1);
    }

    private String extractUrlFromRequest(String request) {
        if (request == null || !request.startsWith("\"GET ")) return null;
        int start = request.indexOf(" ");
        int end = request.indexOf(" ", start + 1);
        if (start == -1 || end == -1) return null;
        return request.substring(start + 1, end);
    }

    private String extractOsFromUserAgent(String userAgent) {
        if (userAgent == null) return "Other";
        String uaLower = userAgent.toLowerCase();
        if (uaLower.contains("windows")) return "Windows";
        if (uaLower.contains("linux")) return "Linux";
        if (uaLower.contains("mac")) return "Mac OS";
        if (uaLower.contains("android")) return "Android";
        if (uaLower.contains("ios") || uaLower.contains("iphone") || uaLower.contains("ipad")) return "iOS";
        return "Other";
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

    public List<String> getExistingPages() {
        return new ArrayList<>(pages);
    }

    public HashMap<String, Double> getOsShareStatistics() {
        HashMap<String, Double> osShares = new HashMap<>();
        int totalOsEntries = osStats.values().stream().mapToInt(Integer::intValue).sum();

        if (totalOsEntries == 0) {
            return osShares;
        }

        for (Map.Entry<String, Integer> entry : osStats.entrySet()) {
            double share = (double) entry.getValue() / totalOsEntries;
            osShares.put(entry.getKey(), share);
        }

        return osShares;
    }
}
