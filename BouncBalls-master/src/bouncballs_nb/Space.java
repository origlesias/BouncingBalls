package bouncballs_nb;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.ArrayList;

public class Space extends Canvas implements Runnable {

    int WIDTH;
    int HEIGHT;

    private int BALLS_NUM = 40;

    private ArrayList<Ball> balls;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<BlackHole> blackholes;
    private Generator generator;
    private ControllerServer cont;
    private Player player;
    private Wormhole worm;
    private Wormhole worm2;
    private BlackHole black;

    public Space(int w, int h) throws IOException {
        this.WIDTH = w;
        this.HEIGHT = h;

        this.generator = new Generator(this);
        blackholes= new ArrayList<BlackHole>();
        black= new BlackHole(700,0,80,700,this);
        blackholes.add(black);
        this.initBalls();
        this.initObstacles();
        player = new Player(this, 100, 100, 4);

        this.setPreferredSize(new Dimension(this.WIDTH, this.HEIGHT));

        setFocusable(true);
        requestFocus();
        this.initKeyListener();
        cont = new ControllerServer(player);
        new Thread(cont).start();
        //worm= new Wormhole(1000,200,300,400,this);
        //new Thread(worm).start();
        //worm2= new Wormhole(400,100,900,200,this);
        //new Thread(worm2).start();
        
        
    } // End of GamePanel() constructor

    private void initKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_SPACE:
                        System.out.println("Pausando / Reactivando");
                        BouncingBalls.PAUSED = !BouncingBalls.PAUSED;
                        break;
                    case KeyEvent.VK_F1:
                        System.out.println("Activando / desactivando DEBUGGING");
                        BouncingBalls.DEBUGGING = !BouncingBalls.DEBUGGING;
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        System.out.println("Eliminando bola");
                        balls.remove(0);
                        BALLS_NUM = balls.size();
                        break;
                    case KeyEvent.VK_F10:
                        System.out.println("Añadiendo bola");
                        addBall();
                        break;
                    case KeyEvent.VK_F5:
                        System.out.println("Activando / desactivando TURBO");
                        BouncingBalls.TURBO = !BouncingBalls.TURBO;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void addBall() {
        Ball ball = this.generator.randomBall();
        balls.add(ball);
        new Thread(ball).start();
    }

    public ArrayList<Ball> getBalls() {
        return this.balls;
    }

    public ArrayList<Obstacle> getObstacles() {
        return this.obstacles;
    }

    public ArrayList<BlackHole> getBlackholes() {
        return blackholes;
    }
    
    

    private void initBalls() {
        balls = new ArrayList<>();
        Ball ball;
        for (int i = 0; i < this.BALLS_NUM; i++) {
            do{
               ball = this.generator.randomBall();
            }while(ball.captured());
            balls.add(ball);
        }

        balls.stream().forEach((b) -> {
            new Thread(b).start();
        });
    }

    private void initObstacles() {
        obstacles = new ArrayList<>();
        // Añadir obstaculos
    }

    private void initAngryBirds(){
        Ball b1= new Ball(this,600,this.HEIGHT-30,30,30,0,0,Color.YELLOW);
        Ball b2= new Ball(this,690,this.HEIGHT-30,30,30,0,0,Color.BLUE);
        Ball b3= new Ball(this,645,this.HEIGHT-15,15,15,0,0,Color.RED);
        Ball b4= new Ball(this,50,this.HEIGHT-40,40,40,22,45,Color.WHITE);
        new Thread(b1).start();
        new Thread(b2).start();
        new Thread(b3).start();
        new Thread(b4).start();
        balls.add(b1);
        balls.add(b2);
        balls.add(b3);
        balls.add(b4);
    }
    
    private void initBillar(){
        Ball b1= new Ball(this,500,250,20,20,0,0,Color.YELLOW);
        Ball b2= new Ball(this,530,265,20,20,0,0,Color.GREEN);
        Ball b3= new Ball(this,530,235,20,20,0,0,Color.BLUE);
        Ball b4= new Ball(this,550,280,20,20,0,0,Color.RED);
        Ball b5= new Ball(this,550,250,20,20,0,0,Color.BLUE);
        Ball b6= new Ball(this,550,220,20,20,0,0,Color.YELLOW);
        Ball b10= new Ball(this,50,250,20,20,6,0,Color.WHITE);
        new Thread(b1).start();
        new Thread(b2).start();
        new Thread(b3).start();
        new Thread(b4).start();
        new Thread(b5).start();
        new Thread(b6).start();
        new Thread(b10).start();
        balls.add(b1);
        balls.add(b2);
        balls.add(b3);
        balls.add(b4);
        balls.add(b5);
        balls.add(b6);
        balls.add(b10);
    }
    
    
    public synchronized void paint() {
        BufferStrategy bs;
        
        
        
        bs = this.getBufferStrategy();
        if (bs == null) {
            return; // Error en la recuperación del BufferStrategy =======================================================>>
        }

        if ((this.WIDTH <= 0) || (this.HEIGHT <= 0)) {
            System.out.println("Map size error: (" + this.WIDTH + "x" + this.HEIGHT + ")");
            return; // Error en los tamaños del mapa ===================================================>>
        }

        Graphics gr = bs.getDrawGraphics();

        
        
        // Pinta el fondo, mejor hacerlo con una BufferedImage
        gr.setColor(Color.BLACK);
        gr.fillRect(0, 0, this.WIDTH, this.HEIGHT);

        black.draw(gr);
        
        // Pinta las bolas
        balls.stream().forEach((b) -> {
            b.draw(gr);
        });

        // Pinta los obstaculos
        obstacles.stream().forEach((o) -> {
            o.draw(gr);
        });

        player.draw(gr);
        //worm.draw(gr);
        //worm2.draw(gr);
        

        ///////////////////////////////////////////////////////////
        // Debugging block
        if (BouncingBalls.DEBUGGING) {
            for (int i = 0; i < this.BALLS_NUM; i++) {
                gr.setColor(this.balls.get(i).getColor());
                gr.setFont(new Font("Courier New", Font.PLAIN, 12));
                gr.drawString("Ball " + (i + 1) + " " + this.balls.get(i).toString(), 20, 30 + i * 20);
                gr.setFont(new Font("Arial", Font.PLAIN, 16));
                gr.setColor(Color.BLACK);
                gr.drawString("" + (i + 1), (int) this.balls.get(i).posX, (int) this.balls.get(i).posY);
            }
            gr.setColor(Color.CYAN);
            gr.setFont(new Font("Courier New", Font.PLAIN, 12));
            gr.drawLine(0, this.HEIGHT / 2, this.WIDTH, this.HEIGHT / 2);
            gr.drawLine(this.WIDTH / 2, 0, this.WIDTH / 2, this.HEIGHT);
            gr.drawString("0º", this.WIDTH - 20, this.HEIGHT / 2 - 10);
            gr.drawString("180º", 10, this.HEIGHT / 2 - 10);
            gr.drawString("90º", this.WIDTH / 2 - 25, 10);
            gr.drawString("270º", this.WIDTH / 2 - 30, this.HEIGHT - 10);
        } // End of debugging block
        ///////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////
        // Turbo block
        if (BouncingBalls.TURBO) {
            gr.setColor(Color.WHITE);
            gr.setFont(new Font("Arial", Font.BOLD, 22));
            gr.drawString("TURBO ON", this.WIDTH - 130, 40);
        } // End of turbo block
        ///////////////////////////////////////////////////////////

        // Cierra los objetos usados
        bs.show();
        gr.dispose();
    }


    @Override
    public void run() {
        this.createBufferStrategy(2);
        while (true) {
            this.paint();
            do {
                try {
                    if (!BouncingBalls.TURBO) {
                        Thread.sleep(15);
                    } else {
                        Thread.sleep(6);
                    }
                } catch (InterruptedException ex) {
                }
            } while (BouncingBalls.PAUSED);
        }
    }
    
    
}
