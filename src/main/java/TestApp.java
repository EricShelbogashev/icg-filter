import model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestApp extends JFrame {
    private JLabel imageLabel;
    private BufferedImage originalImage;

    public TestApp() {
        super("Image Filter Application");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setMaximumSize(new Dimension(1000, 1000));
        setLocationRelativeTo(null);

        // Button for choosing an image
        JButton chooseButton = new JButton("Choose Image");
        chooseButton.addActionListener((ActionEvent e) -> chooseImage());

        // Button for applying filter
        JButton filterButton = new JButton("Apply Filter");
        filterButton.addActionListener((ActionEvent e) -> applyMonochromeFilter());

        JButton filter2Button = new JButton("Apply embossing");
        filter2Button.addActionListener(e -> applyEmbossingFilter());

        JButton ditheringFilterButton = new JButton("Apply dithering");
        ditheringFilterButton.addActionListener(e -> applyDithering());

        JButton bloomButton = new JButton("Apply bloom");
        bloomButton.addActionListener(e -> {
            try {
                applyBloom();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton blurButton = new JButton("Apply blur");
        blurButton.addActionListener(e -> applyGaussian());

        // Label for displaying the image
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(filter2Button);
        buttonPanel.add(ditheringFilterButton);
        buttonPanel.add(bloomButton);
        buttonPanel.add(blurButton);

        // Adding components to frame
        add(buttonPanel, BorderLayout.SOUTH);
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                originalImage = ImageIO.read(selectedFile);
                imageLabel.setIcon(new ImageIcon(originalImage));
                this.pack(); // Automatically adjust the window size to the image size
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading the image: " + ex.getMessage());
            }
        }
    }

    private void applyEmbossingFilter() {
        if (originalImage != null) {
            EmbossingFilter filter1 = new EmbossingFilter();
            BufferedImage image = originalImage;
            ImageProcessor processor = new ImageProcessor(image);
            image = processor.apply(filter1, v -> this.repaint());
            originalImage = image;
            imageLabel.setIcon(new ImageIcon(image));
            this.pack();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyMonochromeFilter() {
        if (originalImage != null) {
            MonochromeFilter filter = new MonochromeFilter();
            BufferedImage image = originalImage;
            ImageProcessor processor = new ImageProcessor(image);
            image = processor.apply(filter, v -> repaint());
            originalImage = image;
            imageLabel.setIcon(new ImageIcon(image));

            this.pack();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyDithering() {
        if (originalImage != null) {
            BufferedImage image = originalImage;
            imageLabel.setIcon(new ImageIcon(Dithering.applyDithering(image)));
            this.pack();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyBloom() throws InterruptedException {
        if (originalImage != null) {
            BloomEffect bloomEffect = new BloomEffect(originalImage, 0.2, 0.5, 6);

            originalImage = bloomEffect.applyEffect();
            imageLabel.setIcon(new ImageIcon(originalImage));

            this.pack();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyGaussian() {
        if (originalImage != null) {
            GaussianBlurFilter filter = new GaussianBlurFilter(5);
            BufferedImage image = originalImage;
            ImageProcessor processor = new ImageProcessor(image);
            image = processor.apply(filter, v -> repaint());
            originalImage = image;
            imageLabel.setIcon(new ImageIcon(image));

            this.pack();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TestApp frame = new TestApp();
            frame.setVisible(true);
        });
    }
}
