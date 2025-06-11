package com.naukma.bonuses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class EatingShark {

    public EatingShark() {
        frames = new Array<>();
        frames.add(new Texture(Gdx.files.internal("shark_level2/frame_00.png")));
        for (int i = 0; i < EATING_FRAMES; i++) {
            frames.add(new Texture(Gdx.files.internal("shark_level2/frame_0" + i + ".png")));
        }

        Texture baseTexture = frames.get(0);
        width = baseTexture.getWidth();
        height = baseTexture.getHeight();

        // Завантаження звуку укусу
        biteSound = Gdx.audio.newSound(Gdx.files.internal("chew.wav"));
    }

    public void startEating() {
        isEating = true;
        stateTime = 0;

        // Програвання звуку
        if (biteSound != null) {
            biteSound.play();
        }
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

    public void renderAt(SpriteBatch batch, float x, float y, float rotation, boolean flipY) {
        if (!isEating) return;

        Texture currentTexture = getCurrentTexture();
        float width = currentTexture.getWidth() * 0.5f;
        float height = currentTexture.getHeight() * 0.5f;

        batch.draw(currentTexture,
            x, y,
            width / 2, height / 2,
            width, height,
            1, 1,
            rotation,
            0, 0,
            currentTexture.getWidth(), currentTexture.getHeight(),
            true, flipY);
    }

    public Texture getCurrentTexture() {
        int frameIndex = (int)(stateTime / FRAME_DURATION) % frames.size;
        return frames.get(frameIndex);
    }

    public boolean isEating() {
        return isEating;
    }

    public void dispose() {
        for (Texture frame : frames) {
            frame.dispose();
        }

        if (biteSound != null) {
            biteSound.dispose();
        }
    }

    private Array<Texture> frames;
    private float stateTime;
    private float frameDuration = 0.1f;
    private boolean isEating = false;
    private static final int EATING_FRAMES = 7;
    private int currentFrame = 0;
    private float width, height;
    private static final float FRAME_DURATION = 0.1f;

    // Звук укусу
    private Sound biteSound;
}
