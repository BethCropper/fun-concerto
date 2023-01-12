package com.thg.accelerator23.connectn.ai.funconcerto;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.Player;

import com.thehutgroup.accelerator.connectn.player.*;
import com.thg.accelerator23.connectn.ai.funconcerto.analysis.BoardAnalyser;
import com.thg.accelerator23.connectn.ai.funconcerto.analysis.GameState;
import com.thg.accelerator23.connectn.ai.funconcerto.movetree.MoveTree;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class FunConcertoAi extends Player {

  //MoveTree tree = readInitialTreeFromFile("src/main/java/com/thg/accelerator23/connectn/ai/funconcerto/movetree/initialMoveTree.txt");
  MoveTree tree = new MoveTree();
  MinMax mM;
  Board previousBoard = new Board(new GameConfig(10,8,4));

  public FunConcertoAi(Counter counter) {

    super(counter, FunConcertoAi.class.getName());
    this.mM = new MinMax();
  }

  public static int indexOf(ArrayList<Integer> arr, int val) {
    return IntStream.range(0, arr.size()).filter(i -> arr.get(i) == val).findFirst().orElse(-1);
  }

  @Override
  public int makeMove(Board board) {

    ArrayList<Integer> validMoves = getValidLocations(board);

    if(isBlankBoard(board) && tree.getChildren().size() == 0){ // Runs on the first move of the game
        System.out.println("Running");
      for (int i = 0; i < validMoves.size(); i++) {
          try {
            tree.addNode(validMoves.get(i));
          } catch (InvalidMoveException e) {
            System.out.println("This shouldn't happen1");
          }
        }
      for(int i = 0; i < tree.getChildren().size(); i++){
        Board secondMoveBoard = tree.getChildren().get(i).getState();
        ArrayList<Integer> validMovesForSecondMove = getValidLocations(secondMoveBoard);
        for (int j = 0; j < validMovesForSecondMove.size(); j++) {
          try {
            tree.getChildren().get(i).addNode(validMovesForSecondMove.get(j));
          } catch (InvalidMoveException e) {
            System.out.println("This shouldn't happen2");
          }
        }
      }
    }
    else if(!isBlankBoard(board) && tree.getChildren().size() == 0) { // Checks if the player is going second
      //tree.setCounter(Counter.O);
      try {
        tree.addNode(getPreviousMove(board));
      } catch (InvalidMoveException e) {
        System.out.println("This shouldn't happen'");
      }
      tree = tree.getChildren().get(0);
      for (int i = 0; i < validMoves.size(); i++) {
        try {
          tree.addNode(validMoves.get(i));
        } catch (InvalidMoveException e) {
          System.out.println("This shouldn't happen1");
        }
      }
      for(int i = 0; i < tree.getChildren().size(); i++){
        Board secondMoveBoard = tree.getChildren().get(i).getState();
        ArrayList<Integer> validMovesForSecondMove = getValidLocations(secondMoveBoard);
        for (int j = 0; j < validMovesForSecondMove.size(); j++) {
          try {
            tree.getChildren().get(i).addNode(validMovesForSecondMove.get(j));
          } catch (InvalidMoveException e) {
            System.out.println("This shouldn't happen2");
          }
        }
      }
    } else {
      System.out.println("Counter1");
      System.out.println(tree.getCounter());
      int previousMove = getPreviousMove(board);
      ArrayList<Integer> validMovesForPreviousBoard = getValidLocations(previousBoard);
      tree = tree.getChildAtPosition(indexOf(validMovesForPreviousBoard, previousMove));
      System.out.println("Counter after ");
      System.out.println(tree.getCounter());
      System.out.println(validMoves.size());
      System.out.println(validMovesForPreviousBoard);
      System.out.println(tree.getState().getCounterAtPosition(new Position(0,0)));
      System.out.println(tree.getState().getCounterAtPosition(new Position(1,0)));
      System.out.println(tree.getState().getCounterAtPosition(new Position(2,0)));
      for (int i = 0; i < validMoves.size(); i++) {
        try {
          tree.addNode(validMoves.get(i));
        } catch (InvalidMoveException e) {
          System.out.println("This shouldn't happen1");
        }
      }
      for(int i = 0; i < tree.getChildren().size(); i++){
        Board secondMoveBoard = tree.getChildren().get(i).getState();
        ArrayList<Integer> validMovesForSecondMove = getValidLocations(secondMoveBoard);
        System.out.println("these");
        System.out.println(validMovesForSecondMove);
        for (int j = 0; j < validMovesForSecondMove.size(); j++) {
          try {
            tree.getChildren().get(i).addNode(validMovesForSecondMove.get(j));
          } catch (InvalidMoveException e) {
            System.out.println("This shouldn't happen2");
          }
        }
      }
    }

    System.out.println(tree.getTotalNodesBelow());
    System.out.println(tree.getCounter());
    int[] newMove = mM.miniMax(tree,true);
    System.out.println("Returned from minimax");
    System.out.println(newMove[1]);
    try {
      previousBoard = new Board(board, newMove[1],getCounter());
    } catch (InvalidMoveException e) {
      System.out.println("This shouldn't happen4");
    }
    tree = tree.getChildAtPosition(indexOf(validMoves, newMove[1]));
    System.out.println("counter at end");
    System.out.println(tree.getCounter());
    return newMove[1];
  }

  public ArrayList<Integer> getValidLocations(Board board){
    ArrayList list = new ArrayList();
    for(int i = 0; i < 10; i++){
      if(!board.hasCounterAtPosition(new Position(i,7))){
        list.add(i);
      }
    }
    return list;
  }

//  public MoveTree readInitialTreeFromFile(String filename){
//    try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
//
//    }catch(IOException e){
//      System.out.println("An error occurred while reading");
//    };
//    return ;
//  }

    public int getPreviousMove(Board newBoard) {
      for(int i = 0; i < 10; i++){
        try {
          Board newBoard1 = new Board(previousBoard, i, getCounter().getOther());
          Counter counter = getTopCounterOnColumn(newBoard1, i);
          Counter counter1 = getTopCounterOnColumn(newBoard, i);
          int index1 = getTopIndexOfCounterOnColumn(newBoard1, i, counter);
          int index2 = getTopIndexOfCounterOnColumn(newBoard, i, counter);
          if(index1 == index2) {
            return i;
          }
        }catch(InvalidMoveException e){

        }
      }
      return -1;
    };

  public int getTopIndexOfCounterOnColumn(Board board, int column, Counter counter){
    for(int i = 7; i >= 0; i--){
      if(board.getCounterAtPosition(new Position(column, i)) == counter) {
        return i;
      }
    }
    return -1;
  }

  public Counter getTopCounterOnColumn(Board board, int column) {
    for(int i = 7; i >= 0; i--){
      if(board.hasCounterAtPosition(new Position(column, i))) {
        return board.getCounterAtPosition(new Position(column, i));
      }
    }
    return null;
  }

  public boolean isBlankBoard(Board board) {
    if(board.getCounterAtPosition(new Position(0,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(1,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(2,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(3,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(4,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(5,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(6,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(7,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(8,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(9,0)) != null) return false;
    if(board.getCounterAtPosition(new Position(10,0)) != null) return false;
    return true;
  }

}