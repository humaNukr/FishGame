package com.naukma.bonuses;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.naukma.entities.Entity;
import com.naukma.ui.GameHUD;

public abstract class Bonus extends Entity {
    protected boolean active;
    protected boolean collected;
    protected Texture texture;
    protected float scale;
    protected float speed;
    protected float worldWidth, worldHeight;
    protected float visibleMinY, visibleMaxY;

    // Animation variables
    protected float animationTime;
    protected float bobOffset;
    protected float bobSpeed;
    protected float bobAmplitude;

    public Bonus(String texturePath, float scale, float speed) {
        super(0, 0); // Початкові координати, які будуть змінені
        this.texture = new Texture(texturePath);
        this.scale = scale;
        this.speed = speed;
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;
        this.active = true;
        this.collected = false;
        this.animationTime = 0f;
        this.bobSpeed = MathUtils.random(1f, 3f);
        this.bobAmplitude = MathUtils.random(5f, 15f);

        initializePosition();
    }

    protected abstract void initializePosition();

    public abstract void onCollected(GameHUD gameHUD);

    public abstract boolean canSpawn(int levelNumber);

    public void update(float deltaTime) {
        if (!active || collected) return;

        animationTime += deltaTime;

        // Floating animation
        bobOffset = (float) Math.sin(animationTime * bobSpeed) * bobAmplitude;

        // Custom movement logic for each bonus type
        updateMovement(deltaTime);

        // Check if bonus went off screen
        checkBounds();
    }

    protected void updateMovement(float deltaTime) {
        // Default: no movement, can be overridden
    }

    protected void checkBounds() {
        if (x < -width * 2 || x > worldWidth + width * 2 ||
            y < -height * 2 || y > worldHeight + height * 2) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active && !collected) {
            float renderY = y + bobOffset;
            batch.draw(texture, x, renderY, width, height);
        }
    }

    public boolean checkCollision(float pointX, float pointY) {
        return pointX >= this.x && pointX <= this.x + this.width &&
               pointY >= this.y && pointY <= this.y + this.height;
    }

    public void collect() {
        collected = true;
        active = false;
    }

    public void setWorldBounds(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void setVisibleBounds(float minY, float maxY) {
        this.visibleMinY = minY;
        this.visibleMaxY = maxY;
    }

    // Getters
    public boolean isActive() { return active; }
    public boolean isCollected() { return collected; }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
