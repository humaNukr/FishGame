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
        targetFishCount = -1; // Не показуємо цільову кількість риб в HUD
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
    protected int getFishUnlockRequirement(int fishTypeIndex) {
        // Переозначаємо вимоги для першого рівня
        switch (fishTypeIndex) {
            case 0: return 5; // Після 20 перших риб (fish_04/) розблоковується другий тип (fish_05/)
            case 1: return 3;  // Після 3 других риб (fish_05/) розблоковується третій тип (fish_02/)
            case 2: return 2;  // Після 2 третіх риб (fish_02/) - перемога
            default: return 5;
        }
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
