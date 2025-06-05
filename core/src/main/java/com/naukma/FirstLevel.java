package com.naukma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FirstLevel extends BasicLevel {

    public FirstLevel() {
        super(1, "Sunlight Reef", "Перший рівень - ознайомчий");
    }

    @Override
    protected void initializeLevel() {
        timeLimit = 60f;
        targetScore = 200;
        maxFishCount = 20;
        sharkSpeed = 200f;
        minFishSpeed = 50f;
        maxFishSpeed = 250f;
        minFishScale = 0.1f;
        maxFishScale = 1f;

        // Налаштування рибок для першого рівня
        
        // 5 середніх рибок з швидкістю 150 і розміром 0.6
        availableFish.add(new FishSpawnData("fish_05/", 15, 0.05f, 10, 0.1f, 3, 150f, 0.6f));

        // 2 великі рибки з швидкістю 80 і розміром 0.9 (повільні, великі, але цінні)
        availableFish.add(new FishSpawnData("fish_02/", 15, 0.16f, 20, 0.1f, 2, 80f, 0.9f));

        // дуже маленькі швидкі рибки з швидкістю 200 і розміром 0.2
        availableFish.add(new FishSpawnData("fish_04/", 15, 0.05f, 15, 0.111f, 5, 250f, 0.2f));
    }

    @Override
    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        return timeRemaining <= 0;
    }

    @Override
    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        return false;
    }

    @Override
    protected void updateLevelLogic(float deltaTime, float sharkX, float sharkY) {
        // Базовий рівень не має спеціальної логіки
    }

    @Override
    protected void renderLevelSpecific(SpriteBatch batch) {
        // Базовий рівень не має спеціального рендерингу
    }
}
