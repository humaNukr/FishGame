package com.naukma.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;

public class EnergyOrb {
    private Texture texture;
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private boolean active = true;
    private boolean reflected = false;
    private static final float SPEED = 600f;
    private float orbSize;

    public EnergyOrb(float startX, float startY) {
        texture = new Texture(Gdx.files.internal("energy_orb.png"));
        this.orbSize = Gdx.graphics.getHeight() * 0.03f; 
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(-SPEED, 0); // Спочатку летить вліво
        this.bounds = new Rectangle(position.x, position.y, orbSize, orbSize);
    }

    public void update(float delta) {
        if (!active) return;
        position.add(velocity.x * delta, velocity.y * delta);
        bounds.setPosition(position);
        // Деактивувати, якщо вийшов за межі екрану
        if (position.x < -bounds.width || position.x > Gdx.graphics.getWidth()) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, position.x, position.y, orbSize, orbSize);
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