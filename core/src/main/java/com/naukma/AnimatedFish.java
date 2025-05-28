package com.naukma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class AnimatedFish {
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
    private static final float Y_CHANGE_INTERVAL = 3f; // секунди між змінами Y координати



    public AnimatedFish(String framesPath, int framesCount, boolean isLookingLeft,
                        float speed, float scale, float frameDuration) {
        this.isLookingLeft = isLookingLeft;
        this.speed = speed;
        this.frameDuration = frameDuration;
        this.frameCount = framesCount;
        frames = new Array<>();
        // Завантажуємо всі кадри
        for (int i = 0; i < framesCount; i++) {
            frames.add(new Texture(Gdx.files.internal(framesPath +"sprite_0"+ i + ".png")));
        }
        width = frames.get(0).getWidth() * scale;
        height = frames.get(0).getHeight() * scale;
        respawn();
    }

    public void respawn() {
        movingRight = MathUtils.randomBoolean(); // true = пливе вправо

        y = MathUtils.random(height, Gdx.graphics.getHeight() - height);

        if (movingRight) {
            x = -width;
        } else {
            x = Gdx.graphics.getWidth() + width;
        }

        rotation = 0;
        isActive = true;
        targetY = y;
        yChangeTimer = 0;
    }




    public void update(float delta) {
        if (!isActive) return;

        stateTime += delta;
        yChangeTimer += delta;

        // Random Y movement
        if (yChangeTimer >= Y_CHANGE_INTERVAL) {
            targetY = MathUtils.random(height, Gdx.graphics.getHeight() - height);
            yChangeTimer = 0;
        }

        // Smooth Y movement
        float yDiff = targetY - y;
        if (Math.abs(yDiff) > 1) {
            y += Math.signum(yDiff) * speed * 0.5f * delta;
        }

        // X movement
        float dirX = movingRight ? 1 : -1;
        x += dirX * speed * delta;

        if (x < -width * 2 || x > Gdx.graphics.getWidth() + width * 2) {
            isActive = false;
        }
    }


    public void render(SpriteBatch batch) {
        if (!isActive) return;

        int frameIndex = (int)(stateTime / frameDuration) % frames.size;
        Texture currentFrame = frames.get(frameIndex);

        boolean flipX = movingRight; // дзеркалимо по X, якщо пливе вправо
        boolean flipY = false;

        batch.draw(currentFrame,
            x, y,
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


    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getScale() { return width / frames.get(0).getWidth(); }
    public void setActive(boolean active) { this.isActive = active; }

}
