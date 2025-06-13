package com.naukma.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class OctopusBoss {
    private Texture bossTexture;
    private float x, y, width, height;
    private int health = 5;
    private int maxHealth = 5;
    private boolean vulnerable = false;
    private float vulnerableTimer = 0f;
    private float vulnerableDuration = 1.2f;
    private boolean tentacleActive = false;
    private Rectangle tentacleRect;

    public OctopusBoss() {
        bossTexture = new Texture(Gdx.files.internal("octopus.png"));
        width = 220;
        height = 220;
        x = Gdx.graphics.getWidth() - width - 80;
        y = Gdx.graphics.getHeight() / 2 - height / 2;
        tentacleRect = new Rectangle(x - 80, y + height / 2 - 30, 80, 60);
    }

    public void update(float deltaTime) {
        // Логіка атаки: періодично активується щупальце і вразливість
        if (!vulnerable) {
            vulnerableTimer += deltaTime;
            if (vulnerableTimer > 2.5f) {
                vulnerable = true;
                tentacleActive = true;
                vulnerableTimer = 0f;
            }
        } else {
            vulnerableTimer += deltaTime;
            if (vulnerableTimer > vulnerableDuration) {
                vulnerable = false;
                tentacleActive = false;
                vulnerableTimer = 0f;
            }
        }
        // Оновити позицію щупальця (можна додати анімацію)
        tentacleRect.setPosition(x - 80, y + height / 2 - 30);
    }

    public void render(SpriteBatch batch) {
        batch.draw(bossTexture, x, y, width, height);
        // Візуалізація щупальця
        if (tentacleActive) {
            batch.setColor(1f, 0.7f, 0.7f, 1f);
            batch.draw(bossTexture, tentacleRect.x, tentacleRect.y, tentacleRect.width, tentacleRect.height);
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    public boolean isVulnerable() { return vulnerable; }
    public void takeDamage() { if (vulnerable) health--; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isTentacleActive() { return tentacleActive; }
    public Rectangle getTentacleRect() { return tentacleRect; }
    public void deactivateTentacle() { tentacleActive = false; }
    public boolean isOnSameLine(float sharkY, float sharkHeight) {
        float tentacleY = tentacleRect.y;
        float tentacleH = tentacleRect.height;
        return (sharkY + sharkHeight > tentacleY) && (sharkY < tentacleY + tentacleH);
    }

    // Створює чорнильну кулю, яка летить у напрямку акули
    public OctopusInk shootInk(float targetY) {
        float inkY = y + height / 2;
        return new OctopusInk(x, inkY, targetY);
    }
} 