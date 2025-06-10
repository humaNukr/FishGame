package com.naukma.levels;

import com.badlogic.gdx.utils.Array;

public class LevelManager {


    public LevelManager() {
        levels = new Array<>();
        currentLevelIndex = 0;
        initializeLevels();
    }

    private void initializeLevels() {
        levels.add(new FirstLevel());   // Легкий рівень
        levels.add(new SecondLevel());  // Середній рівень
        levels.add(new ThirdLevel());   // Складний рівень
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

    private Array<BasicLevel> levels;
    private int currentLevelIndex;
    private BasicLevel currentLevel;
}

