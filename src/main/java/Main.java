import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int correctFileCount = 0;

        while (true) {
            System.out.println("Введите путь к файлу, например: C:\\Users\\username\\Документы\\файл.txt и нажмите <Enter>:");
            String path = scanner.nextLine();

            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists) {
                System.out.println("Файл не найден. Введите корректное имя файла и нажмите <Enter>.");
                continue;
            } else if (isDirectory) {
                System.out.println("Указанный путь ведёт к папке, а не к файлу. Попробуйте снова.");
                continue;
            } else {
                correctFileCount++;
                System.out.println("Путь указан верно");
                System.out.println("Количество верных вводов: " + correctFileCount);

                Statistics stats = new Statistics();

                try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int length = line.length();
                        if (length > 1024) {
                            throw new LineTooLongException("Строка слишком длинная: " + length + " символов. Максимум 1024.");
                        }

                        try {
                            LogEntry entry = new LogEntry(line);
                            stats.addEntry(entry);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Не удалось распарсить строку: " + e.getMessage());
                        }
                    }
                } catch (LineTooLongException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println("Ошибка чтения файла: " + e.getMessage());
                }

                System.out.println("Общее количество строк: " + stats.getTotalLines());
                System.out.printf("Доля запросов от Googlebot: %.2f%%%n", stats.getGooglebotShare());
                System.out.printf("Доля запросов от YandexBot: %.2f%%%n", stats.getYandexbotShare());
                System.out.println("Общий трафик (байт): " + stats.getTotalTraffic());
                System.out.printf("Средний трафик в час (байт/час): %.2f%n", stats.getTrafficRate());
                System.out.printf("Среднее количество посещений не-ботов в час: %.2f%n", stats.getAverageVisitsPerHour());
                System.out.printf("Среднее количество ошибочных запросов в час: %.2f%n", stats.getAverageErrorsPerHour());
                System.out.printf("Средняя посещаемость одним пользователем (не-боты): %.2f%n", stats.getAverageVisitsPerUser());

                List<String> pages = stats.getExistingPages();
                System.out.println("\nСписок всех существующих страниц (уникальные URL):");
                if (pages.isEmpty()) {
                    System.out.println("  Нет уникальных URL.");
                } else {
                    for (String page : pages) {
                        System.out.println("  - " + page);
                    }
                }

                HashMap<String, Double> osShares = stats.getOsShareStatistics();
                System.out.println("\nСтатистика операционных систем (доли от 0 до 1):");
                if (osShares.isEmpty()) {
                    System.out.println("  Нет данных по ОС.");
                } else {
                    for (String os : osShares.keySet()) {
                        System.out.printf("  - %s: %.4f%n", os, osShares.get(os));
                    }
                }

                List<String> notFoundPages = stats.getNotExistingPages();
                System.out.println("\nСписок всех несуществующих страниц (уникальные URL с кодом 404):");
                if (notFoundPages.isEmpty()) {
                    System.out.println("  Нет уникальных URL с кодом 404.");
                } else {
                    for (String page : notFoundPages) {
                        System.out.println("  - " + page);
                    }
                }

                HashMap<String, Double> browserShares = stats.getBrowserShareStatistics();
                System.out.println("\nСтатистика браузеров (доли от 0 до 1):");
                if (browserShares.isEmpty()) {
                    System.out.println("  Нет данных по браузерам.");
                } else {
                    for (String browser : browserShares.keySet()) {
                        System.out.printf("  - %s: %.4f%n", browser, browserShares.get(browser));
                    }
                }

            }
        }
    }
}
