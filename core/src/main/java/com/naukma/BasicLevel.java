package com.naukma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public abstract class BasicLevel {
    protected int levelNumber;
    protected String levelName;
    protected String description;
    protected float timeLimit;
    protected int targetScore;
    protected int maxFishCount;
    protected boolean isCompleted;
    protected boolean isFailed;

    // Налаштування рівня
    protected float sharkSpeed;
    protected float minFishSpeed;
    protected float maxFishSpeed;
    protected float minFishScale;
    protected float maxFishScale;

    // Типи риб для цього рівня
    protected Array<FishSpawnData> availableFish;

    // Відстеження кількості риб кожного виду
    protected ObjectMap<String, Integer> currentFishCounts;

    public BasicLevel(int levelNumber, String levelName, String description) {
        this.levelNumber = levelNumber;
        this.levelName = levelName;
        this.description = description;
        this.availableFish = new Array<>();
        this.currentFishCounts = new ObjectMap<>();
        initializeLevel();
    }

    protected abstract void initializeLevel();

    // Методи для перевірки умов завершення рівня
    public abstract boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten);
    public abstract boolean checkLoseCondition(int currentScore, float timeRemaining, int lives);

    // Спеціальна логіка рівня (якщо потрібно)
    public abstract void updateLevelLogic(float deltaTime, float sharkX, float sharkY);

    // Спеціальний рендеринг для рівня (бонуси, ефекти тощо)
    public abstract void renderLevelSpecific(SpriteBatch batch);

    // Отримання випадкової рибки для спавну з урахуванням фіксованої кількості
    public FishSpawnData getRandomFish() {
        if (availableFish.size == 0) return null;

        Array<FishSpawnData> availableForSpawn = new Array<>();

        for (FishSpawnData fishData : availableFish) {
            if (canSpawnFish(fishData)) {
                availableForSpawn.add(fishData);
            }
        }

        if (availableForSpawn.size == 0) return null;

        return availableForSpawn.random();
    }

    // Перевірка, чи можна створити рибу цього виду
    public boolean canSpawnFish(FishSpawnData fishData) {
        if (!fishData.hasFixedCount()) {
            return true; // Немає обмеження по кількості
        }

        int currentCount = getCurrentFishCount(fishData.path);
        return currentCount < fishData.fixedCount;
    }

    // Отримання поточної кількості риб певного виду
    public int getCurrentFishCount(String fishPath) {
        return currentFishCounts.get(fishPath, 0);
    }

    // Додавання рибки (викликається при створенні)
    public void addFish(String fishPath) {
        int currentCount = getCurrentFishCount(fishPath);
        currentFishCounts.put(fishPath, currentCount + 1);
    }

    // Видалення рибки (викликається при з'їданні або виході з екрану)
    public void removeFish(String fishPath) {
        int currentCount = getCurrentFishCount(fishPath);
        if (currentCount > 0) {
            currentFishCounts.put(fishPath, currentCount - 1);
        }
    }

    // Отримання швидкості для конкретного виду риби
    public float getFishSpeed(FishSpawnData fishData) {
        if (fishData.hasFixedSpeed()) {
            return fishData.fixedSpeed;
        } else {
            return MathUtils.random(minFishSpeed, maxFishSpeed);
        }
    }
    public float getFishScale(FishSpawnData fishData) {
        if (fishData.hasFixedScale()) {
            return fishData.fixedScale;
        } else {
            return MathUtils.random(minFishScale, maxFishScale);
        }
    }


    // Гетери
    public int getLevelNumber() { return levelNumber; }
    public String getLevelName() { return levelName; }
    public String getDescription() { return description; }
    public float getTimeLimit() { return timeLimit; }
    public int getTargetScore() { return targetScore; }
    public int getMaxFishCount() { return maxFishCount; }
    public float getSharkSpeed() { return sharkSpeed; }
    public float getMinFishSpeed() { return minFishSpeed; }
    public float getMaxFishSpeed() { return maxFishSpeed; }
    public float getMinFishScale() { return minFishScale; }
    public float getMaxFishScale() { return maxFishScale; }
    public boolean isCompleted() { return isCompleted; }
    public boolean isFailed() { return isFailed; }

    public void setCompleted(boolean completed) { this.isCompleted = completed; }
    public void setFailed(boolean failed) { this.isFailed = failed; }
}
