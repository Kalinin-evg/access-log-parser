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

    private HashMap<Integer, Integer> visitsPerSecond = new HashMap<>();
    private HashSet<String> refererDomains = new HashSet<>();
    private HashMap<String, Integer> visitsPerIP = new HashMap<>();

    public void addEntry(LogEntry entry) {
        entries.add(entry);
        totalTraffic += entry.getBytes();

        String uaLower = entry.getUserAgent() != null ? entry.getUserAgent().toLowerCase() : "";
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

        if (!uaLower.contains("bot")) {
            nonBotVisits++;
            uniqueNonBotIPs.add(entry.getIp());
        }
        if (entry.getResponseCode() >= 400) {
            errorRequests++;
        }
        if (!uaLower.contains("bot")) {
            int second = entry.getTimestamp().getSecond();
            visitsPerSecond.put(second, visitsPerSecond.getOrDefault(second, 0) + 1);

            visitsPerIP.put(entry.getIp(), visitsPerIP.getOrDefault(entry.getIp(), 0) + 1);
        }

        String referer = entry.getReferer();
        if (referer != null && !referer.isEmpty()) {
            String domain = extractDomainFromReferer(referer);
            if (domain != null) {
                refererDomains.add(domain);
            }
        }
    }

    private String extractUrlFromRequest(String request) {
        if (request == null || request.trim().isEmpty()) return null;
        String[] parts = request.split(" ");
        if (parts.length >= 2 && parts[0].equals("GET")) {
            return parts[1];
        }
        return null;
    }

    private String extractOsFromUserAgent(String userAgent) {
        if (userAgent == null) return "Other";
        String uaLower = userAgent.toLowerCase();
        if (uaLower.contains("windows")) return "Windows";
        if (uaLower.contains("mac")) return "MacOS";
        if (uaLower.contains("linux")) return "Linux";
        if (uaLower.contains("android")) return "Android";
        if (uaLower.contains("ios")) return "iOS";
        return "Other";
    }

    private String extractBrowserFromUserAgent(String userAgent) {
        if (userAgent == null) return "Other";
        String uaLower = userAgent.toLowerCase();
        if (uaLower.contains("chrome") && !uaLower.contains("edg")) return "Chrome";
        if (uaLower.contains("firefox")) return "Firefox";
        if (uaLower.contains("safari") && !uaLower.contains("chrome")) return "Safari";
        if (uaLower.contains("opera")) return "Opera";
        if (uaLower.contains("edg")) return "Edge";
        return "Other";
    }

    private String extractDomainFromReferer(String referer) {
        if (!referer.startsWith("http://") && !referer.startsWith("https://")) return null;
        String url = referer.replaceFirst("https?://", "");
        int slashIndex = url.indexOf("/");
        return slashIndex != -1 ? url.substring(0, slashIndex) : url;
    }

    public int getTotalLines() {
        return entries.size();
    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public double getGooglebotShare() {
        return entries.isEmpty() ? 0 : (double) googlebotCount / entries.size();
    }

    public double getYandexbotShare() {
        return entries.isEmpty() ? 0 : (double) yandexBotCount / entries.size();
    }

    public double getTrafficRate() {
        return entries.isEmpty() ? 0 : (double) totalTraffic / entries.size();
    }

    public List<String> getExistingPages() {
        return new ArrayList<>(pages);
    }

    public Map<String, Double> getOsShareStatistics() {
        int total = osStats.values().stream().mapToInt(Integer::intValue).sum();
        return osStats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> total == 0 ? 0 : (double) e.getValue() / total
                ));
    }

    public List<String> getNotExistingPages() {
        return new ArrayList<>(notFoundPages);
    }

    public Map<String, Double> getBrowserShareStatistics() {
        int total = browserStats.values().stream().mapToInt(Integer::intValue).sum();
        return browserStats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> total == 0 ? 0 : (double) e.getValue() / total
                ));
    }

    public double getAverageVisitsPerHour() {
        if (entries.isEmpty()) return 0;
        long hours = entries.stream()
                .mapToLong(e -> e.getTimestamp().toEpochSecond() / 3600)
                .distinct()
                .count();
        return hours == 0 ? 0 : (double) nonBotVisits / hours;
    }

    public double getAverageErrorsPerHour() {
        if (entries.isEmpty()) return 0;
        long hours = entries.stream()
                .mapToLong(e -> e.getTimestamp().toEpochSecond() / 3600)
                .distinct()
                .count();
        return hours == 0 ? 0 : (double) errorRequests / hours;
    }

    public double getAverageVisitsPerUser() {
        return uniqueNonBotIPs.isEmpty() ? 0 : (double) nonBotVisits / uniqueNonBotIPs.size();
    }

    public int getPeakVisitsPerSecond() {
        return visitsPerSecond.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public List<String> getRefererDomains() {
        return new ArrayList<>(refererDomains);
    }

    public int getMaxVisitsPerUser() {
        return visitsPerIP.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }
}
