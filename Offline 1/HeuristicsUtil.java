public class HeuristicsUtil {
    private final Heuristics heuristics;
    private final int K;
    private final int[][] Goal;

    public HeuristicsUtil(Heuristics heuristics, int K, int[][] goal) {
        this.heuristics = heuristics;
        this.K = K;
        Goal = goal;
    }

    private int HammingDistance(int[][] Board){
        int hammingDistance = 0;
        for(int Counter = 0; Counter < K; Counter++){
            for(int ICounter = 0; ICounter < K; ICounter++){
                if((Board[Counter][ICounter] != -1) && (Board[Counter][ICounter] != Goal[Counter][ICounter])){
                    hammingDistance++;
                }
            }
        }
        return hammingDistance;
    }

    private int ManhattanDistance(int[][] Board){
        int ManhattanDistance = 0;
        for(int Row=0; Row < K; Row++){
            for(int Column=0; Column < K; Column++){
                if(Board[Row][Column] == -1){
                    continue;
                }
                int GoalRow = ((Board[Row][Column] -1) / K);
                int GoalColumn = ((Board[Row][Column] - 1) % K);
                ManhattanDistance += Math.abs(GoalRow - Row) + Math.abs(GoalColumn - Column);
            }
        }
        return ManhattanDistance;
    }

    private int LinearConflict(int[][] Board){
        int LinearConflict = 0;

        //Row Conflicts
        for(int Row=0; Row < K; Row++){
            for(int Column=0; Column < K; Column++){
                if(Board[Row][Column] == -1){
                    continue;
                }
                int GoalRow = (Board[Row][Column] -1) / K;
                if(GoalRow == Row){
                    for(int Tracker=Column+1; Tracker < K; Tracker++){
                        if(Board[Row][Tracker] == -1){
                            continue;
                        }
                        int TrackerGoalRow = (Board[Row][Tracker] -1) / K;
                        if(TrackerGoalRow==Row && Board[Row][Column] > Board[Row][Tracker]){
                            LinearConflict++;
                        }
                    }
                }
            }
        }

        /*
        //Column Conflicts
        for(int Column=0; Column < K; Column++){
            for(int Row=0; Row < K; Row++){
                if(Board[Row][Column] == -1){
                    continue;
                }
                int GoalColumn = ((Board[Row][Column] - 1) % K);
                if(GoalColumn == Column){
                    for(int Tracker=0; Tracker < K; Tracker++){
                        if(Board[Tracker][Column] == -1){
                            continue;
                        }
                        int TrackerGoalColumn = ((Board[Tracker][Column] - 1) % K);
                        if(TrackerGoalColumn == Column && Board[Row][Column] > Board[Tracker][Column]){
                            LinearConflict++;
                        }
                    }
                }
            }
        }*/

        return ManhattanDistance(Board) + 2 * LinearConflict;
    }

    public int HeuristicFunction(int[][] Board){
        switch (heuristics){
            case LINEAR_CONFLICT -> {
                return LinearConflict(Board);
            }
            case HAMMING_DISTANCE -> {
                return HammingDistance(Board);
            }
            case MANHATTAN_DISTANCE -> {
                return ManhattanDistance(Board);
            }
            default -> {
                return -1;
            }
        }
    }

    public Heuristics getHeuristics() {
        return heuristics;
    }
}
