package org.connect4.ai;

import org.connect4.game.Board;

public interface IHeuristic {
    public int evaluate(Board board);
}
