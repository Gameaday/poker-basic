# Pokermon Audio Assets

## Audio System Overview

The Pokermon audio system supports both background music and sound effects to create an engaging gaming experience.

## Music Tracks

### Background Music (android/src/main/assets/audio/music/)
- `main_theme.mp3` - Main menu background music
- `classic_game.mp3` - Classic poker game music
- `adventure_mode.mp3` - Adventure mode background music
- `safari_mode.mp3` - Safari mode background music
- `ironman_mode.mp3` - Ironman mode background music
- `victory_theme.mp3` - Victory celebration music
- `battle_music.mp3` - Monster battle music
- `ambient_calm.mp3` - Calm background music

### Music Specifications
- **Format**: MP3, OGG Vorbis (recommended)
- **Quality**: 128-192 kbps for background music
- **Length**: 2-4 minutes for looping tracks
- **Fade**: Smooth loop points for continuous playback

## Sound Effects

### Sound Effects (android/src/main/res/raw/)
- `card_deal.wav` - Card dealing sound
- `card_flip.wav` - Card flip sound
- `button_click.wav` - UI button click
- `chip_place.wav` - Betting chip placement
- `victory.wav` - Game win sound
- `defeat.wav` - Game loss sound
- `achievement.wav` - Achievement unlocked
- `monster_roar.wav` - Monster battle cry
- `coin_collect.wav` - Collecting rewards
- `menu_nav.wav` - Menu navigation

### Sound Effect Specifications
- **Format**: WAV, OGG (uncompressed preferred for low latency)
- **Quality**: 22-44 kHz sample rate
- **Length**: 0.1-2 seconds for most effects
- **Volume**: Consistent levels across all effects

## Audio Implementation Features

### Music System
- **Dynamic Music**: Changes based on game mode and state
- **Fade Transitions**: Smooth transitions between tracks
- **Loop Management**: Seamless looping for background tracks
- **Volume Control**: User-adjustable music volume
- **Memory Efficient**: Streaming for large music files

### Sound Effects System
- **Low Latency**: Quick response for UI interactions
- **Caching**: Preloaded essential sounds for performance
- **Multiple Streams**: Support for overlapping sound effects
- **Volume Control**: Independent SFX volume control
- **Priority System**: Important sounds override less critical ones

## Placeholder Audio

Until professional audio is created:
1. **Synthesized Tones**: Simple beeps and tones for UI feedback
2. **Creative Commons**: Free audio with proper attribution
3. **Procedural Audio**: Generated sounds for consistency
4. **Minimal Library**: Essential sounds only to reduce app size

## Audio Guidelines

1. **File Size**: Keep individual files under 500KB when possible
2. **Consistency**: Maintain consistent volume levels
3. **Quality**: Balance quality with file size for mobile
4. **Accessibility**: Ensure audio enhances rather than distracts
5. **Performance**: Optimize for low-end Android devices