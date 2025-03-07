import java.util.Scanner;

public class OptimizationMethods {

    // Определение функции f(x) = x^2 - 2x + e^(-x)
    public static double f(double x) {
        return Math.pow(x, 2) - 2 * x + Math.exp(-x);
    }

    // Метод Свенна для нахождения начального интервала неопределенности
    public static double[] swannMethod(double x0, double t) {
        int k = 0;
        double a0, b0, delta;
        double fx0MinusT = f(x0 - t);
        double fx0 = f(x0);
        double fx0PlusT = f(x0 + t);

        if (fx0MinusT >= fx0 && fx0 <= fx0PlusT) {
            return new double[]{x0 - t, x0 + t};
        } else if (fx0MinusT <= fx0 && fx0 >= fx0PlusT) {
            System.out.println("Функция не является унимодальной.");
            return null;
        }

        // Инициализация a0 и b0 перед использованием
        a0 = x0;
        b0 = x0;

        if (fx0MinusT >= fx0 && fx0 >= fx0PlusT) {
            delta = t;
            a0 = x0;
            double x1 = x0 + t;
            k = 1;
        } else {
            delta = -t;
            b0 = x0;
            double x1 = x0 - t;
            k = 1;
        }

        while (true) {
            double xkPlus1 = x0 + Math.pow(2, k) * delta;
            double fxkPlus1 = f(xkPlus1);

            if (fxkPlus1 < f(x0)) {
                if (delta > 0) {
                    a0 = x0;
                } else {
                    b0 = x0;
                }
                x0 = xkPlus1;
                k++;
            } else {
                if (delta > 0) {
                    b0 = xkPlus1;
                } else {
                    a0 = xkPlus1;
                }
                break;
            }
        }

        return new double[]{a0, b0};
    }

    // Метод золотого сечения
    public static double goldenSectionSearch(double a, double b, double l) {
        final double GOLDEN_RATIO = (3 - Math.sqrt(5)) / 2;
        int k = 0;
        double yk, zk;

        do {
            yk = a + GOLDEN_RATIO * (b - a);
            zk = a + b - yk;

            if (f(yk) <= f(zk)) {
                b = zk;
                zk = yk;
                yk = a + GOLDEN_RATIO * (b - a);
            } else {
                a = yk;
                yk = zk;
                zk = a + b - yk;
            }

            k++;
        } while (Math.abs(a - b) > l);

        return (a + b) / 2;
    }

    // Квадратичная интерполяция (итеративный подход)
    public static double quadraticInterpolation(double x1, double dx, double epsilon1, double epsilon2) {
        double x2 = x1 + dx;
        double f1 = f(x1);
        double f2 = f(x2);
        double x3, f3;

        if (f1 > f2) {
            x3 = x1 + 2 * dx;
        } else {
            x3 = x1 - dx;
        }

        f3 = f(x3);
        double Fmin = Math.min(f1, Math.min(f2, f3));
        double xmin = f1 == Fmin ? x1 : (f2 == Fmin ? x2 : x3);

        while (true) {
            double denominator = 2 * ((x2 - x3) * f1 + (x3 - x1) * f2 + (x1 - x2) * f3);
            if (Math.abs(denominator) < 1e-10) { // Проверка знаменателя на ноль
                return xmin;
            }

            double xBar = (Math.pow(x2, 2) - Math.pow(x3, 2)) * f1 + (Math.pow(x3, 2) - Math.pow(x1, 2)) * f2 + (Math.pow(x1, 2) - Math.pow(x2, 2)) * f3;
            xBar /= denominator;
            double fXBar = f(xBar);

            if (Math.abs((Fmin - fXBar) / fXBar) < epsilon1 && Math.abs((xmin - xBar) / xBar) < epsilon2) {
                return xBar;
            }

            // Обновление значений для следующей итерации
            if (xBar < xmin) {
                x3 = xmin;
                f3 = Fmin;
            } else {
                x1 = xmin;
                f1 = Fmin;
            }
            xmin = xBar;
            Fmin = fXBar;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите начальную точку (x0): ");
        double x0 = scanner.nextDouble();

        System.out.print("Введите величину шага (t): ");
        double t = scanner.nextDouble();

        System.out.print("Введите точность (l): ");
        double l = scanner.nextDouble();

        // Ввод epsilon1 и epsilon2 для квадратичной интерполяции
        System.out.print("Введите epsilon1 для квадратичной интерполяции: ");
        double epsilon1 = scanner.nextDouble();
        System.out.print("Введите epsilon2 для квадратичной интерполяции: ");
        double epsilon2 = scanner.nextDouble();

        // Шаг 1: Используем метод Свенна для нахождения начального интервала
        double[] interval = swannMethod(x0, t);
        if (interval == null) {
            System.out.println("Не удалось найти начальный интервал неопределенности.");
            return;
        }
        double a = interval[0];
        double b = interval[1];

        System.out.println("Начальный интервал неопределенности: [" + a + ", " + b + "]");

        // Шаг 2: Ищем минимум методом золотого сечения
        double minGoldenSection = goldenSectionSearch(a, b, l);
        System.out.println("Минимум методом золотого сечения: x = " + minGoldenSection + ", f(x) = " + f(minGoldenSection));

        // Шаг 3: Ищем минимум методом квадратичной интерполяции
        double minQuadraticInterpolation = quadraticInterpolation(a, t, epsilon1, epsilon2);
        System.out.println("Минимум методом квадратичной интерполяции: x = " + minQuadraticInterpolation + ", f(x) = " + f(minQuadraticInterpolation));

        // Шаг 4: Сравниваем результаты
        System.out.println("Разница между методами: " + Math.abs(minGoldenSection - minQuadraticInterpolation));
    }
}