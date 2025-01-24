package com.thg.accelerator23.connectn.ai.disrespectn;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import com.thehutgroup.accelerator.connectn.player.Player;
import java.util.ArrayList;
// import java.util.ArrayList;
// import java.util.HashMap;
import java.util.Map;

public class DisrespectN extends Player {
  private int gameMoveNumber;
  private StateNode root;
  // TODO: Implement actual infinity lol.
  public static final int infinity = 1000000;
  private int depth = 5;

  public DisrespectN(Counter counter, ArrayList<Integer> moveList, int depth)
      throws InvalidMoveException {
    super(counter, DisrespectN.class.getName());
    gameMoveNumber = (moveList.size() + 1) / 2;
    root = new StateNode(new GameState(moveList));
    this.depth = depth;
  }

  public DisrespectN(Counter counter, ArrayList<Integer> moveList) throws InvalidMoveException {
    super(counter, DisrespectN.class.getName());
    gameMoveNumber = (moveList.size() + 1) / 2;
    root = new StateNode(new GameState(moveList));
  }

  public DisrespectN(Counter counter) {
    super(counter, DisrespectN.class.getName());
    gameMoveNumber = 1;
    root = new StateNode();
  }

  public DisrespectN(Counter counter, int depth) {
    this(counter);
    this.depth = depth;
  }

  public StateNode getRoot() {
    return root;
  }

  @Override
  public int makeMove(Board board) {
    // Counter[][] counterPlacements = board.getCounterPlacements();
    Counter myCounter = this.getCounter();

    // Unless it is the first move and we are playing first, we need to get the last
    // move made by
    // the opponent and update the root.
    if (!(gameMoveNumber == 1 && myCounter == Counter.O)) {
      int moveMadeByOpponent = getLastMove(board);

      try {
        root = root.exploreAndGetChild(moveMadeByOpponent); // Go down the tree.
      } catch (InvalidMoveException e) {
        throw new RuntimeException("I tried to explore the last move made by opponent and failed.");
      }
    }

    // HACK: Infinity plus one?! You can do better, Hernán;
    int move = minimax(root, depth, -infinity - 1, infinity + 1, myCounter, true);

    try {
      root = root.exploreAndGetChild(move);
    } catch (InvalidMoveException e) {
      move = randomValidMove();
      try {
        root = root.exploreAndGetChild(move); // Try the random valid move
      } catch (InvalidMoveException ex) {
        System.out.println("No valid move available.");
        return 4;
      }
    }

    // Continue with the next part of the game logic
    ++gameMoveNumber;

    //System.out.println(root.getGameState().getMoveList());

    return move;
  }

  // Stolen from Board class
  // TODO: Reminding Lewis and Shaun that this should be public!
  private static int getColumnHeight(int x, Board board) {
    for (int i = board.getConfig().getHeight(); i >= 0; --i) {
      if (i == 0 || board.getCounterPlacements()[x][i - 1] != null) {
        return i;
      }
    }
    throw new RuntimeException("no y is vacant");
  }

  public int getLastMove(Board board) {
    // Iterate through columns, find the discrepancy in heights with root
    for (int i = 0; i < 10; i++) {
      int newColumnHeight = getColumnHeight(i, board);
      int oldColumnHeight = getColumnHeight(i, root.getGameState().getBoard());
      if (newColumnHeight > oldColumnHeight) {
        return i;
      }
    }
    throw new RuntimeException("Could not determine which move the opponent made.");
  }

  // TODO: When the best available utility is +infinity then we should choose the
  // shortest path. When the best available utility is -infinity we should choose
  // the longest path.
  public static int minimax(
      StateNode node,
      int depth,
      int alpha,
      int beta,
      Counter counter,
      boolean getMoveInsteadOfUtility) {
    if (depth == 0) {
      if (getMoveInsteadOfUtility) {
        throw new RuntimeException("You can't call minimax with depth 0.");
      } else {
        int utility;
        try {
          utility = heuristicUtility(node.getGameState(), counter);
        } catch (RuntimeException e) {
          return -infinity - 1;
        }
        return utility;
      }
    }

    int bestAvailableUtility = -infinity;
    int moveAchievingBestUtility = 4;
    StateNode childConsidered;
    int numberOfInvalidPositionsEncountered = 0;
    for (int i = 0; i < 10; i++) {
      try {
        childConsidered = node.exploreAndGetChild(i);
      } catch (InvalidMoveException e) {
        continue;
      }

      int opponentsUtility =
          minimax(childConsidered, depth - 1, alpha, beta, counter.getOther(), false);

      if (opponentsUtility == -infinity) {
        if (getMoveInsteadOfUtility) {
          return i;
        } else {
          return -opponentsUtility;
        }
      }

      if (opponentsUtility == -infinity - 1) {
        ++numberOfInvalidPositionsEncountered;
        continue;
      }

      if (opponentsUtility == +infinity) {
        continue;
      }

      if (bestAvailableUtility < -opponentsUtility) {
        bestAvailableUtility = -opponentsUtility;
        moveAchievingBestUtility = i;
      }
    }

    if (numberOfInvalidPositionsEncountered == 10) {
      return minimax(node, 0, alpha, beta, counter, getMoveInsteadOfUtility);
    }

    return getMoveInsteadOfUtility ? moveAchievingBestUtility : bestAvailableUtility;
  }

  public static int heuristicUtility(GameState gameState, Counter counter) {
    Map<Integer, Integer> myNumberOfNinALine = gameState.getNumberOfNinALineByCounter(counter);
    Map<Integer, Integer> opponentNumberOfNinALine =
        gameState.getNumberOfNinALineByCounter(counter.getOther());

    if (myNumberOfNinALine.get(4) > 0 && opponentNumberOfNinALine.get(4) == 0) {
      return infinity;
    } else if (myNumberOfNinALine.get(4) == 0 && opponentNumberOfNinALine.get(4) > 0) {
      return -infinity;
    } else if (myNumberOfNinALine.get(4) == 0 && opponentNumberOfNinALine.get(4) == 0) {
      int myScore = myNumberOfNinALine.get(2) + 10 * myNumberOfNinALine.get(3);
      int oponnentScore = opponentNumberOfNinALine.get(2) + 10 * opponentNumberOfNinALine.get(3);
      return myScore - oponnentScore;
    } else {
      throw new RuntimeException("Invalid position, cannot calculate heuristic utility.");
    }
  }

  public int randomValidMove() {
    for (int i = 0; i < 10; i++) {
      try {
        root.exploreMove(i);
      } catch (InvalidMoveException e) {
        continue;
      }
      return i;
    }
    throw new RuntimeException("I can't find any valid moves.");
  }
}