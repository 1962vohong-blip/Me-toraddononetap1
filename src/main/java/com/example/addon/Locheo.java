package com.doomclient.modules;

import com.doomclient.DoomClientMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public abstract class Module {
    
    protected MinecraftClient mc = DoomClientMod.mc;
    private String name;
    private String description;
    private Category category;
    private boolean enabled = false;
    private int keybind;
    
    public Module(String name, String description, Category category, int keybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keybind = keybind;
    }
    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKeybind() { return keybind; }
    public void setKeybind(int key) { this.keybind = key; }
    
    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            onEnable();
            if (mc.player != null) {
                mc.player.sendMessage(Text.of("§a[DoomClient] §f" + name + " §a§lON"), true);
            }
        } else {
            onDisable();
            if (mc.player != null) {
                mc.player.sendMessage(Text.of("§c[DoomClient] §f" + name + " §c§lOFF"), true);
            }
        }
    }
    
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) onEnable();
            else onDisable();
        }
    }
    
    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void onTick();
    
    public enum Category {
        COMBAT("§cCombat"),
        MOVEMENT("§bMovement"),
        RENDER("§dRender"),
        PLAYER("§aPlayer"),
        WORLD("§eWorld"),
        MISC("§7Misc");
        
        private final String displayName;
        Category(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }
}
