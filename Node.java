import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {
    private final int K;
    private int[][] Board;
    private final int[][] Goal;
    private Node Parent = null;
    private final Heuristics heuristics;
    private int FCost = 0;
    private int GCost = 0;
    private int HCost = 0;

    public Node(int k, int[][] initialBoard, Heuristics heuristics, int[][] goal) {
        K = k;
        Board = new int[K][K];
        this.heuristics = heuristics;
        Goal = goal;

        for(int Row = 0; Row < K; Row++)
            for(int Column=0; Column < K; Column++)
                Board[Row][Column] = initialBoard[Row][Column];
    }

    public List<Node> GetPossibleMoves(){
        List<Node> PossibleMoves = new ArrayList<>();
        Node NextMove = MovePuzzlePiece(MoveDirection.LEFT);
        if(!this.equals(NextMove)){
            PossibleMoves.add(NextMove);
        }

        NextMove = MovePuzzlePiece(MoveDirection.RIGHT);
        if(!this.equals(NextMove)){
            PossibleMoves.add(NextMove);
        }

        NextMove = MovePuzzlePiece(MoveDirection.UP);
        if(!this.equals(NextMove)){
            PossibleMoves.add(NextMove);
        }

        NextMove = MovePuzzlePiece(MoveDirection.DOWN);
        if(!this.equals(NextMove)){
            PossibleMoves.add(NextMove);
        }
        return PossibleMoves;
    }

    public StringBuilder OutputString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int Row=0; Row < K; Row++){
            for(int Column=0; Column < K; Column++){
                if(Board[Row][Column]==-1){
                    stringBuilder.append("* ");
                }else{
                    stringBuilder.append(Board[Row][Column]).append(" ");
                }
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("##################\n");
        return stringBuilder;
    }

    public int[][] getBoard() {
        return Board;
    }

    public int getFCost() {
        return FCost;
    }

    public void setFCost(int FCost) {
        this.FCost = FCost;
    }

    public int getGCost() {
        return GCost;
    }

    public void setGCost(int GCost) {
        this.GCost = GCost;
    }

    public int getHCost() {
        return HCost;
    }

    public void setHCost(int HCost) {
        this.HCost = HCost;
    }

    public Node getParent() {
        return Parent;
    }

    public void setParent(Node parent) {
        Parent = parent;
    }

    public void setBoard(int[][] board) {
        Board = board;
    }

    private int[][] ChangeBoardTiles(int TileRow1, int TileColumn1, int TileRow2, int TileColumn2, int[][] board){
        int Temp = board[TileRow1][TileColumn1];
        board[TileRow1][TileColumn1] = board[TileRow2][TileColumn2];
        board[TileRow2][TileColumn2] = Temp;
        return board;
    }

    private int[] GetBlankPosition(){
        int[] Retval = {-1, -1};
        for(int Counter = 0; Counter < K; Counter++){
            for(int ICounter = 0; ICounter < K; ICounter++){
                if(Board[Counter][ICounter] == -1){
                    Retval[0] = Counter;
                    Retval[1] = ICounter;
                    break;
                }
            }
        }
        return Retval;
    }

    private Node MovePuzzlePiece(MoveDirection direction){
        int[][] NewBoard = Board.clone();
        Node node = new Node(K, NewBoard, heuristics, Goal);
        int[] BlankPosition = GetBlankPosition();
        int[] TileToSwap = {BlankPosition[0], BlankPosition[1]};

        switch (direction){
            case UP -> {
                if(BlankPosition[0] > 0){
                    TileToSwap[0] = BlankPosition[0] - 1;
                }
                break;
            }
            case DOWN -> {
                if(BlankPosition[0] < K-1){
                    TileToSwap[0] = BlankPosition[0] + 1;
                }
                break;
            }
            case LEFT -> {
                if(BlankPosition[1] > 0){
                    TileToSwap[1] = BlankPosition[1] - 1;
                }
                break;
            }
            case RIGHT -> {
                if(BlankPosition[1] < K-1){
                    TileToSwap[1] = BlankPosition[1] + 1;
                }
                break;
            }
        }
        node.setBoard(node.ChangeBoardTiles(BlankPosition[0], BlankPosition[1], TileToSwap[0], TileToSwap[1], node.getBoard().clone()));
        HeuristicsUtil heuristicsUtil = new HeuristicsUtil(heuristics, K, Goal);
        node.Parent = this;
        node.GCost = GCost + 1;
        node.HCost = heuristicsUtil.HeuristicFunction(node.getBoard());
        node.FCost = node.GCost + node.HCost;
        return node;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Node){
            if(this==obj){
                return true;
            }
            Node node = (Node) obj;
            boolean Equal = true;
            for(int Row = 0; Row < K; Row++){
                for (int Column=0; Column < K; Column++){
                    if(node.Board[Row][Column] != this.Board[Row][Column]){
                        Equal = false;
                        break;
                    }
                }
            }
            return Equal;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 43 * Arrays.hashCode(Board);
    }
}
