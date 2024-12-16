import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JFrame implements ActionListener {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    private static final int DELAY = 75;

    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R'; // Start moving right
    private boolean running = false;
    private Timer timer;
    private final Random random = new Random();

    public SnakeGame() {
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setLocationRelativeTo(null);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> direction = (direction != 'R') ? 'L' : direction;
                    case KeyEvent.VK_RIGHT -> direction = (direction != 'L') ? 'R' : direction;
                    case KeyEvent.VK_UP -> direction = (direction != 'D') ? 'U' : direction;
                    case KeyEvent.VK_DOWN -> direction = (direction != 'U') ? 'D' : direction;
                }
            }
        });
        this.add(new GamePanel());
        this.setVisible(true);
    }

    private void startGame() {
        spawnApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void spawnApple() {
        do {
            appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (isAppleOnSnake());
    }

    private boolean isAppleOnSnake() {
        for (int i = 0; i < bodyParts; i++) {
            if (x[i] == appleX && y[i] == appleY) {
                return true;
            }
        }
        return false;
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            spawnApple();
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
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

    private class GamePanel extends JPanel {
        GamePanel() {
            this.setBackground(Color.BLACK);
            startGame();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }

        private void draw(Graphics g) {
            if (running) {
                g.setColor(Color.RED);
                g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

                for (int i = 0; i < bodyParts; i++) {
                    g.setColor(i == 0 ? Color.GREEN : new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }

                g.setColor(Color.RED);
                g.setFont(new Font("Ink Free", Font.BOLD, 40));
                String scoreText = "Score: " + applesEaten;
                g.drawString(scoreText, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(scoreText)) / 2, g.getFont().getSize());
            } else {
                gameOver(g);
            }
        }

        private void gameOver(Graphics g) {
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            String gameOverText = "Game Over";
            g.drawString(gameOverText, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameOverText)) / 2, SCREEN_HEIGHT / 2);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            String scoreText = "Score: " + applesEaten;
            g.drawString(scoreText, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(scoreText)) / 2, SCREEN_HEIGHT / 2 + 50);
        }
    }

    public static void main(String[] args) {
        new SnakeGame();
    }
}