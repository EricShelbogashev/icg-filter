import model.ICGFilter;
import model.ImageProcessor;
import model.MatrixView;
import model.Pattern;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

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
        filterButton.addActionListener((ActionEvent e) -> applyFilter());

        // Label for displaying the image
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseButton);
        buttonPanel.add(filterButton);

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

    private void applyFilter() {
        if (originalImage != null) {
            ImageProcessor processor = new ImageProcessor(originalImage);
            BufferedImage image = processor.apply(new ICGFilter(new Pattern(new Point(-1, -1), new Point(1, 1))) {
                @Override
                public int apply(MatrixView matrixView) {
                    int res = (
                        matrixView.get(-1, 0) +
                        matrixView.get(1, 0) -
                        matrixView.get(0, -1) -
                        matrixView.get(0, 1)
                    ) / 4 / 25;
                    return res * 10;
                }
            });
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
