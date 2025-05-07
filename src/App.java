import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    private static final String[] SERVICE_NAMES = {
            "BcastDVRUserService_e27cf",
            "AnotherService",
            "ThirdService"
    };
    private static JButton[][] serviceButtons;
    private static StatusCircle[] statusCircles;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Service Controller");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("Service Control Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 240, 240));

        serviceButtons = new JButton[SERVICE_NAMES.length][3];
        statusCircles = new StatusCircle[SERVICE_NAMES.length];

        for (int i = 0; i < SERVICE_NAMES.length; i++) {
            JPanel servicePanel = new JPanel(new BorderLayout());
            servicePanel.setBackground(new Color(240, 240, 240));
            servicePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            JLabel nameLabel = new JLabel(getDisplayName(SERVICE_NAMES[i]));
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            controlPanel.setBackground(new Color(240, 240, 240));

            serviceButtons[i][0] = createStyledButton("Start", new Color(200, 200, 200), i);
            serviceButtons[i][1] = createStyledButton("Stop", new Color(200, 200, 200), i);
            serviceButtons[i][2] = createStyledButton("Restart", new Color(200, 200, 200), i);

            statusCircles[i] = new StatusCircle();

            final int serviceIndex = i;
            serviceButtons[i][0].addActionListener((ActionEvent e) -> {
                executeCommand(new String[] { "cmd.exe", "/c", "sc", "start", SERVICE_NAMES[serviceIndex] }, () -> {
                    updateStatus(statusCircles[serviceIndex], serviceIndex);
                });
            });

            serviceButtons[i][1].addActionListener((ActionEvent e) -> {
                executeCommand(new String[] { "cmd.exe", "/c", "sc", "stop", SERVICE_NAMES[serviceIndex] }, () -> {
                    updateStatus(statusCircles[serviceIndex], serviceIndex);
                });
            });

            serviceButtons[i][2].addActionListener((ActionEvent e) -> {
                executeCommand(new String[] { "cmd.exe", "/c", "sc", "stop", SERVICE_NAMES[serviceIndex] }, () -> {
                    executeCommand(new String[] { "cmd.exe", "/c", "sc", "start", SERVICE_NAMES[serviceIndex] }, () -> {
                        updateStatus(statusCircles[serviceIndex], serviceIndex);
                    });
                });
            });

            controlPanel.add(serviceButtons[i][0]);
            controlPanel.add(serviceButtons[i][1]);
            controlPanel.add(serviceButtons[i][2]);
            controlPanel.add(statusCircles[i]);

            servicePanel.add(nameLabel, BorderLayout.WEST);
            servicePanel.add(controlPanel, BorderLayout.EAST);

            contentPanel.add(servicePanel);
            updateStatus(statusCircles[i], i);
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text, Color baseColor, int serviceIndex) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBackground(baseColor);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color hoverColor = baseColor.brighter();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(baseColor);
                button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            }
        });

        return button;
    }

    private static void executeCommand(String[] command, Runnable callback) {
        new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                SwingUtilities.invokeLater(callback);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private static void updateStatus(StatusCircle circle, int serviceIndex) {
        new Thread(() -> {
            boolean isRunning = false;
            try {
                Process process = Runtime.getRuntime()
                        .exec(new String[] { "cmd.exe", "/c", "sc", "query", SERVICE_NAMES[serviceIndex] });
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("STATE") && line.contains("RUNNING")) {
                        isRunning = true;
                        break;
                    }
                }
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }

            final boolean finalStatus = isRunning;
            SwingUtilities.invokeLater(() -> {
                circle.setRunning(finalStatus);
                serviceButtons[serviceIndex][0].setEnabled(!finalStatus);
                serviceButtons[serviceIndex][1].setEnabled(finalStatus);
                serviceButtons[serviceIndex][2].setEnabled(finalStatus);
            });
        }).start();
    }

    private static String getDisplayName(String serviceName) {
        try {
            Process process = Runtime.getRuntime()
                    .exec(new String[] { "cmd.exe", "/c", "sc", "GetDisplayName", serviceName });
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("DISPLAY_NAME")) {
                    return line.split(":", 2)[1].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serviceName;
    }

    static class StatusCircle extends JPanel {
        private boolean isRunning = false;

        public void setRunning(boolean running) {
            this.isRunning = running;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(isRunning ? new Color(46, 204, 113) : new Color(231, 76, 60));
            g2d.fillOval(5, 5, 20, 20);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(30, 30);
        }
    }
}