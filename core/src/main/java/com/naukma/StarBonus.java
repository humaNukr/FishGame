package com.naukma;

import com.badlogic.gdx.math.MathUtils;

public class StarBonus extends Bonus {
    private float targetY;
    private boolean hasReachedTarget;
    private float direction;
    private float verticalSpeed;
    private static final float SCORE_MULTIPLIER = 1.4f; // +40% очок
    private static final float EFFECT_DURATION = 30f; // 30 секунд
    
    public StarBonus(int levelNumber) {
        super("bonus2.png", getScaleForLevel(levelNumber), getSpeedForLevel(levelNumber));
        this.direction = MathUtils.random(0f, 360f);
        this.verticalSpeed = speed;
        this.hasReachedTarget = false;
    }
    
    private static float getScaleForLevel(int levelNumber) {
        switch (levelNumber) {
            case 1: return 0.2f; // Зменшено в 3 рази
            case 2: return 0.23f;
            case 3: return 0.27f;
            default: return 0.2f;
        }
    }
    
    private static float getSpeedForLevel(int levelNumber) {
        switch (levelNumber) {
            case 1: return 30f;
            case 2: return 40f;
            case 3: return 50f;
            default: return 30f;
        }
    }
    
    @Override
    protected void initializePosition() {
        if (worldWidth > 0 && worldHeight > 0) {
            // Завжди з'являється знизу екрану
            x = MathUtils.random(width, worldWidth - width);
            y = -height; // Знизу екрану
            
            // Встановлюємо цільову позицію в середній частині екрану
            targetY = MathUtils.random(visibleMinY + 200f, visibleMaxY - 200f);
        } else {
            x = MathUtils.random(100f, 800f);
            y = -50f;
            targetY = 400f;
        }
    }
    
    @Override
    protected void updateMovement(float deltaTime) {
        if (!hasReachedTarget) {
            // Спливаємо вгору до цільової позиції
            y += verticalSpeed * deltaTime;
            
            // Перевіряємо чи досягли цільової позиції
            if (y >= targetY) {
                y = targetY;
                hasReachedTarget = true;
            }
        } else {
            // Коливаємося в цільовій точці
            // Невеликий горизонтальний рух
            x += Math.sin(animationTime * 1.5f) * 15f * deltaTime;
            
            // Обмежуємо горизонтальний рух межами екрану
            x = Math.max(0, Math.min(worldWidth - width, x));
            
            // Змінюємо напрямок іноді для різноманітності
            if (MathUtils.random() < 0.005f) { // 0.5% шанс кожного кадру
                direction += MathUtils.random(-30f, 30f);
            }
        }
    }
    
    @Override
    public void onCollected(GameHUD gameHUD) {
        // Бонус НЕ активується автоматично, а додається до інвентарю
        gameHUD.addBonus(1, 1); // Тип 1 - зірочка
    }
    
    @Override
    public boolean canSpawn(int levelNumber) {
        // Зірочка може з'являтися в будь-який час
        return true;
    }
    
    public static float getScoreMultiplier() {
        return SCORE_MULTIPLIER;
    }
    
    public static float getEffectDuration() {
        return EFFECT_DURATION;
    }
} 