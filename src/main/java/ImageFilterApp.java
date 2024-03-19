import core.filter.Filter;
import core.filter.FilterExecutor;
import misc.BloomFilter;
import model.filter.eric.LanczosResampling;
import model.filter.leonid.GaussianBlurFilter;
import model.filter.leonid.MonochromeFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ImageFilterApp extends JFrame {
    private JLabel imageLabel;
    private JProgressBar progressBar;
    private BufferedImage originalImage;
    private Map<String, Consumer<BufferedImage>> filters;
    private JPanel overlayPanel;

    private void createOverlayPanel() {
        overlayPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Make the overlay panel semi-transparent
                g.setColor(new Color(0, 0, 0, 150)); // Black with alpha for transparency
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false);
        overlayPanel.setVisible(false); // Initially hidden

        // Ensure the overlay panel is sized to cover the entire frame
        overlayPanel.setSize(this.getSize());
        overlayPanel.setPreferredSize(this.getSize());
        MouseAdapter mouseAdapter = new MouseAdapter() {};
        overlayPanel.addMouseListener(mouseAdapter);
        overlayPanel.addMouseMotionListener(mouseAdapter);
        overlayPanel.addMouseWheelListener(mouseAdapter);
        // Progress Bar setup
        progressBar.setStringPainted(true);
        progressBar.setString("Processing...");

        overlayPanel.add(progressBar); // Add the progress bar to the overlay

        this.setGlassPane(overlayPanel); // Use the frame's glass pane for the overlay
    }
    private void showOverlay(boolean show) {
        // Ensure changes are made on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            overlayPanel.setVisible(show);
        });
    }
    public ImageFilterApp() {
        super("Image Filter Application");
        initializeFilters();
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        imageLabel = new JLabel("", SwingConstants.CENTER);
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        createToolbar(); // Create and add the toolbar
        JPanel buttonPanel = createButtonPanel();
        JScrollPane jScrollPane = new JScrollPane(imageLabel);
        addMouseDragFeature(jScrollPane);
        createOverlayPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        add(jScrollPane, BorderLayout.CENTER);
        setMinimumSize(new Dimension(400, 300));
        pack();
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();

        addFilterButton(buttonPanel, "Choose Image", e -> chooseImage());
        filters.forEach((label, action) -> addFilterButton(buttonPanel, label, e -> action.accept(originalImage)));
        createFitToScreenButton(buttonPanel);

        progressBar.setStringPainted(true);
        buttonPanel.add(progressBar);
        return buttonPanel;
    }

    private void createFitToScreenButton(JPanel panel) {
        JButton fitToScreenButton = new JButton("Fit to Screen");
        fitToScreenButton.addActionListener(e -> fitImageToScreen());
        panel.add(fitToScreenButton);
    }

    private void fitImageToScreen() {
        if (originalImage != null) {
            Dimension screenSize = this.getSize();
            double widthRatio = screenSize.getWidth() / originalImage.getWidth();
            double heightRatio = screenSize.getHeight() / originalImage.getHeight();
            double ratio = Math.min(widthRatio, heightRatio); // Scale down a bit to ensure it fits on screen

            int newWidth = (int) (originalImage.getWidth() * ratio);
            int newHeight = (int) (originalImage.getHeight() * ratio);

            final var filter = new LanczosResampling(newWidth, newHeight);
            applyFilter(filter);
        } else {
            JOptionPane.showMessageDialog(this, "No image loaded to fit to screen.");
        }
    }

    private void addFilterButton(JPanel panel, String label, ActionListener action) {
        JButton button = new JButton(label);
        button.addActionListener(action);
        panel.add(button);
    }


    private void initializeFilters() {
        filters = new HashMap<>();
        // Populate the filters map with filter names and corresponding actions
        filters.put("Apply Monochrome", image -> applyFilter(new MonochromeFilter()));
//        filters.put("Apply Embossing", image -> applyFilter(new EmbossingFilter(EmbossingFilter.Light.RIGHT_TOP))); // Assuming EmbossingFilter has a parameter for light direction
//        filters.put("Apply Dithering", image -> applyFilter(new DitheringFilter())); // Assuming a constructor exists for DitheringFilter
        filters.put("Apply Bloom", image -> applyFilter(new BloomFilter(5, 5))); // Assuming a constructor exists for BloomEffect as BloomFilter
        filters.put("Apply Blur", image -> applyFilter(new GaussianBlurFilter(5))); // Assuming blur level is a parameter
    }

    private void createToolbar() {
        JToolBar toolBar = new JToolBar("Image Tools");
        toolBar.setFloatable(false); // Optional: make the toolbar non-floatable

        // Add a button for choosing an image
        JButton chooseImageButton = new JButton("Choose Image");
        chooseImageButton.addActionListener(e -> chooseImage());
        toolBar.add(chooseImageButton);

        // Add other toolbar buttons here as needed, similar to how buttons are added to the panel
        // Example: Fit to Screen button
        JButton fitToScreenButton = new JButton("Fit to Screen");
        fitToScreenButton.addActionListener(e -> fitImageToScreen());
        toolBar.add(fitToScreenButton);

        // Adding filter buttons to the toolbar, for demonstration you can add one or two
        JButton applyMonochromeButton = new JButton("Apply Monochrome");
        applyMonochromeButton.addActionListener(e -> applyFilter(new MonochromeFilter()));
        toolBar.add(applyMonochromeButton);

        // Adding filter buttons to the toolbar, for demonstration you can add one or two
        JButton applyGaussianBlur = new JButton("Apply Gaussian blur");
        applyGaussianBlur.addActionListener(e -> applyFilter(new GaussianBlurFilter(5)));
        toolBar.add(applyGaussianBlur);

        // Continue adding buttons for other filters as needed...

        // Add the toolbar to the JFrame, at the top (NORTH)
        add(toolBar, BorderLayout.NORTH);
    }


    private void applyFilter(Filter filter) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
            return;
        }

        overlayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        showOverlay(true);
        FilterExecutor.of(originalImage)
                .with(filter)
                .progress(this::updateLoader)
                .process()
                .thenAccept(image -> {
                    updateCanvas(image);
                    showOverlay(false);
                })
                .exceptionally(ex -> {
                    JOptionPane.showMessageDialog(this, "Error applying filter: " + ex.getMessage());
                    showOverlay(false); // Ensure overlay is hidden on error
                    return null;
                });
    }

    private void updateLoader(float percent) {
        if (!progressBar.isVisible()) {
            progressBar.setVisible(true); // Show the progress bar
        }

        int progressValue = Math.round(percent * 100);
        progressBar.setValue(progressValue);

        if (!progressBar.isStringPainted()) {
            progressBar.setStringPainted(true);
        }

        if (progressValue >= 100) {
            // Hide the progress bar shortly after completion to allow users to see the completion
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(500); // Wait half a second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                progressBar.setVisible(false);
            });
        }
    }

    private void updateCanvas(BufferedImage image) {
        // Update the original image and the display with the processed image
        this.originalImage = image;
        imageLabel.setIcon(new ImageIcon(image));

        // Reset the UI to its default state after the processing is complete
        resetUIAfterProcessing();
    }

    private void resetUIAfterProcessing() {
        // Reset the cursor to the default cursor indicating the end of processing
        overlayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        // Revalidate the JFrame to ensure UI components are correctly redrawn
        this.revalidate();

        // Set the progress bar to complete
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageFilterApp frame = new ImageFilterApp();
            frame.setVisible(true);
        });
    }
}