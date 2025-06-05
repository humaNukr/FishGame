package com.naukma;
import com.badlogic.gdx.utils.Array;

public class LevelManager {
    private Array<BasicLevel> levels;
    private int currentLevelIndex;
    private BasicLevel currentLevel;

    public LevelManager() {
        levels = new Array<>();
        currentLevelIndex = 0;
        initializeLevels();
    }

    private void initializeLevels() {
        // Створюємо об'єкти рівнів через поліморфізм
        levels.add(new FirstLevel());   // Легкий рівень
        levels.add(new SecondLevel());  // Середній рівень  
        levels.add(new ThirdLevel());   // Складний рівень
    }

    public void startLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.size) {
            currentLevelIndex = levelIndex;
            currentLevel = levels.get(levelIndex);
            currentLevel.setCompleted(false);
            currentLevel.setFailed(false);
        }
    }

    public boolean nextLevel() {
        if (currentLevelIndex < levels.size - 1) {
            currentLevelIndex++;
            currentLevel = levels.get(currentLevelIndex);
            currentLevel.setCompleted(false);
            currentLevel.setFailed(false);
            return true;
        }
        return false; // Немає більше рівнів
    }

    public boolean previousLevel() {
        if (currentLevelIndex > 0) {
            currentLevelIndex--;
            currentLevel = levels.get(currentLevelIndex);
            currentLevel.setCompleted(false);
            currentLevel.setFailed(false);
            return true;
        }
        return false; // Немає попередніх рівнів
    }

    public BasicLevel getCurrentLevel() {
        return currentLevel;
    }

    public BasicLevel getLevel(int index) {
        if (index >= 0 && index < levels.size) {
            return levels.get(index);
        }
        return null;
    }

    public Array<BasicLevel> getAllLevels() {
        return levels;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public int getTotalLevels() {
        return levels.size;
    }

    public boolean hasNextLevel() {
        return currentLevelIndex < levels.size - 1;
    }

    public boolean hasPreviousLevel() {
        return currentLevelIndex > 0;
    }

    public boolean isLastLevel() {
        return currentLevelIndex == levels.size - 1;
    }

    public boolean isFirstLevel() {
        return currentLevelIndex == 0;
    }

    // Методи для отримання інформації про прогрес
    public int getCompletedLevelsCount() {
        int count = 0;
        for (BasicLevel level : levels) {
            if (level.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    public boolean allLevelsCompleted() {
        return getCompletedLevelsCount() == levels.size;
    }

    // Метод для створення нового рівня (через поліморфізм)
    public BasicLevel createLevel(int levelNumber) {
        switch (levelNumber) {
            case 1:
                return new FirstLevel();
            case 2:
                return new SecondLevel();
            case 3:
                return new ThirdLevel();
            default:
                return new FirstLevel(); // За замовчуванням перший рівень
        }
    }
}

