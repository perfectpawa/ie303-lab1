import java.util.Random;

public class B1_CircleArea {
    public static double approximatePi(int iterations) {
        Random rand = new Random();
        int insideCircle = 0;

        for (int i = 0; i < iterations; i++) {
            double x = rand.nextDouble();
            double y = rand.nextDouble();

            if (x * x + y * y <= 1) {
                insideCircle++;
            }
        }

        return (4.0 * insideCircle) / iterations;
    }

    public static double approximateCircleArea(double r, int iterations) {
        double piApprox = approximatePi(iterations);
        return piApprox * r * r;
    }

    public static void main(String[] args) {
        double r = 1;
        int iterations = 1_000_000; 

        double area = approximateCircleArea(r, iterations);
        System.out.println("Circle Area: " + area);
    }
}
