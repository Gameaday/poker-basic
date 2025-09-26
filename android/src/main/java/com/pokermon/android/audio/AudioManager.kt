package com.pokermon.android.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Centralized audio management system for Pokermon.
 * Handles music, sound effects, and audio settings.
 * 
 * @author Pokermon Audio System
 * @version 1.0.0
 */
class AudioManager private constructor(
    private val context: Context
) : DefaultLifecycleObserver {
    
    companion object {
        @Volatile
        private var instance: AudioManager? = null
        
        fun getInstance(context: Context): AudioManager {
            return instance ?: synchronized(this) {
                instance ?: AudioManager(context.applicationContext).also { instance = it }
            }
        }
        
        private const val MAX_STREAMS = 10
        private const val DEFAULT_MUSIC_VOLUME = 0.7f
        private const val DEFAULT_SFX_VOLUME = 0.8f
    }
    
    // Audio components
    private var soundPool: SoundPool? = null
    private var musicPlayer: MediaPlayer? = null
    private var currentMusicJob: Job? = null
    
    // Audio caches and state
    private val soundEffectCache = ConcurrentHashMap<SoundEffect, Int>()
    private val loadedSounds = ConcurrentHashMap<Int, Boolean>()
    
    // Audio settings
    private var musicVolume: Float = DEFAULT_MUSIC_VOLUME
    private var sfxVolume: Float = DEFAULT_SFX_VOLUME
    private var isMusicEnabled: Boolean = true
    private var isSfxEnabled: Boolean = true
    private var currentMusic: BackgroundMusic? = null
    
    /**
     * Sound effects available in the game
     */
    enum class SoundEffect(val fileName: String, val description: String) {
        CARD_DEAL("card_deal.wav", "Card dealing sound"),
        CARD_FLIP("card_flip.wav", "Card flip sound"),
        BUTTON_CLICK("button_click.wav", "UI button click"),
        CHIP_PLACE("chip_place.wav", "Betting chip placement"),
        VICTORY("victory.wav", "Game win sound"),
        DEFEAT("defeat.wav", "Game loss sound"),
        ACHIEVEMENT_UNLOCK("achievement.wav", "Achievement unlocked"),
        MONSTER_ROAR("monster_roar.wav", "Monster battle cry"),
        COIN_COLLECT("coin_collect.wav", "Collecting rewards"),
        MENU_NAVIGATE("menu_nav.wav", "Menu navigation")
    }
    
    /**
     * Background music tracks for different game states
     */
    enum class BackgroundMusic(val fileName: String, val description: String, val isLooping: Boolean = true) {
        MAIN_THEME("main_theme.mp3", "Main menu theme", true),
        CLASSIC_GAME("classic_game.mp3", "Classic poker game music", true),
        ADVENTURE_MODE("adventure_mode.mp3", "Adventure mode music", true),
        SAFARI_MODE("safari_mode.mp3", "Safari mode music", true),
        IRONMAN_MODE("ironman_mode.mp3", "Ironman mode music", true),
        VICTORY_THEME("victory_theme.mp3", "Victory celebration", false),
        BATTLE_MUSIC("battle_music.mp3", "Monster battle music", true),
        AMBIENT_CALM("ambient_calm.mp3", "Calm background music", true)
    }
    
    /**
     * Initialize audio system
     */
    fun initialize() {
        setupSoundPool()
        preloadEssentialSounds()
    }
    
    /**
     * Setup SoundPool for sound effects
     */
    private fun setupSoundPool() {
        soundPool?.release()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .build()
            
        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            loadedSounds[sampleId] = (status == 0)
        }
    }
    
    /**
     * Preload essential sound effects
     */
    private fun preloadEssentialSounds() {
        CoroutineScope(Dispatchers.IO).launch {
            val essentialSounds = listOf(
                SoundEffect.BUTTON_CLICK,
                SoundEffect.CARD_DEAL,
                SoundEffect.CARD_FLIP,
                SoundEffect.VICTORY,
                SoundEffect.DEFEAT
            )
            
            essentialSounds.forEach { soundEffect ->
                loadSoundEffect(soundEffect)
            }
        }
    }
    
    /**
     * Load a sound effect into memory
     */
    suspend fun loadSoundEffect(soundEffect: SoundEffect): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                soundPool?.let { pool ->
                    val resourceId = getAudioResourceId(soundEffect.fileName)
                    if (resourceId != 0) {
                        val soundId = pool.load(context, resourceId, 1)
                        soundEffectCache[soundEffect] = soundId
                        true
                    } else {
                        false
                    }
                } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * Play a sound effect
     */
    fun playSoundEffect(soundEffect: SoundEffect, volume: Float = sfxVolume) {
        if (!isSfxEnabled) return
        
        soundEffectCache[soundEffect]?.let { soundId ->
            if (loadedSounds[soundId] == true) {
                soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
            }
        }
    }
    
    /**
     * Start playing background music
     */
    fun playBackgroundMusic(music: BackgroundMusic, fadeIn: Boolean = true) {
        if (!isMusicEnabled) return
        
        currentMusicJob?.cancel()
        currentMusicJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                stopBackgroundMusic(fadeOut = fadeIn)
                
                musicPlayer?.release()
                musicPlayer = MediaPlayer().apply {
                    val resourceId = getAudioResourceId(music.fileName)
                    if (resourceId != 0) {
                        setDataSource(context, android.net.Uri.parse("android.resource://${context.packageName}/$resourceId"))
                        isLooping = music.isLooping
                        setVolume(if (fadeIn) 0f else musicVolume, if (fadeIn) 0f else musicVolume)
                        prepareAsync()
                        setOnPreparedListener { player ->
                            player.start()
                            currentMusic = music
                            if (fadeIn) {
                                fadeInMusic(player)
                            }
                        }
                        setOnErrorListener { _, _, _ ->
                            currentMusic = null
                            true
                        }
                    }
                }
            } catch (e: Exception) {
                currentMusic = null
            }
        }
    }
    
    /**
     * Stop background music
     */
    fun stopBackgroundMusic(fadeOut: Boolean = false) {
        musicPlayer?.let { player ->
            if (fadeOut) {
                fadeOutMusic(player, {
                    player.stop()
                    player.release()
                })
            } else {
                player.stop()
                player.release()
            }
        }
        musicPlayer = null
        currentMusic = null
    }
    
    /**
     * Pause background music
     */
    fun pauseBackgroundMusic() {
        musicPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            }
        }
    }
    
    /**
     * Resume background music
     */
    fun resumeBackgroundMusic() {
        if (isMusicEnabled) {
            musicPlayer?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                }
            }
        }
    }
    
    /**
     * Set music volume (0.0 to 1.0)
     */
    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        musicPlayer?.setVolume(musicVolume, musicVolume)
    }
    
    /**
     * Set sound effects volume (0.0 to 1.0)
     */
    fun setSfxVolume(volume: Float) {
        sfxVolume = volume.coerceIn(0f, 1f)
    }
    
    /**
     * Enable or disable music
     */
    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (!enabled) {
            pauseBackgroundMusic()
        } else {
            resumeBackgroundMusic()
        }
    }
    
    /**
     * Enable or disable sound effects
     */
    fun setSfxEnabled(enabled: Boolean) {
        isSfxEnabled = enabled
    }
    
    /**
     * Get current audio settings
     */
    fun getAudioSettings(): AudioSettings {
        return AudioSettings(
            musicVolume = musicVolume,
            sfxVolume = sfxVolume,
            isMusicEnabled = isMusicEnabled,
            isSfxEnabled = isSfxEnabled,
            currentMusic = currentMusic
        )
    }
    
    /**
     * Fade in music over time
     */
    private fun fadeInMusic(player: MediaPlayer, durationMs: Long = 2000) {
        val steps = 20
        val stepTime = durationMs / steps
        val volumeStep = musicVolume / steps
        
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 1..steps) {
                val volume = volumeStep * i
                player.setVolume(volume, volume)
                kotlinx.coroutines.delay(stepTime)
            }
        }
    }
    
    /**
     * Fade out music over time
     */
    private fun fadeOutMusic(player: MediaPlayer, onComplete: () -> Unit = {}, durationMs: Long = 1000) {
        val steps = 10
        val stepTime = durationMs / steps
        val volumeStep = musicVolume / steps
        
        CoroutineScope(Dispatchers.Main).launch {
            for (i in steps downTo 1) {
                val volume = volumeStep * i
                player.setVolume(volume, volume)
                kotlinx.coroutines.delay(stepTime)
            }
            onComplete()
        }
    }
    
    /**
     * Get audio resource ID by filename
     */
    private fun getAudioResourceId(fileName: String): Int {
        val resourceName = fileName.substringBeforeLast('.')
        return context.resources.getIdentifier(resourceName, "raw", context.packageName)
    }
    
    /**
     * Lifecycle callbacks
     */
    override fun onPause(owner: LifecycleOwner) {
        pauseBackgroundMusic()
    }
    
    override fun onResume(owner: LifecycleOwner) {
        resumeBackgroundMusic()
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        cleanup()
    }
    
    /**
     * Clean up audio resources
     */
    private fun cleanup() {
        currentMusicJob?.cancel()
        stopBackgroundMusic()
        soundPool?.release()
        soundPool = null
        soundEffectCache.clear()
        loadedSounds.clear()
    }
}

/**
 * Current audio settings data class
 */
data class AudioSettings(
    val musicVolume: Float,
    val sfxVolume: Float,
    val isMusicEnabled: Boolean,
    val isSfxEnabled: Boolean,
    val currentMusic: AudioManager.BackgroundMusic?
)