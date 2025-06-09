package com.naukma;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class ShellBonus extends Bonus {
    private static boolean spawnedThisLevel = false;
    private Texture[] shellFrames;
    private int currentFrame;
    private float animationTimer;
    private float frameDuration;
    private boolean isOpen;
    private boolean hasPearl;
    private float stateTimer;
    private float nextStateChange;
    
    public ShellBonus(int levelNumber) {
        super("shell/frame_00.png", getScaleForLevel(levelNumber), 0f); // Мушля не рухається
        
        // Завантажуємо всі кадри анімації
        shellFrames = new Texture[3];
        shellFrames[0] = new Texture("shell/frame_00.png"); // Закрита
        shellFrames[1] = new Texture("shell/frame_01.png"); // Відкрита з перлиною
        shellFrames[2] = new Texture("shell/frame_02.png"); // Відкрита без перлини
        
        currentFrame = 0;
        animationTimer = 0f;
        frameDuration = 0.5f;
        isOpen = false;
        hasPearl = true;
        stateTimer = 0f;
        nextStateChange = MathUtils.random(2f, 5f); // Зміна стану через 2-5 секунд
    }
    
    private static float getScaleForLevel(int levelNumber) {
        switch (levelNumber) {
            case 1: return 0.27f; // Зменшено в 3 рази
            case 2: return 0.3f;
            case 3: return 0.33f;
            default: return 0.27f;
        }
    }
    
    @Override
    protected void initializePosition() {
        if (worldWidth > 0 && worldHeight > 0) {
            // З'являється на дні екрану з невеликим відступом
            x = MathUtils.random(width, worldWidth - width);
            y = MathUtils.random(50f, 150f); // Біля дна
        } else {
            // Якщо світ ще не ініціалізований, встановлюємо дефолтні значення
            x = MathUtils.random(100f, 800f);
            y = MathUtils.random(50f, 150f);
        }
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        // Оновлюємо стан ракушки
        stateTimer += deltaTime;
        if (stateTimer >= nextStateChange) {
            toggleShellState();
            stateTimer = 0f;
            nextStateChange = MathUtils.random(2f, 5f);
        }
        
        // Оновлюємо анімацію
        updateAnimation(deltaTime);
    }
    
    private void toggleShellState() {
        if (hasPearl) {
            isOpen = !isOpen;
            currentFrame = isOpen ? 1 : 0; // Кадр 1 (з перлиною) або 0 (закрита)
        } else {
            // Якщо перлини немає, ракушка залишається відкритою без перлини
            isOpen = true;
            currentFrame = 2;
        }
    }
    
    private void updateAnimation(float deltaTime) {
        animationTimer += deltaTime;
        // Анімація плавна, без зміни кадрів автоматично - тільки при зміні стану
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (active && !collected) {
            float renderY = y + bobOffset;
            batch.draw(shellFrames[currentFrame], x, renderY, width, height);
        }
    }
    
    @Override
    public boolean checkCollision(float sharkX, float sharkY, float sharkWidth, float sharkHeight) {
        // Колізія можлива тільки коли ракушка відкрита і є перлина
        if (!active || collected || !isOpen || !hasPearl) return false;
        
        return super.checkCollision(sharkX, sharkY, sharkWidth, sharkHeight);
    }
    
    @Override
    public void onCollected(GameHUD gameHUD) {
        if (isOpen && hasPearl) {
            // Додаємо життя замість бонусу до інвентарю
            gameHUD.addLife();
            hasPearl = false;
            currentFrame = 2; // Ракушка без перлини
            
            // Позначаємо що мушля вже з'явилася на цьому рівні
            spawnedThisLevel = true;
        }
    }
    
    @Override
    public boolean canSpawn(int levelNumber) {
        // Тестовий режим - мушля може з'являтися кілька разів
        return true; // Змінено для тестування
    }
    
    public static void resetForNewLevel() {
        spawnedThisLevel = false;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
    
    public boolean hasPearl() {
        return hasPearl;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (shellFrames != null) {
            for (Texture frame : shellFrames) {
                if (frame != null) {
                    frame.dispose();
                }
            }
        }
    }
} 