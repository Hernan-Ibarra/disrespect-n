package com.thg.accelerator23.connectn.ai.disrespectn;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import com.thehutgroup.accelerator.connectn.player.Position;
import java.util.ArrayList;

public class GameState {
  private final Board board;
  private final ArrayList<Integer> moveList;
  private final int numberOfTwoInARow;
  private final int numberOfThreeInARow;
  private final int numberOfFourInARow;
  private final Counter nextCounter;

  public GameState(
      Board board,
      ArrayList<Integer> moveList,
      int numberOfTwoInARow,
      int numberOfThreeInARow,
      int numberOfFourInARow,
      Counter nextCounter) {
    this.board = board;
    this.moveList = moveList;
    this.numberOfTwoInARow = numberOfTwoInARow;
    this.numberOfThreeInARow = numberOfThreeInARow;
    this.numberOfFourInARow = numberOfFourInARow;
    this.nextCounter = nextCounter;
  }

  public Board getBoard() {
    return board;
  }

  public ArrayList<Integer> getMoveList() {
    return moveList;
  }

  public int getNumberOfTwoInARow() {
    return numberOfTwoInARow;
  }

  public int getNumberOfThreeInARow() {
    return numberOfThreeInARow;
  }

  public int getNumberOfFourInARow() {
    return numberOfFourInARow;
  }

  public Counter getNextCounter() {
    return nextCounter;
  }

  public GameState stateWithAdditionalMove(int move) throws InvalidMoveException {

    ArrayList<Integer> newMoveList = new ArrayList<>(moveList);
    newMoveList.add(move);

    Board newBoard = new Board(board, move, nextCounter);

    int newNumberOfTwoInARow = updateNumberOfNInARow(2, numberOfTwoInARow, move);
    int newNumberOfThreeInARow = updateNumberOfNInARow(3, numberOfThreeInARow, move);
    int newNumberOfFourInARow = updateNumberOfNInARow(4, numberOfFourInARow, move);

    return new GameState(
        newBoard,
        newMoveList,
        newNumberOfTwoInARow,
        newNumberOfThreeInARow,
        newNumberOfFourInARow,
        nextCounter.getOther());
  }

  private int updateNumberOfNInARow(int n, int numberOfNInARow, int move) {

    Position movePosition = getMovePosition(move);

    numberOfNInARow += countNInLineAtPosition(n, 0, 1, movePosition, nextCounter);
    numberOfNInARow += countNInLineAtPosition(n, 1, 0, movePosition, nextCounter);
    numberOfNInARow += countNInLineAtPosition(n, 1, 1, movePosition, nextCounter);
    numberOfNInARow += countNInLineAtPosition(n, 1, -1, movePosition, nextCounter);

    return numberOfNInARow;
  }

  // Adapted from Board method getMinVacantY
  private Position getMovePosition(int move) {
    for (int i = this.board.getConfig().getHeight() - 1; i >= 0; --i) {
      if (i == 0 || this.board.getCounterPlacements()[move][i - 1] != null) {
        return new Position(move, i);
      }
    }
    throw new RuntimeException("This move is invalid because the column is full.");
  }

  private int countNInLineAtPosition(
      int n, int stepX, int stepY, Position coordinate, Counter counter) {

    int numberOfCountersFoundInLine = 1;

    Position currentPosition = new Position(coordinate.getX(), coordinate.getY());
    while (true) {
      currentPosition = new Position(currentPosition.getX() + stepX, currentPosition.getY() + stepY);
      Counter counterAtCurrentPosition = board.getCounterAtPosition(currentPosition);

      if (counterAtCurrentPosition == counter.getOther() || counterAtCurrentPosition == null) {
        break;
      } else {
        ++numberOfCountersFoundInLine;
      }
    }

    currentPosition = new Position(coordinate.getX(), coordinate.getY());
    while (true) {
      currentPosition = new Position(currentPosition.getX() - stepX, currentPosition.getY() - stepY);
      Counter counterAtCurrentPosition = board.getCounterAtPosition(currentPosition);

      if (counterAtCurrentPosition == counter.getOther() || counterAtCurrentPosition == null) {
        break;
      } else {
        ++numberOfCountersFoundInLine;
      }
    }

    if (numberOfCountersFoundInLine < n) {
      return 0;
    } else {
      return numberOfCountersFoundInLine - n + 1;
    }
  }
}