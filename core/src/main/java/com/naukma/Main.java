package com.naukma;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {
    
    private LevelManager levelManager;
    private BasicLevel currentLevel;
    private MainMenu mainMenu;
    private boolean showingMenu = true;
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        levelManager = new LevelManager();
        mainMenu = new MainMenu();
        
        // За замовчуванням створюємо перший рівень
        currentLevel = levelManager.createLevel(1);
        currentLevel.create();
    }

    @Override
    public void render() {
        // Перевіряємо чи показуємо меню
        if (showingMenu) {
            handleMainMenu();
            return;
        }

        // ESC обробляється в currentLevel (для pause menu)

        // Рендеримо поточний рівень
        if (currentLevel != null) {
            currentLevel.render();
            
            // Перевіряємо чи треба повернутися до головного меню з pause menu
            if (currentLevel instanceof BasicLevel) {
                BasicLevel basicLevel = (BasicLevel) currentLevel;
                if (basicLevel.shouldReturnToMainMenu()) {
                    showingMenu = true;
                    mainMenu.setActive(true);
                    basicLevel.resetReturnToMainMenuFlag();
                    return;
                }
            }
            
            // Перевіряємо умови завершення рівня
            checkLevelCompletion();
        }
    }

    private void handleMainMenu() {
        mainMenu.handleInput();

        if (!mainMenu.isActive()) {
            showingMenu = false;
            
            // Отримуємо обраний рівень з головного меню
            int selectedLevel = mainMenu.getSelectedLevel();
            if (selectedLevel >= 0) {
                // Створюємо новий рівень через поліморфізм
                switchToLevel(selectedLevel + 1); // +1 тому що рівні в меню починаються з 0
            }
        }

        // Рендеринг меню
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        mainMenu.render(batch);
        batch.end();
    }

    private void switchToLevel(int levelNumber) {
        // Очищаємо попередній рівень
        if (currentLevel != null) {
            currentLevel.dispose();
        }
        
        // Створюємо новий рівень через поліморфізм
        currentLevel = levelManager.createLevel(levelNumber);
        currentLevel.create();
    }

    private void checkLevelCompletion() {
        if (currentLevel.isCompleted()) {
            // Можна додати логіку переходу на наступний рівень
            int nextLevelNumber = currentLevel.getLevelNumber() + 1;
            if (nextLevelNumber <= levelManager.getTotalLevels()) {
                switchToLevel(nextLevelNumber);
            } else {
                // Всі рівні завершено, повертаємось до меню
                showingMenu = true;
                mainMenu.setActive(true);
            }
        }
        
        if (currentLevel.isFailed()) {
            // Можна додати логіку рестарту або повернення до меню
            // Поки що просто рестартуємо рівень
            currentLevel.resetGame();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        
        if (currentLevel != null) {
            currentLevel.dispose();
        }
        
        if (mainMenu != null) {
            mainMenu.dispose();
        }
    }

    // Геттери для доступу до поточного стану
    public BasicLevel getCurrentLevel() {
        return currentLevel;
    }
    
    public LevelManager getLevelManager() {
        return levelManager;
    }
    
    public boolean isShowingMenu() {
        return showingMenu;
    }
}
