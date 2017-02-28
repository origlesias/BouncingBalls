

package bouncballs_nb;

// @author: Oriol Iglesias

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javafx.scene.shape.Circle;


public class BlackHole{
    int posX;
    int posY;
    int width;
    int height;
    Space space;
    
    boolean ocupat=false;
    Ball ball;

    public BlackHole(int posX, int posY, int width, int height, Space space) {
        this.posX = posX;
        this.posY = posY;
        this.height = height;
        this.width = width;
        this.space = space;
    }
    
    public void draw(Graphics g){
        Graphics2D gg= (Graphics2D) g;
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.white);
        gg.fillRect(posX, posY, width, height);
    }
    
    public synchronized void peaje(Ball b){
        while(ocupat&&b!=ball){
                  try {
                      b.waiting= true;
                      wait();
                      b.waiting= false;
                      b.time= System.nanoTime();
                  } catch (InterruptedException interruptedException) {
                  }
        }
        if(ball!=null){
        if(!inside(b)&&b==ball){
            ball=null;
            notifyAll();
            ocupat=false;
        }
        }else{
        ball= b;
        ocupat=true;
        }
    }

    public boolean inside(Ball b) {
        return (b.posX + b.radius > posX 
                && b.posX - b.radius < posX + width
                && b.posY - b.radius < posY + height
                && b.posY + b.radius > posY);
    }
    
    
    
    

}
