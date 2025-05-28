package com.naukma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class AnimatedShark {
    private Array<Texture> frames;
    private float stateTime;
    private float frameDuration = 0.1f;
    private boolean isEating = false;
    private static final int EATING_FRAMES = 8;
    private int currentFrame = 0;
    private float width, height;

    public AnimatedShark() {
        frames = new Array<>();
        // Load normal sprite
        frames.add(new Texture(Gdx.files.internal("shark/sprite_0.png")));
        // Load eating animation frames
        for (int i = 0; i < EATING_FRAMES; i++) {
            frames.add(new Texture(Gdx.files.internal("shark/sprite_" + i + ".png")));
        }

        // Зберігаємо початкові розміри
        Texture baseTexture = frames.get(0);
        width = baseTexture.getWidth();
        height = baseTexture.getHeight();
    }


    public void startEating() {
        isEating = true;
        stateTime = 0;
        currentFrame = 0;
    }

    public void update(float delta) {
        if (isEating) {
            stateTime += delta;
            currentFrame = (int)(stateTime / frameDuration);
            if (currentFrame >= EATING_FRAMES) {
                isEating = false;
                currentFrame = 0;
            }
        }
    }

    public Texture getCurrentTexture() {
        Texture currentTexture = isEating ? frames.get(currentFrame + 1) : frames.get(0);
        // Масштабуємо всі кадри до розміру першої текстури
        if (currentTexture.getWidth() != width || currentTexture.getHeight() != height) {
            currentTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return currentTexture;
    }

    public boolean isEating() {
        return isEating;
    }

    public void dispose() {
        for (Texture frame : frames) {
            frame.dispose();
        }
    }
}
