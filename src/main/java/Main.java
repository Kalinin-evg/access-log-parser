import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число:");
        int number = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число:");
        int number2 = new Scanner(System.in).nextInt();
        System.out.println("Сумма чисел: " + (number + number2));
        System.out.println("Разность чисел: " + (number - number2));
        System.out.println("Произведение чисел: " + (number * number2));
        double d=number;
        double d2=number2;
        System.out.println("Частное чисел: " + (d/d2));
    }
}
