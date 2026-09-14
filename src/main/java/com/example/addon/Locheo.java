package com.bot;

import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class WTapBot implements NativeMouseListener {
    private Robot robot;
    private boolean enabled = false;
    
    private static final int W_KEY = KeyEvent.VK_W;
    private static final long W_PRESS_DELAY = 50;
    private static final long W_RELEASE_DELAY = 20;
    
    public WTapBot() {
        try {
            this.robot = new Robot();
            GlobalScreen.addNativeMouseListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        System.out.println("W-Tap: " + (enabled ? "ON" : "OFF"));
    }
    
    private void wtap() {
        try {
            robot.keyRelease(W_KEY);
            Thread.sleep(W_RELEASE_DELAY);
            robot.keyPress(W_KEY);
            Thread.sleep(W_PRESS_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        if (enabled && e.getButton() == NativeMouseEvent.BUTTON1) {
            wtap();
        }
    }
    
    @Override
    public void nativeMousePressed(NativeMouseEvent e) {}
    
    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {}
    
    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {}
    
    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {}
}
