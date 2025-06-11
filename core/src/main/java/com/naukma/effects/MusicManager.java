package com.naukma.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

public class MusicManager implements Disposable {

    private Map<String, Music> musicMap;
    private Music currentMusic;
    private String currentMusicKey;
    private float volume = 0.5f;

    public static final String MUSIC_MAIN_MENU = "main_menu_music";
    public static final String MUSIC_LEVEL_1 = "level_1_music";
    public static final String MUSIC_LEVEL_2 = "level_2_music";
    public static final String MUSIC_LEVEL_3 = "level_3_music";
    public static final String MUSIC_GAME_OVER = "game_over_music";
    public static final String MUSIC_VICTORY = "victory_music";
    // Додайте інші ключі для різних рівнів/станів, якщо потрібно

    public MusicManager() {
        musicMap = new HashMap<>();
        // Завантажуйте музику лише за потреби, але для прикладу можна і тут
        // Gdx.app.log("MusicManager", "MusicManager initialized.");
    }

    /**
     * Завантажує музичний файл за заданим ключем.
     * Якщо музика вже завантажена, нічого не робить.
     * @param key Унікальний ключ для музики (наприклад, "main_menu_music")
     * @param filePath Шлях до музичного файлу (наприклад, "audio/main_menu.mp3")
     */
    public void loadMusic(String key, String filePath) {
        if (!musicMap.containsKey(key)) {
            try {
                Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
                music.setVolume(volume);
                music.setLooping(true); // За замовчуванням більшість музики буде зациклена
                musicMap.put(key, music);
                Gdx.app.log("MusicManager", "Loaded music: " + key + " from " + filePath);
            } catch (Exception e) {
                Gdx.app.error("MusicManager", "Failed to load music: " + filePath, e);
            }
        }
    }

    /**
     * Відтворює музику за заданим ключем.
     * Якщо інша музика грає, вона буде зупинена і замінена.
     * @param key Ключ музики, яку потрібно відтворити.
     */
    public void playMusic(String key) {
        if (currentMusicKey != null && currentMusicKey.equals(key)) {
            // Музика вже грає
            if (currentMusic != null && !currentMusic.isPlaying()) {
                currentMusic.play();
                Gdx.app.log("MusicManager", "Resumed music: " + key);
            }
            return;
        }

        stopMusic(); // Зупиняємо поточну музику

        Music musicToPlay = musicMap.get(key);
        if (musicToPlay != null) {
            currentMusic = musicToPlay;
            currentMusicKey = key;
            currentMusic.play();
            Gdx.app.log("MusicManager", "Playing music: " + key);
        } else {
            Gdx.app.error("MusicManager", "Music not found for key: " + key);
        }
    }

    /**
     * Зупиняє поточну відтворювану музику.
     */
    public void stopMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
            Gdx.app.log("MusicManager", "Stopped music: " + currentMusicKey);
        }
        currentMusic = null;
        currentMusicKey = null;
    }

    /**
     * Ставить поточну музику на паузу.
     */
    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
            Gdx.app.log("MusicManager", "Paused music: " + currentMusicKey);
        }
    }

    /**
     * Відновлює відтворення музики, якщо вона була на паузі.
     */
    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
            Gdx.app.log("MusicManager", "Resumed music: " + currentMusicKey);
        }
    }

    /**
     * Перевіряє, чи грає музика за заданим ключем.
     * @param key Ключ музики.
     * @return true, якщо музика грає, інакше false.
     */
    public boolean isPlaying(String key) {
        return currentMusicKey != null && currentMusicKey.equals(key) && currentMusic != null && currentMusic.isPlaying();
    }

    /**
     * Перевіряє, чи музика взагалі грає.
     * @return true, якщо будь-яка музика грає, інакше false.
     */
    public boolean anyMusicPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume)); // Обмежуємо від 0 до 1
        if (currentMusic != null) {
            currentMusic.setVolume(this.volume);
        }
        for (Music music : musicMap.values()) {
            music.setVolume(this.volume);
        }
    }

    public float getVolume() {
        return volume;
    }

    /**
     * Звільняє всі ресурси музики.
     */
    @Override
    public void dispose() {
        for (Music music : musicMap.values()) {
            music.dispose();
        }
        musicMap.clear();
        currentMusic = null;
        currentMusicKey = null;
        Gdx.app.log("MusicManager", "MusicManager disposed.");
    }
}
