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
        String[] parts = line.split(" ");
        if (parts.length < 12) {
            throw new IllegalArgumentException("Некорректный формат строки лога");
        }
        this.ip = parts[0];
        this.timestamp = parseTimestamp(parts[3] + " " + parts[4]);
        this.request = parts[5] + " " + parts[6] + " " + parts[7];
        this.responseCode = Integer.parseInt(parts[8]);
        this.bytes = parts[9].equals("-") ? 0 : Long.parseLong(parts[9]);
        this.referer = parts[10];
        this.userAgent = line.substring(line.indexOf("\"", line.indexOf("\"", line.indexOf("\"") + 1) + 1) + 1);
    }


    private OffsetDateTime parseTimestamp(String timestampStr) {

        if (timestampStr.startsWith("[") && timestampStr.endsWith("]")) {
            timestampStr = timestampStr.substring(1, timestampStr.length() - 1);
        }

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
