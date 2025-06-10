package com.naukma;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SwimmingShark extends SwimmingFish {
    public SwimmingShark(float initialScale) {
        super(
            "shark_moving/",
            SHARK_FRAMES,
            false,
            200f,
            initialScale,
            0.1f
        );
    }

    public void renderAt(SpriteBatch batch, float x, float y, float rotation) {
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
            rotation > 90 && rotation < 270
        );
    }

    private Texture getFrameTexture() {
        int frameIndex = (int)(System.currentTimeMillis() / 100 % SHARK_FRAMES);
        return super.getFrame(frameIndex);
    }

    private static final int SHARK_FRAMES = 7;
}
