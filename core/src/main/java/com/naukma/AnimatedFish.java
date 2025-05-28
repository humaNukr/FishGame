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
    private boolean isActive;
    private final boolean isLookingLeft;
    private boolean movingRight;


    public AnimatedFish(String framesPath, int framesCount, boolean isLookingLeft,
                        float speed, float scale, float frameDuration) {
        this.isLookingLeft = isLookingLeft;
        this.speed = speed;
        this.frameDuration = frameDuration;

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
    }




    public void update(float delta) {
        if (!isActive) return;

        stateTime += delta;

        float dirX = movingRight ? 1 : -1;
        float dirY = 0; // рівно пливе по X

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
}
