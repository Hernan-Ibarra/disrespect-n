package com.thg.accelerator23.connectn.ai.disrespectn;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.GameConfig;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import com.thehutgroup.accelerator.connectn.player.Position;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {
  private final Board board;
  private final ArrayList<Integer> moveList;
  private Map<Integer, Integer> numberOfNInALineX;
  private Map<Integer, Integer> numberOfNInALineO;
  private final Counter nextCounter;

  public GameState(
      Board board,
      ArrayList<Integer> moveList,
      Map<Integer, Integer> numberOfNInALineX,
      Map<Integer, Integer> numberOfNInALineO,
      Counter nextCounter) {
    this.board = board;
    this.moveList = moveList;
    this.numberOfNInALineX = numberOfNInALineX;
    this.numberOfNInALineO = numberOfNInALineO;
    this.nextCounter = nextCounter;
  }

  private static final GameConfig defaultConfig = new GameConfig(10, 8, 4);
  private static final Board emptyBoard = new Board(defaultConfig);
  private static final ArrayList<Integer> emptyMoveList = new ArrayList<>();
  private static final Map<Integer, Integer> defaultNumberOfNInALineX =
      new HashMap<>() {
        {
          put(2, 0);
          put(3, 0);
          put(4, 0);
        }
      };
  private static final Map<Integer, Integer> defaultNumberOfNInALineO =
      new HashMap<>() {
        {
          put(2, 0);
          put(3, 0);
          put(4, 0);
        }
      };
  private static final Counter startingCounter = Counter.O;

  public GameState() {
    this(
        emptyBoard,
        emptyMoveList,
        defaultNumberOfNInALineX,
        defaultNumberOfNInALineO,
        startingCounter);
  }

  public GameState(ArrayList<Integer> moveList) throws InvalidMoveException {
    GameState currentState = new GameState();
    for (int move : moveList) {
      currentState = currentState.stateWithAdditionalMove(move);
    }
    this.board = currentState.getBoard();
    this.moveList = moveList;
    this.numberOfNInALineX = currentState.getNumberOfNinALineByCounter(Counter.X);
    this.numberOfNInALineO = currentState.getNumberOfNinALineByCounter(Counter.O);
    this.nextCounter = currentState.getNextCounter();
  }

  public Board getBoard() {
    return board;
  }

  public ArrayList<Integer> getMoveList() {
    return moveList;
  }

  public Counter getNextCounter() {
    return nextCounter;
  }

  public Map<Integer, Integer> getNumberOfNinALineByCounter(Counter counter) {
    switch (counter) {
      case X:
        return numberOfNInALineX;
      case O:
        return numberOfNInALineO;
      default:
        throw new RuntimeException("Counter is not X or O");
    }
  }

  public GameState stateWithAdditionalMove(int move) throws InvalidMoveException {
    Board newBoard = new Board(board, move, nextCounter);
    ArrayList<Integer> newMoveList = new ArrayList<>(moveList);
    newMoveList.add(move);

    Map<Integer, Integer> newNumberOfNInALineX = numberOfNInALineX;
    Map<Integer, Integer> newNumberOfNInALineO = numberOfNInALineO;

    switch (nextCounter) {
      case X:
        newNumberOfNInALineX = updateNumberOfNInALine(numberOfNInALineX, move, Counter.X);
        break;
      case O:
        newNumberOfNInALineO = updateNumberOfNInALine(numberOfNInALineO, move, Counter.O);
        break;
      default:
        break;
    }

    return new GameState(
        newBoard, newMoveList, newNumberOfNInALineX, newNumberOfNInALineO, nextCounter.getOther());
  }

  private Map<Integer, Integer> updateNumberOfNInALine(
      Map<Integer, Integer> numberOfNInALine, int move, Counter counter) {

    Position movePosition = getMovePosition(move);

    Map<Integer, Integer> newNumberOfNInALine = new HashMap<>(numberOfNInALine);

    for (int i = 2; i <= 4; i++) {
      int count = newNumberOfNInALine.getOrDefault(i, 0);

      count += countNInLineAtPosition(i, 0, 1, movePosition, counter);
      count += countNInLineAtPosition(i, 1, 0, movePosition, counter);
      count += countNInLineAtPosition(i, 1, 1, movePosition, counter);
      count += countNInLineAtPosition(i, 1, -1, movePosition, counter);

      newNumberOfNInALine.put(i, count);
    }

    return newNumberOfNInALine;
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

    int numberOfCountersFoundInDirectionOne = 0;
    Position currentPosition = new Position(coordinate.getX(), coordinate.getY());
    while (true) {
      currentPosition =
          new Position(currentPosition.getX() + stepX, currentPosition.getY() + stepY);
      Counter counterAtCurrentPosition = board.getCounterAtPosition(currentPosition);

      if (counterAtCurrentPosition == counter) {
        ++numberOfCountersFoundInDirectionOne;
      } else {
        break;
      }
    }

    int numberOfCountersFoundInDirectionTwo = 0;
    currentPosition = new Position(coordinate.getX(), coordinate.getY());
    while (true) {
      currentPosition =
          new Position(currentPosition.getX() - stepX, currentPosition.getY() - stepY);
      Counter counterAtCurrentPosition = board.getCounterAtPosition(currentPosition);

      if (counterAtCurrentPosition == counter) {
        ++numberOfCountersFoundInDirectionTwo;
      } else {
        break;
      }
    }

    // I apologise for the rest of this code. This is the mathematician in me trying
    // to code.
    int p = numberOfCountersFoundInDirectionOne;
    int q = numberOfCountersFoundInDirectionTwo;

    if (p + q < n - 1) {
      return 0;
    } else if (q <= n - 1 && p <= n - 1) {
      return p + q - n + 2;
    } else if (p <= n - 1) {
      return p + 1;
    } else if (q <= n - 1) {
      return q + 1;
    } else if (p >= n && q >= n) {
      return n;
    } else {
      throw new RuntimeException("Something went wrong when counting counters.");
    }
  }
}