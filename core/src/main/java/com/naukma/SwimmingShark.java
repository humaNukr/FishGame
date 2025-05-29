package com.naukma;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SwimmingShark extends SwimmingFish {
    public SwimmingShark() {
        super(
            "shark_moving/",  // шлях до папки з кадрами
            11,               // кількість кадрів
            false,          // не дивиться вліво
            200f,          // швидкість
            SHARK_SCALE,    // масштаб
            0.1f           // тривалість кадру
        );
    }

    public void renderAt(SpriteBatch batch, float x, float y, float rotation) {
        Texture currentTexture = getFrameTexture(); // використовуємо getFrameTexture замість getCurrentTexture
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
        int frameIndex = (int)(System.currentTimeMillis() / 100 % 8); // анімація з 8 кадрів
        return super.getFrame(frameIndex);
    }

    private static final float SHARK_SCALE = 0.5f;
}
