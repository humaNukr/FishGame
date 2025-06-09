package com.naukma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SecondLevel extends BasicLevel {

    public SecondLevel() {
        super(2);
    }

    @Override
    protected void initializeLevel() {
        timeLimit = 75f; // Менше часу
        targetScore = 300; // Більше очок
        targetFishCount = 25; // Ціль для другого рівня
        maxFishCount = 15; // 15 рибок на екрані
        livesCount = 1; // 1 життя на другому рівні
        sharkSpeed = 220f; // Швидша акула
        minFishSpeed = 80f;
        maxFishSpeed = 200f; // Швидші рибки
        minFishScale = 0.2f;
        maxFishScale = 0.9f;

        // Більш складні рибки з різними характеристиками
        availableFish.add(new FishSpawnData("fish_07/", 15, 0.05f, 12, 0.2f, 12, 250f, 0.2f));

        // Швидкі середні рибки
        availableFish.add(new FishSpawnData("fish_10/", 15, 0.05f, 10, 0.1f, 3, 160f, 0.5f));

        // Великі повільні рибки (високі очки, але важко зловити через розмір)
        availableFish.add(new FishSpawnData("fish_08/", 15, 0.05f, 20, 0.5f, 3, 120f, 0.8f));
    }

    @Override
    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        // Виграш якщо набрано цільовий результат або з'їдено достатньо рибок
        return currentScore >= targetScore || fishEaten >= 25;
    }

    @Override
    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        // Програш якщо закінчився час і не досягнуто ціль або закінчились життя
        return lives < 0 || (timeRemaining <= 0 && currentScore < targetScore);
    }

    @Override
    protected void updateLevelLogic(float deltaTime, float sharkX, float sharkY) {
        // Можна додати спеціальну логіку пізніше
        // Наприклад, випадкові бонуси або ускладнення
    }

    @Override
    protected void renderLevelSpecific(SpriteBatch batch) {
        // Можна додати спеціальні ефекти для цього рівня
    }
}
