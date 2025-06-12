package com.naukma.levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ThirdLevel extends BasicLevel {

    public ThirdLevel() {
        super(3);
    }

    @Override
    protected void initializeLevel() {
        availableFish.clear();
        timeLimit = 120f; // Більше часу для складного рівня
        targetScore = -1; // Без цільового рахунку
        targetFishCount = -1; // Не показуємо цільову кількість риб в HUD
        maxFishCount = 15; // 15 рибок на екрані
        livesCount = 0; // 0 життів на третьому рівні (найскладніший)
        sharkSpeed = 250f; // Найшвидша акула
        minFishSpeed = 120f;
        maxFishSpeed = 280f; // Дуже швидкі рибки
        minFishScale = 0.15f;
        maxFishScale = 1.0f;

        // Перші дуже маленькі надшвидкі рибки - треба з'їсти 20
        availableFish.add(new FishSpawnData("fish_15/", 15, 0.05f, 6, 0.2f, 25, 250f, 0.2f));

        // Великі швидкі рибки - треба з'їсти 8
        availableFish.add(new FishSpawnData("fish_14/", 15, 0.05f, 25, 0.1f, 10, 160f, 0.5f));

        // Гігантські рибки - треба з'їсти 4
        availableFish.add(new FishSpawnData("fish_13/", 15, 0.16f, 35, 0.1f, 6, 120f, 0.8f));
    }

    @Override
    protected int getFishUnlockRequirement(int fishTypeIndex) {
        // Переозначаємо вимоги для третього рівня
        switch (fishTypeIndex) {
            case 0:
                return 2; // Після 20 перших риб (fish_15/) розблоковується другий тип (fish_14/)
            case 1:
                return 2;  // Після 8 других риб (fish_14/) розблоковується третій тип (fish_13/)
            case 2:
                return 2;  // Після 4 третіх риб (fish_13/) - перемога
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
