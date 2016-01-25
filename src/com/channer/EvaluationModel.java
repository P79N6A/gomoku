package com.channer;

import java.util.List;

/**
 * Created by channerduan on 1/2/16.
 */
public class EvaluationModel {


    public static int Evaluates(State state, boolean isBlack) {
        int value = 0;
        value += evaluateForPattern(state, 5);
        value += evaluateForPattern(state, 6);
        value += evaluateForPattern(state, 7);
        value += evaluateForPattern(state, 8);
        if (!isBlack) value = -value;
        return value;
    }

    public static int Evaluates(State state) {
        return Evaluates(state, true);
    }

    /**
     * Game result detect
     *
     * @param state
     * @return 1 means black win
     * -1 means white win
     * -2 means draw
     */
    public static int EndDetection(State state) {
        boolean isDraw = true;
        for (int i = 0;i < State.BORAD_SIZE;i++) {
            for (int j = 0;j < State.BORAD_SIZE;j++) {
                if (state.board[i][j] == 0) {
                    isDraw = false;
                    break;
                }
            }
        }
        if (isDraw) return -2;
        int value = evaluateForPattern(state, 5);
        if (value > HeuristicModel.THRESHOLD_OF_WIN) return 1;
        else if (value < -HeuristicModel.THRESHOLD_OF_WIN) return -1;
        else return 0;
    }

    private static class Pattern {
        public int patternSize;
        char pattern[];
        int index;

        public Pattern(int patternSize) {
            this.patternSize = patternSize;
            pattern = new char[patternSize];
            index = -1;
        }

        public void reset() {
            index = -1;
        }

        public void put(char c) {
            index = (index + 1) % patternSize;
            pattern[index] = c;
        }

        public int matches() {
            List<char[]> list = HeuristicModel.getTemplate(patternSize);
            int i, j;
            char[] template;
            int length = list.size();
            for (i = 0; i < length; i++) {
                template = list.get(i);
                for (j = 0; j < patternSize; j++) {
                    // add 1 for pattern is important! point to front of pattern
                    if (pattern[(index + j + 1) % patternSize] != template[j]) break;
                }
                if (j == patternSize) {
//                    System.out.println("patternSize:" + patternSize + "  index:" + i);
                    return HeuristicModel.getTemplateValue(patternSize, i);
                }
            }
            return 0;
        }
    }

    private static int evaluateForPattern(State state, int patternSize) {
        int size = State.BORAD_SIZE;
        int value = 0;
        int i, j;
        Pattern pattern1 = new Pattern(patternSize);
        Pattern pattern2 = new Pattern(patternSize);
        for (i = 0; i < size; i++) {
            pattern1.reset();
            pattern2.reset();
            for (j = 0; j < patternSize; j++) {
                pattern1.put(state.board[j][i]);
                pattern2.put(state.board[i][j]);
            }
            j--;
            do {
//                System.out.println("i:" + i + " j:" + j);
                value += pattern1.matches();
                value += pattern2.matches();

                j++;
                if (j == size) break;
//                pattern1 -= state.board[j - patternSize][i];
                pattern1.put(state.board[j][i]);
//                pattern1 += state.board[j][i];
//                pattern2 -= state.board[i][j - patternSize];
                pattern2.put(state.board[i][j]);
//                pattern2 += state.board[i][j];
            } while (true);
        }
        //(k1-k2: 45 degree, k1-k3 : 135 degree)
        int k1, k2, k3;
        for (i = -(size - patternSize); i <= size - patternSize; i++) {
            if (i < 0) {
                k1 = -i;
                k2 = 0;
                k3 = size - 1;
            } else {
                k1 = 0;
                k2 = i;
                k3 = size - i - 1;
            }
            pattern1.reset();
            pattern2.reset();
            for (j = 0; j < patternSize; j++) {
                pattern1.put(state.board[k1 + j][k2 + j]);
                pattern2.put(state.board[k1 + j][k3 - j]);
            }
            j--;
            do {
                value += pattern1.matches();
                value += pattern2.matches();

                j++;
                // 2 patterns are symmetric, so detection for 1 is enough
                if (k1 + j == size || k2 + j == size) break;
//                pattern1 -= state.board[k1 + j - patternSize][k2 + j - patternSize];
                pattern1.put(state.board[k1 + j][k2 + j]);
//                pattern1 += state.board[k1 + j][k2 + j];
//                pattern2 -= state.board[k1 + j - patternSize][k3 - j + patternSize];
                pattern2.put(state.board[k1 + j][k3 - j]);
//                pattern2 += state.board[k1 + j][k3 - j];
            } while (true);
        }

        return value;
    }
}
