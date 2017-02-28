package bouncballs_nb;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {

    float posX;
    float posY;

    float speed;

    float size = 20;
    
    double angle=0;

    private static final Color DEFAULT_COLOR = Color.WHITE;

    private Space space;

    public Player(Space space, float posX, float posY, float speed) {
        this.space = space;
        this.posX = posX;
        this.posY = posY;
        this.speed = speed;
    }

    public void move(double angle, double power){
        if(power!=0) this.angle=angle;
        posX+= (float) -(Math.cos(angle)*power/10);
        posY+= (float) -(Math.sin(angle)*power/10);
    }
    
    
    public void moveRight() {
        if (!(posX + size + speed >= space.WIDTH)) {
            posX += speed;
        }
    }

    public void moveLeft() {
        if (!(posX - speed <= 0)) {
            posX -= speed;
        }
    }

    public void moveUp() {
        if (!(posY - speed <= 0)) {
            posY -= speed;
        }
    }

    public void moveLUp() {
        if (!(posY - speed <= 0 
                || posX - speed <= 0)) {
            posY -= (speed / 3) * 2;
            posX -= (speed / 3) * 2;
        }
    }

    public void moveRUp() {
        if (!(posY - speed <= 0 
                || posX + size + speed >= space.WIDTH)) {
            posY -= (speed / 3) * 2;
            posX += (speed / 3) * 2;
        }
    }

    public void moveBottom() {
        if (!(posY + size + speed >= space.HEIGHT)) {
            posY += speed;
        }
    }

    public void moveLBottom() {
        if (!(posY + size + speed >= space.HEIGHT 
                || posX - speed <= 0)) {
            posY += (speed / 3) * 2;
            posX -= (speed / 3) * 2;
        }
    }

    public void moveRBottom() {
        if (!(posY + size + speed >= space.HEIGHT 
                || posX + size + speed >= space.WIDTH)) {
            posY += (speed / 3) * 2;
            posX += (speed / 3) * 2;
        }
    }

    public void draw(Graphics g) {
        Graphics2D gg= (Graphics2D) g;
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            BufferedImage img;
            File f = new File("src\\img\\nave.png");
            img = ImageIO.read(f);
            size = img.getWidth();
            gg.rotate(angle+Math.PI/2, posX+size/2, posY+size/2);
            gg.drawImage(img, (int) posX, (int) posY, null);
        } catch (IOException ex) {
        }
        
    }
}
