package com.naukma;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.naukma.levels.BasicLevel;
import com.naukma.levels.LevelManager;
import com.naukma.ui.MainMenu;
import com.badlogic.gdx.audio.Music;

public class Main extends ApplicationAdapter {

    private LevelManager levelManager;
    private BasicLevel currentLevel;
    private MainMenu mainMenu;
    private boolean showingMenu = true;
    private SpriteBatch batch;
    private Music backgroundMusic;
    private String currentMusicFile = "";

    @Override
    public void create() {
        batch = new SpriteBatch();
        levelManager = new LevelManager();
        mainMenu = new MainMenu();
        currentLevel = levelManager.createLevel(1);
        currentLevel.create();
        setMusic("main_menu.mp3");
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

                // Перевіряємо Game Over меню
                if (basicLevel.shouldReturnToMainMenuFromGameOver()) {
                    showingMenu = true;
                    mainMenu.setActive(true);
                    basicLevel.resetGameOverFlags();
                    return;
                }

                // Перевіряємо вихід з гри
                if (basicLevel.shouldExitGameFromGameOver()) {
                    Gdx.app.exit();
                    return;
                }

                if (basicLevel.shouldRestartLevel()) {
                    switchToLevel(currentLevel.getLevelNumber());
                    currentLevel.resetRestartFlags();
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
                // Встановлюємо музику для відповідного рівня
                switch (selectedLevel + 1) {
                    case 1:
                        setMusic("background_1.mp3");
                        break;
                    case 2:
                        setMusic("background_2.mp3");
                        break;
                    case 3:
                        setMusic("background_3.mp3");
                        break;
                }
            }
        }

        // Якщо меню активується після Game Over, повертаємо музику головного меню
        if (showingMenu && !isMusicMainMenu()) {
            setMusic("main_menu.mp3");
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

        currentLevel = levelManager.createLevel(levelNumber);
        currentLevel.create();
        // Встановлюємо музику для відповідного рівня
        switch (levelNumber) {
            case 1:
                setMusic("background_1.mp3");
                break;
            case 2:
                setMusic("background_2.mp3");
                break;
            case 3:
                setMusic("background_3.mp3");
                break;
        }
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
                setMusic("main_menu.mp3");
            }
        } else if (currentLevel.isFailed()) {
            // Якщо рівень "провалено" (наприклад, вихід з вікна перемоги), повертаємось до меню
            showingMenu = true;
            mainMenu.setActive(true);
            setMusic("main_menu.mp3");
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

        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
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

    public void setMusic(String musicFile) {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(musicFile));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0f);
        backgroundMusic.play();
        currentMusicFile = musicFile;
    }

    // Додати методи для перемоги та поразки
    public void showGameOverMenu() {
        setMusic("lose.mp3");
    }
    public void showVictoryWindow() {
        setMusic("win.mp3");
    }
    public void pauseMusic() {
        if (backgroundMusic != null) backgroundMusic.pause();
    }
    public void resumeMusic() {
        if (backgroundMusic != null) backgroundMusic.play();
    }

    // Додаю допоміжний метод для перевірки чи грає музика головного меню
    private boolean isMusicMainMenu() {
        return "main_menu.mp3".equals(currentMusicFile);
    }
}
