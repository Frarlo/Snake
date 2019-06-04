package me.ferlo.snake;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import static me.ferlo.snake.Constants.*;
import me.ferlo.snake.render.Renderable;
import me.ferlo.snake.render.Renderer;

/**
 * Main class
 * @author ferlin_francesco
 */
public class Snake implements KeyListener {
    private static Snake instance;
    
    private final JFrame frame = new JFrame();
    private final Random random = new Random();

    // Squadrette

    private final Quadratino[][] squadrette;
    private final Pitone cobra;
    private final Renderer renderer;
    private int score;
    
    private Snake() {
        final Color color1 = Color.green, color2 = new Color(50, 147, 60);
        Color currColor = color2;
        
        List<Renderable> cosi = new ArrayList<>();
        squadrette = new Quadratino[TABLE_WIDTH / SQUARE_WIDTH][TABLE_HEIGHT / SQUARE_HEIGHT];
        for(int i = 0; i < squadrette.length; i++) {
            currColor = currColor == color2 ? color1 : color2;
            for(int j = 0; j < squadrette[i].length; j++) {
                currColor = currColor == color2 ? color1 : color2;
                Quadratino sq = new Quadratino(SQUARE_WIDTH * i, SQUARE_HEIGHT * j, 
                        SQUARE_WIDTH, SQUARE_HEIGHT, currColor);
                squadrette[i][j] = sq;
                cosi.add(sq);
            }
        }
        cobra = new Pitone(this);
        cosi.add(cobra);
        
        renderer = new Renderer(cosi.toArray(new Renderable[0]));
    }
    
    public static Snake getInstance() {
        if(instance == null)
            instance = new Snake();
        return instance;
    }
    
    public void start() {
        frame.setResizable(false);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(renderer);
        frame.addKeyListener(this);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
            
        JOptionPane.showOptionDialog(frame, 
                 "Press start to start", "Start Game", 
                 JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                 null, new Object[] {"Start"}, "Start");
        
        restart();
        gameLoop();
    }
    
    private void restart() {
        final Quadratino[] middleColomn = squadrette[squadrette.length / 2];
        final Quadratino middleSquare = middleColomn[middleColomn.length / 2];
        cobra.reset(middleSquare);
        
        score = 0;
        
        for (Quadratino[] squadrette1 : squadrette)
            for (Quadratino squadrette11 : squadrette1)
                squadrette11.setMela(false);
        spawnMela();
    }
    
    private void gameLoop() {
        try {
            while (true) {
                cobra.move();

                if (cobra.getHead().hasMela()) {
                    score++;
                    spawnMela();
                    cobra.getHead().setMela(false);
                    cobra.allungaLaSerpe();
                }

                renderer.repaint();
                Thread.sleep(1000 / 60L);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (SeiMortoException ex) {
            int n = JOptionPane.showOptionDialog(frame, 
                 "Sei mmmmorto !!!", "RIP", 
                 JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                 null, new Object[] {"Restart", "Exit"}, "Restart");
            
            if(n == 0) {
                restart();
                gameLoop();
            } else {
                System.exit(0);
            }
        }
    }
    
    private void spawnMela() {
        Quadratino randomSq;
        do {
            final Quadratino[] randomCol = squadrette[random.nextInt(squadrette.length)];
            randomSq = randomCol[random.nextInt(randomCol.length)];
        } while(randomSq.hasMela());
        
        randomSq.setMela(true);
    }
    
    public Quadratino getQuadrato(int x, int y) {
        for(Quadratino[] quadrati : squadrette)
            if (x >= quadrati[0].getMiddleX() - SQUARE_WIDTH / 2 && 
                    x <= quadrati[0].getMiddleX() + SQUARE_WIDTH / 2)
                for (Quadratino quadrato : quadrati)
                    if (y >= quadrato.getMiddleY() - SQUARE_HEIGHT / 2 && 
                            y <= quadrato.getMiddleY() + SQUARE_HEIGHT / 2)
                        return quadrato;
        throw new RuntimeException("There is no quadratino !!!");
    }

    public int getScore() {
        return score;
    }
    
    @Override
    public void keyPressed(KeyEvent ke) {
        switch(ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                cobra.changeDirection(MoveDirection.UP);
                break;
            case KeyEvent.VK_DOWN:
                cobra.changeDirection(MoveDirection.DOWN);
                break;
            case KeyEvent.VK_RIGHT:
                cobra.changeDirection(MoveDirection.RIGHT);
                break;
            case KeyEvent.VK_LEFT:
                cobra.changeDirection(MoveDirection.LEFT);
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {}

    @Override
    public void keyReleased(KeyEvent ke) {}
}
