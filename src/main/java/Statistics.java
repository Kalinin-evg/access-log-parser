import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistics {
    private List<LogEntry> entries = new ArrayList<>();
    private long totalTraffic = 0;
    private int googlebotCount = 0;
    private int yandexBotCount = 0;
    private HashSet<String> pages = new HashSet<>();
    private HashMap<String, Integer> osStats = new HashMap<>();
    private HashSet<String> notFoundPages = new HashSet<>();
    private HashMap<String, Integer> browserStats = new HashMap<>();
    private long nonBotVisits = 0;
    private long errorRequests = 0;
    private HashSet<String> uniqueNonBotIPs = new HashSet<>();

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

        if (entry.getResponseCode() == 404) {
            String request = entry.getRequest();
            String url = extractUrlFromRequest(request);
            if (url != null) {
                notFoundPages.add(url);
            }
        }
        String browser = extractBrowserFromUserAgent(entry.getUserAgent());
        browserStats.put(browser, browserStats.getOrDefault(browser, 0) + 1);

        // Новые инкременты
        if (!uaLower.contains("bot")) {
            nonBotVisits++;
            uniqueNonBotIPs.add(entry.getIp());
        }
        if (entry.getResponseCode() >= 400) {
            errorRequests++;
        }
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

    private String extractBrowserFromUserAgent(String userAgent) {
        if (userAgent == null) return "Other";
        String uaLower = userAgent.toLowerCase();
        if (uaLower.contains("chrome") && !uaLower.contains("edg")) return "Chrome";
        if (uaLower.contains("firefox")) return "Firefox";
        if (uaLower.contains("safari") && !uaLower.contains("chrome")) return "Safari";
        if (uaLower.contains("edg")) return "Edge";
        if (uaLower.contains("opera")) return "Opera";
        if (uaLower.contains("msie") || uaLower.contains("trident")) return "Internet Explorer";

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

    public List<String> getNotExistingPages() {
        return new ArrayList<>(notFoundPages);
    }

    public HashMap<String, Double> getBrowserShareStatistics() {
        HashMap<String, Double> browserShares = new HashMap<>();
        int totalBrowserEntries = browserStats.values().stream().mapToInt(Integer::intValue).sum();

        if (totalBrowserEntries == 0) {
            return browserShares;
        }

        for (Map.Entry<String, Integer> entry : browserStats.entrySet()) {
            double share = (double) entry.getValue() / totalBrowserEntries;
            browserShares.put(entry.getKey(), share);
        }

        return browserShares;
    }

    // Новый метод: среднее количество посещений сайта за час (не боты)
    public double getAverageVisitsPerHour() {
        if (entries.isEmpty()) return 0.0;

        // Используем Stream API для подсчёта не-ботов (хотя поле уже ведёт подсчёт, но для демонстрации Stream API)
        long nonBotCount = entries.stream()
                .filter(entry -> !entry.getUserAgent().toLowerCase().contains("bot"))
                .count();

        LogEntry first = entries.get(0);
        LogEntry last = entries.get(entries.size() - 1);
        long seconds = java.time.Duration.between(first.getTimestamp(), last.getTimestamp()).getSeconds();
        if (seconds <= 0) return nonBotCount;

        double hours = seconds / 3600.0;
        return nonBotCount / hours;
    }

    // Новый метод: среднее количество ошибочных запросов в час
    public double getAverageErrorsPerHour() {
        if (entries.isEmpty()) return 0.0;

        // Используем Stream API для подсчёта ошибок
        long errorCount = entries.stream()
                .filter(entry -> entry.getResponseCode() >= 400)
                .count();

        LogEntry first = entries.get(0);
        LogEntry last = entries.get(entries.size() - 1);
        long seconds = java.time.Duration.between(first.getTimestamp(), last.getTimestamp()).getSeconds();
        if (seconds <= 0) return errorCount;

        double hours = seconds / 3600.0;
        return errorCount / hours;
    }

    // Новый метод: средняя посещаемость одним пользователем (не боты, уникальные IP)
    public double getAverageVisitsPerUser() {
        // Используем Stream API для подсчёта уникальных IP не-ботов и общего количества посещений не-ботов
        long nonBotCount = entries.stream()
                .filter(entry -> !entry.getUserAgent().toLowerCase().contains("bot"))
                .count();
        long uniqueIPCount = entries.stream()
                .filter(entry -> !entry.getUserAgent().toLowerCase().contains("bot"))
                .map(LogEntry::getIp)
                .distinct()
                .count();

        if (uniqueIPCount == 0) return 0.0;
        return (double) nonBotCount / uniqueIPCount;
    }
}
