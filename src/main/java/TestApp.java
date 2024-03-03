import model.ICGFilter;
import model.ImageProcessor;
import model.MatrixView;
import model.Pattern;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
            ICGFilter filter = new ICGFilter(new Pattern(new Point(-1, -1), new Point(1, 1))) {
                @Override
                public int apply(MatrixView matrixView) {
                    Optional<Color> optional = Stream.of(
                                    matrixView.get(-1, -1),
                                    matrixView.get(-1, 0),
                                    matrixView.get(-1, 1),
                                    matrixView.get(0, -1),
                                    matrixView.get(0, 1),
                                    matrixView.get(1, -1),
                                    matrixView.get(1, 0),
                                    matrixView.get(1, 1)
                            ).map(Color::new)
                            .map(color -> List.of(color.getRed(), color.getGreen(), color.getBlue()))
                            .reduce((a, b) -> List.of(a.getFirst() + b.getFirst(), a.get(1) + b.get(1), a.getLast() + b.getLast()))
                            .map(list -> new Color(list.getFirst() / 8, list.get(1) / 8, list.getLast() / 8));
                    return optional.orElse(Color.BLACK).getRGB();
                }
            };
            BufferedImage image = originalImage;
            ImageProcessor processor = new ImageProcessor(image);
            image = processor.apply(filter, System.out::println);
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
