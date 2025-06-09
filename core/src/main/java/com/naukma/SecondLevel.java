package com.naukma;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SecondLevel extends BasicLevel {

    public SecondLevel() {
        super(2);
    }

    @Override
    protected void initializeLevel() {
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
            case 0: return 15; // Після 15 перших риб (fish_07/) розблоковується другий тип (fish_10/)
            case 1: return 8;  // Після 8 других риб (fish_10/) розблоковується третій тип (fish_08/)
            case 2: return 4;  // Після 4 третіх риб (fish_08/) - перемога
            default: return 5;
        }
    }

    @Override
    public boolean checkWinCondition(int currentScore, float timeRemaining, int fishEaten) {
        // Перемога тільки коли акула досягла 3-го рівня і з'їла всіх потрібних риб
        int sharkLevel = gameHUD != null ? gameHUD.getSharkLevel() : 1;
        
        if (sharkLevel < 3) {
            return false; // Акула ще не досягла максимального рівня
        }
        
        // Перевіряємо чи всі типи риб з'їдені в достатній кількості
        for (int i = 0; i < availableFish.size; i++) {
            String fishPath = availableFish.get(i).path;
            int requiredCount = getFishUnlockRequirement(i);
            int eatenCount = getEatenFishCount(fishPath);
            
            if (eatenCount < requiredCount) {
                return false; // Якщо не з'їли достатньо цього типу - ще не перемога
            }
        }
        
        return true; // Акула 3-го рівня і всі типи риб з'їдені в достатній кількості
    }

    @Override
    public boolean checkLoseCondition(int currentScore, float timeRemaining, int lives) {
        // Програш тільки при втраті життів або закінченні часу без перемоги
        return lives < 0 || timeRemaining <= 0;
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
