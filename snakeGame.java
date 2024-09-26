package ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Snake extends JPanel implements ActionListener, KeyListener {

    private final int WIDTH = 500;
    private final int HEIGHT = 500;
    private final int UNIT_SIZE = 10;
    private final int GAME_UNITS = (WIDTH * HEIGHT) / UNIT_SIZE;
    private final int DELAY = 75; // Higher delay -> slower game
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction;
    private boolean running = false;
    private Timer timer;
    private Timer animationTimer;
    private JButton playAgainButton;
    private JButton exitButton;

    // Animation state
    private boolean showingScoreAnimation = false;
    private int animationY = 0;

    public Snake() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        initializeButtons();
        startGame();
    }

    private void initializeButtons() {
        playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds((WIDTH - 200) / 2, HEIGHT / 2 + 60, 200, 50);
        playAgainButton.addActionListener(new ActionListener() {
            // @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        exitButton = new JButton("Exit");
        exitButton.setBounds((WIDTH - 200) / 2, HEIGHT / 2 + 120, 200, 50);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        this.setLayout(null);
        this.add(playAgainButton);
        this.add(exitButton);
    }

    private void startGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        running = true;
        spawnApple();
        timer = new Timer(DELAY, this);
        timer.start();
        playAgainButton.setVisible(false);
        exitButton.setVisible(false);
    }

    private void spawnApple() {
        Random random = new Random();
        appleX = random.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            spawnApple();
            startScoreAnimation();
        }
    }

    private void startScoreAnimation() {
        showingScoreAnimation = true;
        animationY = appleY;
        animationTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationY -= 5;
                if (animationY < appleY - 50) {
                    showingScoreAnimation = false;
                    animationTimer.stop();
                }
                repaint();
            }
        });
        animationTimer.start();
    }

    private void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // Check if head touches borders
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            showEndButtons();
        }
    }

    private void showEndButtons() {
        playAgainButton.setVisible(true);
        exitButton.setVisible(true);
    }

    private void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.GREEN);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);

        // Display score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 20));
        g.drawString("Score: " + applesEaten, (WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                HEIGHT / 2 + 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // Draw apple
            g.setColor(Color.GREEN);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(new Color(255, 0, 175));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("Score: " + applesEaten, 10, 20);

            // Draw score animation
            if (showingScoreAnimation) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString("+1", appleX, animationY);
            }
        } else {
            gameOver(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        Snake snakeGame = new Snake();
        frame.add(snakeGame);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
