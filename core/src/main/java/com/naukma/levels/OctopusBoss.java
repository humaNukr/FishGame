package com.naukma.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;

public class OctopusBoss {
    private Texture bossTexture;
    private Texture eyeTexture;
    private float x, y, width, height;
    private int health;
    private int maxHealth;

    // Стани боса
    private enum State {
        IDLE, VULNERABLE
    }
    private State currentState;
    private float stateTimer = 0f;
    private static final float IDLE_DURATION = 5.0f;
    private static final float VULNERABLE_DURATION = 15.0f;

    // Стрільба
    private float shootTimer = 0f;
    private static final float SHOOT_INTERVAL = 2.0f; // Стріляти кожні 2 секунди у фазі атаки

    public OctopusBoss() {
        bossTexture = new Texture(Gdx.files.internal("octopus.png"));
        eyeTexture = new Texture(Gdx.files.internal("boss_eye.png"));
        width = Gdx.graphics.getHeight() * 0.45f;
        height = Gdx.graphics.getHeight() * 0.45f;
        x = Gdx.graphics.getWidth() - width - 40;
        y = (Gdx.graphics.getHeight() - height) / 2;
        
        // Початкові налаштування
        health = 10;
        maxHealth = 10;
        currentState = State.IDLE;
        stateTimer = 0f;
    }

    public EnergyOrb update(float deltaTime) {
        stateTimer += deltaTime;

        // Зміна станів
        if (currentState == State.IDLE && stateTimer >= IDLE_DURATION) {
            currentState = State.VULNERABLE;
            stateTimer = 0;
        } else if (currentState == State.VULNERABLE && stateTimer >= VULNERABLE_DURATION) {
            currentState = State.IDLE;
            stateTimer = 0;
        }
        
        // Логіка для стану VULNERABLE
        if (currentState == State.VULNERABLE) {
            shootTimer += deltaTime;
            if (shootTimer >= SHOOT_INTERVAL) {
                shootTimer = 0;
                // Створити та повернути новий енергетичний снаряд у випадковій точці по Y
                float orbY = MathUtils.random(0, Gdx.graphics.getHeight() - Gdx.graphics.getHeight() * 0.03f); 
                return new EnergyOrb(x, orbY);
            }
        }

        return null; // Не стріляти, якщо не час
    }

    public void render(SpriteBatch batch) {
        batch.draw(bossTexture, x, y, width, height);

        // Відображення "ока", коли бос вразливий
        if (currentState == State.VULNERABLE) {
            float pulse = (float) Math.abs(Math.sin(stateTimer * 2.0f));
            float eyeSize = Gdx.graphics.getHeight() * 0.1f * (1.0f + pulse * 0.2f);
            float eyeX = x + width * 0.45f - (eyeSize - 80) / 2;
            float eyeY = y + height * 0.74f - (eyeSize - 80) / 2;
            
            Color c = batch.getColor();
            batch.setColor(1, 1, 0, 0.7f + pulse * 0.3f); // Жовте світіння
            batch.draw(eyeTexture, eyeX, eyeY, eyeSize, eyeSize);
            batch.setColor(c);
        }
        // Додаю скидання кольору після рендеру ока
        batch.setColor(Color.WHITE);
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void dispose() {
        bossTexture.dispose();
        eyeTexture.dispose();
    }

    public OctopusInk shootInk(float targetY) {
        // Чорнило стріляє тільки в IDLE фазі, щоб не перевантажувати
        if (currentState == State.IDLE) {
            float inkY = y + height * 0.6f;
            return new OctopusInk(x, inkY, targetY);
        }
        return null;
    }

    public boolean checkCollision(Rectangle sharkRect, float sharkY, float sharkHeight) {
        Rectangle tentacleRect = new Rectangle(x, y, width, height);
        float overlapTop = (sharkY + sharkHeight) - y;
        if (overlapTop >= height * 0.2f) {
            takeDamage(1); // Припустимо, що колізія знизу завдає 1 одиниці шкоди
            return true;
        }
        float tentacleBottom = y + height;
        float sharkTop = sharkY + sharkHeight;
        if (sharkTop >= tentacleBottom && sharkTop <= tentacleBottom + sharkHeight * 0.1f) {
            takeDamage(1); // Припустимо, що колізія зверху завдає 1 одиниці шкоди
            return true;
        }
        return false;
    }
} 