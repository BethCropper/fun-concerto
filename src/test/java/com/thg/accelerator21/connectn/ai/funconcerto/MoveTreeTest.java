package com.thg.accelerator21.connectn.ai.funconcerto;
import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.GameConfig;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import com.thg.accelerator23.connectn.ai.funconcerto.movetree.MoveTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MoveTreeTest {
    @DisplayName("Test that an empty constructor gives an empty tree")
    @Test
    void testMoveTreeEmptyConstructor(){
        MoveTree emptyMoveTree = new MoveTree();

        Assertions.assertEquals(emptyMoveTree.getChildren(), new ArrayList<>());
        Assertions.assertEquals(emptyMoveTree.getCounter(), Counter.O);
    }

    @DisplayName("Test that a make a new node constructor initialises as it should")
    @Test
    void testAddNode() throws InvalidMoveException, IndexOutOfBoundsException {
        MoveTree addingMoveTree = new MoveTree();
        addingMoveTree.addNode(3);
        MoveTree childTree = addingMoveTree.getChildAtPosition(3);

        Assertions.assertEquals(childTree.getChildren(), new ArrayList<>());
        Assertions.assertEquals(childTree.getCounter(), Counter.X);
        Assertions.assertEquals(childTree.getPosition(), 3);
        Assertions.assertThrows(IndexOutOfBoundsException.class,() -> addingMoveTree.getChildAtPosition(2));
    }

    @DisplayName("Test that checks popping the left leaf works as expected")
    @Test
    void testPopLeftLeaf() throws InvalidMoveException, IndexOutOfBoundsException {
        MoveTree addingMoveTree = new MoveTree();
        addingMoveTree.addNode(1);
        addingMoveTree.addNode(2);
        addingMoveTree.addNode(3);
        addingMoveTree.getChildAtPosition(1).addNode(1);
        addingMoveTree.getChildAtPosition(1).addNode(2);
        addingMoveTree.getChildAtPosition(1).getChildAtPosition(1).addNode(4);
        addingMoveTree.getChildAtPosition(1).getChildAtPosition(1).addNode(5);

        MoveTree leftLeaf = addingMoveTree.popLeftLeaf();

        Assertions.assertEquals(leftLeaf.getPosition(), 4);
        Assertions.assertEquals(leftLeaf.getChildren(), new ArrayList<>());

        MoveTree leftLeaf2 = addingMoveTree.popLeftLeaf();
        Assertions.assertEquals(leftLeaf2.getPosition(), 5);

        MoveTree leftLeaf3 = addingMoveTree.popLeftLeaf();
        Assertions.assertEquals(leftLeaf3.getPosition(), 1);
    }

    @DisplayName("Test that checks popping the left leaf on an empty list throws an exception")
    @Test
    void testPopLeftLeafEmptyTree() throws IndexOutOfBoundsException {
        MoveTree addingMoveTree = new MoveTree();
        Assertions.assertThrows(IndexOutOfBoundsException.class, addingMoveTree::popLeftLeaf);
    }
}
