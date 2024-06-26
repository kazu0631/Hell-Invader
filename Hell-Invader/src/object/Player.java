package object;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Player extends GameObject {

    GamePanel gp;
    KeyHandler keyH;
    BufferedImage[] sprites = new BufferedImage[3];
    BufferedImage image;
    String direction;
    public ArrayList<Bullet> bullets = new ArrayList<>();

    public int hp;
    float boost;

    int cooldown, threshhold;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp.SCREEN_WIDTH/2 - gp.TILE_SIZE/6, gp.SCREEN_HEIGHT - gp.TILE_SIZE*2,
                gp.TILE_SIZE/4, gp.TILE_SIZE/4, 6, 6);
        this.gp = gp;
        this.keyH = keyH;

        hp = 5;
        boost = 1.2f;

        direction = "idle";

        threshhold = 6;
        cooldown = threshhold;

        getImage();
    }

    public void getImage() {

        try {

            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/sprite/player_sprite.png"));
            sprites[0] = spritesheet.getSubimage(0, 0, 16, 16);
            sprites[1] = spritesheet.getSubimage(16, 0, 16, 16);
            sprites[2] = spritesheet.getSubimage(32, 0, 16, 16);

            image = sprites[0];

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {

        setInputs();

        setAnimation();

        setScreenLimit();

        // destroy timer
        if(hp == 0) {
            gp.timerAlive.destroyTimer();
            gp.gameState = gp.OVER_STATE;
        }

        // update bullets
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();

            if(bullets.get(i).y < 0) {
                bullets.remove(bullets.get(i));
            }
        }
    }

    public void draw(Graphics g) {

        // draw player
        g.drawImage(image, x - gp.TILE_SIZE + gp.TILE_SIZE/6, y - gp.TILE_SIZE + gp.TILE_SIZE/8,
                gp.TILE_SIZE*2, gp.TILE_SIZE*2, null);

        // draw bullets
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }
    }

    public void setInputs() {

        // MOVEMENT KEYS
        if(keyH.left) {
            direction = "left";
            x -= vx;
            if(keyH.boost) {
                x -= vx * boost;
            }
        }
        if(keyH.right) {
            direction = "right";
            x += vx;
            if(keyH.boost) {
                x += vx * boost;
            }
        }
        if(keyH.up) {
            direction = "idle";
            y -= vy;
        }
        if(keyH.down) {
            direction = "idle";
            y += vy;
        }

        if(keyH.up && keyH.left) {
            direction = "left";
        }
        if(keyH.up && keyH.right) {
            direction = "right";
        }
        if(keyH.left && keyH.right) {
            direction = "idle";
        }
        if(!keyH.left && !keyH.right) {
            direction = "idle";
        }

        // control fire rate
        cooldown++;
        if(keyH.shoot && cooldown >= threshhold) {
            gp.playSE(4);
            bullets.add(new Bullet(x - gp.TILE_SIZE/4, y, gp));
            bullets.add(new Bullet(x+width, y, gp));
            cooldown = 0;
        }

    }

    public void setAnimation() {

        // animation
        switch (direction) {
            case "idle":
                image = sprites[0];
                break;
            case "left":
                image = sprites[1];
                break;
            case "right":
                image = sprites[2];
        }
    }

    public void setScreenLimit() {

        if(x < 0) {
            x = 0;
        }
        if(x > gp.SCREEN_WIDTH - width) {
            x = gp.SCREEN_WIDTH - width;
        }
        if(y < 0) {
            y = 0;
        }
        if(y > gp.SCREEN_HEIGHT - height) {
            y = gp.SCREEN_HEIGHT - height;
        }
    }
}