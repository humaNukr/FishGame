package com.naukma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class SwimmingFish {

    public SwimmingFish(String framesPath, int framesCount, boolean isLookingLeft,
                        float speed, float scale, float frameDuration) {
        this.isLookingLeft = isLookingLeft;
        this.speed = speed;
        this.frameDuration = frameDuration;
        this.frameCount = framesCount;
        frames = new Array<>();

        for (int i = 0; i < framesCount; i++) {
            frames.add(new Texture(Gdx.files.internal(framesPath +"sprite_0"+ i + ".png")));
        }
        width = frames.get(0).getWidth() * scale;
        height = frames.get(0).getHeight() * scale;

        // Отримуємо розміри світу
        worldWidth = Gdx.graphics.getWidth();
        worldHeight = Gdx.graphics.getHeight() * 3.0f;

        isActive = true;
    }

    // Метод для встановлення розмірів світу
    public void setWorldBounds(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        respawn();
    }

    public void respawn() {
        movingRight = MathUtils.randomBoolean();

        // Випадковий Y по всій висоті світу
        y = MathUtils.random(height, worldHeight - height);

        if (movingRight) {
            x = -width * 2;
        } else {
            x = worldWidth + width * 2;
        }

        rotation = 0;
        isActive = true;
        targetY = y;
        yChangeTimer = MathUtils.random(0, Y_CHANGE_INTERVAL);
    }

    public void update(float delta) {
        if (!isActive) return;

        stateTime += delta;
        yChangeTimer += delta;

        if (yChangeTimer >= Y_CHANGE_INTERVAL) {
            targetY = MathUtils.random(height, worldHeight - height);
            yChangeTimer = 0;
        }

        float yDiff = targetY - y;
        if (Math.abs(yDiff) > 1) {
            y += Math.signum(yDiff) * speed * 0.3f * delta;
        }

        if (movingRight) {
            x += speed * delta;
        } else {
            x -= speed * delta;
        }

        if ((movingRight && x > worldWidth + width * 2) ||
            (!movingRight && x < -width * 2)) {
            isActive = false;
        }
    }

    public void renderAt(SpriteBatch batch, float worldX, float worldY) {
        if (!isActive) return;

        int frameIndex = (int)(stateTime / frameDuration) % frames.size;
        Texture currentFrame = frames.get(frameIndex);


        boolean flipX;
        if (isLookingLeft) {
            flipX = movingRight;
        } else {
            flipX = !movingRight;
        }
        boolean flipY = false;

        batch.draw(currentFrame,
            worldX, worldY,
            width / 2, height / 2,
            width, height,
            1, 1,
            rotation,
            0, 0,
            currentFrame.getWidth(), currentFrame.getHeight(),
            flipX, flipY);
    }

    public void dispose() {
        for (Texture frame : frames) {
            frame.dispose();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Texture getFrame(int index) {
        return frames.get(index);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getScale() { return width / frames.get(0).getWidth(); }
    public void setActive(boolean active) { this.isActive = active; }

    private final Array<Texture> frames;
    private float stateTime;
    private float frameDuration = 0.05f;
    private float x, y;
    private float width, height;
    private float rotation;
    private float speed;
    private int frameCount;
    private boolean isActive;
    private final boolean isLookingLeft;
    private boolean movingRight;
    private float targetY;
    private float yChangeTimer;
    private static final float Y_CHANGE_INTERVAL = 3f;

    private float worldWidth, worldHeight;
}
