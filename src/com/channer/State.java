package com.channer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by channerduan on 1/2/16.
 */
public class State {
    // Size of board, important!
    public static final int BORAD_SIZE = 9;

    public static final char VALUE_OF_BLACK = 1;
    public static final char VALUE_OF_WHITE = 10;


    // create for diretly use State as search node
    public SearchInfo searchInfo;
    public int value = 0;

    public State actionForSearch(int x, int y) {
        searchInfo = new SearchInfo(x, y);
        return this;
    }

    public static final class SearchInfo {
        public int moveX;  // horizontal
        public int moveY;  // vertical

        public SearchInfo(int x, int y) {
            this.moveX = x;
            this.moveY = y;
        }

        public String toString() {
            return "" + (char) ('a' + moveX) + (char) ('a' + moveY);
        }
    }

    // Create specific situation
    public static State CREATE(String blacks, String white) {
        State state = new State();
        String pieces;
        for (int i = 0; i < 2; i++) {
            if (i == 0) pieces = blacks;
            else pieces = white;
            for (int j = 0; j < pieces.length() - 1; j += 2) {
                state.put(pieces.charAt(j) - 'a', pieces.charAt(j + 1) - 'a', i == 0);
            }
        }
        return state;
    }


    public static final int VALID_AREA_CONSTRAINT = 2;

    // Squares
    char board[][];

    // Valid area, to prune
    int l, b, r, t;


    public State() {
        board = new char[BORAD_SIZE][];
        for (int i = 0; i < BORAD_SIZE; i++) {
            board[i] = new char[BORAD_SIZE];
            for (int j = 0; j < BORAD_SIZE; j++)
                board[i][j] = 0;
        }
        l = b = BORAD_SIZE;
        r = t = -1;
    }

    public State(State state) {
        this.l = state.l;
        this.r = state.r;
        this.t = state.t;
        this.b = state.b;
        board = new char[BORAD_SIZE][];
        for (int i = 0; i < BORAD_SIZE; i++) {
            board[i] = new char[BORAD_SIZE];
            for (int j = 0; j < BORAD_SIZE; j++)
                board[i][j] = state.board[i][j];
        }
    }

    public State evaluate(boolean isBlack) {
        value = EvaluationModel.Evaluates(this, isBlack);
        return this;
    }

    public List<State> deduce(boolean isBlack, boolean isPruningByValidArea) {
        return deduce(isBlack, isPruningByValidArea, 0, false);
    }

    // research the performance
    private long tmpForTimeCount;
    public long deduceCost1 = 0;
    public long deduceCost2 = 0;

    public List<State> deduce(boolean isBlack, boolean isPruningByValidArea, int sort, boolean isBlackSearch) {
        List<State> list = new ArrayList<State>();
        int l, r, b, t;
        if (isPruningByValidArea) {
            l = this.l;
            r = this.r;
            b = this.b;
            t = this.t;
        } else {
            l = 0;
            r = BORAD_SIZE - 1;
            b = 0;
            t = BORAD_SIZE - 1;
        }
        tmpForTimeCount = System.currentTimeMillis();
        int i, j;
        for (i = l; i <= r; i++) {
            for (j = b; j <= t; j++) {
                if (board[i][j] == 0) {
                    if (sort == 0) {
                        list.add(new State(this).put(i, j, isBlack).actionForSearch(i, j));
                    } else {
                        list.add(new State(this).put(i, j, isBlack).actionForSearch(i, j).evaluate(isBlackSearch));
                    }
                }
            }
        }
        deduceCost1 = System.currentTimeMillis() - tmpForTimeCount;
        if (sort != 0) {
            tmpForTimeCount = System.currentTimeMillis();
            list = Util.StateSort(list, sort == 1);
            deduceCost2 = System.currentTimeMillis() - tmpForTimeCount;
        }
        return list;
    }

    /**
     * check a position valid or not
     */
    public boolean check(int x, int y) {
        if (board[x][y] == 0) return true;
        else return false;
    }

    public State put(int x, int y, boolean isBlack) {
        if (isBlack) board[x][y] = VALUE_OF_BLACK;
        else board[x][y] = VALUE_OF_WHITE;
        if (x - VALID_AREA_CONSTRAINT < l) {
            l = x - VALID_AREA_CONSTRAINT;
            if (l < 0) l = 0;
        }
        if (x + VALID_AREA_CONSTRAINT > r) {
            r = x + VALID_AREA_CONSTRAINT;
            if (r > BORAD_SIZE - 1) r = BORAD_SIZE - 1;
        }
        if (y - VALID_AREA_CONSTRAINT < b) {
            b = y - VALID_AREA_CONSTRAINT;
            if (b < 0) b = 0;
        }
        if (y + VALID_AREA_CONSTRAINT > t) {
            t = y + VALID_AREA_CONSTRAINT;
            if (t > BORAD_SIZE - 1) t = BORAD_SIZE - 1;
        }
        return this;
    }

    public void print() {
        for (int j = BORAD_SIZE - 1; j >= 0; j--) {
            for (int i = 0; i < BORAD_SIZE; i++) {
                switch (board[i][j]) {
                    case VALUE_OF_BLACK:
                        System.out.print(" # ");
                        break;
                    case VALUE_OF_WHITE:
                        System.out.print(" % ");
                        break;
                    default:
                        System.out.print(" - ");
                        break;
                }
            }
            System.out.println(" " + (char) ('a' + j));
        }
        for (int i = 0; i < BORAD_SIZE; i++)
            System.out.print(" " + (char) ('a' + i) + " ");
        System.out.print("\n");
//        System.out.println("" + l + " " + t + " " + r + " " + b);
    }
}
