import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

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
            }
            else if (isDirectory) {
                System.out.println("Указанный путь ведёт к папке, а не к файлу. Попробуйте снова.");
                continue;
            } else {
                correctFileCount++;
                System.out.println("Путь указан верно");
                System.out.println("Количество вернных вводов " + correctFileCount);
                int totalLines = 0;
                int minLength = Integer.MAX_VALUE;
                int maxLength = Integer.MIN_VALUE;

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
                        if (length < minLength) {
                            minLength = length;
                        }
                        if (length > maxLength) {
                            maxLength = length;
                        }
                    }
                    reader.close();

                    System.out.println("Общее количество строк в файле: " + totalLines);
                    if (totalLines > 0) {
                        System.out.println("Длина самой длинной строки в файле: " + maxLength);
                        System.out.println("Длина самой короткой строки в файле: " + minLength);
                    } else {
                        System.out.println("Файл пустой, статистика не рассчитана.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

