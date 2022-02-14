import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GhostBuster {
    static final double CUMULATIVE_PROBABILITY_EDGES = 0.9;
    static final double SENSOR_CORRECT_PROBABILITY = 0.85;

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Find Casper!");
        Scanner scanner = new Scanner(new File("input.txt"));

        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int k = scanner.nextInt();

        Grid grid = new Grid(n, m, k);
        grid.setInitialGrid();

        grid.setObstacles(scanner,k);
        System.out.println("Initial Probability:");
        grid.printGrid();
        grid.calculateTransitionMatrix();

        while (scanner.hasNextLine()){
            char choice = scanner.next().charAt(0);
            switch (choice) {
                case 'R' -> {
                    int x = scanner.nextInt();
                    int y = scanner.nextInt();
                    int SensorValue = scanner.nextInt();

                    grid.calculateEmissionMatrix(x, y, SensorValue);
                    grid.calculateNewProbabilityMatrix();
                }
                case 'C' -> {
                    Pair pair = grid.getHighestProbableCoordinate();
                    System.out.println("Casper is most probably at (" + pair.getxCoordinate() + ", " + pair.getyCoordinate() + ")");
                    System.out.println();
                }
                case 'Q' -> {
                    System.out.println("Bye Casper!");
                    return;
                }
            }
        }
    }
}
