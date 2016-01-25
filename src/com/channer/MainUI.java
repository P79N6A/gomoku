package com.channer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by channerduan on 1/2/16.
 */
public class MainUI extends JFrame {

    public static int SIZE_OF_FRAME;

    public static void launch() {
        MainUI frame = new MainUI();
    }

    public static void showTest(State state) {
        MainUI frame = new MainUI();
        frame.mBoard.updateState(state);
        frame.mGameController.setLock();
    }

    private BoardUI mBoard;

    private static final String FILE_BLACK = "black.png";
    private static final String FILE_WHITE = "white.png";


    public MainUI() {
        try {
            loadImages();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.getContentPane(),
                    "Please ensure '" + FILE_BLACK + "' and '" + FILE_WHITE + "' exist in the path '/pics'!",
                    "ImageFile lost", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        this.setTitle("Go-moku!");
        Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize();
//        System.out.println(displaySize);
        int size = displaySize.width > displaySize.height ? displaySize.height : displaySize.width;
        int radius = (int) ((double) size * 0.27 + 0.5d);
        SIZE_OF_FRAME = radius * 2;
        this.setSize(SIZE_OF_FRAME, SIZE_OF_FRAME);
        this.setLocation(displaySize.width / 2 - radius, displaySize.height / 2 - radius);
        this.setLocationRelativeTo(null);
        mBoard = new BoardUI(mPics.get(FILE_BLACK),
                mPics.get(FILE_WHITE), mGameController);
        Container rootContainer = this.getContentPane();
        rootContainer.add(mBoard);

        this.setBackground(Color.WHITE);

        this.setVisible(true);


        mGameController.reset();    // start game!

        // test case
//        mBoard.updateState(State.CREATE("edfdgdhdeceeef", "ddfcgchefeegge"));
    }

    // images load
    private static Map<String, Image> mPics = new HashMap<String, Image>();

    private void loadImages() throws Exception {
        File file = new File("pics");
        File[] files = file.listFiles();
        for (File f : files) {
            Image image = null;
            image = ImageIO.read(f);
            System.out.println(f.getName());
            mPics.put(f.getName(), image);
        }
        if (!mPics.containsKey(FILE_BLACK) ||
                !mPics.containsKey(FILE_WHITE)) {
            throw new Exception();
        }
    }

    // control the state of game
    public GameController mGameController = new GameController(this);

    public static class GameController {

        private int won;
        private int lost;
        private int draw;

        private State state;

        private boolean isLock = false;

        private MainUI mainUI;

        public GameController(MainUI mainUI) {
            this.mainUI = mainUI;
            won = lost = draw = 0;
        }

        public void setLock() {
            isLock = true;
        }

        public void reset() {
            state = new State();
            state.put(State.BORAD_SIZE / 2, State.BORAD_SIZE / 2, true);
//            state.print();
            String strTitle = "Welcome! Game start, ";

            if (won + lost + draw == 0) {
                strTitle += "please click the empty space of the board.";
            } else {
                strTitle = strTitle + "[won:" + won + ", lost:" + lost + ", draw:" + draw + "]";
            }

            mainUI.setTitle(strTitle);
            mainUI.mBoard.updateState(state);
            funNumber = 0;
            isLock = false;
            searchResultRefresh(searchResult1, "a3");
            searchResultRefresh(searchResult2, "a4");
            searchResultRefresh(searchResult3, "ano");

        }

        private void searchResultRefresh(ArrayList<GameTreeSearch> list, String tag) {
            if (list.isEmpty()) return;
            System.out.print(tag + "Nodes = [");
            for (GameTreeSearch search : list) {
                System.out.print(search.getNodesNum() + " ");
            }
            System.out.println("]");

            System.out.print(tag + "Evaluation = [");
            for (GameTreeSearch search : list) {
                System.out.print(search.getEvaluateTime() + " ");
            }
            System.out.println("]");

            System.out.print(tag + "Time = [");
            for (GameTreeSearch search : list) {
                System.out.print(search.getTime() + " ");
            }
            System.out.println("]");

            list.clear();
        }

        private int funNumber = 0;

        public void playerClick(int i, int j) {
            if (isLock) {
                String strTitle = "Computer is thinking";
                for (int k = 0; k < funNumber; k++) strTitle += "!";
                funNumber++;
                mainUI.setTitle(strTitle);
                return;
            }

            if (!state.check(i, j)) {
                String strTitle = "Please click the empty space of the board";
                for (int k = 0; k < funNumber; k++) strTitle += "!";
                funNumber++;
                mainUI.setTitle(strTitle);
            } else {
                funNumber = 0;
                state.put(i, j, false);
                mainUI.mBoard.updateState(state, i, j);
                if (gameEndCheck()) return;
                mainUI.setTitle("Computer is thinking...");

                // AI
                isLock = true;
                mAIWork = new AISwingWorker(state);
                mAIWork.execute();

            }
        }

        private boolean gameEndCheck() {
            boolean isEnd = true;
            int status = EvaluationModel.EndDetection(state);
            if (status == 1) {
                lost++;
                JOptionPane.showMessageDialog(mainUI.getContentPane(),
                        "Computer Wins! You need more practise!",
                        "Loser", JOptionPane.WARNING_MESSAGE);
            } else if (status == -1) {
                won++;
                JOptionPane.showMessageDialog(mainUI.getContentPane(),
                        "Congradulations! You win!",
                        "Winner", JOptionPane.INFORMATION_MESSAGE);
            } else if (status == -2) {
                draw++;
                JOptionPane.showMessageDialog(mainUI.getContentPane(),
                        "Boring game!",
                        "Draw", JOptionPane.INFORMATION_MESSAGE);
            } else {
                isEnd = false;
            }
            if (isEnd) {
                reset();
            }
            return isEnd;
        }

        private void callFromAI() {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("receive AI res");
            try {
                GameTreeSearch search = (GameTreeSearch) mAIWork.get();
                if (search.getMoveStr() != null &&
                        !search.getMoveStr().equals("")) {
                    state.put(search.getMoveX(), search.getMoveY(), true);
                }
//                state.print();
                mainUI.mBoard.updateState(state, search.getMoveX(), search.getMoveY());
                isLock = false;
                if (gameEndCheck()) return;

                mainUI.setTitle("Your turn:");
            } catch (Exception e) {
            }
        }

        // play and test
        ArrayList<GameTreeSearch> searchResult1 = new ArrayList<GameTreeSearch>();
        ArrayList<GameTreeSearch> searchResult2 = new ArrayList<GameTreeSearch>();
        ArrayList<GameTreeSearch> searchResult3 = new ArrayList<GameTreeSearch>();


        private AISwingWorker mAIWork;

        private class AISwingWorker extends SwingWorker {

            State stateForSearch;

            public AISwingWorker(State state) {
                stateForSearch = new State(state);
            }

            @Override
            protected Object doInBackground() throws Exception {

                GameTreeSearch search1; // , search2, search3;
                search1 = new GameTreeSearch(true, 4, true)
                        .enableDeduceSortForABSearch()
                        .enableStopAtWinForDeduceSortABSearch()
                        .enableFilterSubstateForDeduceSortABSearch(0.1d, 4)
//                        .setTag("ab filter 3")
                        .ABSearch(stateForSearch);
                search1.print();
//                search2 = new GameTreeSearch(true, 4, true)
//                        .enableDeduceSortForABSearch()
//                        .enableStopAtWinForDeduceSortABSearch()
//                        .enableFilterSubstateForDeduceSortABSearch(0.1d, 4)
//                        .setTag("ab filter 4")
//                        .ABSearch(stateForSearch);
//                search2.print();
//                search3 = new GameTreeSearch(true, 4, true)
//                        .enableDeduceSortForABSearch()
//                        .enableStopAtWinForDeduceSortABSearch()
//                        .setTag("ab no filter")
//                        .ABSearch(stateForSearch);
//                search3.print();

                searchResult1.add(search1);
//                searchResult2.add(search2);
//                searchResult3.add(search3);

//                if (!search1.getMoveStr().equals(search2.getMoveStr()) ||
//                        !search1.getMoveStr().equals(search3.getMoveStr())) {
//                    for (int k = 0;k < 10;k++)
//                        System.out.println("!!!!!!!!!!!!!!");
//                }

                return search1;
            }

            @Override
            public void done() {
                callFromAI();
            }

        }
    }


}
