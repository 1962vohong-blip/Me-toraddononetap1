package com.example.addon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SpearGodOntap extends JFrame {
    private boolean isMenuVisible = false;

    public SpearGodOntap() {
        setTitle("Spear God Menu - Independent");
        setSize(350, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 150));

        JLabel titleLabel = new JLabel("=== SPEAR GOD MENU ===", JLabel.CENTER);
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JCheckBox spearToggle = new JCheckBox("Bật One Tap Spear");
        spearToggle.setForeground(Color.WHITE);
        spearToggle.setFont(new Font("Arial", Font.BOLD, 14));
        spearToggle.setOpaque(false);

        JLabel rangeLabel = new JLabel("• Tầm đánh: 20 Blocks");
        rangeLabel.setForeground(Color.LIGHT_GRAY);
        rangeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel damageLabel = new JLabel("• Sát thương: 100 Dame");
        damageLabel.setForeground(Color.LIGHT_GRAY);
        damageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton exitBtn = new JButton("Thoát ứng dụng");
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setBackground(new Color(220, 53, 69));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.addActionListener(e -> System.exit(0));

        contentPanel.add(spearToggle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(rangeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        contentPanel.add(damageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(exitBtn);

        add(contentPanel, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    isMenuVisible = !isMenuVisible;
                    setVisible(isMenuVisible);
                    if (isMenuVisible) {
                        toFront();
                        repaint();
                    }
                }
            }
        });
        
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SpearGodOntap app = new SpearGodOntap();
            app.setVisible(false);
            JOptionPane.showMessageDialog(null, 
                "Ứng dụng đã sẵn sàng!\nHãy bấm phím [ P ] trên bàn phím để bật/tắt Menu.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
