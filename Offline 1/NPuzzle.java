import java.io.*;

public class NPuzzle {
    private final int K;
    private final int[][] InitialBoard;
    private final int[][] Goal;
    private PrintWriter OutputFile;

    public NPuzzle(int k, int[][] initialBoard, File OutputFile) {
        K = k;
        InitialBoard = initialBoard;

        Goal = new int[K][K];
        for(int Counter = 0; Counter < K; Counter++){
            for(int ICOunter = 0; ICOunter < K; ICOunter++){
                Goal[Counter][ICOunter] = K * Counter + ICOunter + 1;
                if(Counter == K-1 && ICOunter == K-1){
                    Goal[Counter][ICOunter] = -1;
                }
            }
        }

        try {
            this.OutputFile = new PrintWriter(new FileOutputStream(OutputFile));
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file");
            System.exit(-1);
        }
    }

    public void SolvePuzzle(){
        Node temp = new Node(K, InitialBoard, Heuristics.LINEAR_CONFLICT, Goal);
        OutputFile.write("Given Board:\n" + temp.OutputString().toString());
        OutputFile.flush();

        if(Solvable()){
            StringBuilder stringBuilder = new StringBuilder();

            HeuristicsUtil heuristicsUtil = new HeuristicsUtil(Heuristics.LINEAR_CONFLICT, K, Goal);
            Node node = new Node(K, InitialBoard, Heuristics.LINEAR_CONFLICT, Goal);
            AStarUtil aStarUtil = new AStarUtil(node, heuristicsUtil, Goal, false);
            stringBuilder.append(aStarUtil.AStarSearch());

            System.out.println("Linear Conflict Done");

            OutputFile.write(stringBuilder.toString());
            OutputFile.flush();
            stringBuilder = new StringBuilder();

            heuristicsUtil = new HeuristicsUtil(Heuristics.MANHATTAN_DISTANCE, K, Goal);
            node = new Node(K, InitialBoard, Heuristics.MANHATTAN_DISTANCE, Goal);
            aStarUtil = new AStarUtil(node, heuristicsUtil, Goal, false);
            stringBuilder.append(aStarUtil.AStarSearch());

            System.out.println("Manhattan Distance Done");

            OutputFile.write(stringBuilder.toString());
            OutputFile.flush();
            stringBuilder = new StringBuilder();

            heuristicsUtil = new HeuristicsUtil(Heuristics.HAMMING_DISTANCE, K, Goal);
            node = new Node(K, InitialBoard, Heuristics.HAMMING_DISTANCE, Goal);
            aStarUtil = new AStarUtil(node, heuristicsUtil, Goal, true);
            stringBuilder.append(aStarUtil.AStarSearch());

            System.out.println("Hamming Distance Done");

            OutputFile.write(stringBuilder.toString());
            OutputFile.flush();
            stringBuilder = new StringBuilder();

            OutputFile.write(stringBuilder.toString());
            OutputFile.flush();
        }else{
            OutputFile.write("This puzzle is unsolvable");
        }
        System.out.println("Output Ready On File");
        OutputFile.close();
    }

    private int GetInversionCount(int[] Tiles){
        int Inversions = 0;
        for(int Counter = 0; Counter < Tiles.length - 1; Counter++){
            if(Tiles[Counter] != -1){
                for(int ICounter = Counter+1; ICounter < Tiles.length; ICounter++){
                    if(Tiles[ICounter] != -1){
                        if(Tiles[Counter] > Tiles[ICounter]){
                            Inversions++;
                        }
                    }
                }
            }
        }
        return Inversions;
    }

    private int[] GetTiles(){
        int[] Tiles = new int[K*K];
        int Tracker = 0;
        for(int Counter = 0; Counter < K; Counter++){
            for(int ICounter = 0; ICounter < K; ICounter++){
                Tiles[Tracker] = InitialBoard[Counter][ICounter];
                Tracker++;
            }
        }
        return Tiles;
    }

    private int GetBlankPosition(){
        for(int Counter = K - 1; Counter >= 0; Counter--){
            for(int ICounter = K-1; ICounter >= 0; ICounter--){
                if(InitialBoard[Counter][ICounter] == -1){
                    return K - Counter;
                }
            }
        }
        return -1;
    }

    private boolean Solvable(){
        int[] TilesArray = GetTiles();
        int InversionCount = GetInversionCount(TilesArray);
        if(K%2 != 0){
            // If odd value of K, then inversion count must be even
            return (InversionCount%2 == 0);
        }else{
            // If even value of K, we need the value of * counting from bottom
            int BlankPosition = GetBlankPosition();
            // If Even row from bottom, and odd inversions, then puzzle is solvable
            if(BlankPosition%2==0 && InversionCount%2!=0) return true;
            // If Odd row from bottom, and even inversions, then puzzle is solvable
            else return BlankPosition % 2 != 0 && InversionCount % 2 == 0;
        }
    }
}