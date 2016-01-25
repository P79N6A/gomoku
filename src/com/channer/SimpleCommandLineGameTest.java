package com.channer;

import java.util.List;
import java.util.Scanner;

/**
 * Created by channerduan on 1/2/16.
 */
public class SimpleCommandLineGameTest {

    static void SimpleGameTest() {
        State s;
        s = new State();
        s.put(State.BORAD_SIZE / 2, State.BORAD_SIZE / 2, true);
        s.print();
        System.out.println("Welcome to play! Five-in-a-row!");
        System.out.println("You turn, please input the term like 'ef' to play:");
        String str;
        Scanner c = new Scanner(System.in);
        while (true) {
            str = c.next();
            s.put(str.charAt(0) - 'a', str.charAt(1) - 'a', false);
            s.print();
            if (isGameEnd(s)) break;
            System.out.println("Computer is thinking...");
            GameTreeSearch search;
//            search = new GameTreeSearch(true, 2, true).MinimaxSearch(s);
//            search = new GameTreeSearch(true, 3, true).ABSearch(s);
            search = new GameTreeSearch(true, 3, true).enableDeduceSortForABSearch().ABSearch(s);
            search.print();
            if (search.getMoveStr() != null &&
                    !search.getMoveStr().equals("")) {
                s.put(search.getMoveX(), search.getMoveY(), true);
            }
            s.print();
            if (isGameEnd(s)) break;
            System.out.println("Your turn:");
        }
    }

    static boolean isGameEnd(State s) {
        boolean isEnd = true;
        int status = EvaluationModel.EndDetection(s);
        if (status == 1) {
            System.out.println("Computer Wins! You need more practise!\n");
        } else if (status == -1) {
            System.out.println("Congradulations! You win!\n");
        } else if (status == -2) {
            System.out.println("Boring game! Draw!\n");
        } else {
            isEnd = false;
        }
        return isEnd;
    }
}
