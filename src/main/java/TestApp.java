import core.filter.Filter;
import core.filter.FilterExecutor;
import misc.BloomEffect;
import misc.Dithering;
import misc.EmbossingFilter;
import misc.ImageProcessor;
import model.filter.darya.ClosestPalette;
import model.filter.darya.ColorStretchFilter;
import model.filter.darya.FillColorFilter;
import model.filter.darya.WaterShedFilter;
import model.filter.leonid.GaussianBlurFilter;
import model.filter.leonid.MonochromeFilter;
import model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TestApp extends JFrame {
    private final JLabel imageLabel;
    private final JProgressBar progressBar;
    private BufferedImage originalImage;
    private final List<JButton> buttons;
    private int w = 5;
    private int[] kv = {16, 16, 16};

    public TestApp() {
        super("Image Filter Application");
        this.buttons = new ArrayList<>();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setMaximumSize(new Dimension(1000, 1000));
        setLocationRelativeTo(null);

        // Button for choosing an image
        JButton chooseButton = new JButton("Choose Image");
        buttons.add(chooseButton);
        chooseButton.addActionListener((ActionEvent e) -> chooseImage());


        JButton chooseSizeButton = new JButton("Choose size");
        chooseSizeButton.addActionListener((ActionEvent e) -> chooseSize());

        JButton chooseLevelButton = new JButton("Choose level");
        chooseLevelButton.addActionListener((ActionEvent e) -> chooseLevel());
        // Button for applying model.filter
        JButton filterButton = new JButton("Apply Filter");
        buttons.add(filterButton);
        filterButton.addActionListener((ActionEvent e) -> applyWaterShedFilter());

        JButton filter2Button = new JButton("Apply embossing");
        buttons.add(filter2Button);
        filter2Button.addActionListener(e -> applyEmbossingFilter());

        JButton ditheringFilterButton = new JButton("Apply dithering");
        buttons.add(ditheringFilterButton);
        ditheringFilterButton.addActionListener(e -> applyDithering());

        JButton bloomButton = new JButton("Apply bloom");
        buttons.add(bloomButton);

        bloomButton.addActionListener(e -> {
            try {
                applyBloom();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton blurButton = new JButton("Apply blur");
        buttons.add(blurButton);

        blurButton.addActionListener(e -> applyGaussian());

        // Label for displaying the image
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseButton);
        buttonPanel.add(chooseSizeButton);
        buttonPanel.add(chooseLevelButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(filter2Button);
        buttonPanel.add(ditheringFilterButton);
        buttonPanel.add(bloomButton);
        buttonPanel.add(blurButton);
        progressBar = new JProgressBar(0, 100);
        buttonPanel.add(progressBar);

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
    private void chooseSize(){
        ChooseWindowSize chooser = new ChooseWindowSize(this, w);
        if (chooser.is_new)
            w = Integer.parseInt(chooser.selectedSize());
    }

    private void chooseLevel(){
        ChooseKvantLevel chooser = new ChooseKvantLevel(this);
        if (chooser.is_new)
            kv = chooser.selectedValues();
    }

    private void applyEmbossingFilter() {
        if (originalImage != null) {
            EmbossingFilter filter1 = new EmbossingFilter(EmbossingFilter.Light.RIGHT_TOP);
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


    private void applyWaterShedFilter(){
        var filter = new WaterShedFilter(kv);
        var filter2 = new ColorStretchFilter(kv);
        var filter3 = new FillColorFilter();
        this.buttons.forEach(JButton::disable);
        this.setCursor(Cursor.WAIT_CURSOR);
        FilterExecutor.of(originalImage)
                .with(filter).with(filter2).with(filter3)
                .progress(this::updateLoader)
                .process()
                .thenAccept(this::updateCanvas)
                .exceptionally(ex -> {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return null;
                });
    }

    private void applyMonochromeFilter() {
        if (originalImage != null) {
            MonochromeFilter filter = new MonochromeFilter();
            applyFilter(filter);
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyDithering() {
        if (originalImage != null) {
            BufferedImage image = originalImage;
            imageLabel.setIcon(new ImageIcon(Dithering.applyDithering(image, 2, 2, 2, Dithering.MatrixOption.eight)));
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
            applyFilter(filter);
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyFilter(Filter filter) {
        this.buttons.forEach(JButton::disable);
        this.setCursor(Cursor.WAIT_CURSOR);
        FilterExecutor.of(originalImage)
                .with(filter)
                .progress(this::updateLoader)
                .process()
                .thenAccept(this::updateCanvas)
                .exceptionally(ex -> {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    return null;
                });
    }

    private void updateLoader(float percent) {
        progressBar.setValue((int) (percent * 100)); // Начальное значение прогресса
        progressBar.setStringPainted(true); // Показывать процент выполнения
    }

    private void updateCanvas(BufferedImage image) {
        this.originalImage = image;
        imageLabel.setIcon(new ImageIcon(image));
        this.setCursor(Cursor.DEFAULT_CURSOR);
        this.revalidate();
        this.pack();
        this.buttons.forEach(JButton::enable);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TestApp frame = new TestApp();
            frame.setVisible(true);
        });
    }
}
