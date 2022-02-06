import java.util.*;

class NodeCompare implements Comparator<Node>{
    @Override
    public int compare(Node o1, Node o2) {
        return Integer.compare(o1.getFCost(), o2.getFCost());
    }
}

public class AStarUtil {
    private final int[][] Goal;
    private final HeuristicsUtil heuristicsUtil;
    private final boolean bPrintSteps;

    private final List<Node> NodesToExplore = new ArrayList<>();
    private final List<Node> NodesExplored = new ArrayList<>();


    public AStarUtil(Node StartNode, HeuristicsUtil heuristics, int[][] goal, boolean bPrintSteps) {
        heuristicsUtil = heuristics;
        Goal = goal;
        this.bPrintSteps = bPrintSteps;

        StartNode.setHCost(heuristicsUtil.HeuristicFunction(StartNode.getBoard()));
        StartNode.setGCost(0);
        StartNode.setFCost(StartNode.getGCost() + StartNode.getHCost());
        NodesToExplore.add(StartNode);
    }


    public StringBuilder AStarSearch(){
        Node NextNode = null;
        while (!NodesToExplore.isEmpty()){
            //Get the node with minimal F cost
            NextNode = ExtractMin(NodesToExplore);
            //If it is the goal, we are done
            if(IsGoal(NextNode.getBoard().clone())){
                break;
            }
            //NodesToExplore.remove(NextNode);
            RemoveNode(NextNode);
            NodesExplored.add(NextNode);

            //System.out.println(NextNode.OutputString());

            //Obtain the maximum four possible moves from this position
            List<Node> NextList = NextNode.GetPossibleMoves();
            //System.out.println(NextList.size());
            for(Node node : NextList){

                //System.out.println(node.OutputString());

                //If already explored, no need to add again
                if(AlreadyExplored(node)){
                    //System.out.println("Explored\n");
                    continue;
                }
                //If not already in the list to explore, add it there
                if(!Contains(node)){
                    NodesToExplore.add(node);
                    //System.out.println("Adding\n");
                }else{
                    //Otherwise Update the parameters wrt current node if necessary
                    Node PreviousInstance = GetPreviousNode(node);
                    if(PreviousInstance == null){
                        continue;
                    }

                    //If current instance has less G Cost than the previous instance, we will take the current one
                    if(PreviousInstance.getGCost() > node.getGCost()){
                        PreviousInstance.setParent(node.getParent());
                        PreviousInstance.setGCost(node.getGCost());
                        PreviousInstance.setFCost(node.getFCost());
                        PreviousInstance.setHCost(node.getHCost());
                    }
                    //System.out.println("Updated");
                }
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }

        //If goal has been reached, return status
        StringBuilder stringBuilder = new StringBuilder();
        if(NextNode != null){
            stringBuilder = PrintSolution(NextNode);
        }
        return stringBuilder;
    }

    private Node ExtractMin(List<Node> list){
        return Collections.min(list, new NodeCompare());
    }

    private boolean IsGoal(int[][] GivenBoard){
        for (int Row=0; Row < Goal[0].length; Row++){
            for (int Column=0; Column < Goal[0].length; Column++){
                if(Goal[Row][Column] != GivenBoard[Row][Column]){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean AlreadyExplored(Node Check){
        for(Node node : NodesExplored){
            if(node.equals(Check)){
                return true;
            }
        }
        return false;
    }

    private boolean Contains(Node Check){
        for(Node node : NodesToExplore){
            if(node.equals(Check)){
                return true;
            }
        }
        return false;
    }

    private Node GetPreviousNode(Node Current){
        for(Node node : NodesToExplore){
            if(node.equals(Current)){
                return node;
            }
        }
        return null;
    }

    private StringBuilder PrintSolution(Node FinalNode){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*").append(Heuristics.toString(heuristicsUtil.getHeuristics())).append(" Heuristics*\n");
        stringBuilder.append("Number of Steps: ").append(FinalNode.getGCost()).append("\n");
        stringBuilder.append("Number of Nodes Explored: ").append(NodesExplored.size() + NodesToExplore.size()).append("\n");
        stringBuilder.append("Number of Nodes Expanded: ").append(NodesExplored.size()).append("\n\n");

        if(bPrintSteps){
            Stack<Node> Steps = new Stack<>();
            while (FinalNode.getParent() != null){
                Steps.push(FinalNode);
                FinalNode = FinalNode.getParent();
            }
            Steps.push(FinalNode);
            stringBuilder.append("Steps:\n");
            while (!Steps.isEmpty()){
                Node Step = Steps.pop();
                stringBuilder.append(Step.OutputString());
            }
        }
        return stringBuilder;
    }

    private void RemoveNode(Node node){
        for(Node node1 : NodesToExplore){
            if(node1.equals(node)){
                NodesToExplore.remove(node1);
                break;
            }
        }
    }
}