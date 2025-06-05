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
        levels.add(new FirstLevel());
        // levels.add(new Level2());
        // levels.add(new Level3());
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

    public BasicLevel getCurrentLevel() {
        return currentLevel;
    }

    public Array<BasicLevel> getAllLevels() {
        return levels;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public boolean hasNextLevel() {
        return currentLevelIndex < levels.size - 1;
    }
}

