package com.channer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * the Entrance of whole project
 * Created by channerduan on 1/2/16.
 */
public class Entrance {


    public static void main(String[] args) {
        // knowledge engine init
        HeuristicModel.Initial(false);
        // basic test
//        boardTest();
//        evaluationTest();
//        sortTest();
//        specialCaseSolve();   // important to optimize the knowledge engine

        // research test
//        searchTest();
//        computersCsompeteTest();
//        SimpleCommandLineGameTest.SimpleGameTest();

        // show test
//        MainUI.showTest(State.CREATE("eeefde", "ffegfe"));

        // main
        MainUI.launch();

    }

    static void searchTest() {
        State s;
        int i, j;
        s = State.CREATE("eeefde", "ffegfe");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));

//        s = State.CREATE("edfd", "ddfc");
        s.print();
        System.out.println("" + EvaluationModel.Evaluates(s, false));

//        List<State> stateList = s.deduce(true, true);
//        System.out.println("size of list:" + stateList.size());
//        for (State tmp : stateList) {
//            tmp.print();
//            System.out.println(tmp.searchInfo.toString());
//            System.out.println("");
//        }

        // Minimax with different depth
//        boolean isAreaPruning = true;
//        new GameTreeSearch(true, 1, isAreaPruning).setTag("Minimax").MinimaxSearch(s).print();
//        new GameTreeSearch(true, 2, isAreaPruning).setTag("Minimax").MinimaxSearch(s).print();
//        new GameTreeSearch(true, 3, isAreaPruning).setTag("Minimax").MinimaxSearch(s).print();
//
//        // AB with different depth
//        new GameTreeSearch(true, 1, isAreaPruning).setTag("AB").ABSearch(s).print();
//        new GameTreeSearch(true, 2, isAreaPruning).setTag("AB").ABSearch(s).print();
//        new GameTreeSearch(true, 3, isAreaPruning).setTag("AB").ABSearch(s).print();


        // Area Pruning(1)
//        new GameTreeSearch(true, 2, false).setTag("Minimax").MinimaxSearch(s).print();
//        new GameTreeSearch(true, 2, true).setTag("Minimax").MinimaxSearch(s).print();

        // Area Pruning(2)
//        new GameTreeSearch(true, 2, false).setTag("Minimax")
//                .MinimaxSearch(State.CREATE("edfd", "ddfc")).print();
//        new GameTreeSearch(true, 2, false).setTag("Minimax")
//                .MinimaxSearch(State.CREATE("edfdgdhdeceeefeb", "afbgchdi")).print();

        // AB Pruning
//        new GameTreeSearch(true, 3, true).setTag("Minimax").MinimaxSearch(s).print();
//        new GameTreeSearch(true, 3, true).setTag("ab").ABSearch(s).print();

        // AB with Sort deduce Pruning
//        for (i = 1;i <= 4;i++) {
//            new GameTreeSearch(true, i, true)
//                    .setTag("ab").ABSearch(s).print();
//            new GameTreeSearch(true, i, true)
//                    .enableDeduceSortForABSearch()
//                    .setTag("ab with sort").ABSearch(s).print();
//        }

        // AB with filter
//        new GameTreeSearch(true, 4, true)
//                .enableDeduceSortForABSearch()
//                .enableStopAtWinForDeduceSortABSearch()
//                .setTag("ab with sort").ABSearch(s).print();
//        new GameTreeSearch(true, 4, true)
//                .enableDeduceSortForABSearch()
//                .enableStopAtWinForDeduceSortABSearch()
//                .enableFilterSubstateForDeduceSortABSearch(0.4d)
//                .setTag("ab with sort").ABSearch(s).print();

        double baseRatio = 0;
        List<GameTreeSearch> list = new ArrayList<GameTreeSearch>();
        for (i = 0; i < 10; i++) {
            baseRatio += 0.1d;
            list.add(new GameTreeSearch(true, 4, true)
                    .enableDeduceSortForABSearch()
                    .enableStopAtWinForDeduceSortABSearch()
                    .enableFilterSubstateForDeduceSortABSearch(baseRatio, 5)
                    .setTag("ab with sort").ABSearch(s));
        }
        System.out.print("Nodes = [");
        for (GameTreeSearch search : list) {
            System.out.print(search.getNodesNum() + " ");
        }
        System.out.println("]");

        System.out.print("Evaluate = [");
        for (GameTreeSearch search : list) {
            System.out.print(search.getEvaluateTime() + " ");
        }
        System.out.println("]");

        for (GameTreeSearch search : list) {
            search.print();
        }

//        new GameTreeSearch(true, 5, true)
//                .enableDeduceSortForABSearch()
//                .setTag("ab with sort").ABSearch(s).print();

//        new GameTreeSearch(true, 5, true).enableDeduceSortForABSearch()
//                .setTag("ab with sort").ABSearch(s).print();


        // AB with Sort deduce Pruning
//        new GameTreeSearch(true, 3, true).setTag("ab").enableDeduceSortForABSearch()
//                .setTag("ab with sort").ABSearch(State.CREATE("cbdddedfee", "cccdcecgeg")).print();
//        new GameTreeSearch(true, 3, true).enableDeduceSortForABSearch().enableStopAtWinForDeduceSortABSearch()
//                .setTag("ab with sort and stop").ABSearch(State.CREATE("cbdddedfee", "cccdcecgeg")).print();

    }

    static void specialCaseSolve() {
        State s;
        // stop at win problem
//        s = State.CREATE("cbdddedfee", "cccdcecgeg");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s, true));
//        new GameTreeSearch(true, 3, true).enableDeduceSortForABSearch()
//                .ABSearch(s).print();
//        new GameTreeSearch(true, 4, true).enableDeduceSortForABSearch()
//                .setTag("ab with sort").ABSearch(s).print();

        // depth optimize
        s = State.CREATE("dddedfcfecedee", "cccdcedcebegfc");
        s.print();
        System.out.println("" + EvaluationModel.Evaluates(s, true));
        new GameTreeSearch(true, 3, true)
                .enableDeduceSortForABSearch()
                .enableStopAtWinForDeduceSortABSearch()
                .ABSearch(s).print();
        new GameTreeSearch(true, 4, true)
                .enableDeduceSortForABSearch()
                .enableStopAtWinForDeduceSortABSearch()
                .enableFilterSubstateForDeduceSortABSearch(0.2d, 4)
                .ABSearch(s).print();
        new GameTreeSearch(true, 5, true)
                .enableDeduceSortForABSearch()
                .enableStopAtWinForDeduceSortABSearch()
                .enableFilterSubstateForDeduceSortABSearch(0.1d, 5)
                .ABSearch(s).print();
    }

    static void computersCompeteTest() {
        State s = new State().put(State.BORAD_SIZE / 2, State.BORAD_SIZE / 2, true);
        GameTreeSearch search;
        boolean isEnd;
        for (int i = 0; ; i++) {
            if (i % 2 == 0) {   // white turn
                System.out.println("white side thinking...\n");
//                search = new GameTreeSearch(false, 4, true)
//                        .enableDeduceSortForABSearch()
//                        .enableStopAtWinForDeduceSortABSearch()
//                        .ABSearch(s);
                search = new GameTreeSearch(false, 4, true)
                        .enableDeduceSortForABSearch()
                        .enableStopAtWinForDeduceSortABSearch()
                        .enableFilterSubstateForDeduceSortABSearch(0.15d, 4)
                        .ABSearch(s);

                search.print();
            } else {    // black turn
                System.out.println("black side thinking...\n");
//                search = new GameTreeSearch(true, 4, true)
//                        .enableDeduceSortForABSearch()
//                        .enableStopAtWinForDeduceSortABSearch()
//                        .ABSearch(s);
                search = new GameTreeSearch(true, 5, true)
                        .enableDeduceSortForABSearch()
                        .enableStopAtWinForDeduceSortABSearch()
                        .enableFilterSubstateForDeduceSortABSearch(0.1d, 5)
                        .ABSearch(s);
                search.print();
            }
            if (search.getMoveStr() != null &&
                    !search.getMoveStr().equals("")) {
                s.put(search.getMoveX(), search.getMoveY(), i % 2 == 1);
            }
            s.print();
            isEnd = true;
            int status = EvaluationModel.EndDetection(s);
            if (status == 1) {
                System.out.println("Black win!\n");
            } else if (status == -1) {
                System.out.println("White win!\n");
            } else if (status == -2) {
                System.out.println("Boring game! Draw!\n");
            } else {
                isEnd = false;
            }
            if (isEnd) break;
        }
    }

    static void sortTest() {
        List<State> list = new ArrayList<State>();
        list.add(State.CREATE("edfd", "ddfc").evaluate(true));
        list.add(State.CREATE("dcedfegf", "").evaluate(true));
        list.add(State.CREATE("", "dcedfegf").evaluate(true));
        list.add(State.CREATE("eafa", "edeceb").evaluate(true));
        list.add(State.CREATE("", "eafa").evaluate(true));

        for (State tmp : list) System.out.println(tmp.value);
        list = Util.StateSort(list, true);
        System.out.println("----------------------------ascend");
        for (State tmp : list) System.out.println(tmp.value);
        list = Util.StateSort(list, false);
        System.out.println("----------------------------descend");
        for (State tmp : list) System.out.println(tmp.value);
    }

    static void evaluationTest() {
        State s;
        /*
            win detection
         */
//        s = State.CREATE("edfdgdhdeceeefeb", "ddfcgche");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//        System.out.println("win detection:" + EvaluationModel.EndDetection(s));
//
//        s = State.CREATE("ddfcgche", "edfdgdhdeceeefeb");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//        System.out.println("win detection:" + EvaluationModel.EndDetection(s));

         /*
            simple cases
          */
//        s = State.CREATE("aabacada", "");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//        s = State.CREATE("fb", "fffefdfc");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));

         /*
            slope cases
          */
//        s = State.CREATE("dcedfegf", "");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//        s = State.CREATE("afbgchdi", "");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//
//        s = State.CREATE("aebfcgdh", "");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//        s = State.CREATE("eafbgchd", "");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//
//        s = State.CREATE("", "aebdccdb");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//        s = State.CREATE("", "fhgghfie");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//
//        s = State.CREATE("", "adbecfdg");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));

         /*
            short patterns' cases
          */
//        s = State.CREATE("", "cbdc");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));
//
//        s = State.CREATE("eafa", "");
//        s.print();
//        System.out.println("" + EvaluationModel.Evaluates(s));

    }

    static void boardTest() {
        State s;
        s = new State();
        s.put(4, 4, true);
        s.print();
        String str;
        Scanner c = new Scanner(System.in);
        while (true) {
            str = c.next();
            s.put(str.charAt(0) - 'a', str.charAt(1) - 'a', false);
            s.print();
        }
    }


}
