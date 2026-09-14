package com.bot;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main implements NativeKeyListener {
    private BotMenu menu;
    private WTapBot wtapBot;
    private TriggerBot triggerBot;
    private boolean menuOpen = false;
    
    public Main() {
        try {
            this.wtapBot = new WTapBot();
            this.triggerBot = new TriggerBot();
            this.menu = new BotMenu(this);
            
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void start() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            System.err.println("Failed to register: " + e.getMessage());
            System.exit(1);
        }
        
        GlobalScreen.addNativeKeyListener(this);
        System.out.println("Bot loaded. RIGHT_SHIFT to open menu.");
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // RIGHT_SHIFT opens/closes menu
        if (e.getKeyCode() == NativeKeyEvent.VC_SHIFT_R) {
            menuOpen = !menuOpen;
            if (menuOpen) {
                menu.setVisible(true);
            } else {
                menu.setVisible(false);
            }
        }
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}
    
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}
    
    public WTapBot getWTapBot() {
        return wtapBot;
    }
    
    public TriggerBot getTriggerBot() {
        return triggerBot;
    }
    
    public static void main(String[] args) {
        Main bot = new Main();
        bot.start();
        
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
