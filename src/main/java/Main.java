import java.util.Scanner;

import java.io.File;

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
            }
        }
    }
}

