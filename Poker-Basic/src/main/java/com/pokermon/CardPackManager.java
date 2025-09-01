package com.pokermon;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Manages card packs for the poker game, providing dynamic discovery
 * of available card art sets and standardized access to card images.
 */
public class CardPackManager {
    
    public static final String TEXT_SYMBOLS = "TEXT_SYMBOLS";
    public static final String CARDS_RESOURCE_PATH = "Cards";
    
    private static volatile CardPackManager instance;
    private Map<String, String> availableCardPacks;
    
    private CardPackManager() {
        discoverCardPacks();
    }
    
    /**
     * Get singleton instance of CardPackManager.
     */
    public static CardPackManager getInstance() {
        if (instance == null) {
            synchronized (CardPackManager.class) {
                if (instance == null) {
                    instance = new CardPackManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Discover available card packs from the resources directory.
     */
    private void discoverCardPacks() {
        availableCardPacks = new LinkedHashMap<>();
        
        // Always add text symbols option first
        availableCardPacks.put(TEXT_SYMBOLS, "Text + Symbols (Classic)");
        
        try {
            // Try to discover card packs from resources
            Set<String> cardPackNames = getCardPacksFromResources();
            
            for (String packName : cardPackNames) {
                // Convert folder name to display name
                String displayName = formatDisplayName(packName);
                availableCardPacks.put(packName, displayName);
            }
            
        } catch (Exception e) {
            System.err.println("Warning: Could not discover card packs: " + e.getMessage());
            // Add TET as fallback if discovery fails
            if (!availableCardPacks.containsKey("TET")) {
                availableCardPacks.put("TET", "Eternal Tortoise Cards");
            }
        }
        
        // Always ensure CLASSIC is available as a fallback option
        if (!availableCardPacks.containsKey("CLASSIC")) {
            availableCardPacks.put("CLASSIC", "Classic");
        }
    }
    
    /**
     * Get card packs from resources directory, handling both filesystem and JAR scenarios.
     */
    private Set<String> getCardPacksFromResources() {
        Set<String> packNames = new TreeSet<>();
        
        try {
            // First try to get resource as URL to determine if we're in a JAR
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream cardsStream = classLoader.getResourceAsStream(CARDS_RESOURCE_PATH);
            
            if (cardsStream != null) {
                cardsStream.close();
                
                // Try to get the actual resource path
                String resourcePath = classLoader.getResource(CARDS_RESOURCE_PATH).getPath();
                
                if (resourcePath.contains("!")) {
                    // We're in a JAR file
                    packNames.addAll(getCardPacksFromJar(resourcePath));
                } else {
                    // We're in filesystem (development mode)
                    packNames.addAll(getCardPacksFromFilesystem(resourcePath));
                }
            }
        } catch (Exception e) {
            // Fallback: assume TET is available
            packNames.add("TET");
        }
        
        return packNames;
    }
    
    /**
     * Get card packs from JAR file.
     */
    private Set<String> getCardPacksFromJar(String jarResourcePath) {
        Set<String> packNames = new TreeSet<>();
        
        try {
            // Extract JAR file path from resource path
            String jarPath = jarResourcePath.substring(5, jarResourcePath.indexOf("!"));
            
            try (JarFile jarFile = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    
                    // Look for directories under Cards/
                    if (name.startsWith(CARDS_RESOURCE_PATH + "/") && 
                        name.length() > CARDS_RESOURCE_PATH.length() + 1 &&
                        name.endsWith("/")) {
                        
                        String packName = name.substring(CARDS_RESOURCE_PATH.length() + 1, name.length() - 1);
                        if (!packName.contains("/")) { // Only direct subdirectories
                            packNames.add(packName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading JAR: " + e.getMessage());
        }
        
        return packNames;
    }
    
    /**
     * Get card packs from filesystem.
     */
    private Set<String> getCardPacksFromFilesystem(String resourcePath) {
        Set<String> packNames = new TreeSet<>();
        
        try {
            File cardsDir = new File(resourcePath);
            if (cardsDir.exists() && cardsDir.isDirectory()) {
                File[] subdirs = cardsDir.listFiles(File::isDirectory);
                if (subdirs != null) {
                    for (File subdir : subdirs) {
                        packNames.add(subdir.getName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading filesystem: " + e.getMessage());
        }
        
        return packNames;
    }
    
    /**
     * Format pack folder name into display name.
     */
    private String formatDisplayName(String packName) {
        switch (packName.toUpperCase()) {
            case "TET":
                return "Eternal Tortoise Cards";
            case "CLASSIC":
                return "Classic";
            default:
                // Convert camelCase or snake_case to Title Case
                String result = packName.replaceAll("([a-z])([A-Z])", "$1 $2")
                              .replaceAll("_", " ")
                              .toLowerCase();
                              
                // Capitalize first letter of each word
                StringBuilder titleCase = new StringBuilder();
                boolean capitalizeNext = true;
                for (char c : result.toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        capitalizeNext = true;
                        titleCase.append(c);
                    } else if (capitalizeNext) {
                        titleCase.append(Character.toUpperCase(c));
                        capitalizeNext = false;
                    } else {
                        titleCase.append(c);
                    }
                }
                return titleCase.toString();
        }
    }
    
    /**
     * Get all available card packs as a map of pack name to display name.
     */
    public Map<String, String> getAvailableCardPacks() {
        return new LinkedHashMap<>(availableCardPacks);
    }
    
    /**
     * Get the display name for a card pack.
     */
    public String getDisplayName(String packName) {
        return availableCardPacks.getOrDefault(packName, packName);
    }
    
    /**
     * Check if a card pack is available.
     */
    public boolean isCardPackAvailable(String packName) {
        return availableCardPacks.containsKey(packName);
    }
    
    /**
     * Get the resource path for a card image.
     */
    public String getCardImagePath(String packName, String rankName, String suitName) {
        if (TEXT_SYMBOLS.equals(packName)) {
            return null; // Indicates to use text symbols instead
        }
        
        return CARDS_RESOURCE_PATH + "/" + packName + "/" + rankName + " of " + suitName + ".jpg";
    }
    
    /**
     * Get the resource path for a card back image.
     */
    public String getCardBackImagePath(String packName) {
        if (TEXT_SYMBOLS.equals(packName)) {
            return null; // Indicates to use text symbols instead
        }
        
        return CARDS_RESOURCE_PATH + "/" + packName + "/card_back.jpg";
    }
    
    /**
     * Refresh the list of available card packs.
     */
    public void refresh() {
        discoverCardPacks();
    }
}