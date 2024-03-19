import core.filter.Filter;
import core.filter.FilterExecutor;
import misc.BloomFilter;
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

    public ImageFilterApp() {
        super("Image Filter Application");
        initializeFilters();
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(600, 800);
        imageLabel = new JLabel("", SwingConstants.CENTER);
        progressBar = new JProgressBar();

        JPanel buttonPanel = createButtonPanel();
        JScrollPane jScrollPane = new JScrollPane(imageLabel);
        addMouseDragFeature(jScrollPane);

        add(buttonPanel, BorderLayout.SOUTH);
        add(jScrollPane, BorderLayout.CENTER);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();

        addFilterButton(buttonPanel, "Choose Image", e -> chooseImage());
        filters.forEach((label, action) -> addFilterButton(buttonPanel, label, e -> action.accept(originalImage)));

        progressBar.setStringPainted(true);
        buttonPanel.add(progressBar);
        return buttonPanel;
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

    private void applyFilter(Filter filter) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
            return;
        }

        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        FilterExecutor.of(originalImage)
                .with(filter)
                .progress(this::updateLoader)
                .process()
                .thenAccept(this::updateCanvas)
                .exceptionally(ex -> {
                    JOptionPane.showMessageDialog(this, "Error applying filter: " + ex.getMessage());
                    return null;
                });
    }

    private void updateLoader(float percent) {
        // Update the progress bar value based on the percent completion of the task
        int progressValue = Math.round(percent * 100);
        progressBar.setValue(progressValue);

        // Ensure that the progress bar displays the progress percentage.
        // This call can be moved to the UI initialization section if the progress bar
        // is always intended to show the string. Doing so would eliminate the need
        // to repeatedly set it true here.
        if (!progressBar.isStringPainted()) {
            progressBar.setStringPainted(true);
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
        rootPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

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