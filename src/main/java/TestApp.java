import model.*;

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
    private int w = 5;
    private int[] kv = {16, 16, 16};
    private int[][] M1 = {{0, 2}, {3, 1}};
    private int[][] M2 = {{0, 8, 2, 10}, {12, 4, 14, 6}, {3, 11, 1, 9}, {15, 7, 13, 5}};
    private int[][] M3 = {{0, 32, 8, 40, 2, 34, 10, 42}, {48, 16, 56, 24, 50, 18, 58, 26}, {12, 44, 4, 36, 14, 46, 6, 38},
            {60, 28, 52, 20, 62, 30, 54, 22}, {3, 35, 11, 43, 1, 33, 9, 41}, {51, 19, 59, 27, 49, 17, 57, 25},
            {15, 47, 7, 39, 13, 45, 5, 37}, {63, 31, 55, 23, 61, 29, 53, 21}};

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
        //JButton filterButton = new JButton("Apply Filter");
        //filterButton.addActionListener((ActionEvent e) -> OrderedDithering());

        //JButton chooseSizeButton = new JButton("Choose size");
        //chooseSizeButton.addActionListener((ActionEvent e) -> chooseSize());

        //JButton chooseLevelButton = new JButton("Choose level");
        //chooseLevelButton.addActionListener((ActionEvent e) -> chooseLevel());

        // Label for displaying the image
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseButton);
        //buttonPanel.add(filterButton);
        //buttonPanel.add(chooseSizeButton);
        //buttonPanel.add(chooseLevelButton);

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
    private void mychooseSize(){
        ChooseWindowSize chooser = new ChooseWindowSize(this, w);
        if (chooser.is_new)
            w = Integer.parseInt(chooser.selectedSize());
    }

    private void mychooseLevel(){
        ChooseKvantLevel chooser = new ChooseKvantLevel(this);
        if (chooser.is_new)
            kv = chooser.selectedValues();
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

    private void BlackWhiteFilter() {
        if (originalImage != null) {
            ICGFilter filter = new ICGFilter(new Pattern(new Point(-1, -1), new Point(1, 1))) {
                @Override
                public int apply(MatrixView matrixView) {
                    int pixel = matrixView.get(0, 0);
                    float R = (float)((pixel & 0x00FF0000) >> 16);
                    float G = (float)((pixel & 0x0000FF00) >> 8);
                    float B = (float)(pixel & 0x000000FF);
                    R = G = B = (R + G + B) / 3.0f;
                    int newPixel = 0xFF000000 | ((int)R << 16) | ((int)G << 8) | ((int)B);
                    return newPixel;
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

    private void SmoothingFilter() {
        if (originalImage != null) {
            ICGFilter filter = new ICGFilter(new Pattern(new Point(-1 * w, -1 * w), new Point(w, w))) {
                @Override
                public int apply(MatrixView matrixView) {
                    float res_r = 0;
                    float res_g = 0;
                    float res_b = 0;
                    int alpha = (matrixView.get(0, 0) >> 24) & 0xFF;
                    float sigma = w / 2.1f;
                    for (int x = -1 * w; x <= w; x++)
                        for (int y = -1 * w; y <= w; y++){
                            float k = (float) (1.0f / (2.0f * Math.PI * sigma * sigma) * Math.pow(2.7f,  -1.0f * (x * x + y * y) / (2.0f * sigma * sigma)));
                            res_r += ((matrixView.get(x, y) >> 16) & 0xFF) * k;
                            res_g += ((matrixView.get(x, y) >> 8) & 0xFF) * k;
                            res_b += ((matrixView.get(x, y)) & 0xFF) * k;
                        }
                    if (Math.round(res_r) > 255 || Math.round(res_r) < 0)
                        res_r = 255;
                    if (Math.round(res_b) > 255 || Math.round(res_b) < 0)
                        res_b = 255;
                    if (Math.round(res_g) > 255 || Math.round(res_g) < 0)
                        res_g = 255;
                    return ((alpha & 0xFF) << 24) |
                            ((Math.round(res_r) & 0xFF) << 16) |
                            ((Math.round(res_g) & 0xFF) << 8) |
                            ((Math.round(res_b) & 0xFF));
                }
            };
            BufferedImage image = originalImage;
            ImageProcessor processor = new ImageProcessor(image);
            image = processor.apply(filter, v->this.repaint());
            originalImage = image;
            imageLabel.setIcon(new ImageIcon(image));
            this.pack();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }
    private int find_closest_palette_color(int red, int green, int blue, int alpha){
        int []result = {red, green, blue};
        for (int i = 0; i < 3; i++) {
            if (result[i] < 0)
                result[i] = 0;
            if (result[i] > 255)
                result[i] = 255;
            float del = (float)(kv[i] - 1);
            result[i] = (int)((float)((int)((float)result[i] / 255 * del)) / del * 255);
        }
        return ((alpha & 0xFF) << 24) |
                ((result[0] & 0xFF) << 16) |
                ((result[1] & 0xFF) << 8) |
                ((result[2] & 0xFF));
    }

    private void WaterShedFilter(){
        if (originalImage != null) {
            ICGFilter filter = new ICGFilter(new Pattern(new Point(-1, -1), new Point(1, 1))) {
                @Override
                public int apply(MatrixView matrixView) {
                    int oldpix = matrixView.get(0, 0);
                    int old_red = (oldpix >> 16) & 0xFF;
                    int old_green = (oldpix >> 8) & 0xFF;
                    int old_blue = (oldpix) & 0xFF;
                    int alpha = (oldpix >> 24) & 0xFF;
                    float err_red = 0;
                    float err_blue = 0;
                    float err_green = 0;
                    int[] values = {matrixView.get(-1, -1), matrixView.get(-1, 0), matrixView.get(-1, 1), matrixView.get(1, -1), matrixView.get(1, 0), matrixView.get(1, 1)};
                    float[] koef = {-1.0f / 9, -2.0f / 9, -1.0f / 9, 1.0f / 9, 2.0f / 9, 1.0f / 9};
                    for (int i = 0; i < 4; i++){
                        err_red += (((values[i] >> 16) & 0xFF)) * koef[i];
                        err_green += (((values[i] >> 8) & 0xFF)) * koef[i];
                        err_blue += (((values[i]) & 0xFF)) * koef[i];
                    }
                    int r = find_closest_palette_color(old_red + (int)err_red, old_green + (int)err_green, old_blue + (int)err_blue, alpha);
                    return r;
                }
            };
            BufferedImage image = originalImage;
            ImageProcessor processor = new ImageProcessor(image);
            image = processor.apply(filter, v->this.repaint());
            originalImage = image;
            imageLabel.setIcon(new ImageIcon(image));
            this.pack();
            ColorStretchFunc();
            FillColor();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }
    private void ColorStretchFunc(){
        if (originalImage != null) {
            ICGFilter filter = new ICGFilter(new Pattern(new Point(-2, -2), new Point(2, 2))) {
                @Override
                public int apply(MatrixView matrixView) {
                    int oldpix = matrixView.get(0, 0);
                    int alpha = (oldpix >> 24) & 0xFF;
                    float err_red = 0;
                    float err_blue = 0;
                    float err_green = 0;
                    float koef = 1.0f / 25;
                    for (int i = -2; i < 2; i++)
                        for (int j = -2; j < 2; j++) {
                            err_red += (((matrixView.get(i, j) >> 16) & 0xFF)) * koef;
                            err_green += (((matrixView.get(i, j) >> 8) & 0xFF)) * koef;
                            err_blue += (((matrixView.get(i, j)) & 0xFF)) * koef;
                        }
                    int r = find_closest_palette_color((int)err_red, (int)err_green, (int)err_blue, alpha);
                    return r;
                }
            };
            BufferedImage image = originalImage;
            ImageProcessor processor = new ImageProcessor(image);
            image = processor.apply(filter, v->this.repaint());
            originalImage = image;
            imageLabel.setIcon(new ImageIcon(image));
            this.pack();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }
    private void FillColor(){
        if (originalImage != null) {
            ICGFilter filter = new ICGFilter(new Pattern(new Point(0, 0), new Point(0, 0))) {
                @Override
                public int apply(MatrixView matrixView) {
                    int alpha = (matrixView.get(0, 0) >> 24) & 0xFF;
                    int r = ((alpha & 0xFF) << 24) |
                            (((((matrixView.get(0, 0) & 0xFF) / 2) * 30 + 40) & 0xFF) << 16) |
                            (((((matrixView.get(0, 0) & 0xFF) / 2) * 10 + 40) & 0xFF) << 8) |
                            (((((matrixView.get(0, 0) & 0xFF) / 2) * 20 + 40) & 0xFF));
                    return r;
                }
            };
            BufferedImage image = originalImage;
            ImageProcessor processor = new ImageProcessor(image);
            image = processor.apply(filter, v->this.repaint());
            originalImage = image;
            imageLabel.setIcon(new ImageIcon(image));
            this.pack();
        }else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void MyFloydDithering(){
        if (originalImage != null) {
            ICGFilter filter = new ICGFilter(new Pattern(new Point(-1 * w, -1 * w), new Point(w, w))) {
                @Override
                public int apply(MatrixView matrixView) {
                    int oldpix = matrixView.get(0, 0);
                    int old_red = (oldpix >> 16) & 0xFF;
                    int old_green = (oldpix >> 8) & 0xFF;
                    int old_blue = (oldpix) & 0xFF;
                    int alpha = (oldpix >> 24) & 0xFF;
                    float err_red = 0;
                    float err_blue = 0;
                    float err_green = 0;
                    int[] values = {matrixView.get(-1, 0), matrixView.get(1, -1), matrixView.get(0, -1), matrixView.get(-1, -1)};
                    float[] koef = {7.0f / 16, 3.0f / 16, 5.0f / 16, 1 / 16.0f};
                    for (int i = 0; i < 4; i++){
                        err_red += (old_red - ((values[i] >> 16) & 0xFF)) * koef[i];
                        err_green += (old_green - ((values[i] >> 8) & 0xFF)) * koef[i];
                        err_blue += (old_blue - ((values[i]) & 0xFF)) * koef[i];
                    }
                    int r = find_closest_palette_color(old_red + (int)err_red, old_green + (int)err_green, old_blue + (int)err_blue, alpha);
                    return r;
                }
            };
            BufferedImage image = originalImage;
            ImageProcessor processor = new ImageProcessor(image);
            image = processor.apply(filter, v->this.repaint());
            originalImage = image;
            imageLabel.setIcon(new ImageIcon(image));
            this.pack();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void MyOrderedDithering(){
        if (originalImage != null) {
            BufferedImage image = originalImage;
            int red = -1000, green = -1000, blue = -1000;
            for (int i = 0; i < image.getWidth(); i++)
                for (int j = 0; j < image.getHeight(); j++) {
                    int oldpix = image.getRGB(i, j);
                    if (kv[0] == 4)
                        red = ((oldpix >> 16) & 0xFF) + 32 - M3[j % 8][i % 8];
                    if (kv[1] == 4)
                        green = ((oldpix >> 8) & 0xFF) + 32 - M3[j % 8][i % 8];
                    if (kv[2] == 4)
                        blue = ((oldpix) & 0xFF) + 32 - M3[j % 8][i % 8];
                    if (kv[0] == 16)
                        red = ((oldpix >> 16) & 0xFF) + 8 - M2[j % 4][i % 4];
                    if (kv[1] == 16)
                        green = ((oldpix >> 8) & 0xFF) + 8 - M2[j % 4][i % 4];
                    if (kv[2] == 16)
                        blue = ((oldpix) & 0xFF) + 8 - M2[j % 4][i % 4];
                    if (kv[0] == 64)
                        red = ((oldpix >> 16) & 0xFF) + 2 - M1[j % 2][i % 2];
                    if (kv[1] == 64)
                        green = ((oldpix >> 8) & 0xFF) + 2 - M1[j % 2][i % 2];
                    if (kv[2] == 64)
                        blue = ((oldpix) & 0xFF) + 2 - M1[j % 2][i % 2];
                    int alpha = (oldpix >> 24) & 0xFF;
                    if (red == -1000 || green == -1000 || blue == -1000)
                        return;
                    image.setRGB(i, j, find_closest_palette_color(red, green, blue, alpha));
                }
            originalImage = image;
            this.repaint();
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
