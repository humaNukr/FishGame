package com.naukma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ThirdLevel extends BasicLevel {

    public ThirdLevel() {
        super(3, "Глибокий океан", "Третій рівень - найвища складність");
    }

    @Override
    protected void initializeLevel() {
        timeLimit = 60f; // Найменше часу
        targetScore = 500; // Найбільше очок
        maxFishCount = 30; // Найбільше рибок на екрані
        sharkSpeed = 250f; // Найшвидша акула
        minFishSpeed = 120f;
        maxFishSpeed = 280f; // Дуже швидкі рибки
        minFishScale = 0.15f;
        maxFishScale = 1.0f;

        // Найскладніші рибки
        
        // Дуже маленькі надшвидкі рибки (дуже важко зловити)
        availableFish.add(new FishSpawnData("fish_08/", 15, 0.05f, 6, 0.1f, 8, 250f, 0.15f));
        
        // Маленькі швидкі рибки
        availableFish.add(new FishSpawnData("fish_09/", 15, 0.05f, 10, 0.1f, 6, 200f, 0.3f));
        
        // Середні дуже швидкі рибки  
        availableFish.add(new FishSpawnData("fish_04/", 15, 0.05f, 15, 0.1f, 8, 180f, 0.4f));
        
        // Великі швидкі рибки (складно зловити, але дають багато очок)
        availableFish.add(new FishSpawnData("fish_05/", 15, 0.05f, 25, 0.1f, 3, 160f, 0.7f));
        
        // Гігантські рибки (найважчі для з'їдання, але найбільше очок)
        availableFish.add(new FishSpawnData("fish_03/", 15, 0.16f, 35, 0.1f, 1, 120f, 1.0f));
    }

    @Override
    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        // Виграш якщо набрано цільовий результат або з'їдено достатньо рибок
        return currentScore >= targetScore || fishEaten >= 35;
    }

    @Override
    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        // Програш якщо закінчився час і не досягнуто ціль
        return timeRemaining <= 0 && currentScore < targetScore;
    }

    @Override
    protected void updateLevelLogic(float deltaTime, float sharkX, float sharkY) {
        // Спеціальна логіка для складного рівня
        // Можна додати додаткові ускладнення:
        // - Рибки тікають від акули
        // - Періодичні "шторми" що змінюють швидкість
        // - Бонусні рибки що з'являються рідко
    }

    @Override
    protected void renderLevelSpecific(SpriteBatch batch) {
        // Можна додати спеціальні ефекти для найскладнішого рівня
        // - Темніший фон
        // - Частинки у воді
        // - Спеціальні візуальні ефекти
    }
} 