package Tetris;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Tetris extends JFrame {

    private JLabel scoreStatusBar;

    public Tetris() {
        initUI();
    }

    private void initUI() {

        scoreStatusBar = new JLabel("Score: 0");
        add(scoreStatusBar, BorderLayout.SOUTH);

        var board = new Board(this);
        add(board);
        board.start();

        setTitle("Tetris");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
    }

    JLabel getStatusBar() {return scoreStatusBar;}

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            var game = new Tetris();
            game.setVisible(true);
        });
    }
}
