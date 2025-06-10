package com.naukma.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class SwimmingFish extends Entity {

    protected float scale;

    public SwimmingFish(String framesPath, int framesCount, boolean isLookingLeft,
                        float speed, float scale, float frameDuration) {
        super(0, 0); // Початкові координати, які будуть змінені в respawn()
        this.isLookingLeft = isLookingLeft;
        this.speed = speed;
        this.frameDuration = frameDuration;
        this.frameCount = framesCount;
        this.scale = scale;
        frames = new Array<>();

        for (int i = 0; i < framesCount; i++) {
            if(i < 10)
                frames.add(new Texture(Gdx.files.internal(framesPath + "frame_0" + i + ".png")));
            else {
                frames.add(new Texture(Gdx.files.internal(framesPath + "frame_" + i + ".png")));
            }
        }
        this.width = frames.get(0).getWidth() * scale;
        this.height = frames.get(0).getHeight() * scale;

        // Зберігаємо інформацію про тип рибки
        this.fishType = framesPath;

        // Отримуємо розміри світу
        worldWidth = Gdx.graphics.getWidth();
        worldHeight = Gdx.graphics.getHeight() * 3.0f;
        
        // За замовчуванням видима область = світ
        visibleMinY = 0;
        visibleMaxY = worldHeight;

        isActive = true;
    }

    // Метод для встановлення розмірів світу
    public void setWorldBounds(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        respawn();
    }
    
    // Новий метод для встановлення видимих меж (без HUD)
    public void setVisibleBounds(float minY, float maxY) {
        this.visibleMinY = minY;
        this.visibleMaxY = maxY;
    }

    public void respawn() {
        movingRight = MathUtils.randomBoolean();

        // Спавн тільки у видимій області по Y (без HUD)
        y = MathUtils.random(visibleMinY + height, visibleMaxY - height);

        // Спавн тільки справа або зліва за межами видимого поля
        float screenWidth = Gdx.graphics.getWidth();
        if (movingRight) {
            // Спавн зліва за межами екрану
            x = -width * 2;
        } else {
            // Спавн справа за межами екрану
            x = screenWidth + width * 2;
        }

        rotation = 0;
        isActive = true;
        targetY = y;
        yChangeTimer = MathUtils.random(0, Y_CHANGE_INTERVAL);
    }
    
    // Метод для зміни типу рибки (коли вона виходить за екран)
    public void changeType(String newFishType, int frameCount, float newSpeed, float newScale, float newFrameDuration) {
        // Зберігаємо старі кадри для очищення
        Array<Texture> oldFrames = new Array<>();
        for (Texture frame : frames) {
            oldFrames.add(frame);
        }
        
        // Завантажуємо нові кадри
        frames.clear();
        for (int i = 0; i < frameCount; i++) {
            if(i < 10)
                frames.add(new Texture(Gdx.files.internal(newFishType + "frame_0" + i + ".png")));
            else {
                frames.add(new Texture(Gdx.files.internal(newFishType + "frame_" + i + ".png")));
            }
        }
        
        // Оновлюємо параметри
        this.fishType = newFishType;
        this.speed = newSpeed;
        this.frameDuration = newFrameDuration;
        this.frameCount = frameCount;
        
        // Оновлюємо розміри
        this.width = frames.get(0).getWidth() * newScale;
        this.height = frames.get(0).getHeight() * newScale;
        
        // Очищуємо старі текстури
        for (Texture frame : oldFrames) {
            frame.dispose();
        }
        
        // Респавнимо з новими параметрами
        respawn();
    }

    public void update(float deltaTime) {
        if (!isActive) return;

        stateTime += deltaTime;
        yChangeTimer += deltaTime;

        if (yChangeTimer >= Y_CHANGE_INTERVAL) {
            // Обмежуємо цільовий Y видимими межами
            targetY = MathUtils.random(visibleMinY + height, visibleMaxY - height);
            yChangeTimer = 0;
        }

        float yDiff = targetY - y;
        if (Math.abs(yDiff) > 1) {
            y += Math.signum(yDiff) * speed * 0.3f * deltaTime;
        }

        if (movingRight) {
            x += speed * deltaTime;
        } else {
            x -= speed * deltaTime;
        }

        if ((movingRight && x > worldWidth + width * 2) ||
            (!movingRight && x < -width * 2)) {
            isActive = false;
        }
    }

    public void renderAt(SpriteBatch batch, float worldX, float worldY) {
        if (!isActive) return;

        int frameIndex = (int)(stateTime / frameDuration) % frames.size;
        Texture currentFrame = frames.get(frameIndex);


        boolean flipX;
        if (isLookingLeft) {
            flipX = movingRight;
        } else {
            flipX = !movingRight;
        }
        boolean flipY = false;

        batch.draw(currentFrame,
            worldX, worldY,
            width / 2, height / 2,
            width, height,
            1, 1,
            rotation,
            0, 0,
            currentFrame.getWidth(), currentFrame.getHeight(),
            flipX, flipY);
    }

    public void dispose() {
        for (Texture frame : frames) {
            frame.dispose();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setFishType(String fishType) {
        this.fishType = fishType;
    }

    public String getFishType() {
        return fishType;
    }

    public Texture getFrame(int index) {
        return frames.get(index);
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getScale() { return scale; }
    public void setScale(float scale) {
        this.scale = scale;
        if (frames != null && frames.size > 0) {
            this.width = frames.get(0).getWidth() * scale;
            this.height = frames.get(0).getHeight() * scale;
        }
    }
    public void setActive(boolean active) { this.isActive = active; }

    private final Array<Texture> frames;
    private float stateTime;
    private float frameDuration = 0.10f;
    private float rotation;
    private float speed;
    private int frameCount;
    private boolean isActive;
    private final boolean isLookingLeft;
    private boolean movingRight;
    private float targetY;
    private float yChangeTimer;
    private static final float Y_CHANGE_INTERVAL = 3f;

    private float worldWidth, worldHeight;
    private float visibleMinY, visibleMaxY; // Видимі межі для спавну
    private String fishType; // Тип рибки (шлях до папки)
}
