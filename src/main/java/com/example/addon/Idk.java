package com.bot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BotMenu extends JFrame {
    private Main main;
    private JToggleButton wtapToggle;
    private JToggleButton triggerToggle;
    private JLabel statusLabel;
    
    public BotMenu(Main main) {
        this.main = main;
        
        setTitle("Bot Menu - 1.21.1");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel titleLabel = new JLabel("PvP Bot Controls");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        
        // W-Tap Toggle
        wtapToggle = new JToggleButton("W-Tap: OFF");
        wtapToggle.setFont(new Font("Arial", Font.PLAIN, 12));
        wtapToggle.addActionListener(e -> toggleWTap());
        panel.add(wtapToggle);
        panel.add(Box.createVerticalStrut(8));
        
        // Trigger Toggle
        triggerToggle = new JToggleButton("Trigger: OFF");
        triggerToggle.setFont(new Font("Arial", Font.PLAIN, 12));
        triggerToggle.addActionListener(e -> toggleTrigger());
        panel.add(triggerToggle);
        panel.add(Box.createVerticalStrut(10));
        
        // Status
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GREEN);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));
        
        // Close button
        JButton closeBtn = new JButton("Close (RIGHT_SHIFT)");
        closeBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        closeBtn.addActionListener(e -> setVisible(false));
        panel.add(closeBtn);
        
        add(panel);
    }
    
    private void toggleWTap() {
        boolean enabled = wtapToggle.isSelected();
        main.getWTapBot().setEnabled(enabled);
        wtapToggle.setText("W-Tap: " + (enabled ? "ON" : "OFF"));
        updateStatus();
    }
    
    private void toggleTrigger() {
        boolean enabled = triggerToggle.isSelected();
        main.getTriggerBot().setEnabled(enabled);
        triggerToggle.setText("Trigger: " + (enabled ? "ON" : "OFF"));
        updateStatus();
    }
    
    private void updateStatus() {
        boolean wtapOn = wtapToggle.isSelected();
        boolean triggerOn = triggerToggle.isSelected();
        
        if (wtapOn && triggerOn) {
            statusLabel.setText("Status: Both Active");
            statusLabel.setForeground(new Color(255, 100, 0));
        } else if (wtapOn || triggerOn) {
            statusLabel.setText("Status: Active");
            statusLabel.setForeground(Color.GREEN);
        } else {
            statusLabel.setText("Status: Idle");
            statusLabel.setForeground(Color.GRAY);
        }
    }
}
