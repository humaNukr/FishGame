package com.naukma.levels;

public class FishSpawnData {
    public String path;
    public int frameCount;
    public float frameDuration;
    public int points;
    public float spawnWeight; // Ймовірність спавну (0.0 - 1.0)

    // Нові поля для фіксованих параметрів
    public int fixedCount; // Фіксована кількість цього виду риб
    public float fixedSpeed; // Фіксована швидкість для цього виду
    public boolean useFixedSpeed; // Чи використовувати фіксовану швидкість
    public float fixedScale; // Фіксований розмір для цього виду
    public boolean useFixedScale; // Чи використовувати фіксований розмір

    public FishSpawnData(String path, int frameCount, float frameDuration, int points, float spawnWeight) {
        this.path = path;
        this.frameCount = frameCount;
        this.frameDuration = frameDuration;
        this.points = points;
        this.spawnWeight = spawnWeight;
        this.fixedCount = -1; // -1 означає без обмеження
        this.fixedSpeed = -1; // -1 означає використовувати випадкову швидкість
        this.useFixedSpeed = false;
        this.fixedScale = -1; // -1 означає використовувати випадковий розмір
        this.useFixedScale = false;
    }

    // Конструктор з фіксованими параметрами (швидкість)
    public FishSpawnData(String path, int frameCount, float frameDuration, int points,
                         float spawnWeight, int fixedCount, float fixedSpeed) {
        this.path = path;
        this.frameCount = frameCount;
        this.frameDuration = frameDuration;
        this.points = points;
        this.spawnWeight = spawnWeight;
        this.fixedCount = fixedCount;
        this.fixedSpeed = fixedSpeed;
        this.useFixedSpeed = fixedSpeed > 0;
        this.fixedScale = -1;
        this.useFixedScale = false;
    }

    // Новий конструктор з фіксованими параметрами (швидкість і розмір)
    public FishSpawnData(String path, int frameCount, float frameDuration, int points,
                         float spawnWeight, int fixedCount, float fixedSpeed, float fixedScale) {
        this.path = path;
        this.frameCount = frameCount;
        this.frameDuration = frameDuration;
        this.points = points;
        this.spawnWeight = spawnWeight;
        this.fixedCount = fixedCount;
        this.fixedSpeed = fixedSpeed;
        this.useFixedSpeed = fixedSpeed > 0;
        this.fixedScale = fixedScale;
        this.useFixedScale = fixedScale > 0;
    }

    // Методи для роботи з фіксованими параметрами
    public boolean hasFixedCount() {
        return fixedCount > 0;
    }

    public boolean hasFixedSpeed() {
        return useFixedSpeed && fixedSpeed > 0;
    }

    public boolean hasFixedScale() {
        return useFixedScale && fixedScale > 0;
    }

    public void setFixedCount(int count) {
        this.fixedCount = count;
    }

    public void setFixedSpeed(float speed) {
        this.fixedSpeed = speed;
        this.useFixedSpeed = speed > 0;
    }

    public void setFixedScale(float scale) {
        this.fixedScale = scale;
        this.useFixedScale = scale > 0;
    }
}
