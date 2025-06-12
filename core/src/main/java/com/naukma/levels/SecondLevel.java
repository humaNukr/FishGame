package com.naukma.levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SecondLevel extends BasicLevel {

    public SecondLevel() {
        super(2);
    }

    @Override
    protected void initializeLevel() {
        availableFish.clear();
        timeLimit = 100f; // Збільшуємо час для нової логіки
        targetScore = -1; // Відключаємо цільовий рахунок
        targetFishCount = -1; // Не показуємо цільову кількість риб в HUD
        maxFishCount = 15; // 15 рибок на екрані
        livesCount = 1; // 1 життя на другому рівні
        sharkSpeed = 220f; // Швидша акула
        minFishSpeed = 80f;
        maxFishSpeed = 200f; // Швидші рибки
        minFishScale = 0.2f;
        maxFishScale = 0.9f;

        // Перші малі швидкі рибки - треба з'їсти 15
        availableFish.add(new FishSpawnData("fish_07/", 15, 0.05f, 12, 0.2f, 20, 250f, 0.2f));

        // Середні рибки - треба з'їсти 8
        availableFish.add(new FishSpawnData("fish_10/", 15, 0.05f, 10, 0.1f, 10, 160f, 0.5f));

        // Великі рибки - треба з'їсти 4
        availableFish.add(new FishSpawnData("fish_08/", 15, 0.05f, 20, 0.5f, 6, 120f, 0.8f));
    }

    @Override
    protected int getFishUnlockRequirement(int fishTypeIndex) {
        // Переозначаємо вимоги для другого рівня
        switch (fishTypeIndex) {
            case 0:
                return 2; // Після 15 перших риб (fish_07/) розблоковується другий тип (fish_10/)
            case 1:
                return 2;  // Після 8 других риб (fish_10/) розблоковується третій тип (fish_08/)
            case 2:
                return 2;  // Після 4 третіх риб (fish_08/) - перемога
            default:
                return 5;
        }
    }

    @Override
    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        // Програш тільки при втраті життів або закінченні часу без перемоги
        return lives < 0 || timeRemaining <= 0;
    }

}
