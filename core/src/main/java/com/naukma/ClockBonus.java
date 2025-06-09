package com.naukma;

import com.badlogic.gdx.math.MathUtils;

public class ClockBonus extends Bonus {
    private float targetY;
    private boolean hasReachedTarget;
    private float direction;
    private static final float TIME_BONUS = 10f; // 10 секунд
    
    public ClockBonus(int levelNumber) {
        super("bonus3.png", getScaleForLevel(levelNumber), getSpeedForLevel(levelNumber));
        this.direction = MathUtils.random(0f, 360f);
        this.hasReachedTarget = false;
    }
    
    private static float getScaleForLevel(int levelNumber) {
        switch (levelNumber) {
            case 1: return 0.17f; // Зменшено в 3 рази
            case 2: return 0.2f;
            case 3: return 0.23f;
            default: return 0.17f;
        }
    }
    
    private static float getSpeedForLevel(int levelNumber) {
        switch (levelNumber) {
            case 1: return 40f;
            case 2: return 50f;
            case 3: return 60f;
            default: return 40f;
        }
    }
    
    @Override
    protected void initializePosition() {
        if (worldWidth > 0 && worldHeight > 0) {
            // Завжди з'являється знизу екрану
            x = MathUtils.random(width, worldWidth - width);
            y = -height; // Знизу екрану
            
            // Встановлюємо цільову позицію в середній частині екрану
            targetY = MathUtils.random(visibleMinY + 150f, visibleMaxY - 150f);
        } else {
            x = MathUtils.random(100f, 800f);
            y = -50f;
            targetY = 300f;
        }
    }
    
    @Override
    protected void updateMovement(float deltaTime) {
        if (!hasReachedTarget) {
            // Спливаємо вгору до цільової позиції
            y += speed * deltaTime;
            
            // Перевіряємо чи досягли цільової позиції
            if (y >= targetY) {
                y = targetY;
                hasReachedTarget = true;
            }
        } else {
            // Коливаємося в цільовій точці з більшою амплітудою
            // Рух по колу
            x += Math.sin(animationTime * 2f) * 20f * deltaTime;
            y += Math.cos(animationTime * 1.5f) * 12f * deltaTime;
            
            // Обмежуємо рух межами екрану
            x = Math.max(0, Math.min(worldWidth - width, x));
            y = Math.max(visibleMinY, Math.min(visibleMaxY - height, y));
            
            // Корегуємо позицію до цільової
            if (Math.abs(y - targetY) > 50f) {
                y += (targetY - y) * 0.1f; // Повертаємося до цільової позиції
            }
        }
    }
    
    @Override
    public void onCollected(GameHUD gameHUD) {
        // Бонус НЕ активується автоматично, а додається до інвентарю
        gameHUD.addBonus(2, 1); // Тип 2 - годинник
    }
    
    @Override
    public boolean canSpawn(int levelNumber) {
        // Годинник може з'являтися коли завгодно
        return true;
    }
    
    public static float getTimeBonus() {
        return TIME_BONUS;
    }
} 