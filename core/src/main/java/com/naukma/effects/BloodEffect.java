package com.naukma.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.naukma.entities.Entity;

public class BloodEffect extends Entity {

    public BloodEffect() {
        super(-100, -100); // Початково ховаємо за екраном
        bloodTexture = new Texture(Gdx.files.internal("blood.png"));
        alpha = 1f;
        isActive = false;
        scale = 0.3f;
        this.width = bloodTexture.getWidth() * scale;
        this.height = bloodTexture.getHeight() * scale;
        color = new Color(1, 1, 1, 1);
    }

    // Конструктор з кастомною текстурою
    public BloodEffect(String texturePath) {
        super(-100, -100); // Початково ховаємо за екраном
        bloodTexture = new Texture(Gdx.files.internal(texturePath));
        alpha = 1f;
        isActive = false;
        scale = 0.1f;
        this.width = bloodTexture.getWidth() * scale;
        this.height = bloodTexture.getHeight() * scale;
        color = new Color(1, 1, 1, 1);
    }

    public void spawn(float x, float y) {
        this.x = x;
        this.y = y;
        this.alpha = 1f;
        this.isActive = true;
    }

    public void update(float delta) {
        if (isActive) {
            alpha -= FADE_SPEED * delta;
            if (alpha <= 0) {
                isActive = false;
                alpha = 0;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (isActive) {
            Color oldColor = new Color(batch.getColor()); // <-- важливо
            color.a = alpha;
            batch.setColor(color);

            float width = bloodTexture.getWidth() * scale;
            float height = bloodTexture.getHeight() * scale;
            batch.draw(bloodTexture, x - width/2, y - height/2, width, height);

            batch.setColor(oldColor); // <-- повертаємо колір назад
        }
    }


    public void dispose() {
        bloodTexture.dispose();
    }

    private Texture bloodTexture;
    private float alpha;
    private boolean isActive;
    private float scale;
    private static final float FADE_SPEED = 2f;
    private Color color;
}
