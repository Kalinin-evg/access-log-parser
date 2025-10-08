import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {
    private String ip;
    private OffsetDateTime timestamp;
    private String request;
    private int responseCode;
    private long bytes;
    private String referer;
    private String userAgent;

    public LogEntry(String line) {
        String regex = "^(\\S+) \\S+ \\S+ \\[([^\\]]+)\\] \"([^\"]+)\" (\\d+) (\\d+|\\-) \"([^\"]+)\" \"([^\"]+)\".*$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Некорректный формат строки лога: " + line);
        }

        this.ip = matcher.group(1);
        this.timestamp = parseTimestamp(matcher.group(2));
        this.request = matcher.group(3);
        this.responseCode = Integer.parseInt(matcher.group(4));
        this.bytes = matcher.group(5).equals("-") ? 0 : Long.parseLong(matcher.group(5));
        this.referer = matcher.group(6);
        this.userAgent = matcher.group(7);
    }

    private OffsetDateTime parseTimestamp(String timestampStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        return OffsetDateTime.parse(timestampStr, formatter);
    }


    public String getIp() { return ip; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public String getRequest() { return request; }
    public int getResponseCode() { return responseCode; }
    public long getBytes() { return bytes; }
    public String getReferer() { return referer; }
    public String getUserAgent() { return userAgent; }
}
