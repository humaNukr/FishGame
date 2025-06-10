package com.naukma.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class ScrollingBackground {

    public ScrollingBackground(String texturePath) {
        backgroundTexture = new Texture(Gdx.files.internal(texturePath));

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Створюємо камеру з розмірами екрану
        camera = new OrthographicCamera(screenWidth, screenHeight);

        // Розміри світу - тільки по вертикалі
        worldWidth = screenWidth; // Ширина світу = ширина екрану
        worldHeight = screenHeight * WORLD_SCALE; // Висота збільшена

        // Початкова позиція камери (центр по вертикалі, зафіксована по горизонталі)
        camera.position.set(worldWidth / 2f, worldHeight / 2f, 0);
        camera.update();
    }

    public void updateCamera(float targetX, float targetY, float targetWidth, float targetHeight) {
        // Центруємо камеру тільки по вертикалі
        float targetCenterY = targetY + targetHeight / 2f;

        // Обмежуємо камеру межами світу тільки по вертикалі
        float halfCameraHeight = camera.viewportHeight / 2f;

        // X завжди фіксований по центру
        float newCameraX = worldWidth / 2f;
        // Y слідкує за ціллю з обмеженнями
        float newCameraY = MathUtils.clamp(targetCenterY, halfCameraHeight, worldHeight - halfCameraHeight);

        camera.position.set(newCameraX, newCameraY, 0);
        camera.update();
    }

    public void render(SpriteBatch batch) {
        // Встановлюємо проекційну матрицю для batch
        batch.setProjectionMatrix(camera.combined);

        // Малюємо один великий фон, що покриває весь світ
        batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
    }

    // Перетворення світових координат в екранні
    public float worldToScreenX(float worldX) {
        return worldX - (camera.position.x - camera.viewportWidth / 2f);
    }

    public float worldToScreenY(float worldY) {
        return worldY - (camera.position.y - camera.viewportHeight / 2f);
    }

    // Перетворення екранних координат у світові
    public float screenToWorldX(float screenX) {
        return screenX + (camera.position.x - camera.viewportWidth / 2f);
    }

    public float screenToWorldY(float screenY) {
        return screenY + (camera.position.y - camera.viewportHeight / 2f);
    }

    // Перевірка, чи знаходиться об'єкт в видимій області
    public boolean isInView(float worldX, float worldY, float width, float height) {
        float cameraLeft = camera.position.x - camera.viewportWidth / 2f;
        float cameraRight = camera.position.x + camera.viewportWidth / 2f;
        float cameraBottom = camera.position.y - camera.viewportHeight / 2f;
        float cameraTop = camera.position.y + camera.viewportHeight / 2f;

        return worldX + width >= cameraLeft &&
            worldX <= cameraRight &&
            worldY + height >= cameraBottom &&
            worldY <= cameraTop;
    }

    // Геттери
    public float getWorldWidth() { return worldWidth; }
    public float getWorldHeight() { return worldHeight; }
    public float getCameraX() { return camera.position.x - camera.viewportWidth / 2f; }
    public float getCameraY() { return camera.position.y - camera.viewportHeight / 2f; }
    public OrthographicCamera getCamera() { return camera; }

    public void dispose() {
        backgroundTexture.dispose();
    }

    private Texture backgroundTexture;
    private OrthographicCamera camera;
    private float worldWidth, worldHeight;


    private static final float WORLD_SCALE = 1.5f;
}
