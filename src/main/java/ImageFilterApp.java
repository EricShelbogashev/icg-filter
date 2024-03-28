import context.ApplicationComponents;
import context.ApplicationContext;
import context.ApplicationProperties;
import context.ImageHolder;
import core.filter.Filter;
import core.filter.FilterExecutor;
import core.options.Setting;
import model.options.SettingsDialogGenerator;
import view.ProgressPanel;
import view.filters.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ImageFilterApp extends JFrame {
    private final ApplicationContext applicationContext;
    private final ApplicationComponents applicationComponents;
    private final List<FilterViewUnit> filterUnits;

    public ImageFilterApp(ApplicationProperties applicationProperties) {
        super("Image Filter Application");
        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        JScrollPane scrollPane = createScrollPanel(imageLabel);
        filterUnits = List.of(
                new FitImageToScreenFilterViewUnit(scrollPane::getSize, this::applyFilters), // Let it be first, because lower there is hard-coded resizing listener that takes first element of this list and acts as if it's FitImageToScreen
                new WindFilterViewUnit(this::applyFilters),
                new GammaFilterViewUnit(this::applyFilters),
                new RotateImageViewUnit(this::applyFilters),
                new BloomFilterViewUnit(this::applyFilters),
                new NegativeFilterViewUnit(this::applyFilters),
                new EmbossingFilterViewUnit(this::applyFilters),
                new MonochromeFilterViewUnit(this::applyFilters),
                new DitheringFilterViewUnit(this::applyFilters),
                new SharpnessViewUnit(this::applyFilters),
                new MotionBlurViewUnit(this::applyFilters)
        );
        applicationComponents = new ApplicationComponents(
                imageLabel,
                createOverlayPanel(),
                scrollPane
        );
        initializeUI();
        ImageHolder imageHolder = new ImageHolder();
        applicationContext = new ApplicationContext(imageHolder, applicationProperties);
        addComponentListener(new ComponentResizeEndListener() {
            @Override
            public void resizeTimedOut() {
                FitImageToScreenFilterViewUnit fitFilter = (FitImageToScreenFilterViewUnit) filterUnits.getFirst(); //very bad, but I can't think how do this better with current architecture.
                FitImageTurnOn turnedOn;
                try {
                    turnedOn = fitFilter.getSettings().get(1).value();
                } catch (NullPointerException a) {
                    return;
                }
                if (turnedOn == FitImageTurnOn.ON) {
                    fitFilter.applyFilter(applicationContext.imageHolder().getCurrentImage());
                }
            }
        });

    }

    public static void main(String[] args) {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        SwingUtilities.invokeLater(() -> {
            ImageFilterApp frame = new ImageFilterApp(applicationProperties);
            frame.setVisible(true);
        });
    }

    private ProgressPanel createOverlayPanel() {
        ProgressPanel progressPanel = new ProgressPanel();
        this.setGlassPane(progressPanel);
        return progressPanel;
    }

    private void showOverlay(boolean show) {
        SwingUtilities.invokeLater(() ->
                applicationComponents
                        .progressPanel()
                        .setVisible(show));
    }

    private void initializeUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        createMenuBar();
        createToolbarButtons();

        setMinimumSize(new Dimension(640, 480));
        pack();
    }

    private JScrollPane createScrollPanel(JLabel imageLabel) {
        JScrollPane jScrollPane = new JScrollPane(imageLabel);
        jScrollPane.setViewportBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createDashedBorder(Color.BLACK, 5, 2)));
        addMouseDragFeature(jScrollPane);
        add(jScrollPane, BorderLayout.CENTER);
        return jScrollPane;
    }

    private void createToolbarButtons() {
        JToolBar toolBar = new JToolBar("Image Tools");
        toolBar.setFloatable(false);
        filterUnits.forEach(filterViewUnit -> {
            JButton toolbarButton = new JButton();
            toolbarButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(filterViewUnit.getIconPath()))));
            toolbarButton.setToolTipText(filterViewUnit.getTipText());
            toolbarButton.addActionListener(e -> {
                applyFilter(filterViewUnit);
            });
            toolBar.add(toolbarButton);
        });
        add(toolBar, BorderLayout.NORTH);
    }

    private JMenu createFilterMenuItems() {
        JMenu filterMenu = new JMenu();
        filterMenu.setText("Filter");
        filterUnits.forEach(filterViewUnit -> {
            JMenuItem menuItem = new JMenuItem(filterViewUnit.getFilterName());
            menuItem.addActionListener(e -> {
                applyFilter(filterViewUnit);
            });
            filterMenu.add(menuItem);
        });
        return filterMenu;
    }

    private void applyFilter(FilterViewUnit filterViewUnit) {
        if (applicationContext.imageHolder().getOriginalImage() == null) {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
            return;
        }
        List<Setting<?>> options = filterViewUnit.getSettings();
        if (options == null) {
            filterViewUnit.applyFilter(applicationContext.imageHolder().getOriginalImage());
        } else {
            SettingsDialogGenerator.generateAndShowDialog(options, () -> {
                filterViewUnit.applyFilter(applicationContext.imageHolder().getOriginalImage());
            });
        }

    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu();
        helpMenu.setText("Help");

        JMenuItem aboutProgram = new JMenuItem("About program");
        String aboutMessage = """
                ICGFilter is program for applying filters. You have to choose and load an image before
                applying and for some of filters you have to choose some parameters. Also there is
                opportunity for showing original image.
                 Authors:\s
                Shelbogashev Eric
                Shaikhutdinov Leonid
                Avtsinova Daria
                Kulakov Michael
                Bochkarev Egor\s
                """;
        aboutProgram.addActionListener(e -> JOptionPane.showMessageDialog(this, aboutMessage));
        helpMenu.add(aboutProgram);
        return helpMenu;
    }

//    private JMenu createModifyMenu() {
//        JMenu modifyMenu = new JMenu("Modify");
//        JMenuItem fit = new JMenuItem("Fit image to screen");
//        fit.addActionListener(e -> chooseFitAlgorithm());
//        modifyMenu.add(fit);
//
//        JMenuItem dithering = new JMenuItem("Dither");
//        dithering.addActionListener(e -> chooseDitheringOrder());
//        modifyMenu.add(dithering);
//
//        return modifyMenu;
//    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(e -> chooseImage());
        fileMenu.add(open);

        JMenuItem saveAs = new JMenuItem("Save as");
        saveAs.addActionListener(e -> saveFileAs());
        fileMenu.add(saveAs);

        return fileMenu;
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createHelpMenu());
        menuBar.add(createFileMenu());
        menuBar.add(createFilterMenuItems());
//        menuBar.add(createModifyMenu());

        setJMenuBar(menuBar);
    }

//    private void onSwitchImagePressed(JToggleButton button) {
//        if (applicationContext.imageHolder().getCurrentImage() != null) {
//            if (isOriginalImage) {
//                isOriginalImage = false;
//                updateCanvas(editedImage);
//                button.setSelected(false);
//            } else {
//                isOriginalImage = true;
//                updateCanvas(originalImage);
//                button.setSelected(true);
//
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Please choose an image first.");
//        }
//    }

    private void updateLoader(float percent) {
        if (!applicationComponents.progressPanel().progressBar().isVisible()) {
            applicationComponents.progressPanel().progressBar().setVisible(true);
        }

        int progressValue = Math.round(percent * 100);
        applicationComponents.progressPanel().progressBar().setValue(progressValue);

        if (!applicationComponents.progressPanel().progressBar().isStringPainted()) {
            applicationComponents.progressPanel().progressBar().setStringPainted(true);
        }
    }

    private void updateCanvas(BufferedImage image) {
        applicationContext.imageHolder().commitChanges(image);
        applicationComponents.imageLabel().setIcon(new ImageIcon(image));
        resetUIAfterProcessing();
    }

    private void resetUIAfterProcessing() {
        applicationComponents.progressPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.revalidate();
        applicationComponents.progressPanel().progressBar().setValue(100);
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
            BufferedImage loadedImage = ImageIO.read(imageFile);
            applicationContext.imageHolder().setCurrentImage(loadedImage);
            applicationContext.imageHolder().setOriginalImage(loadedImage);
            applicationContext.imageHolder().commitChanges(loadedImage);
            applicationComponents.imageLabel().setIcon(new ImageIcon(loadedImage));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
        }
    }

    public void saveFileAs() {
        if (applicationContext.imageHolder().getOriginalImage() == null) {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".png")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
            }
            try {
                ImageIO.write(applicationContext.imageHolder().getEditedImage(), "png", selectedFile);
                System.out.println("Image successfully saved " + selectedFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Unable to save image " + e.getMessage());
            }
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

                        applicationComponents.imageLabel().scrollRectToVisible(view);
                        origin = e.getPoint();
                    }
                }
            }
        };

        pane.getViewport().addMouseListener(ma);
        pane.getViewport().addMouseMotionListener(ma);
    }

    private void applyFilters(List<Filter> filters) {
        if (applicationContext.imageHolder().getCurrentImage() == null) {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
            return;
        }

        applicationComponents.progressPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        showOverlay(true);

        FilterExecutor.Builder builder = FilterExecutor.of(copyOfImage(applicationContext.imageHolder().getOriginalImage()));
        for (Filter filter : filters) {
            builder = builder.with(filter);
        }
        builder.progress(this::updateLoader)
                .process()
                .thenAccept(newImage -> {
                    applicationContext.imageHolder().commitChanges(copyOfImage(newImage));
                    updateCanvas(applicationContext.imageHolder().getEditedImage());
                    showOverlay(false);
                })
                .exceptionally(ex -> {
                    JOptionPane.showMessageDialog(this, "Error applying filter: " + ex.getMessage());
                    showOverlay(false);
                    return null;
                });
    }

    private static BufferedImage copyOfImage(BufferedImage other) {
        final var width = other.getWidth();
        final var height = other.getHeight();
        final var bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bufferedImage.setRGB(x, y, other.getRGB(x, y));
            }
        }
        return bufferedImage;
    }

    private static abstract class ComponentResizeEndListener
            extends ComponentAdapter
            implements ActionListener {

        private final Timer timer;

        public ComponentResizeEndListener() {
            this(30);
        }

        public ComponentResizeEndListener(int delayMS) {
            timer = new Timer(delayMS, this);
            timer.setRepeats(false);
            timer.setCoalesce(false);
        }

        @Override
        public void componentResized(ComponentEvent e) {
            timer.restart();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            resizeTimedOut();
        }

        public abstract void resizeTimedOut();
    }
}
