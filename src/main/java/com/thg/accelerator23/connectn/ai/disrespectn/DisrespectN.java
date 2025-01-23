package com.thg.accelerator23.connectn.ai.disrespectn;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import com.thehutgroup.accelerator.connectn.player.Player;
// import java.util.ArrayList;
// import java.util.HashMap;
import java.util.Map;

public class DisrespectN extends Player {
  private int gameMoveNumber;
  private StateNode root;
  // TODO: Implement actual infinity lol.
  private static int infinity = 1000000;

  public DisrespectN(Counter counter) {
    super(counter, DisrespectN.class.getName());
    gameMoveNumber = 1;
    root = new StateNode();
  }

  @Override
  public int makeMove(Board board) {
    Counter[][] counterPlacements = board.getCounterPlacements();
    Counter myCounter = this.getCounter();

    // HACK: Infinity plus one?! You can do better, Hernán;
    int move = minimax(root, 3, -infinity - 1, infinity + 1, myCounter, true);

    ++gameMoveNumber;

    return move;
  }

  // TODO: When the best available utility is +infinity then we should choose the
  // shortest
  // path. When the best available utility is -infinity we should choose the
  // longest path.
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
        return heuristicUtility(node.getGameState(), counter);
      }
    }

    int bestAvailableUtility = -infinity;
    int moveAchievingBestUtility = 4;
    StateNode childConsidered;
    for (int i = 0; i < 10; i++) {
      try {
        childConsidered = node.exploreAndGetChild(i);
      } catch (InvalidMoveException e) {
        throw new RuntimeException("The minimax algorithm tried to explore an invalid node");
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

      if (opponentsUtility == +infinity) {
        continue;
      }

      if (bestAvailableUtility < -opponentsUtility) {
        bestAvailableUtility = -opponentsUtility;
        moveAchievingBestUtility = i;
      }
    }

    if (getMoveInsteadOfUtility) {
      return moveAchievingBestUtility;
    } else {
      return bestAvailableUtility;
    }
  }

  private static int heuristicUtility(GameState gameState, Counter counter) {
    Map<Integer, Integer> myNumberOfNinALine = gameState.getNumberOfNinALineByCounter(counter);
    Map<Integer, Integer> opponentNumberOfNinALine =
        gameState.getNumberOfNinALineByCounter(counter.getOther());

    if (myNumberOfNinALine.get(4) > 0 && opponentNumberOfNinALine.get(4) == 0) {
      return infinity;
    } else if (myNumberOfNinALine.get(4) == 0 && opponentNumberOfNinALine.get(4) > 0) {
      return -infinity;
    } else if (myNumberOfNinALine.get(4) == 0 && opponentNumberOfNinALine.get(4) == 0) {
      int myScore = myNumberOfNinALine.get(2) + 10 * myNumberOfNinALine.get(3);
      int oponnentScore = opponentNumberOfNinALine.get(2) + 10 * myNumberOfNinALine.get(3);
      return myScore - oponnentScore;
    } else {
      throw new RuntimeException("Invalid position, cannot calculate heuristic utility.");
    }
  }
}