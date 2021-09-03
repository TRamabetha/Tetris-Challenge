package Tetris;

import Tetris.Shape.gameShapes;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class Board extends JPanel {

    private final int BOARD_WIDTH = 15;
    private final int BOARD_HEIGHT = 26;
    private int PERIOD_INTERVAL = 400;

    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private int removedRowsNum = 0;
    private int currentShapeX = 0;
    private int currentShapeY = 0;
    private JLabel scoreStatusBar;
    private Shape currentShape;
    private Shape nextShape;
    private gameShapes[] board;

    public Board(Tetris parent) { initBoard(parent); }

    private void initBoard(Tetris parent) {

        setFocusable(true);
        scoreStatusBar = parent.getStatusBar();
        addKeyListener(new TAdapter());
    }

    private int squareWidth() { return (int) getSize().getWidth() / BOARD_WIDTH; }

    private int squareHeight() { return (int) getSize().getHeight() / BOARD_HEIGHT; }

    private gameShapes shapeAt(int x, int y) { return board[(y * BOARD_WIDTH) + x]; }

    void start() {

        currentShape = new Shape();
        nextShape = new Shape();
        
        board = new gameShapes[BOARD_WIDTH * BOARD_HEIGHT];

        clearBoard();
        newPiece();

        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }

    private void pause() {

        isPaused = !isPaused;

        if (isPaused) { scoreStatusBar.setText( "paused"); }
        
        else { scoreStatusBar.setText(String.valueOf(removedRowsNum)); }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        var size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {

                gameShapes shape = shapeAt(j, BOARD_HEIGHT - i - 1);

                if (shape != gameShapes.NoShape) {

                    drawSquare(g, j * squareWidth(),boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (currentShape.getShape() != gameShapes.NoShape) {

            for (int i = 0; i < 4; i++) {

                int x = currentShapeX + currentShape.x(i);
                int y = currentShapeY - currentShape.y(i);

                drawSquare(g, x * squareWidth(),boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),currentShape.getShape());
            }
        }
    }

    private void dropDown() {

        int newY = currentShapeY;

        while (newY > 0) {

            if (!tryMove(currentShape, currentShapeX, newY - 1)) { break; }
            
            newY--;
        }

        pieceDropped();
    }

    private void oneLineDown() {

        if (!tryMove(currentShape, currentShapeX, currentShapeY - 1)) { pieceDropped();}
        
    }

    private void clearBoard() {
    	
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) { board[i] = gameShapes.NoShape; }
    
    }

    private void pieceDropped() {

        for (int i = 0; i < 4; i++) {

            int x = currentShapeX + currentShape.x(i);
            int y = currentShapeY - currentShape.y(i);
            board[(y * BOARD_WIDTH) + x] = currentShape.getShape();
        }

        removeFullLines();

        if (!isFallingFinished) { newPiece(); }
        
    }

    private void newPiece() {

    	
        currentShape.setRandomShape();
        currentShapeX = BOARD_WIDTH / 2 + 1;
        currentShapeY = BOARD_HEIGHT - 1 + currentShape.minY();
        
        checkGameOver();
        
    }
    
    private void checkGameOver() {
    	
    	if (!tryMove(currentShape, currentShapeX, currentShapeY)) {

            currentShape.setShape(gameShapes.NoShape);
            timer.stop();

            var gameOverMessage = String.format("Game over. Score: %d", removedRowsNum);
            scoreStatusBar.setText(gameOverMessage);
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {

        for (int i = 0; i < 4; i++) {

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) { return false; }

            if (shapeAt(x, y) != gameShapes.NoShape) { return false; }
            
        }

        currentShape = newPiece;
        currentShapeX = newX;
        currentShapeY = newY;

        repaint();

        return true;
    }

    private void removeFullLines() {

        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {

            boolean lineIsFull = true;

            for (int j = 0; j < BOARD_WIDTH; j++) {

                if (shapeAt(j, i) == gameShapes.NoShape) {

                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {

                numFullLines++;

                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    
                	for (int j = 0; j < BOARD_WIDTH; j++) { board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1); }
                }
            }
        }

        if (numFullLines > 0) {

            removedRowsNum += numFullLines;

            scoreStatusBar.setText(String.valueOf("Score: " + removedRowsNum));
            isFallingFinished = true;
            currentShape.setShape(gameShapes.NoShape);
            checkLevelProgression(removedRowsNum);
        }
    }
    
    private void checkLevelProgression(int score) {
    	timer.stop();
    	score += 50;
    	if (score>20) {
    		score = score/2;
    	}
    	PERIOD_INTERVAL = PERIOD_INTERVAL - score;
    	timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }
    
    private void drawSquare(Graphics g, int x, int y, gameShapes shape) {

        Color colors[] = {new Color(142, 36, 170), new Color(244, 81, 30),
        			new Color(0, 151, 167), new Color(38, 198, 218),
        			new Color(251, 192, 45), new Color(211, 47, 47),
        			new Color(57, 73, 171), new Color(96, 125, 139)
        };

        var color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) { startGameCycle(); }
        
    }

    private void startGameCycle() {

        update();
        repaint();
    }

    private void update() {

        if (isPaused) { return; }

        if (isFallingFinished) {

            isFallingFinished = false;
            newPiece();
        }
        else { oneLineDown(); }
        
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            if (currentShape.getShape() == gameShapes.NoShape) { return; }

            int keycode = e.getKeyCode();

            // Java 12 switch expressions
            switch (keycode) {

                case KeyEvent.VK_P -> pause();
                case KeyEvent.VK_LEFT -> tryMove(currentShape, currentShapeX - 1, currentShapeY);
                case KeyEvent.VK_RIGHT -> tryMove(currentShape, currentShapeX + 1, currentShapeY);
                case KeyEvent.VK_DOWN -> oneLineDown();
                case KeyEvent.VK_UP -> tryMove(currentShape.rotate(), currentShapeX, currentShapeY);
                case KeyEvent.VK_D -> dropDown();
            }
        }
    }
}
