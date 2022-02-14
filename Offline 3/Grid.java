import java.util.Scanner;
import java.util.Vector;

public class Grid {
    private final int width;
    private final int height;
    private final double initProb;
    private final double[][] probMatrix;
    private final double [][] transitionMatrix;
    private final double [][] emissionMatrix;

    public Grid(int height, int width, int obstacles) {
        this.width = width;
        this.height = height;
        this.probMatrix = new double[height][width];
        this.initProb = 1.0/((width * height)-obstacles);

        transitionMatrix = new double[height*width][height*width];
        emissionMatrix = new double[height*width][height*width];
    }

    public void setInitialGrid(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                probMatrix[i][j] = initProb;
            }
        }
    }

    private void transpose(double[][] A, double[][] B, int width, int height)
    {
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                B[i][j] = A[j][i];
            }
        }
    }

    private double[][] multiplyMatrix(
            int row1, int col1, double[][] A,
            int row2, int col2, double[][] B)
    {
        double[][] C = new double[row1][col2];
        if (row2 != col1) {
            System.out.println("Multiplication Not Possible\n");
            return C;
        }

        for (int i = 0; i < row1; i++) {
            for (int j = 0; j < col2; j++) {
                for (int k = 0; k < row2; k++)
                    C[i][j] += A[i][k] * B[k][j];
            }
        }
        return C;
    }

    private double[][] flatterMatrix(double[][] matrix, int rows, int columns){
        double[][] flatMatrix = new double[rows*columns][1];
        int counter = 0;
        for(int i=0; i<rows; i++){
            for(int j=0; j<columns; j++){
                flatMatrix[counter][0] = matrix[i][j];
                counter++;
            }
        }
        return flatMatrix;
    }


    public void setObstacles(Scanner scanner, int obstacles) {
        int x, y;
        for (int i = 0; i < obstacles; i++) {
            x = scanner.nextInt();
            y = scanner.nextInt();
            probMatrix[x][y] = 0;
        }
    }

    public void printGrid(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.format("%.5f ", probMatrix[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private int getObstaclesInEdges(Vector<Pair> edges){
        int Counter = 0;
        for (Pair pair: edges) {
            if(probMatrix[pair.getxCoordinate()][pair.getyCoordinate()] == 0){
                Counter++;
            }
        }
        return  Counter;
    }

    public Pair getHighestProbableCoordinate(){
        Pair pair = new Pair(-1, -1);
        double maximumProbability = 0.0;
        for(int i=0; i< height; i++){
            for (int j=0; j<width; j++){
                if(probMatrix[i][j] > maximumProbability){
                    maximumProbability = probMatrix[i][j];
                    pair.setxCoordinate(i);
                    pair.setyCoordinate(j);
                }
            }
        }
        return pair;
    }

    public void calculateTransitionMatrix(){
        for(int i=0; i<height*width; i++) {
            for (int j = 0; j < height * width; j++) {
                transitionMatrix[i][j] = 0;
            }
        }

        for(int i=0; i<height; i++){
            for (int j=0; j<width;j++){
                double edgeProbability;
                double cornerProbability;
                Vector<Pair> edgeIndex = new Vector<>();
                Vector<Pair> Corners = new Vector<>();
                if(i+1 < height) edgeIndex.add(new Pair(i+1, j));
                if(i-1 >= 0) edgeIndex.add(new Pair(i-1, j));
                if(j+1 < width) edgeIndex.add(new Pair(i, j+1));
                if(j-1 >= 0) edgeIndex.add(new Pair(i, j-1));

                int edgeCount = edgeIndex.size() - getObstaclesInEdges(edgeIndex);
                Corners.add(new Pair(i, j));
                if(i+1 < height && j+1 < width) Corners.add(new Pair(i+1, j+1));
                if(i+1 < height && j-1 >= 0) Corners.add(new Pair(i+1, j-1));
                if(i-1 >=0 && j+1 < width) Corners.add(new Pair(i-1, j+1));
                if(i-1 >=0 && j-1 >= 0) Corners.add(new Pair(i-1, j-1));
                int cornerCount = Corners.size() - getObstaclesInEdges(Corners);

                if(cornerCount == 0){
                    edgeProbability = 1.0 / edgeCount;
                }else{
                    edgeProbability = GhostBuster.CUMULATIVE_PROBABILITY_EDGES / edgeCount;
                }

                if(edgeCount == 0){
                    cornerProbability = 1.0 / cornerCount;
                }else{
                    cornerProbability = (1-GhostBuster.CUMULATIVE_PROBABILITY_EDGES) / cornerCount;
                }
                int transitionRow = i * width + j;
                for (Pair pair: edgeIndex) {
                    if(probMatrix[pair.getxCoordinate()][pair.getyCoordinate()] != 0){
                        int transitionColumn = pair.getxCoordinate() * width + pair.getyCoordinate();
                        transitionMatrix[transitionRow][transitionColumn] = edgeProbability;
                    }
                }

                for (Pair pair: Corners) {
                    if(probMatrix[pair.getxCoordinate()][pair.getyCoordinate()] != 0){
                        int transitionColumn = pair.getxCoordinate() * width + pair.getyCoordinate();
                        transitionMatrix[transitionRow][transitionColumn] = cornerProbability;
                    }
                }
            }
        }
    }

    public void calculateEmissionMatrix(int row, int column, int sensorValue){
        for(int i=0; i<height*width; i++) {
            for (int j = 0; j < height * width; j++) {
                emissionMatrix[i][j] = 0;
            }
        }

        double valueToSet = (sensorValue == 1) ? GhostBuster.SENSOR_CORRECT_PROBABILITY : (1 - GhostBuster.SENSOR_CORRECT_PROBABILITY);
        Vector<Pair> Neighbours = new Vector<>();

        if(row + 1 < height) Neighbours.add(new Pair(row + 1, column));
        if(row - 1 >= 0) Neighbours.add(new Pair(row - 1, column));
        if(column + 1 < width) Neighbours.add(new Pair(row, column + 1));
        if(column - 1 >= 0) Neighbours.add(new Pair(row, column - 1));

        Neighbours.add(new Pair(row, column));
        if(row + 1 < height && column + 1 < width) Neighbours.add(new Pair(row+1, column+1));
        if(row + 1 < height && column - 1 >= 0) Neighbours.add(new Pair(row + 1, column - 1));
        if(row - 1 >=0 && column + 1 < width) Neighbours.add(new Pair(row - 1, column + 1));
        if(row - 1 >= 0 && column - 1 >= 0) Neighbours.add(new Pair(row - 1, column - 1));

        for (Pair pair: Neighbours) {
            int emissionIndex = pair.getxCoordinate() * width + pair.getyCoordinate();
            emissionMatrix[emissionIndex][emissionIndex] = valueToSet;
        }
        for(int i = 0; i < height*width; i++){
            if(emissionMatrix[i][i] == 0){
                emissionMatrix[i][i] = (1-valueToSet);
            }
        }
    }

    private double[][] normalizeLineMatrix(double[][] matrix, int row){
        double[][] retMatrix = new double[row][1];
        double totalValue = 0.0;

        for(int i=0; i<row; i++){
            for(int j = 0; j< 1; j++){
                totalValue += matrix[i][j];
                retMatrix[i][j] = matrix[i][j];
            }
        }

        for(int i=0; i<row; i++){
            for (int j = 0; j< 1; j++){
                retMatrix[i][j] = retMatrix[i][j] / totalValue;
            }
        }
        return retMatrix;
    }

    public void calculateNewProbabilityMatrix(){
        double[][] anotherMatrix = new double[height*width][height*width];
        transpose(transitionMatrix, anotherMatrix, height*width, height*width);
        anotherMatrix = multiplyMatrix(height*width, height*width, emissionMatrix, height*width, height*width, anotherMatrix);

        double[][] flatProbMatrix = flatterMatrix(probMatrix, height, width);
        anotherMatrix = multiplyMatrix(height*width, height*width, anotherMatrix,  height*width, 1, flatProbMatrix);
        anotherMatrix = normalizeLineMatrix(anotherMatrix, height*width);

        int Counter = 0;
        for(int i=0; i<height; i++){
            for (int j=0; j<width; j++){
                probMatrix[i][j] = anotherMatrix[Counter][0];
                Counter++;
            }
        }
        printGrid();
    }
}
