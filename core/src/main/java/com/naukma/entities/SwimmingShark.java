package com.naukma.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SwimmingShark extends SwimmingFish {
    public SwimmingShark(float initialScale) {
        super(
            "shark_moving_level2/",
            SHARK_FRAMES,
            false,
            200f,
            initialScale,
            0.1f
        );
    }

    public SwimmingShark(float initialScale, String animationPath) {
        super(
            animationPath,
            SHARK_FRAMES,
            false,
            200f,
            initialScale,
            0.1f
        );
    }

    public Texture getSharkTexture() {
        return getFrameTexture();
    }

    public void renderAt(SpriteBatch batch, float x, float y, float rotation, boolean flipY) {
        Texture currentTexture = getFrameTexture();
        float width = currentTexture.getWidth() * getScale();
        float height = currentTexture.getHeight() * getScale();

        batch.draw(currentTexture,
            x, y,
            width/2, height/2,
            width, height,
            1, 1,
            rotation,
            0, 0,
            currentTexture.getWidth(),
            currentTexture.getHeight(),
            true,
            flipY
        );
    }

    private Texture getFrameTexture() {
        int frameIndex = (int)(System.currentTimeMillis() / 100 % SHARK_FRAMES);
        return super.getFrame(frameIndex);
    }

    private static final int SHARK_FRAMES = 7;
}
