package com.bot;

import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class TriggerBot implements NativeMouseListener {
    private Robot robot;
    private boolean enabled = false;
    private static final int CLICK_DELAY = 50;
    
    public TriggerBot() {
        try {
            this.robot = new Robot();
            GlobalScreen.addNativeMouseListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        System.out.println("Trigger: " + (enabled ? "ON" : "OFF"));
    }
    
    private boolean detectTarget() {
        try {
            Rectangle screenRect = new Rectangle(
                Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 60,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 60,
                120, 120
            );
            
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            int[] pixels = new int[screenshot.getWidth() * screenshot.getHeight()];
            screenshot.getRGB(0, 0, screenshot.getWidth(), screenshot.getHeight(), pixels, 0, screenshot.getWidth());
            
            for (int pixel : pixels) {
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                
                if (r > 180 && g < 80 && b < 80) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        if (enabled && detectTarget()) {
            try {
                robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                Thread.sleep(CLICK_DELAY);
                robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {}
    @Override
    public void nativeMousePressed(NativeMouseEvent e) {}
    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {}
    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {}
}
