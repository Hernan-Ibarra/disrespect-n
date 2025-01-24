package com.thg.accelerator21.connectn.ai.name;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import com.thehutgroup.accelerator.connectn.player.Position;
import com.thg.accelerator23.connectn.ai.disrespectn.DisrespectN;
import com.thg.accelerator23.connectn.ai.disrespectn.GameState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
  /**
   * Rigorous Test :-)
   */
  @Test
  public void shouldAnswerWithTrue() {
    assertTrue(true);
  }

  @Test
  public void testCountingCountersInLine() throws InvalidMoveException {
    ArrayList<Integer> moveList = new ArrayList<>(Arrays.asList(0,1,0,1,1,2,0));
    GameState gameState = new GameState(moveList);

    Map<Integer, Integer> numberOfNinALineX = gameState.getNumberOfNinALineByCounter(Counter.X);
    Map<Integer, Integer> numberOfNinALineO = gameState.getNumberOfNinALineByCounter(Counter.O);

    assertEquals(numberOfNinALineX.get(4), 0);
    assertEquals(numberOfNinALineX.get(3), 0);
    assertEquals(numberOfNinALineX.get(2), 3);

    assertEquals(numberOfNinALineO.get(4), 0);
    assertEquals(numberOfNinALineO.get(3), 1);
    assertEquals(numberOfNinALineO.get(2), 4);

  }

  @Test
  public void testHeuristicUtility() throws InvalidMoveException {
    ArrayList<Integer> moveList = new ArrayList<>(Arrays.asList(0,1,0,1,1,2,0));
    DisrespectN alice = new DisrespectN(Counter.X, moveList);

    int evalForX = DisrespectN.heuristicUtility(alice.getRoot().getGameState(), Counter.X);
    int evalForO = DisrespectN.heuristicUtility(alice.getRoot().getGameState(), Counter.O);

    assertEquals(evalForX, -evalForO, "Evaluation for X is the negative of evaluation for O");
    assertEquals(4 + 10 - 3, evalForO, "Evaluation for O was calculated by hand to be 11");

  }

  @Test
  public void testHeuristicUtility2() throws InvalidMoveException {
    ArrayList<Integer> moveList = new ArrayList<>(Arrays.asList(0,0,1,0,2,0,3));    DisrespectN alice = new DisrespectN(Counter.X, moveList);

    int evalForX = DisrespectN.heuristicUtility(alice.getRoot().getGameState(), Counter.X);
    int evalForO = DisrespectN.heuristicUtility(alice.getRoot().getGameState(), Counter.O);

    assertEquals(evalForX, -evalForO, "Evaluation for X is the negative of evaluation for O");
    assertEquals(DisrespectN.infinity, evalForO, "Evaluation for O was calculated by hand to be 11");
  }

  @Test
  public void testMinimax() throws InvalidMoveException {
    ArrayList<Integer> moveList = new ArrayList<>(Arrays.asList(0,0,1,0));
    DisrespectN bob = new DisrespectN(Counter.X, moveList, 3);

    Board board = bob.getRoot().getGameState().getBoard();
    board = new Board(board, 2, Counter.O);

    // There is only one move Bob can make that doesn't lose immediately.
    int obviousMove = bob.makeMove(board);

    assertEquals(3, obviousMove);
  }

  @Test
  public void testGetLastMove() throws InvalidMoveException {
    ArrayList<Integer> moveList = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0));
    DisrespectN charlie = new DisrespectN(Counter.O, moveList, 3);

    Board board = charlie.getRoot().getGameState().getBoard();
    Position pos = new Position(0, 7);

    assertNull(board.getCounterAtPosition(pos));

    board = new Board(board, 0, Counter.X);
    assertEquals(Counter.X, board.getCounterAtPosition(pos));

    int whatMoveWasMade = charlie.getLastMove(board);

    assertEquals(0, whatMoveWasMade);
  }
}


