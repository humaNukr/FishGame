package com.naukma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SecondLevel extends BasicLevel {

    public SecondLevel() {
        super(2, "Неспокійне море", "Другий рівень - підвищена складність");
    }

    @Override
    protected void initializeLevel() {
        timeLimit = 75f; // Менше часу
        targetScore = 300; // Більше очок
        maxFishCount = 25; // Більше рибок на екрані
        sharkSpeed = 220f; // Швидша акула
        minFishSpeed = 80f;
        maxFishSpeed = 200f; // Швидші рибки
        minFishScale = 0.2f;
        maxFishScale = 0.9f;

        // Більш складні рибки з різними характеристиками
        
        // Дуже маленькі швидкі рибки (важко зловити)
        availableFish.add(new FishSpawnData("fish_04/", 15, 0.05f, 8, 0.1f, 6, 180f, 0.2f));
        
        // Середні рибки з помірною швидкістю  
        availableFish.add(new FishSpawnData("fish_05/", 15, 0.05f, 12, 0.1f, 8, 120f, 0.5f));
        
        // Швидкі середні рибки
        availableFish.add(new FishSpawnData("fish_06/", 15, 0.05f, 10, 0.1f, 4, 150f, 0.4f));
        
        // Великі повільні рибки (високі очки, але важко зловити через розмір)
        availableFish.add(new FishSpawnData("fish_07/", 15, 0.05f, 20, 0.1f, 2, 80f, 0.8f));
    }

    @Override
    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        // Виграш якщо набрано цільовий результат або з'їдено достатньо рибок
        return currentScore >= targetScore || fishEaten >= 25;
    }

    @Override
    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        // Програш якщо закінчився час і не досягнуто ціль
        return timeRemaining <= 0 && currentScore < targetScore;
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