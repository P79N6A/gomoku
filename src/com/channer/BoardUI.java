package com.channer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by channerduan on 1/2/16.
 */
public class BoardUI extends JPanel implements MouseListener {

    private double mCellSize;
    private int mPieceSize;
    private int mBoardSize;

    private int mHeight;
    private int mWidth;
    private double l, r, t, b;

    private Image blackImage;
    private Image whiteImage;


    // Core for show
    private State mState;

    // Core for logic
    private MainUI.GameController mGameControl;

    public BoardUI(Image blackImage, Image whiteImage, MainUI.GameController controller) {
        this.setVisible(true);
        this.addMouseListener(this);
        mBoardSize = State.BORAD_SIZE;
        this.blackImage = blackImage;
        this.whiteImage = whiteImage;
        this.setBackground(Color.WHITE);
        this.mGameControl = controller;
    }

    public void updateState(State state) {
        updateState(state, -1, -1);
    }

    public void updateState(State state, int x, int y) {
        this.mState = state;
        mStressX = x;
        mStressY = y;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        mHeight = this.getHeight();
        mWidth = this.getWidth();
//        System.out.println("BoardUI height:" + mHeight + ", width:" + mWidth);
        int frameSize = mHeight;
        if (mHeight > mWidth) frameSize = mWidth;
        mCellSize = frameSize / (double) (State.BORAD_SIZE + 2);
        mPieceSize = (int) (mCellSize * 0.7d + 0.5d);
        double radius = mCellSize * (double) mBoardSize / 2d;
        l = mWidth / 2 - radius;
        r = mWidth / 2 + radius;
        t = mHeight / 2 - radius;
        b = mHeight / 2 + radius;
//        System.out.println("BoardUI l:" + l + ", r:" + r + ", t:" + t + ", b:" + b);
        drawboard(g);

        if (mState != null) {
            drawPieces(g);
        }
    }


    int mStressX = -1;
    int mStressY = -1;

    private void drawOnePiece(Graphics g, int i, int j, boolean isBlack) {
        boolean isStress = false;
        if (i == mStressX && j == mStressY) isStress = true;
        j = mBoardSize - 1 - j;
        double x = l + (double) i * mCellSize;
        double y = t + (double) j * mCellSize;
        double gap = (mCellSize - mPieceSize) / 2d;
        if (isBlack) {
            g.drawImage(blackImage, (int) (x + gap + 0.5d), (int) (y + gap + 0.5d), mPieceSize, mPieceSize, null);
        } else {
            g.drawImage(whiteImage, (int) (x + gap + 0.5d), (int) (y + gap + 0.5d), mPieceSize, mPieceSize, null);
        }

        if (isStress) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            Stroke stroke = new BasicStroke(3f);
            g2d.setStroke(stroke);
            g2d.setColor(Color.CYAN);
            double ratio = 0.8d;
            double stressGap = ((1d - ratio) * mCellSize) / 2d;
            g2d.drawOval((int) (x + stressGap + 0.5d), (int) (y + stressGap + 0.5d),
                    (int) (mCellSize * ratio), (int) (mCellSize * ratio));
        }
    }

    private void drawPieces(Graphics g) {
//        mState.print();
        for (int i = 0; i < mBoardSize; i++) {
            for (int j = 0; j < mBoardSize; j++) {
                if (mState.board[i][j] == State.VALUE_OF_BLACK) {
                    drawOnePiece(g, i, j, true);
                } else if (mState.board[i][j] == State.VALUE_OF_WHITE) {
                    drawOnePiece(g, i, j, false);
                }
            }
        }


    }

    private void drawboard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        Stroke stroke = new BasicStroke(1.5f);
        g2d.setStroke(stroke);
        double sX, sY, eX, eY;
        sX = l + mCellSize / 2;
        sY = t + mCellSize / 2;
        eX = r - mCellSize / 2;
        eY = b - mCellSize / 2;
        double k1 = t + mCellSize / 2, k2 = l + mCellSize / 2;
        for (int i = 0; i < mBoardSize; i++) {
            g2d.drawLine((int) sX, (int) k1, (int) eX, (int) k1);
            g2d.drawLine((int) k2, (int) sY, (int) k2, (int) eY);
            k1 += mCellSize;
            k2 += mCellSize;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getX() > l && e.getX() < r && e.getY() > t && e.getY() < b) {
            int x = (int) (((double) e.getX() - l) / mCellSize);
            // Y axis in swing is reverse for gomoku
            int y = mBoardSize - 1 - (int) (((double) e.getY() - t) / mCellSize);
//            System.out.println("click board(" + e.getX() + ", " + e.getY() + ") >> (" + x + ", " + y + ")");
            mGameControl.playerClick(x, y);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
