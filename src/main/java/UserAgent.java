public class UserAgent {
    private String browser;
    private String os;

    public UserAgent(String userAgentString) {
        this.browser = "Unknown";
        this.os = "Unknown";

        if (userAgentString.toLowerCase().contains("googlebot")) {
            this.browser = "Googlebot";
        } else if (userAgentString.toLowerCase().contains("yandexbot")) {
            this.browser = "YandexBot";
        } else if (userAgentString.toLowerCase().contains("mozilla")) {
            this.browser = "Mozilla";
            if (userAgentString.toLowerCase().contains("windows")) {
                this.os = "Windows";
            } else if (userAgentString.toLowerCase().contains("linux")) {
                this.os = "Linux";
            } else if (userAgentString.toLowerCase().contains("mac")) {
                this.os = "MacOS";
            }
        }
    }

    public String getBrowser() { return browser; }
    public String getOs() { return os; }

    public boolean isGooglebot() { return "Googlebot".equals(browser); }
    public boolean isYandexBot() { return "YandexBot".equals(browser); }
}
