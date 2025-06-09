package com.naukma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FirstLevel extends BasicLevel {

    public FirstLevel() {
        super(1);
    }

    @Override
    protected void initializeLevel() {
        timeLimit = 90f; // Збільшуємо час оскільки треба з'їсти більше риб
        targetScore = -1; // Відключаємо цільовий рахунок - рахунок може бути нескінченно великим
        targetFishCount = 25; // Загалом треба з'їсти 25 риб (20+3+2)
        maxFishCount = 15; // 15 рибок на екрані
        livesCount = 3; // 3 життя на першому рівні
        sharkSpeed = 200f;
        minFishSpeed = 50f;
        maxFishSpeed = 250f;
        minFishScale = 0.1f;
        maxFishScale = 1f;

        // 20+ маленьких риб (fish_04/) - перші що треба з'їсти
        availableFish.add(new FishSpawnData("fish_04/", 15, 0.05f, 15, 0.111f, 25, 250f, 0.2f));
        
        // 3+ середні рибки (fish_05/) - другі що треба з'їсти  
        availableFish.add(new FishSpawnData("fish_05/", 15, 0.05f, 10, 0.1f, 5, 150f, 0.6f));

        // 2+ великі рибки (fish_02/) - останні що треба з'їсти
        availableFish.add(new FishSpawnData("fish_02/", 15, 0.16f, 20, 0.1f, 3, 80f, 0.9f));

    }

    @Override
    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        // Перевіряємо кількість з'їдених риб кожного типу
        int fish04Eaten = getEatenFishCount("fish_04/"); // Перші маленькі рибки
        int fish05Eaten = getEatenFishCount("fish_05/"); // Середні рибки  
        int fish02Eaten = getEatenFishCount("fish_02/"); // Великі рибки
        
        // Перемога тільки якщо з'їли:
        // - 20 перших риб (fish_04/)
        // - 3 середні риби (fish_05/)
        // - 2 великі риби (fish_02/)
        return fish04Eaten >= 20 && fish05Eaten >= 3 && fish02Eaten >= 2;
    }

    @Override
    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        // Програш тільки при втраті життів або закінченні часу без перемоги
        return lives < 0 || timeRemaining <= 0;
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
