package com.naukma.bonuses;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.naukma.ui.GameHUD;

public class ShellBonus extends Bonus {


    public ShellBonus(int levelNumber) {
        super("shell/frame_00.png", getScaleForLevel(levelNumber), 0f);

        this.currentLevel = levelNumber;

        shellFrames = new Texture[3];
        shellFrames[0] = new Texture("shell/frame_00.png"); // Закрита
        shellFrames[1] = new Texture("shell/frame_01.png"); // Відкрита з перлиною
        shellFrames[2] = new Texture("shell/frame_02.png"); // Відкрита без перлини

        pearlEffect = new Texture("pearl_effect.png");

        currentFrame = 0;
        animationTimer = 0f;
        frameDuration = 0.5f;
        isOpen = false;
        hasPearl = true;
        stateTimer = 0f;
        nextStateChange = MathUtils.random(2f, 5f); // Зміна стану через 2-5 секунд
        pearlGlowTimer = 0f;
        pearlGlowAlpha = 1f;

        // Ініціалізуємо змінні анімації (оскільки не викликаємо super.update())
        this.animationTime = 0f;
        this.bobSpeed = MathUtils.random(1f, 3f);
        this.bobAmplitude = MathUtils.random(5f, 15f);
    }

    private static float getScaleForLevel(int levelNumber) {
        switch (levelNumber) {
            case 1: return 0.27f; // Зменшено в 3 рази
            case 2: return 0.3f;
            case 3: return 0.33f;
            default: return 0.27f;
        }
    }

    @Override
    protected void initializePosition() {
        if (worldWidth > 0 && worldHeight > 0) {
            // З'являється ще нижче на дні екрану
            x = MathUtils.random(width, worldWidth - width);
            y = MathUtils.random(20f, 80f); // Ще нижче біля дна
        } else {
            // Якщо світ ще не ініціалізований, встановлюємо дефолтні значення
            x = MathUtils.random(100f, 800f);
            y = MathUtils.random(20f, 80f);
        }
    }

    @Override
    public void update(float deltaTime) {
        // Для ракушки використовуємо кастомну логіку update без checkBounds()
        if (!active || collected) return;

        animationTime += deltaTime;

        // Floating animation
        bobOffset = (float) Math.sin(animationTime * bobSpeed) * bobAmplitude;

        // НЕ викликаємо checkBounds() для ракушки - вона повинна залишатися на місці

        // Оновлюємо стан ракушки тільки якщо перлина ще є
        if (hasPearl) {
            stateTimer += deltaTime;
            if (stateTimer >= nextStateChange) {
                toggleShellState();
                stateTimer = 0f;
                nextStateChange = MathUtils.random(2f, 5f);
            }
        }

        // Оновлюємо анімацію свічіння перлини
        if (hasPearl && isOpen) {
            pearlGlowTimer += deltaTime;
            // Мигання з періодом 2 секунди (повний цикл)
            float glowCycle = (float) Math.sin(pearlGlowTimer * Math.PI); // 0 до 1 і назад
            pearlGlowAlpha = 0.3f + glowCycle * 0.7f; // Від 0.3 до 1.0
        }

        // Оновлюємо анімацію
        updateAnimation(deltaTime);
    }

    private void toggleShellState() {
        if (hasPearl) {
            isOpen = !isOpen;
            currentFrame = isOpen ? 1 : 0; // Кадр 1 (з перлиною) або 0 (закрита)
        } else {
            // Якщо перлини немає, ракушка залишається відкритою без перлини
            isOpen = true;
            currentFrame = 2;
        }
    }

    private void updateAnimation(float deltaTime) {
        animationTimer += deltaTime;
        // Анімація плавна, без зміни кадрів автоматично - тільки при зміні стану
    }

    @Override
    public void render(SpriteBatch batch) {
        if (active) {
            float renderY = y + bobOffset;

            // Малюємо ракушку
            batch.draw(shellFrames[currentFrame], x, renderY, width, height);

            // Малюємо ефект свічіння перлини якщо ракушка відкрита і є перлина
            if (hasPearl && isOpen) {
                // Зберігаємо поточний колір
                float originalAlpha = batch.getColor().a;

                // Встановлюємо альфу для ефекту мигання
                batch.setColor(1f, 1f, 1f, pearlGlowAlpha);

                // Малюємо ефект перлини по центру ракушки
                float effectX = x + width * 0.25f; // Центруємо ефект
                float effectY = renderY + height * 0.25f;
                float effectSize = width * 0.5f; // Розмір ефекту

                batch.draw(pearlEffect, effectX, effectY, effectSize, effectSize);

                // Відновлюємо колір
                batch.setColor(1f, 1f, 1f, originalAlpha);
            }
        }
    }

    @Override
    public boolean checkCollision(float sharkX, float sharkY, float sharkWidth, float sharkHeight) {
        // Колізія можлива тільки коли ракушка відкрита і є перлина
        if (!active || collected || !isOpen || !hasPearl) return false;

        return super.checkCollision(sharkX, sharkY, sharkWidth, sharkHeight);
    }

    @Override
    public void onCollected(GameHUD gameHUD) {
        if (isOpen && hasPearl) {
            // Життя буде додано в BasicLevel
            hasPearl = false;
            isOpen = true; // Залишаємо відкритою
            currentFrame = 2; // Ракушка без перлини (3-й кадр)

            // Позначаємо номер рівня, на якому ракушка була зібрана
            lastSpawnedLevel = currentLevel;

            // НЕ позначаємо як collected, щоб ракушка залишилася на екрані
            // collected = true; // Це НЕ викликаємо!
        }
    }

    @Override
    public boolean canSpawn(int levelNumber) {
        return lastSpawnedLevel != levelNumber;
    }

    public static void resetForNewLevel() {
        // Скидаємо статичну змінну при перезапуску рівня
        lastSpawnedLevel = -1;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean hasPearl() {
        return hasPearl;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (shellFrames != null) {
            for (Texture frame : shellFrames) {
                if (frame != null) {
                    frame.dispose();
                }
            }
        }
        if (pearlEffect != null) {
            pearlEffect.dispose();
        }
    }

    private static int lastSpawnedLevel = -1;
    private int currentLevel;
    private Texture[] shellFrames;
    private Texture pearlEffect;
    private int currentFrame;
    private float animationTimer;
    private float frameDuration;
    private boolean isOpen;
    private boolean hasPearl;
    private float stateTimer;
    private float nextStateChange;
    private float pearlGlowTimer;
    private float pearlGlowAlpha;
}
