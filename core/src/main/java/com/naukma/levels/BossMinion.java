package com.naukma.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class BossMinion {
    private Texture minionTexture;
    private float x, y, width, height, speed;
    private boolean active = true;

    public BossMinion(float y) {
        minionTexture = new Texture(Gdx.files.internal("octopus.png"));
        width = 70f;
        height = 70f;
        x = Gdx.graphics.getWidth();
        this.y = y;
        speed = 250f;
    }

    public void update(float deltaTime) {
        if (!active) return;
        x -= speed * deltaTime;
        if (x + width < 0) active = false;
    }

    public void render(SpriteBatch batch) {
        if (active) batch.draw(minionTexture, x, y, width, height);
    }

    public Rectangle getRect() { return new Rectangle(x, y, width, height); }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { active = a; }
} 