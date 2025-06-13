package com.naukma.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class OctopusInk {
    private Texture inkTexture;
    private float x, y, width, height, speed;
    private boolean active = true;

    public OctopusInk(float startX, float startY, float targetY) {
        inkTexture = new Texture(Gdx.files.internal("ink.png")); // Додайте ink.png у assets
        width = 40;
        height = 40;
        x = startX;
        y = targetY - height/2;
        speed = 400f;
    }

    public void update(float deltaTime) {
        if (!active) return;
        x -= speed * deltaTime;
        if (x + width < 0) active = false;
    }

    public void render(SpriteBatch batch) {
        if (active) batch.draw(inkTexture, x, y, width, height);
    }

    public Rectangle getRect() { return new Rectangle(x, y, width, height); }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { active = a; }
} 