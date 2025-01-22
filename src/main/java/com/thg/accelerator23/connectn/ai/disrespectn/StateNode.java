package com.thg.accelerator23.connectn.ai.disrespectn;

import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;

public class StateNode {
  private GameState gameState;
  private StateNode[] children;

  public StateNode(GameState gameState) {
    this.gameState = gameState;
    this.children = new StateNode[10];
  }

  public void exploreMove(int move) throws InvalidMoveException {
    if (children[move] != null) {
      return;
    }
    children[move] = new StateNode(gameState.stateWithAdditionalMove(move));
  }

  public GameState getGameState() {
    return gameState;
  }
}