package com.thg.accelerator23.connectn.ai.disrespectn;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.Player;

public class DisrespectN extends Player {
  int gameMoveNumber;

  public DisrespectN(Counter counter) {
    super(counter, DisrespectN.class.getName());
    gameMoveNumber = 1;
  }

  @Override
  public int makeMove(Board board) {
    Counter[][] counterPlacements = board.getCounterPlacements();
    Counter myCounter = this.getCounter();
    ++gameMoveNumber;
    return 4;
  }

  // Returns how many n-in-a-line patterns of counter are in board.
  // TODO: Implement this
  private int nInALineCount(int n, Counter counter, Board board) {

    return 4;
  }

  // TODO: Implement this with infinite integers.
  private int heuristicUtility(Counter counter, Board board) {
    int twoInALineCount = nInALineCount(2, counter, board);
    int threeInALineCount = nInALineCount(3, counter, board);
    int fourInALineCount = nInALineCount(4, counter, board);
    return twoInALineCount + (10 * threeInALineCount) + (1000 * fourInALineCount);
  }
}