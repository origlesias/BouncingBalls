package bouncballs_nb;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import javax.imageio.ImageIO;

public class Ball implements Runnable {

    
    float posX;
    float posY;

    float speedX;
    float speedY;

    float radius;
    float mass;
    
    long time;
    boolean waiting=false;

    private Color color;
    private static final Color DEFAULT_COLOR = Color.PINK;
    private static final float gravity = 0.5f;
    private static final float mu= 0.001f;

    private Space space;

    public Ball(Space space, float posX, float posY, float radius, float mass,
            float speed, float angle, Color color) {
        this.space = space;
        this.posX = posX;
        this.posY = posY;
        // Convert (speed, angle) to (posX, posY), with posY-axis inverted
        this.speedX = (float) (speed * Math.cos(Math.toRadians(angle)));
        this.speedY = (float) (-speed * Math.sin(Math.toRadians(angle)));
        this.radius = radius;
        this.mass = mass;
        this.color = color;
        this.checkValidColor();
    }

    public Ball(Space space, float posX, float posY, float radius, float mass,
            float speed, float angle) {
        this(space, posX, posY, radius, mass, speed, angle, DEFAULT_COLOR);
    }

    private void checkValidColor() {
        if (color == Color.BLACK) {
            System.out.println("Color de bola no válido, estableciendo color por defecto.");
            color = DEFAULT_COLOR;
        }
    }

    public Color getColor() {
        return this.color;
    }

    // Temporal
    public Rectangle getLimits() {
        return new Rectangle((int) (posX - radius), (int) (posY - radius), (int) (radius * 2), (int) (radius * 2));
    }

    private void move() {
        float dtime =2*(System.nanoTime()- time)/(float) (Math.pow(10, 8));
        CollisionPhysics.checkBall2WallCollision(this, space);
        CollisionPhysics.checkBall2BallCollision(this, space.getBalls());
        CollisionPhysics.checkBall2BlackHoleCollision(this, space.getBlackholes());
        speedY -= speedY*mu*dtime;
        speedX -= speedX*mu*dtime;
        posX += speedX*dtime;
        posY += speedY*dtime;
        time= System.nanoTime();
    }

    public void bounceX() {
        this.speedX *= -1;
    }

    public void bounceY() {
        this.speedY *= -1;
    }

    public void draw(Graphics g) {
        Graphics2D gg= (Graphics2D) g;
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //gg.drawImage(loadImage("img/punto.png"),(int) posX,(int) posY, (int) radius*2, (int) radius*2, space);
        gg.setColor(color);
        gg.fillOval((int) posX-(int) radius,(int) posY-(int) radius, (int) radius*2, (int) radius*2);

        ///////////////////////////////////////////////////////////
        // Debugging block
        if (BouncingBalls.DEBUGGING) {
            g.drawRect((int) (posX - radius), (int) (posY - radius), (int) radius * 2, (int) radius * 2);
            g.setColor(Color.RED);
            g.drawLine((int) posX, (int) (posY - radius), (int) posX, (int) (posY + radius));
            g.drawLine((int) (posX - radius), (int) posY, (int) (posX + radius), (int) posY);
            g.setColor(Color.MAGENTA);
            g.drawLine((int) posX, (int) posY, (int) (posX + (speedX * 10)), (int) (posY + (speedY * 10)));
        } // End of debugging block
        ///////////////////////////////////////////////////////////
    }

    public boolean captured(){
        boolean captured=false;
        for(BlackHole b:space.getBlackholes()){
            if(b.inside(this)) captured= true;
        }
        return captured;
    }
    
    public float getSpeed() {
        return (float) Math.sqrt(speedX * speedX + speedY * speedY);
    }

    public float getAngle() {
        return (float) Math.toDegrees(Math.atan2(-speedY, speedX));
    }

    private StringBuilder sb = new StringBuilder();
    private Formatter formatter = new Formatter(sb);

    private static BufferedImage loadImage(String fileName) {
        BufferedImage img;

        img = null;
        try {
            img = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.out.println("Load image error: <" + fileName + ">");
            img = null;
        }

        return img;
    }
    
    @Override
    public String toString() {
        sb.delete(0, sb.length());
        formatter.format("@(%3.0f,%3.0f) r=%3.0f V=(%3.0f,%3.0f) speed=%4.1f angle=%4.0fº",
                posX, posY, radius, speedX, speedY, getSpeed(), getAngle());  // \u0398 is theta
        return sb.toString();
    }

    @Override
    public void run() {
        time= System.nanoTime();
        while (true) {
            this.move();
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
