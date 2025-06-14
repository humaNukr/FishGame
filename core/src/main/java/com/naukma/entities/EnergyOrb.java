package com.naukma.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class EnergyOrb extends Entity {
    private Texture texture;
    private Vector2 velocity;
    private Rectangle bounds;
    private boolean active = true;
    private boolean reflected = false;
    private static final float SPEED = 600f;
    private float orbSize;

    public EnergyOrb(float startX, float startY) {
        super(startX, startY);
        texture = new Texture(Gdx.files.internal("energy_orb.png"));
        this.orbSize = Gdx.graphics.getHeight() * 0.03f;
        this.width = orbSize;
        this.height = orbSize;
        this.velocity = new Vector2(-SPEED, 0); // Спочатку летить вліво
        this.bounds = new Rectangle(x, y, orbSize, orbSize);
    }

    public void update(float delta) {
        if (!active) return;
        x += velocity.x * delta;
        y += velocity.y * delta;
        bounds.setPosition(x, y);
        // Деактивувати, якщо вийшов за межі екрану
        if (x < -bounds.width || x > Gdx.graphics.getWidth()) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y, orbSize, orbSize);
        }
    }

    public void reflect() {
        velocity.x *= -1.5f; // Повертається назад швидше
        reflected = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isReflected() {
        return reflected;
    }

    public void dispose() {
        texture.dispose();
    }
}
