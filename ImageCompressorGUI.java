package compressor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ImageCompressorGUI extends JFrame implements ActionListener {
    private static final Color BACKGROUND_COLOR = new Color(37, 44, 53);
    private static final Color FOREGROUND_COLOR = Color.WHITE;
    private static final Color BUTTON_COLOR = new Color(50, 100, 200);
    private static final Color BUTTON_HOVER_COLOR = new Color(70, 120, 220);
    private static final Color BORDER_COLOR = new Color(80, 80, 80);
    private static final int IMAGE_PREVIEW_WIDTH = 380;
    private static final int IMAGE_PREVIEW_HEIGHT = 380;

    private final JLabel statusLabel;
    private File selectedFile;
    private final ImageCompressor compressor;
    private final FileHandler fileHandler;
    private final JLabel imagePreview;
    private JSlider qualitySlider;
    private JTextField qualityTextField;

    public ImageCompressorGUI() {
        compressor = new ImageCompressor();
        fileHandler = new FileHandler();

        setTitle("Image Compressor");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        imagePreview = createImagePreview();
        add(imagePreview, BorderLayout.CENTER);
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.EAST);
        statusLabel = createStatusLabel();
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JLabel createHeader() {
        JLabel header = new JLabel("Image Compressor", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(FOREGROUND_COLOR);
        header.setBorder(new EmptyBorder(20, 5, 20, 5));
        return header;
    }

    private JLabel createImagePreview() {
        JLabel preview = new JLabel("No Image Selected", JLabel.CENTER);
        preview.setFont(new Font("Arial", Font.PLAIN, 14));
        preview.setPreferredSize(new Dimension(IMAGE_PREVIEW_WIDTH, IMAGE_PREVIEW_HEIGHT));
        preview.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        preview.setForeground(FOREGROUND_COLOR);
        preview.setBackground(BACKGROUND_COLOR);
        preview.setOpaque(true);
        return preview;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        controlPanel.add(createButton("Select Image", e -> selectImage()), gbc);
        gbc.gridy++;
        controlPanel.add(createButton("Compress", this), gbc);
        gbc.gridy++;

        qualitySlider = createQualitySlider();
        controlPanel.add(createQualityPanel(), gbc);
        gbc.gridy++;
        controlPanel.add(qualitySlider, gbc);

        return controlPanel;
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(BUTTON_COLOR);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(FOREGROUND_COLOR);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
        button.addMouseListener(new ButtonHoverListener(button));
        button.addActionListener(actionListener);
        return button;
    }

    private JSlider createQualitySlider() {
        JSlider slider = new JSlider(0, 100, 70);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setForeground(FOREGROUND_COLOR);
        slider.setBackground(BACKGROUND_COLOR);

        slider.addChangeListener(e -> qualityTextField.setText(String.valueOf(qualitySlider.getValue())));

        return slider;
    }

    private JPanel createQualityPanel() {
        JPanel qualityPanel = new JPanel();
        qualityPanel.setBackground(BACKGROUND_COLOR);

        JLabel label = new JLabel("Image Quality:", JLabel.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        qualityPanel.add(label);

        qualityTextField = createQualityTextField();
        qualityPanel.add(qualityTextField);

        JLabel percentLabel = new JLabel("%");
        percentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        percentLabel.setForeground(Color.WHITE);
        qualityPanel.add(percentLabel);

        return qualityPanel;
    }

    private JTextField createQualityTextField() {
        JTextField textField = new JTextField(2);
        textField.setText(String.valueOf(qualitySlider.getValue()));
        textField.setForeground(FOREGROUND_COLOR);
        textField.setBackground(BACKGROUND_COLOR);
        textField.setCaretColor(FOREGROUND_COLOR);
        textField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        textField.setHorizontalAlignment(JTextField.CENTER);

        // Add a listener to update slider when text field is changed
        textField.addActionListener(e -> {
            try {
                int value = Integer.parseInt(textField.getText());
                if (value < 0 || value > 100) throw new NumberFormatException();
                qualitySlider.setValue(value);
            } catch (NumberFormatException ex) {
                textField.setText(String.valueOf(qualitySlider.getValue())); // Reset to slider value if input is invalid
            }
        });

        return textField;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel("Status: No file selected", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(FOREGROUND_COLOR);
        label.setBorder(new EmptyBorder(15, 0, 15, 0));
        return label;
    }

    private static class ButtonHoverListener extends java.awt.event.MouseAdapter {
        private final JButton button;
        private final Color originalColor;

        public ButtonHoverListener(JButton button) {
            this.button = button;
            this.originalColor = button.getBackground();
        }

        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            button.setBackground(BUTTON_HOVER_COLOR);
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            button.setBackground(originalColor);
        }

        @Override
        public void mousePressed(java.awt.event.MouseEvent evt) {
            button.setBackground(BUTTON_HOVER_COLOR.darker());
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            button.setBackground(BUTTON_HOVER_COLOR);
        }
    }

    private void selectImage() {
        selectedFile = fileHandler.chooseFile();
        if (selectedFile != null) {
            statusLabel.setText("Selected file: " + selectedFile.getName());
            displayImagePreview(selectedFile);
        } else {
            resetImagePreview();
        }
    }

    private void resetImagePreview() {
        statusLabel.setText("No file selected");
        imagePreview.setIcon(null);
        imagePreview.setText("No Image Selected");
        imagePreview.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    public void displayImagePreview(File file) {
        try {
            ImageIcon icon = new ImageIcon(file.getPath());
            Image originalImage = icon.getImage();

            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);

            // Calculate new dimensions maintaining aspect ratio
            double aspectRatio = (double) originalWidth / originalHeight;
            int newWidth = IMAGE_PREVIEW_WIDTH;
            int newHeight = (int) (IMAGE_PREVIEW_WIDTH / aspectRatio);

            if (newHeight > IMAGE_PREVIEW_HEIGHT) {
                newHeight = IMAGE_PREVIEW_HEIGHT;
                newWidth = (int) (IMAGE_PREVIEW_HEIGHT * aspectRatio);
            }

            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            imagePreview.setIcon(new ImageIcon(scaledImage));
            imagePreview.setText(null); // Clear text if image is displayed
        } catch (Exception e) {
            imagePreview.setText("Could not load image preview.");
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (selectedFile != null) {
            try {
                File outputFile = new File("compressed_" + selectedFile.getName());
                float quality = qualitySlider.getValue() / 100f;
                compressor.compressImage(selectedFile, outputFile, quality);
                statusLabel.setText("Image compressed to: " + outputFile.getPath());
                JOptionPane.showMessageDialog(this,
                        "Image successfully compressed!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: Unsupported Format. Use either .jpg or .png",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select an image file first!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Please select an image first.");
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            int confirmed = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) {
                dispose();
            }
        } else {
            super.processWindowEvent(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageCompressorGUI gui = new ImageCompressorGUI();
            gui.setVisible(true);
        });
    }
}
