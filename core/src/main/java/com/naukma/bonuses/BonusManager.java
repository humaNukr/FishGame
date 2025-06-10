package com.naukma.bonuses;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.naukma.ui.GameHUD;

public class BonusManager {
    private Array<Bonus> activeBonuses;
    private float nextSpawnTimer;
    private float spawnInterval;
    private int currentLevel;
    private float worldWidth, worldHeight;
    private float visibleMinY, visibleMaxY;

    // Spawn chances for different bonus types
    private static final float SHELL_SPAWN_CHANCE = 0.3f;  // 30% шанс
    private static final float STAR_SPAWN_CHANCE = 0.35f;  // 35% шанс
    private static final float CLOCK_SPAWN_CHANCE = 0.35f; // 35% шанс

    // Spawn intervals based on level
    private static final float[] SPAWN_INTERVALS = {15f, 12f, 10f}; // Секунди між спавнами

    public BonusManager(int levelNumber) {
        this.activeBonuses = new Array<>();
        this.currentLevel = levelNumber;
        this.spawnInterval = getSpawnIntervalForLevel(levelNumber);
        this.nextSpawnTimer = MathUtils.random(5f, spawnInterval); // Перший спавн через 5-15 секунд
    }

    private float getSpawnIntervalForLevel(int levelNumber) {
        if (levelNumber <= SPAWN_INTERVALS.length) {
            return SPAWN_INTERVALS[levelNumber - 1];
        }
        return SPAWN_INTERVALS[SPAWN_INTERVALS.length - 1]; // Останній інтервал для високих рівнів
    }

    public void setWorldBounds(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        // Оновлюємо межі для всіх активних бонусів
        for (Bonus bonus : activeBonuses) {
            bonus.setWorldBounds(worldWidth, worldHeight);
        }
    }

    public void setVisibleBounds(float minY, float maxY) {
        this.visibleMinY = minY;
        this.visibleMaxY = maxY;

        // Оновлюємо видимі межі для всіх активних бонусів
        for (Bonus bonus : activeBonuses) {
            bonus.setVisibleBounds(minY, maxY);
        }
    }

    public void update(float deltaTime) {
        // Оновлюємо всі активні бонуси
        for (int i = activeBonuses.size - 1; i >= 0; i--) {
            Bonus bonus = activeBonuses.get(i);
            bonus.update(deltaTime);

            // Видаляємо неактивні бонуси
            if (!bonus.isActive()) {
                bonus.dispose();
                activeBonuses.removeIndex(i);
            }
        }

        // Спавн нових бонусів
        nextSpawnTimer -= deltaTime;
        if (nextSpawnTimer <= 0) {
            trySpawnBonus();
            nextSpawnTimer = spawnInterval + MathUtils.random(-2f, 2f); // Додаємо варіативність
        }
    }

    private void trySpawnBonus() {
        // Рахуємо тільки активні бонуси (ракушки без перлини не рахуються)
        int activeBonusCount = 0;
        for (Bonus bonus : activeBonuses) {
            if (bonus instanceof ShellBonus) {
                ShellBonus shell = (ShellBonus) bonus;
                if (shell.hasPearl()) {
                    activeBonusCount++; // Ракушка з перлиною рахується
                }
                // Ракушка без перлини не рахується
            } else {
                activeBonusCount++; // Інші бонуси завжди рахуються
            }
        }

        // Максимум 3 активні бонуси одночасно
        if (activeBonusCount >= 3) return;

        // Випадково вибираємо тип бонусу
        float random = MathUtils.random();
        Bonus newBonus = null;

        if (random < SHELL_SPAWN_CHANCE) {
            // Спавн мушлі - перевіряємо чи немає вже активної ракушки
            boolean hasActiveShell = false;
            for (Bonus bonus : activeBonuses) {
                if (bonus instanceof ShellBonus) {
                    hasActiveShell = true;
                    break;
                }
            }

            if (!hasActiveShell) {
                ShellBonus testShell = new ShellBonus(currentLevel);
                if (testShell.canSpawn(currentLevel)) {
                    newBonus = testShell;
                } else {
                    testShell.dispose(); // Очищуємо якщо не можна заспавнити
                }
            }
        } else if (random < SHELL_SPAWN_CHANCE + STAR_SPAWN_CHANCE) {
            // Спавн зірочки
            newBonus = new StarBonus(currentLevel);
        } else if (random < SHELL_SPAWN_CHANCE + STAR_SPAWN_CHANCE + CLOCK_SPAWN_CHANCE) {
            // Спавн годинника
            newBonus = new ClockBonus(currentLevel);
        }
        // Якщо random >= всіх шансів, то нічого не спавниться (10% шанс)

        if (newBonus != null) {
            newBonus.setWorldBounds(worldWidth, worldHeight);
            newBonus.setVisibleBounds(visibleMinY, visibleMaxY);
            activeBonuses.add(newBonus);
        }
    }

    public void render(SpriteBatch batch) {
        for (Bonus bonus : activeBonuses) {
            bonus.render(batch);
        }
    }

    public Bonus checkCollisions(float sharkNoseX, float sharkNoseY) {
        for (Bonus bonus : activeBonuses) {
            if (bonus.checkCollision(sharkNoseX, sharkNoseY)) {
                return bonus;
            }
        }
        return null;
    }

    public void collectBonus(Bonus bonus, GameHUD gameHUD) {
        if (bonus != null && activeBonuses.contains(bonus, true)) {
            bonus.onCollected(gameHUD);

            // Для ракушки не викликаємо collect(), щоб вона залишилася на екрані
            if (!(bonus instanceof ShellBonus)) {
                bonus.collect();
            }
        }
    }

    public void reset() {
        // Очищуємо всі активні бонуси
        for (Bonus bonus : activeBonuses) {
            bonus.dispose();
        }
        activeBonuses.clear();

        // Скидаємо таймер спавну (швидший спавн після ресету)
        nextSpawnTimer = MathUtils.random(2f, 5f); // Перший спавн через 2-5 секунд після ресету

        // Скидаємо статичні змінні для мушлі
        ShellBonus.resetForNewLevel();
    }

    public void dispose() {
        for (Bonus bonus : activeBonuses) {
            bonus.dispose();
        }
        activeBonuses.clear();
    }

    // Методи для примусового спавну бонусів (для тестування)
    public void forceSpawnShell() {
        // Перевіряємо чи немає вже активної ракушки
        boolean hasActiveShell = false;
        for (Bonus bonus : activeBonuses) {
            if (bonus instanceof ShellBonus) {
                hasActiveShell = true;
                break;
            }
        }

        if (!hasActiveShell) {
            ShellBonus shell = new ShellBonus(currentLevel);
            if (shell.canSpawn(currentLevel)) {
                shell.setWorldBounds(worldWidth, worldHeight);
                shell.setVisibleBounds(visibleMinY, visibleMaxY);
                activeBonuses.add(shell);
            } else {
                shell.dispose(); // Очищуємо якщо не можна заспавнити
            }
        }
    }

    public void forceSpawnStar() {
        Bonus star = new StarBonus(currentLevel);
        star.setWorldBounds(worldWidth, worldHeight);
        star.setVisibleBounds(visibleMinY, visibleMaxY);
        activeBonuses.add(star);
    }

    public void forceSpawnClock() {
        Bonus clock = new ClockBonus(currentLevel);
        clock.setWorldBounds(worldWidth, worldHeight);
        clock.setVisibleBounds(visibleMinY, visibleMaxY);
        activeBonuses.add(clock);
    }

    // Геттери
    public int getActiveBonusCount() {
        return activeBonuses.size;
    }

    public Array<Bonus> getActiveBonuses() {
        return activeBonuses;
    }
}
