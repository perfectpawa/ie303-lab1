import java.util.Random;

public class B2_ApproximatePi {
    public static double approximatePi(int iterations) {
        Random rand = new Random();
        int insideCircle = 0;

        for (int i = 0; i < iterations; i++) {
            double x = 2 * rand.nextDouble() - 1;
            double y = 2 * rand.nextDouble() - 1;

            if (x * x + y * y <= 1) {
                insideCircle++;
            }
        }

        return 4.0 * insideCircle / iterations;
    }

    public static void main(String[] args) {
        int iterations = 1_000_000;
        double piApprox = approximatePi(iterations);

        System.out.println("Pi: " + piApprox);
    }
}
