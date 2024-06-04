/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p3_chilam_yao;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Gameboard extends JPanel implements MouseListener, MouseMotionListener {

    // instance variables, which cannot be changed
    private final Color BROWN = new Color(242, 192, 121);
    private final int GRID_SIZE = 100;
    private final int PIECE_SIZE = 60;
    private final Color BLACK_PIECE_COLOR = Color.BLACK;
    private final Color WHITE_PIECE_COLOR = Color.WHITE;
    private final int win = 5;
    // variables that can be changed during game
    private int[][] boardKeeper;
    private ArrayList<Point> pieceCoordinates;
    private ArrayList<Boolean> pieceColors; // true for black, false for white
    private boolean isBlackTurn;
    private boolean gameOver;
    private String winner;
    private static Timer timer;
    private static int seconds = 0;
    private static JButton restartButton;
    private static JButton redoButton;
    private static JButton clearButton;
    private static boolean restart;

    public Gameboard() { //constructors, initial values
        this.setBackground(BROWN);
        restart = false;
        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearBoard());
        redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> redoLastMove());
        restartButton = new JButton("Restart");
        restartButton.addActionListener(w -> restartGame());
        restartButton.setVisible(false);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(redoButton);
        buttonPanel.add(restartButton);
        JLabel label = new JLabel(formatTime(seconds));
        Font font = new Font("Times New Roman", Font.ITALIC, 20);
        label.setFont(font);
        buttonPanel.add(label);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        startTimer(label);

        boardKeeper = new int[8][9];
        pieceCoordinates = new ArrayList<>();
        pieceColors = new ArrayList<>();
        isBlackTurn = true;

        for (int i = 0; i < boardKeeper.length; i++) {
            for (int j = 0; j < boardKeeper[i].length; j++) {
                boardKeeper[i][j] = 0;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D pen = (Graphics2D) g;
        pen.setColor(Color.BLACK);

        // Draw horizontal grid lines
        for (int i = 0; i < 8; i++) {
            pen.drawLine(0, GRID_SIZE * (i + 2), getWidth(), GRID_SIZE * (i + 2));
        }

        // Draw vertical grid lines
        for (int i = 0; i < 10; i++) {
            pen.drawLine(GRID_SIZE * (i + 1), 200, GRID_SIZE * (i + 1), getHeight());
        }

        // Draw pieces
        for (int i = 0; i < pieceCoordinates.size(); i++) {
            Point p = pieceCoordinates.get(i);
            pen.setColor(pieceColors.get(i) ? BLACK_PIECE_COLOR : WHITE_PIECE_COLOR);
            pen.fillOval(p.x - PIECE_SIZE / 2, p.y - PIECE_SIZE / 2, PIECE_SIZE, PIECE_SIZE);
        }
        if (gameOver) { // while gameover print the winner
            timer.stop();
            if (isBlackTurn) {
                pen.setColor(Color.BLACK);
            } else {
                pen.setColor(Color.WHITE);
            }
            clearButton.setEnabled(false);
            redoButton.setEnabled(false);
            pen.setFont(new Font("Times New Roman", Font.BOLD, 60));
            pen.drawString(winner + " wins!", getWidth() / 2 - 150, getHeight() / 2);
            restartButton.setVisible(true);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = ((e.getX() + GRID_SIZE / 4) / GRID_SIZE) * GRID_SIZE;
        int y = ((e.getY() + GRID_SIZE / 4) / GRID_SIZE) * GRID_SIZE;

        if (isValidIntersection(x, y) && !isIntersectionOccupied(x, y) && !gameOver) { // add information of the piece if the location is valid
            pieceCoordinates.add(new Point(x, y));
            pieceColors.add(isBlackTurn);
            boardKeeper[(y / GRID_SIZE) - 2][(x / GRID_SIZE) - 1] = isBlackTurn ? 1 : 2;
            if (checkWin((y / GRID_SIZE) - 2, (x / GRID_SIZE) - 1)) { // determine anyone win else switch player
                if (isBlackTurn) { // determine winner
                    winner = "Black";
                } else {
                    winner = "White";
                }
                gameOver = true;
            } else {
                isBlackTurn = !isBlackTurn;
            }
            repaint();
        }
    }

    private boolean isValidIntersection(int x, int y) {
        return x >= GRID_SIZE && x <= 9 * GRID_SIZE && y >= 2 * GRID_SIZE && y <= 9 * GRID_SIZE;
    }

    private boolean isIntersectionOccupied(int x, int y) {
        return boardKeeper[(y / GRID_SIZE) - 2][(x / GRID_SIZE) - 1] != 0;
    }

    private void clearBoard() {
        // remove all the information of all these pieces from each arrays and arraylist
        pieceCoordinates.clear();
        pieceColors.clear();
        for (int i = 0; i < boardKeeper.length; i++) {
            for (int j = 0; j < boardKeeper[i].length; j++) {
                boardKeeper[i][j] = 0;
            }
        }
        isBlackTurn = true;
        repaint();
    }

    private void redoLastMove() {
        // remove all the information of last step from each arrays and arraylist
        if (!pieceCoordinates.isEmpty()) {
            int lastIndex = pieceCoordinates.size() - 1;
            Point lastPoint = pieceCoordinates.remove(lastIndex);
            pieceColors.remove(lastIndex);
            boardKeeper[(lastPoint.y / GRID_SIZE) - 2][(lastPoint.x / GRID_SIZE) - 1] = 0;
            isBlackTurn = !isBlackTurn;
            repaint();
        }
    }

    private boolean checkWin(int row, int col) {
        int color = boardKeeper[row][col];
        //check four directions
        return checkDirection(row, col, color, 0, 1) // Horizontal
                || checkDirection(row, col, color, 1, 0) // Vertical
                || checkDirection(row, col, color, 1, 1) // Diagonal down-right
                || checkDirection(row, col, color, 1, -1); // Diagonal down-left
    }

    private boolean checkDirection(int row, int col, int color, int rowDir, int colDir) {
        int count = 1;
        // Count consecutive pieces in the positive direction
        count += countConsecutive(row, col, color, rowDir, colDir);
        // Count consecutive pieces in the negative direction
        count += countConsecutive(row, col, color, -rowDir, -colDir);
        return count >= win;
    }

    //check if there are enough consecutive pieces in a specific direction
    private int countConsecutive(int row, int col, int color, int rowDir, int colDir) {
        int count = 0;
        // count pieces
        for (int i = 1; i < win; i++) {
            int newRow = row + i * rowDir;
            int newCol = col + i * colDir;
            // Check if the new position is out of bounds or does not have the same color piece
            if (newRow < 0 || newRow >= boardKeeper.length || newCol < 0 || newCol >= boardKeeper[0].length || boardKeeper[newRow][newCol] != color) {
                break;
            }
            count++;
        }
        return count;
    }

    private static String formatTime(int seconds) { // display time in mm:ss format
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private static void startTimer(JLabel label) { // start timer method
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                label.setText(formatTime(seconds));
            }
        });
        timer.start();
    }

    private void restartGame() { // initial all the value back to the beginning of the game
        restart = true;
        clearBoard();
        seconds = 0;
        gameOver = false;
        restartButton.setVisible(false);
        timer.start();
        clearButton.setEnabled(true);
        redoButton.setEnabled(true);
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

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
