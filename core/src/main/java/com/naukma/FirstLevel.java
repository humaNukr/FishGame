package com.naukma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FirstLevel extends BasicLevel {

    public FirstLevel() {
        super(1);
    }

    @Override
    protected void initializeLevel() {
        timeLimit = 60f;
        targetScore = 200;
        targetFishCount = 15; // Ціль для першого рівня
        maxFishCount = 15; // 15 рибок на екрані
        livesCount = 3; // 3 життя на першому рівні
        sharkSpeed = 200f;
        minFishSpeed = 50f;
        maxFishSpeed = 250f;
        minFishScale = 0.1f;
        maxFishScale = 1f;

        availableFish.add(new FishSpawnData("fish_04/", 15, 0.05f, 15, 0.111f, 5, 250f, 0.2f));
        // 5 середніх рибок з швидкістю 150 і розміром 0.6
        availableFish.add(new FishSpawnData("fish_05/", 15, 0.05f, 10, 0.1f, 3, 150f, 0.6f));

        // 2 великі рибки з швидкістю 80 і розміром 0.9 (повільні, великі, але цінні)
        availableFish.add(new FishSpawnData("fish_02/", 15, 0.16f, 20, 0.1f, 2, 80f, 0.9f));


    }

    @Override
    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        return currentScore >= targetScore;
    }

    @Override
    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        return lives < 0;
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
