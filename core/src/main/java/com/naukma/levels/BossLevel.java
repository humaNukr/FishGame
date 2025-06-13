package com.naukma.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.naukma.ui.GameHUD;

public class BossLevel {
    private Texture sharkTexture;
    private float sharkX, sharkY;
    private float sharkWidth, sharkHeight;
    private float sharkSpeed = 400f;
    private int sharkHealth = 3;
    private int sharkMaxHealth = 3;

    private OctopusBoss boss;
    private Array<BossMinion> minions;
    private GameHUD hud;

    private boolean isGameOver = false;
    private boolean isVictory = false;

    private Array<OctopusInk> inkShots = new Array<>();
    private long lastInkTime = 0;
    private long lastMinionTime = 0;

    private Texture backgroundTexture;

    public BossLevel() {
        backgroundTexture = new Texture(Gdx.files.internal("background_boss.png"));
        sharkTexture = new Texture(Gdx.files.internal("shark\\frame_00.png"));
        sharkWidth = 120;
        sharkHeight = 60;
        sharkX = 80;
        sharkY = Gdx.graphics.getHeight() / 2 - sharkHeight / 2;
        boss = new OctopusBoss();
        minions = new Array<>();
        hud = new GameHUD();
    }

    public void update(float deltaTime) {
        if (isGameOver || isVictory) return;
        handleInput(deltaTime);
        boss.update(deltaTime);
        for (BossMinion m : minions) m.update(deltaTime);
        for (OctopusInk ink : inkShots) ink.update(deltaTime);
        checkCollisions();
        if (sharkHealth <= 0) isGameOver = true;
        if (boss.getHealth() <= 0) isVictory = true;
        // Спавн чорнильних куль
        if (TimeUtils.nanoTime() - lastInkTime > 1_500_000_000L) {
            inkShots.add(boss.shootInk(sharkY + sharkHeight/2));
            lastInkTime = TimeUtils.nanoTime();
        }
        // Спавн міньйонів
        if (TimeUtils.nanoTime() - lastMinionTime > 3_000_000_000L) {
            minions.add(new BossMinion(MathUtils.random(0, Gdx.graphics.getHeight() - 60)));
            lastMinionTime = TimeUtils.nanoTime();
        }
    }

    private void handleInput(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            sharkY += sharkSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            sharkY -= sharkSpeed * deltaTime;
        }
        sharkY = Math.max(0, Math.min(Gdx.graphics.getHeight() - sharkHeight, sharkY));
        // Атака по Space
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (boss.isVulnerable() && boss.isOnSameLine(sharkY, sharkHeight)) {
                boss.takeDamage();
            }
        }
    }

    private void checkCollisions() {
        Rectangle sharkRect = new Rectangle(sharkX, sharkY, sharkWidth, sharkHeight);
        if (boss.isTentacleActive() && boss.getTentacleRect().overlaps(sharkRect)) {
            sharkHealth--;
            boss.deactivateTentacle();
        }
        for (BossMinion m : minions) {
            if (m.isActive() && m.getRect().overlaps(sharkRect)) {
                sharkHealth = Math.min(sharkMaxHealth, sharkHealth + 1);
                m.setActive(false);
            }
        }
        for (OctopusInk ink : inkShots) {
            if (ink.isActive() && ink.getRect().overlaps(sharkRect)) {
                sharkHealth--;
                ink.setActive(false);
            }
        }
    }

    public void render(SpriteBatch batch) {
        // Фон
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Дзеркальна акула
        batch.draw(sharkTexture, sharkX + sharkWidth, sharkY, -sharkWidth, sharkHeight);
        boss.render(batch);
        for (BossMinion m : minions) if (m.isActive()) m.render(batch);
        for (OctopusInk ink : inkShots) if (ink.isActive()) ink.render(batch);
        hud.renderBossFight(batch, sharkHealth, sharkMaxHealth, boss.getHealth(), boss.getMaxHealth());
    }
} 