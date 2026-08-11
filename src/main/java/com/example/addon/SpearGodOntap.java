import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SpearMenuApp extends JFrame {
    private JPanel menuPanel;
    private boolean isMenuVisible = false;

    public SpearMenuApp() {
        // Cài đặt cửa sổ chính (Trong suốt hoặc ẩn nền nếu muốn làm overlay dạng menu game)
        setTitle("Spear God Menu - Independent");
        setSize(350, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setUndecorated(true); // Bỏ viền cửa sổ giống menu game thuần túy
        setBackground(new Color(0, 0, 0, 150)); // Nền bán trong suốt

        // Tiêu đề Menu
        JLabel titleLabel = new JLabel("=== SPEAR GOD MENU ===", JLabel.CENTER);
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Panel chứa các chức năng
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Trạng thái tính năng
        JCheckBox spearToggle = new JCheckBox("Bật One Tap Spear");
        spearToggle.setForeground(Color.WHITE);
        spearToggle.setFont(new Font("Arial", Font.BOLD, 14));
        spearToggle.setOpaque(false);

        // Thông tin chi tiết
        JLabel rangeLabel = new JLabel("• Tầm đánh: 20 Blocks");
        rangeLabel.setForeground(Color.LIGHT_GRAY);
        rangeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel damageLabel = new JLabel("• Sát thương: 100 Dame");
        damageLabel.setForeground(Color.LIGHT_GRAY);
        damageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Nút thoát ứng dụng
        JButton exitBtn = new JButton("Thoát ứng dụng");
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setBackground(new Color(220, 53, 69));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.addActionListener(e -> System.exit(0));

        // Thêm vào panel
        contentPanel.add(spearToggle);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(rangeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        contentPanel.add(damageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(exitBtn);

        add(contentPanel, BorderLayout.CENTER);

        // Lắng nghe phím bấm toàn cục trên cửa sổ (Phím P)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Kiểm tra nếu bấm phím P
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
            SpearMenuApp app = new SpearMenuApp();
            // Ban đầu ẩn menu đi, người dùng bấm P để bật lên
            app.setVisible(false);
            
            // Tạo một cửa sổ nền nhỏ hoặc thông báo hướng dẫn phím bấm
            JOptionPane.showMessageDialog(null, 
                "Ứng dụng đã chạy ngầm!\nHãy bấm phím [ P ] trên bàn phím để bật/tắt Menu Spear.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
