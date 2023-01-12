package com.thg.accelerator23.connectn.ai.funconcerto.movetree;

import java.util.ArrayList;
import java.util.List;
import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.GameConfig;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;

import static java.util.Collections.min;

public class MoveTree {
    private final Counter counter;
    private final Board state;
    private int mostRecentPosition;
    private List<MoveTree> moveList;

    public MoveTree() {
        this.counter = Counter.O;
        GameConfig config = new GameConfig(10, 8, 4);
        this.state = new Board(config);
        this.moveList = new ArrayList<>();
    }

    public MoveTree(Counter counter, Board state, int position){
        this.counter = counter;
        this.state = state;
        this.mostRecentPosition = position;
        this.moveList = new ArrayList<>();
    }

    public MoveTree(MoveTree another) {
        this.counter = another.getCounter();
        this.state = getState();
        this.mostRecentPosition = another.getPosition();
        this.moveList = another.getChildren();
    }

    public Counter getCounter(){
        return this.counter;
    }

    public Board getState(){
        return this.state;
    }

    public int getPosition(){
        return this.mostRecentPosition;
    }

    public void addNode(int position) throws InvalidMoveException {
        Counter newCounter = counter.getOther();
        Board newState = new Board(state, position, newCounter);
        MoveTree newNode = new MoveTree(counter.getOther(), newState, position);
        moveList.add(newNode);
    }
    public void removeNode(List<Integer> moveListToGetToNode){
        removeNodeRecursion(this, moveListToGetToNode);
    }

    private void removeNodeRecursion(MoveTree tree, List<Integer> moveListToGetToNode){
        if (moveListToGetToNode.size() == 1){
            List<MoveTree> listFromWhichToDeleteMove = tree.getChildren();
            for (int i = 0; i < listFromWhichToDeleteMove.size(); i++) {
                if (moveListToGetToNode.get(0) == listFromWhichToDeleteMove.get(i).getPosition()){
                    listFromWhichToDeleteMove.remove(i);
                    break;
                }
            }
        } else {
            int nextNodeIndex = moveListToGetToNode.get(0);
            moveListToGetToNode.remove(0);
            removeNodeRecursion(tree.getChildAtPosition(nextNodeIndex), moveListToGetToNode);
        }
    }
    public List<MoveTree> getLeaves(){
        MoveTree treeCopy = new MoveTree(this);
        List<MoveTree> leafList = new ArrayList<>();
        if (treeCopy.getChildren().size() == 0) {
            leafList.add(this);
        }

        while (treeCopy.getChildren().size() != 0) {
            leafList.add(treeCopy.popLeftLeaf());
        }

        return leafList;
    }
    public List<MoveTree> getChildren(){
        return moveList;
    }

    private MoveTree popLeftLeafNode(MoveTree tree,MoveTree previousTree, List<Integer> traversePath) {
        if (tree.getChildren().isEmpty()) {
            MoveTree nodeCopy = new MoveTree(tree);
            previousTree.removeNode(traversePath);
            return nodeCopy;
        } else {
            ArrayList<Integer> childPositions = new ArrayList<>();
            for (MoveTree child : tree.getChildren()) {
                childPositions.add(child.getPosition());
            }
            int leftPosition = min(childPositions);
            List<Integer> path = new ArrayList<Integer>();
            path.add(leftPosition);
            return popLeftLeafNode(tree.getChildAtPosition(leftPosition),tree, path);
        }
    }

    public MoveTree popLeftLeaf(){
        return popLeftLeafNode(this, this, new ArrayList<>());
    }

    public MoveTree getChildAtPosition(int position) throws IndexOutOfBoundsException {
        List<MoveTree> children = this.getChildren();
        for( MoveTree child : children){
            if (child.getPosition() == position){
                return child;
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
