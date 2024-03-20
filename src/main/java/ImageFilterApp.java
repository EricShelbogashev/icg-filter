import core.filter.Filter;
import core.filter.FilterExecutor;
import core.filter.Image;
import model.ChooseKvantLevel;
import model.ChooseWindowSize;
import model.filter.darya.ColorStretchFilter;
import model.filter.darya.FillColorFilter;
import model.filter.darya.MyFloydDithering;
import model.filter.darya.WaterShedFilter;
import model.filter.eric.LanczosResampling;
import model.filter.leonid.BloomFilter;
//import model.filter.leonid.EmbossingFilter;
import model.filter.leonid.GaussianBlurFilter;
import model.filter.leonid.MixFilter;
import model.filter.leonid.MonochromeFilter;
import model.filter.leonid.NegativeFilter;
import model.filter.leonid.OrderedDithering;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageFilterApp extends JFrame {
    int[] levels_kvant = {2, 2, 2};
    int window_size = 5;
    private JLabel imageLabel;
    private JProgressBar progressBar;
    private BufferedImage originalImage;
    private JPanel overlayPanel;

    private void createOverlayPanel() {
        overlayPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.setVisible(false);
        overlayPanel.setSize(this.getSize());
        overlayPanel.setPreferredSize(this.getSize());

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Processing...");

        overlayPanel.add(progressBar);

        this.setGlassPane(overlayPanel);
    }

    private void showOverlay(boolean show) {
        SwingUtilities.invokeLater(() -> overlayPanel.setVisible(show));
    }

    public ImageFilterApp() {
        super("Image Filter Application");
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        imageLabel = new JLabel("", SwingConstants.CENTER);
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        createToolbar();
        JScrollPane jScrollPane = new JScrollPane(imageLabel);
        addMouseDragFeature(jScrollPane);
        createOverlayPanel();
        add(jScrollPane, BorderLayout.CENTER);
        setMinimumSize(new Dimension(800, 600));
        pack();
    }

    private void fitImageToScreen() {
        if (originalImage != null) {
            Dimension screenSize = this.getSize();
            double widthRatio = screenSize.getWidth() / originalImage.getWidth();
            double heightRatio = screenSize.getHeight() / originalImage.getHeight();
            double ratio = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (originalImage.getWidth() * ratio);
            int newHeight = (int) (originalImage.getHeight() * ratio);

            final var filter = new LanczosResampling(newWidth, newHeight);
            applyFilters(filter);
        } else {
            JOptionPane.showMessageDialog(this, "No image loaded to fit to screen.");
        }
    }

    private void applyBloomEffect() {
        if (originalImage != null) {
            BloomFilter bloomFilter = new BloomFilter(0.3, 0.7);
            GaussianBlurFilter blurFilter = new GaussianBlurFilter(5);
            MixFilter mixFilter = new MixFilter(new Image(originalImage));
            applyFilters(bloomFilter, blurFilter, mixFilter);

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyWaterShed() {
        if (originalImage != null) {
            WaterShedFilter waterShedFilter = new WaterShedFilter(levels_kvant);
            ColorStretchFilter colorStretchFilter = new ColorStretchFilter(levels_kvant);
            FillColorFilter fillColorFilter = new FillColorFilter();
            applyFilter(waterShedFilter, colorStretchFilter, fillColorFilter);

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    /*private void applyFSDithering() {
        if (originalImage != null) {
            FSDithering fsDithering = new FSDithering(2, 2, 2);
            applyFilter(fsDithering);


        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }*/

    private void createToolbar() {
        JToolBar toolBar = new JToolBar("Image Tools");
        toolBar.setFloatable(false);

        JButton chooseImageButton = new JButton("Choose Image");
        chooseImageButton.addActionListener(e -> chooseImage());
        toolBar.add(chooseImageButton);

        JButton chooseKvant = new JButton("Choose Kv Level");
        chooseKvant.addActionListener(e -> chooseKvantLevel());
        toolBar.add(chooseKvant);

        JButton chooseWind = new JButton("Choose Window S");
        chooseWind.addActionListener(e -> chooseWindowSize());
        toolBar.add(chooseWind);

        JButton fitToScreenButton = new JButton("Fit to Screen");
        fitToScreenButton.addActionListener(e -> fitImageToScreen());
        toolBar.add(fitToScreenButton);

        JButton applyMonochromeButton = new JButton("Apply Monochrome");
        applyMonochromeButton.addActionListener(e -> applyFilter(new MonochromeFilter()));
        toolBar.add(applyMonochromeButton);

        JButton applyNegativeButton = new JButton("Apply Negative");
        applyNegativeButton.addActionListener(e -> applyFilter(new NegativeFilter()));
        toolBar.add(applyNegativeButton);

        JButton applyGaussianBlur = new JButton("Apply Gaussian blur");
        applyGaussianBlur.addActionListener(e -> applyFilter(new GaussianBlurFilter(window_size)));
        toolBar.add(applyGaussianBlur);

        JButton applyBloom = new JButton("Apply Bloom effect");
        applyBloom.addActionListener(e -> applyBloomEffect());
        toolBar.add(applyBloom);

        JButton applyWaterShedButton = new JButton("Apply Watershed");
        applyWaterShedButton.addActionListener(e -> applyWaterShed());
        toolBar.add(applyWaterShedButton);

        JButton applyOrderedDithering = new JButton("Apply ordered dithering");
        applyOrderedDithering.addActionListener(e -> applyFilter( new MyFloydDithering(levels_kvant)));
        toolBar.add(applyOrderedDithering);

        /*JButton applyFSDitheringButton = new JButton("Apply FSDithering");
        applyFSDitheringButton.addActionListener(e -> applyFSDithering());
        toolBar.add(applyFSDitheringButton);*/

        //JButton applyEmbossingButton = new JButton("Apply embossing");
        //applyEmbossingButton.addActionListener(e -> applyFilter(new EmbossingFilter(EmbossingFilter.Light.LEFT_TOP)));
        //toolBar.add(applyEmbossingButton);

        add(toolBar, BorderLayout.NORTH);
    }


    private void applyFilter(Filter... filters) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
            return;
        }

        overlayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        showOverlay(true);

        FilterExecutor.Builder builder = FilterExecutor.of(originalImage);
        for (Filter filter : filters) {
            builder = builder.with(filter);
        }

        builder
                .progress(this::updateLoader)
                .process()
                .thenAccept(image -> {
                    updateCanvas(image);
                    showOverlay(false);
                })
                .exceptionally(ex -> {
                    JOptionPane.showMessageDialog(this, "Error applying filter: " + ex.getMessage());
                    showOverlay(false);
                    return null;
                });
    }

    private void updateLoader(float percent) {
        if (!progressBar.isVisible()) {
            progressBar.setVisible(true);
        }

        int progressValue = Math.round(percent * 100);
        progressBar.setValue(progressValue);

        if (!progressBar.isStringPainted()) {
            progressBar.setStringPainted(true);
        }
    }

    private void updateCanvas(BufferedImage image) {
        this.originalImage = image;
        imageLabel.setIcon(new ImageIcon(image));
        resetUIAfterProcessing();
    }

    private void resetUIAfterProcessing() {
        overlayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.revalidate();
        progressBar.setValue(100);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadImage(selectedFile);
        }
    }

    private void chooseKvantLevel() {
        ChooseKvantLevel chooser = new ChooseKvantLevel(this);
        levels_kvant = chooser.selectedValues();
    }

    private void chooseWindowSize() {
        ChooseWindowSize chooser = new ChooseWindowSize(this, window_size);
        window_size = Integer.parseInt(chooser.selectedSize());
    }

    private void loadImage(File imageFile) {
        try {
            originalImage = ImageIO.read(imageFile);
            imageLabel.setIcon(new ImageIcon(originalImage));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
        }
    }

    private void addMouseDragFeature(JScrollPane pane) {
        MouseAdapter ma = new MouseAdapter() {
            private Point origin;

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    origin = e.getPoint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (origin != null && SwingUtilities.isLeftMouseButton(e)) {
                    JViewport viewport = pane.getViewport();
                    if (viewport != null) {
                        int deltaX = origin.x - e.getX();
                        int deltaY = origin.y - e.getY();
                        Rectangle view = viewport.getViewRect();
                        view.x += deltaX;
                        view.y += deltaY;

                        imageLabel.scrollRectToVisible(view);
                        origin = e.getPoint();
                    }
                }
            }
        };

        pane.getViewport().addMouseListener(ma);
        pane.getViewport().addMouseMotionListener(ma);
    }

    private void applyFilters(Filter... filters) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
            return;
        }

        overlayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        showOverlay(true);

        FilterExecutor.Builder builder = FilterExecutor.of(originalImage);
        for (Filter filter : filters) {
            builder = builder.with(filter);
        }
        builder.progress(this::updateLoader)
                .process()
                .thenAccept(image -> {
                    updateCanvas(image);
                    showOverlay(false);
                })
                .exceptionally(ex -> {
                    JOptionPane.showMessageDialog(this, "Error applying filter: " + ex.getMessage());
                    showOverlay(false);
                    return null;
                });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageFilterApp frame = new ImageFilterApp();
            frame.setVisible(true);
        });
    }
}
