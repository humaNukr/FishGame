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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;

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

    private boolean sharkInvulnerable = false;
    private float invulnerableTimer = 0f;
    private float invulnerableDuration = 1.5f;

    private Array<OctopusInk> inkShots = new Array<>();
    private long lastInkTime = 0;
    private long lastMinionTime = 0;
    private long lastTentacleTime = 0;
    private long lastEnergyOrbTime = 0;

    private Array<EnergyOrb> energyOrbs = new Array<>();

    private Texture backgroundTexture;
    private Texture whitePixel;
    private Texture tentacleTexture;

    private Array<TentacleStrike> tentacleStrikes = new Array<>();

    private int orbsCaught = 0;

    // Внутрішній клас для атаки щупальцем
    private static class TentacleStrike {
        float y;
        float timer;
        boolean warning; // миготіння
        boolean active;  // удар
        boolean finished;
        boolean damaged = false;
        float warningDuration = 1.5f;
        float strikeDuration = 3.0f;
        float beamHeight = 120f;
        public TentacleStrike(float y) {
            this.y = y;
            this.timer = 0f;
            this.warning = true;
            this.active = false;
            this.finished = false;
        }
        public void update(float delta) {
            timer += delta;
            if (warning && timer >= warningDuration) {
                warning = false;
                active = true;
                timer = 0f;
            } else if (active && timer >= strikeDuration) {
                active = false;
                finished = true;
            }
        }
        public boolean isBlinking() {
            return warning && ((int)(timer * 8) % 2 == 0);
        }
        public boolean isActive() { return active; }
        public boolean isFinished() { return finished; }
        public float getY() { return y; }
        public float getHeight() { return beamHeight; }
    }

    public BossLevel() {
        backgroundTexture = new Texture(Gdx.files.internal("background_boss.png"));
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
        tentacleTexture = new Texture(Gdx.files.internal("tentacle.png"));
        sharkTexture = new Texture(Gdx.files.internal("shark\\frame_00.png"));
        sharkWidth = Gdx.graphics.getHeight() * 0.07f * 1.975609f;
        sharkHeight = Gdx.graphics.getHeight() * 0.07f; 
        sharkX = 80;
        sharkY = Gdx.graphics.getHeight() / 2 - sharkHeight / 2;
        boss = new OctopusBoss();
        minions = new Array<>();
        hud = new GameHUD();
    }

    public void update(float deltaTime) {
        if (isGameOver || isVictory) return;
        handleInput(deltaTime);

        // Оновлення боса та отримання нових снарядів
        EnergyOrb newOrb = boss.update(deltaTime);
        if (newOrb != null) {
            energyOrbs.add(newOrb);
        }

        for (BossMinion m : minions) m.update(deltaTime);
        for (OctopusInk ink : inkShots) ink.update(deltaTime);
        for (EnergyOrb orb : energyOrbs) orb.update(deltaTime);
        // Tentacle strikes
        for (TentacleStrike t : tentacleStrikes) t.update(deltaTime);
        // Видалити завершені
        for (int i = tentacleStrikes.size - 1; i >= 0; i--) {
            if (tentacleStrikes.get(i).isFinished()) tentacleStrikes.removeIndex(i);
        }
        for (int i = energyOrbs.size - 1; i >= 0; i--) {
            if (!energyOrbs.get(i).isActive()) {
                energyOrbs.get(i).dispose();
                energyOrbs.removeIndex(i);
            }
        }
        checkCollisions();
        if (sharkInvulnerable) {
            invulnerableTimer += deltaTime;
            if (invulnerableTimer >= invulnerableDuration) {
                sharkInvulnerable = false;
            }
        }
        if (sharkHealth <= 0) isGameOver = true;
        if (boss.getHealth() <= 0) isVictory = true;
        // Спавн чорнильних куль
        if (TimeUtils.nanoTime() - lastInkTime > 1_500_000_000L) {
            OctopusInk newInk = boss.shootInk(sharkY + sharkHeight/2);
            if (newInk != null) {
                inkShots.add(newInk);
            }
            lastInkTime = TimeUtils.nanoTime();
        }
        // Спавн міньйонів
        if (TimeUtils.nanoTime() - lastMinionTime > 3_000_000_000L) {
            minions.add(new BossMinion(MathUtils.random(0, Gdx.graphics.getHeight() - 60)));
            lastMinionTime = TimeUtils.nanoTime();
        }
        // Спавн щупальця-променя
        if (TimeUtils.nanoTime() - lastTentacleTime > 4_000_000_000L) {
            float y = MathUtils.random(40, Gdx.graphics.getHeight() - 120);
            tentacleStrikes.add(new TentacleStrike(y));
            lastTentacleTime = TimeUtils.nanoTime();
        }
    }

    private void takeDamage() {
        if (sharkInvulnerable) return;
        sharkHealth--;
        sharkInvulnerable = true;
        invulnerableTimer = 0f;
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
            Rectangle sharkAttackRect = new Rectangle(sharkX + sharkWidth, sharkY, 50, sharkHeight);

            // Відбиття снаряду
            for (EnergyOrb orb : energyOrbs) {
                if (orb.isActive() && !orb.isReflected() && sharkAttackRect.overlaps(orb.getBounds())) {
                    orb.reflect();
                }
            }
        }
    }

    private void checkCollisions() {
        if (sharkInvulnerable) return;

        Rectangle sharkRect = new Rectangle(sharkX, sharkY, sharkWidth, sharkHeight);

        // Перевірка на чорнильні кулі
        for (OctopusInk ink : inkShots) {
            if (ink.isActive() && ink.getRect().overlaps(sharkRect)) {
                takeDamage();
                ink.setActive(false);
                return;
            }
        }
        
        // Перевірка на енергетичні снаряди (ловимо на Space)
        boolean spacePressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        for (EnergyOrb orb : energyOrbs) {
            if (orb.isActive() && !orb.isReflected() && orb.getBounds().overlaps(sharkRect)) {
                if (spacePressed) {
                    orb.setActive(false);
                    orbsCaught++;
                }
            }
            // Перевірка, чи відбитий снаряд влучив у боса
            if (orb.isReflected() && orb.getBounds().overlaps(new Rectangle(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight()))) {
                boss.takeDamage(2); // Відбитий снаряд наносить більше шкоди
                orb.setActive(false);
            }
        }

        // Колізія з променем-щупальцем
        for (TentacleStrike t : tentacleStrikes) {
            if (t.isActive() && !t.damaged) {
                Rectangle tentacleRect = new Rectangle(0, t.getY(), boss.getX(), t.getHeight());
                if (sharkRect.overlaps(tentacleRect)) {
                    // Випадок 1: Акула підходить до променя знизу (її Y < Y променя)
                    // Колізія спрацьовує одразу при торканні.
                    if (sharkY < t.getY()) {
                        takeDamage();
                        t.damaged = true;
                        return;
                    }

                    // Випадок 2: Акула підходить до променя зверху.
                    // Колізія спрацьовує тільки при зануренні на 50% висоти променя.
                    float penetrationDepth = (t.getY() + t.getHeight()) - sharkY;
                    if (penetrationDepth >= t.getHeight() * 0.5f) {
                        takeDamage();
                        t.damaged = true;
                        return;
                    }
                }
            }
        }

        // Перевірка на міньйонів (вони лікують, тому без return)
        for (BossMinion m : minions) {
            if (m.isActive() && m.getRect().overlaps(sharkRect)) {
                sharkHealth = Math.min(sharkMaxHealth, sharkHealth + 1);
                m.setActive(false);
            }
        }
    }

    public void render(SpriteBatch batch) {
        // Фон
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Дзеркальна акула
        boolean isSharkVisible = !sharkInvulnerable || ((int)(invulnerableTimer * 10) % 2 == 0);
        if (isSharkVisible) {
            batch.draw(sharkTexture, sharkX + sharkWidth, sharkY, -sharkWidth, sharkHeight);
        }
        boss.render(batch);
        for (BossMinion m : minions) if (m.isActive()) m.render(batch);
        for (OctopusInk ink : inkShots) if (ink.isActive()) ink.render(batch);
        for (EnergyOrb orb : energyOrbs) orb.render(batch);
        // Рендер щупалець-променів
        for (TentacleStrike t : tentacleStrikes) {
            if ((t.warning && t.isBlinking()) || t.isActive()) {
                float tentacleWidth = tentacleTexture.getWidth();
                float tentacleHeight = tentacleTexture.getHeight();
                float scaleY = t.getHeight() / tentacleHeight;
                float tentacleX = 0; // Початок зліва
                float drawWidth = boss.getX(); // Від лівого краю екрану до лівого краю восьминога
                batch.setColor(0.7f, 0.2f, 1f, t.isActive() ? 0.9f : 0.5f);
                batch.draw(tentacleTexture,
                    tentacleX, t.getY(),
                    drawWidth, tentacleHeight * scaleY
                );
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }
        hud.renderBossFight(batch, sharkHealth, sharkMaxHealth, boss.getHealth(), boss.getMaxHealth());
    
    }
} 