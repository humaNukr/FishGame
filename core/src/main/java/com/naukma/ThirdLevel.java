package com.naukma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ThirdLevel extends BasicLevel {

    public ThirdLevel() {
        super(3);
    }

    @Override
    protected void initializeLevel() {
        timeLimit = 60f; // Найменше часу
        targetScore = 500; // Найбільше очок
        targetFishCount = 35; // Ціль для третього рівня
        maxFishCount = 15; // 15 рибок на екрані
        livesCount = 0; // 0 життів на третьому рівні
        sharkSpeed = 250f; // Найшвидша акула
        minFishSpeed = 120f;
        maxFishSpeed = 280f; // Дуже швидкі рибки
        minFishScale = 0.15f;
        maxFishScale = 1.0f;

        // Найскладніші рибки

        // Дуже маленькі надшвидкі рибки (дуже важко зловити)
        availableFish.add(new FishSpawnData("fish_15/", 15, 0.05f, 6, 0.2f, 12, 250f, 0.2f));

        // Великі швидкі рибки (складно зловити, але дають багато очок)
        availableFish.add(new FishSpawnData("fish_14/", 15, 0.05f, 25, 0.1f, 3, 160f, 0.5f));

        // Гігантські рибки (найважчі для з'їдання, але найбільше очок)
        availableFish.add(new FishSpawnData("fish_13/", 15, 0.16f, 35, 0.1f, 1, 120f, 0.8f));
    }

    @Override
    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        // Виграш якщо набрано цільовий результат або з'їдено достатньо рибок
        return currentScore >= targetScore || fishEaten >= 35;
    }

    @Override
    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        // Програш якщо закінчився час і не досягнуто ціль або закінчились життя (завжди програш при отриманні урону)
        return lives < 0 || (timeRemaining <= 0 && currentScore < targetScore);
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
