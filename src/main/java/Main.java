import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

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
                System.out.println("Файл не найден.Введите корректное имя файла и нажмите <Enter>.");
                continue;
            } else if (isDirectory) {
                System.out.println("Указанный путь ведёт к папке, а не к файлу. Попробуйте снова.");
                continue;
            } else {
                correctFileCount++;
                System.out.println("Путь указан верно");
                System.out.println("Количество вернных вводов " + correctFileCount);
                int totalLines = 0;
                int googlebotCount = 0;
                int yandexbotCount = 0;
                try {
                    FileReader fileReader = new FileReader(path);
                    BufferedReader reader = new BufferedReader(fileReader);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int length = line.length();
                        totalLines++;

                        if (length > 1024) {
                            throw new LineTooLongException("Строка слишком длинная: " + length + " символов. Максимум 1024.");
                        }

                        String userAgent = extractUserAgent(line);
                        if (userAgent != null) {
                            String uaLower = userAgent.toLowerCase();
                            if (uaLower.contains("googlebot")) {
                                googlebotCount++;
                            }
                            if (uaLower.contains("yandexbot")) {
                                yandexbotCount++;
                            }
                        }
                    }
                } catch (LineTooLongException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println("Ошибка чтения файла: " + e.getMessage());
                }

                System.out.println("Общее количество строк: " + totalLines);
                if (totalLines > 0) {
                    double googlebotShare = (double) googlebotCount / totalLines * 100;
                    double yandexbotShare = (double) yandexbotCount / totalLines * 100;
                    System.out.printf("Доля запросов от Googlebot: %.2f%%%n", googlebotShare);
                    System.out.printf("Доля запросов от YandexBot: %.2f%%%n", yandexbotShare);
                } else {
                    System.out.println("Файл пуст или не содержит строк.");
                }
            }
        }
    }
    private static String extractUserAgent(String line) {
        int start = line.lastIndexOf('"');
        int end = line.lastIndexOf('"', start - 1);
        if (start != -1 && end != -1 && start > end) {
            return line.substring(end + 1, start);
        }
        return null;
    }
}

