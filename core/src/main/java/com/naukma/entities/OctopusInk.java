package com.naukma.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class OctopusInk extends Entity {
    private Texture inkTexture;
    private float speed;
    private boolean active = true;

    public OctopusInk(float startX, float startY, float targetY) {
        super(startX, targetY - 80/2f);
        inkTexture = new Texture(Gdx.files.internal("ink2.png"));
        this.width = 80;
        this.height = 80;
        this.speed = 400f;
    }

    public void update(float deltaTime) {
        if (!active) return;
        x -= speed * deltaTime;
        if (x + width < 0) active = false;
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.setColor(1f, 1f, 1f, 1f); // Повністю білі
            batch.draw(inkTexture, x, y, width, height);
            batch.setColor(1f, 1f, 1f, 1f); // Повертаємо стандартний колір
        }
    }

    public Rectangle getRect() { return new Rectangle(x, y, width, height); }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { active = a; }
}
