package com.channer;

import java.util.List;

/**
 * Created by channerduan on 1/2/16.
 */
public class GameTreeSearch {

    private boolean isPruningArea;

    private boolean isBlackSearch;
    private int mDepth;

    private int mNodes[];

    private int totalSubstateGenerated = 0;

    private String mResultMove;
    private int mResultValue;

    public GameTreeSearch(boolean isBlack, int depth, boolean isPruningArea) {
        this.isPruningArea = isPruningArea;
        isBlackSearch = isBlack;
        this.mDepth = depth;
        mNodes = new int[depth];
    }

    // time cost record
    private long mTimeMark;
    private int mTimeCost;

    private void triggerTimeRecord() {
        mTimeMark = System.currentTimeMillis();
    }

    private void stopTimeRecord() {
        mTimeCost = (int) (System.currentTimeMillis() - mTimeMark);
    }

    // DeduceSort params
    private boolean isDeduceSort = false;

    public GameTreeSearch enableDeduceSortForABSearch() {
        isDeduceSort = true;
        return this;
    }

    // Stop at win
    private boolean isStopAtWin = false;

    public GameTreeSearch enableStopAtWinForDeduceSortABSearch() {
        isStopAtWin = true;
        return this;
    }

    // Filter some Substate
    private boolean isFilterSubstate = false;
    private double filterRatio;
    private int depthKeep;  // just filter the depth lower than it

    public GameTreeSearch enableFilterSubstateForDeduceSortABSearch(double filterRatio, int depthKeep) {
        isFilterSubstate = true;
        this.filterRatio = filterRatio;
        this.depthKeep = depthKeep;
        return this;
    }

    private List<State> filter(List<State> list) {
//        System.out.println("original size:" + list.size());
        int finalSize = (int) ((double) list.size() * filterRatio + 0.5d);
        list = list.subList(0, finalSize);
//        System.out.println("final size:" + list.size() + "\n");
        return list;
    }


    private String mTag;

    public GameTreeSearch setTag(String tag) {
        mTag = tag;
        return this;
    }

    public String getMoveStr() {
        return mResultMove;
    }

    public int getMoveX() {
        return mResultMove.charAt(0) - 'a';
    }

    public int getMoveY() {
        return mResultMove.charAt(1) - 'a';
    }

    public int getTime() {
        return mTimeCost;
    }

    public int getEvaluateTime() {
        return evaluateTime;
    }

    public int getGeneratedNum() {
        return totalSubstateGenerated;
    }

    public int getNodesNum() {
        int sum = 0;
        int i;
        for (i = mDepth - 1; i >= 0; i--) {
            sum += mNodes[i];
        }
        return sum;
    }

    public void print() {
        int i;
        System.out.println("");
        if (mTag != null) System.out.println(mTag + " (" + mDepth + "):");
        System.out.println("Suggested move:" + mResultMove);
        System.out.println("Suggested value:" + mResultValue);
        int sum = 0;
        for (i = mDepth - 1; i >= 0; i--) {
            sum += mNodes[i];
        }
        System.out.println("Node expand (" + sum + "):");
        for (i = mDepth - 1; i >= 0; i--) {
            System.out.println("level " + i + ":" + mNodes[i]);
        }
        System.out.println("totalSubstateGenerated:" + totalSubstateGenerated);
        System.out.println("evaluate time:" + evaluateTime);
        System.out.println("time cost:" + mTimeCost);
        if (deduceCost != 0)
            System.out.println("time deduce cost:" + deduceCost);
        if (deduceCost1 != 0)
            System.out.println("time deduce1 cost:" + deduceCost1);
        if (deduceCost2 != 0)
            System.out.println("time deduce2 cost:" + deduceCost2);

    }

    /*
     *  minimax search
     */

    public GameTreeSearch MinimaxSearch(State state) {
        triggerTimeRecord();
        List<State> list = state.deduce(isBlackSearch, isPruningArea);
        if (list.isEmpty()) {
            // "" means draw
            mResultMove = "";
            return this;
        }
        mNodes[mDepth - 1] += list.size();
        int value = Integer.MIN_VALUE;
        int tmpValue;
        State tmp = null;
        for (State childState : list) {
            tmpValue = MinValue(childState, mDepth - 1, !isBlackSearch);
//            System.out.println("" + (++testMark) + " tmpValue:" + tmpValue);
            if (tmpValue > value) {
                value = tmpValue;
                tmp = childState;
            }
        }
        totalSubstateGenerated += list.size();
        mResultMove = tmp.searchInfo.toString();
        mResultValue = value;
        stopTimeRecord();
        return this;
    }

    private int MaxValue(State state, int depth, boolean isBlackTurn) {
        if (depth == 0) {
            return EvaluationModel.Evaluates(state, isBlackSearch);
        }
        List<State> list = state.deduce(isBlackTurn, isPruningArea);
        if (list.isEmpty()) return EvaluationModel.Evaluates(state, isBlackSearch);
        totalSubstateGenerated += list.size();
        mNodes[depth - 1] += list.size();
        int value = Integer.MIN_VALUE;
        int tmpValue;
        for (State childState : list) {
            tmpValue = MinValue(childState, depth - 1, !isBlackTurn);
            if (tmpValue > value) value = tmpValue;
        }
        return value;
    }

    private int MinValue(State state, int depth, boolean isBlackTurn) {
        if (depth == 0) {
            return EvaluationModel.Evaluates(state, isBlackSearch);
        }
        List<State> list = state.deduce(isBlackTurn, isPruningArea);
        if (list.isEmpty()) return EvaluationModel.Evaluates(state, isBlackSearch);
        totalSubstateGenerated += list.size();
        mNodes[depth - 1] += list.size();
        int value = Integer.MAX_VALUE;
        int tmpValue;
        for (State childState : list) {
            tmpValue = MaxValue(childState, depth - 1, !isBlackTurn);
            if (tmpValue < value) value = tmpValue;
        }
        return value;
    }


    /*
     *  a-b search
     */
    public GameTreeSearch ABSearch(State state) {
        triggerTimeRecord();
        List<State> list;
        if (isDeduceSort) {
            list = state.deduce(isBlackSearch, isPruningArea, -1, isBlackSearch);
            totalSubstateGenerated += list.size();
            // I don't want to reduce it!!!
            if (isFilterSubstate && mDepth < depthKeep) list = filter(list);
        } else {
            list = state.deduce(isBlackSearch, isPruningArea);
            totalSubstateGenerated += list.size();
        }
        if (list.isEmpty()) {
            // "" means draw
            mResultMove = "";
            return this;
        }
        mNodes[mDepth - 1] += list.size();
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;
        int tmpValue;
        State tmp = null;
        for (State childState : list) {
            // stop the search at win node
            if (isStopAtWin && isDeduceSort && HeuristicModel.isWin(childState.value, isBlackSearch)) {
                a = childState.value;
                tmp = childState;
                break;
            }
            tmpValue = MinValue(childState, mDepth - 1, !isBlackSearch, a, b);
            if (tmpValue > a) {
                a = tmpValue;
                tmp = childState;
            }
        }

        mResultMove = tmp.searchInfo.toString();
        mResultValue = a;
        stopTimeRecord();
        return this;
    }

    private long tmpForTimeCount;

    private long deduceCost = 0;
    private long deduceCost1 = 0;
    private long deduceCost2 = 0;

    // core!!!
    private int evaluateTime = 0;


    private int MaxValue(State state, int depth, boolean isBlackTurn, int a, int b) {
        if (depth == 0) {
            if (isDeduceSort) return state.value;
            else {
                evaluateTime++;
                return EvaluationModel.Evaluates(state, isBlackSearch);
            }
        }
        List<State> list;
        tmpForTimeCount = System.currentTimeMillis();
        if (isDeduceSort) {
            list = state.deduce(isBlackTurn, isPruningArea, -1, isBlackSearch);
            evaluateTime += list.size();
            totalSubstateGenerated += list.size();
            if (isFilterSubstate && depth < depthKeep) list = filter(list);
        } else {
            list = state.deduce(isBlackTurn, isPruningArea);
            totalSubstateGenerated += list.size();
        }
        deduceCost += System.currentTimeMillis() - tmpForTimeCount;
        deduceCost1 += state.deduceCost1;
        deduceCost2 += state.deduceCost2;
        if (list.isEmpty()) return EvaluationModel.Evaluates(state, isBlackSearch);
        int tmpValue;
        for (State childState : list) {
            mNodes[depth - 1]++;
            // stop the search at win node
            if (isStopAtWin && isDeduceSort && HeuristicModel.isWin(childState.value, isBlackTurn)) {
                return childState.value;
            }
            tmpValue = MinValue(childState, depth - 1, !isBlackTurn, a, b);
            if (tmpValue > a) {
                a = tmpValue;
                if (a >= b) return a;
            }
        }
        return a;
    }

    private int testMark = 0;

    private int MinValue(State state, int depth, boolean isBlackTurn, int a, int b) {
        if (depth == 0) {
            if (isDeduceSort) return state.value;
            else {
                evaluateTime++;
                return EvaluationModel.Evaluates(state, isBlackSearch);
            }
        }
        List<State> list;
        tmpForTimeCount = System.currentTimeMillis();
        if (isDeduceSort) {
            list = state.deduce(isBlackTurn, isPruningArea, 1, isBlackSearch);
            evaluateTime += list.size();
            totalSubstateGenerated += list.size();
            if (isFilterSubstate && depth < depthKeep) list = filter(list);
        } else {
            list = state.deduce(isBlackTurn, isPruningArea);
            totalSubstateGenerated += list.size();
        }
        deduceCost += System.currentTimeMillis() - tmpForTimeCount;
        deduceCost1 += state.deduceCost1;
        deduceCost2 += state.deduceCost2;
        if (list.isEmpty()) return EvaluationModel.Evaluates(state, isBlackSearch);
        int tmpValue;
        for (State childState : list) {
            mNodes[depth - 1]++;
            // stop the search at win node
            if (isStopAtWin && isDeduceSort && HeuristicModel.isWin(childState.value, isBlackTurn)) {
                return childState.value;
            }
            tmpValue = MaxValue(childState, depth - 1, !isBlackTurn, a, b);
//            System.out.println("" + (++testMark) +" a:" + a + " b:" + b);
            if (tmpValue < b) {
                b = tmpValue;
                if (b <= a) return b;
            }
        }
        return b;
    }

}
