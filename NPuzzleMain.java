import java.io.File;
import java.io.IOException;
import java.util.Scanner;

enum Heuristics{
    HAMMING_DISTANCE,
    MANHATTAN_DISTANCE,
    LINEAR_CONFLICT;

    public static String toString(Heuristics heuristics) {
        switch (heuristics){
            case MANHATTAN_DISTANCE -> {
                return "Manhattan Distance";
            }
            case HAMMING_DISTANCE -> {
                return "Hamming Distance";
            }
            case LINEAR_CONFLICT -> {
                return "Linear Conflict";
            }
        }
        return "Unknown";
    }
}

enum MoveDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN
}

public class NPuzzleMain {
    public static void main(String[] args) {
        int K;
        int[][] InitialBoard;

        Scanner scanner = null;
        File OutputFile = null;
        try {
            File file = new File("input.txt");
            OutputFile = new File("output.txt");
            OutputFile.createNewFile();
            scanner = new Scanner(file);
        } catch (IOException e) {
            System.out.println("File not found");
            System.exit(-1);
        }

        K = scanner.nextInt();
        InitialBoard = new int[K][K];

        for(int Counter = 0; Counter < K; Counter++){
            for(int ICounter = 0; ICounter < K; ICounter++){
                String input = scanner.next();
                if(input.equals("*")){
                    InitialBoard[Counter][ICounter] = -1;
                }else{
                    InitialBoard[Counter][ICounter] = Integer.parseInt(input);
                }
            }
        }

        scanner.close();

        NPuzzle nPuzzle = new NPuzzle(K, InitialBoard, OutputFile);
        nPuzzle.SolvePuzzle();
    }
}
