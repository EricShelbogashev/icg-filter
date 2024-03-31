import context.ApplicationComponents;
import context.ApplicationContext;
import context.ApplicationProperties;
import context.ImageHolder;
import core.filter.Image;
import core.options.Setting;
import model.filter.egor.ComponentResizeEndListener;
import model.options.SettingsDialogGenerator;
import view.ProgressPanel;
import view.filters.FilterViewUnit;
import view.filters.bloom.BloomFilterViewUnit;
import view.filters.dithering.DitheringFilterViewUnit;
import view.filters.embossing.EmbossingFilterViewUnit;
import view.filters.fit.FitImageToScreenFilterViewUnit;
import view.filters.fit.FitImageTurnOn;
import view.filters.gamma.GammaFilterViewUnit;
import view.filters.gaussian.GaussianFilterViewInit;
import view.filters.monochrome.MonochromeFilterViewUnit;
import view.filters.motionblur.MotionBlurViewUnit;
import view.filters.negative.NegativeFilterViewUnit;
import view.filters.roberts.RobertsFilterViewInit;
import view.filters.rotate.RotateImageViewUnit;
import view.filters.sharpness.SharpnessViewUnit;
import view.filters.sobel.SobelFilterViewInit;
import view.filters.vhs.VHSFilterViewUnit;
import view.filters.watercolor.WatercolorFilterViewUnit;
import view.filters.watershed.WaterShedFilterViewInit;
import view.filters.wind.WindFilterViewUnit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ImageFilterApp extends JFrame {
    private final ApplicationContext context;
    private final ApplicationComponents components;
    private final List<FilterViewUnit> filterUnits;
    private final FitImageToScreenFilterViewUnit fitFilterUnit;

    public ImageFilterApp(ApplicationProperties applicationProperties) {
        super("Image Filter Application");
        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        JScrollPane scrollPane = createScrollPanel(imageLabel);
        fitFilterUnit = new FitImageToScreenFilterViewUnit(scrollPane::getSize, this::updateLoader);
        filterUnits = List.of(
                new WindFilterViewUnit(this::updateLoader),
                new GammaFilterViewUnit(this::updateLoader),
                new RotateImageViewUnit(this::updateLoader),
                new BloomFilterViewUnit(this::updateLoader),
                new NegativeFilterViewUnit(this::updateLoader),
                new EmbossingFilterViewUnit(this::updateLoader),
                new MonochromeFilterViewUnit(this::updateLoader),
                new DitheringFilterViewUnit(this::updateLoader),
                new SharpnessViewUnit(this::updateLoader),
                new MotionBlurViewUnit(this::updateLoader),
                new GaussianFilterViewInit(this::updateLoader),
                new RobertsFilterViewInit(this::updateLoader),
                new SobelFilterViewInit(this::updateLoader),
                new WaterShedFilterViewInit(this::updateLoader),
                new VHSFilterViewUnit(this::updateLoader),
                new WatercolorFilterViewUnit(this::updateLoader)
        );
        components = new ApplicationComponents(
                imageLabel,
                createOverlayPanel(),
                scrollPane,
                createShowOriginalImageButton()
        );
        initializeUI();
        ImageHolder imageHolder = new ImageHolder();
        context = new ApplicationContext(imageHolder, applicationProperties);
        addComponentListener(new ComponentResizeEndListener(30) {
            @Override
            public void resizeTimedOut() {
                FitImageTurnOn turnedOn = fitFilterUnit.getFitOptions().on().value();
                if (turnedOn == FitImageTurnOn.ON && context.imageHolder().getCurrentImage() != null) {
                    fitCurrentImageToScreen().join();
                    updateCanvas(context.imageHolder().getResizedCurrentImage());
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

    private JToggleButton createShowOriginalImageButton() {
        JToggleButton showOriginalImageButton = new JToggleButton("Show original");
        showOriginalImageButton.setToolTipText("Select to show original image.");
        showOriginalImageButton.addActionListener(e -> {
            onSwitchImagePressed(showOriginalImageButton);
        });
        showOriginalImageButton.setEnabled(false);
        return showOriginalImageButton;
    }

    private void showOverlay(boolean show) {
        SwingUtilities.invokeLater(() ->
                components
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

        JButton fitButton = new JButton();
        fitButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(fitFilterUnit.getIconPath()))));
        fitButton.addActionListener(e -> {
            initFitFilter();
        });
        toolBar.add(fitButton);
        filterUnits.forEach(filterViewUnit -> {
            try {
                JButton toolbarButton = new JButton();
                toolbarButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(filterViewUnit.getIconPath()))));
                toolbarButton.setToolTipText(filterViewUnit.getTipText());
                toolbarButton.addActionListener(e -> {
                    applyFilter(context.imageHolder().getOriginalImage(), filterViewUnit);
                });
                toolBar.add(toolbarButton);
            } catch (NullPointerException ignored) {
            }
        });

        toolBar.add(components.showOriginalImageButton());
        add(toolBar, BorderLayout.NORTH);
    }

    private JMenu createFilterMenuItems() {
        JMenu filterMenu = new JMenu();
        filterMenu.setText("Filters");

        JMenuItem fitMenuItem = new JMenuItem(fitFilterUnit.getFilterName());
        fitMenuItem.addActionListener(e -> {
            initFitFilter();
            if (context.imageHolder().getOriginalImage() != null) {
                initFitFilter();
            }
        });
        filterMenu.add(fitMenuItem);
        filterUnits.forEach(filterViewUnit -> {
            JMenuItem menuItem = new JMenuItem(filterViewUnit.getFilterName());
            menuItem.addActionListener(e -> {
                applyFilter(context.imageHolder().getOriginalImage(), filterViewUnit);
            });
            filterMenu.add(menuItem);
        });
        return filterMenu;
    }

    private void applyFilter(BufferedImage image, FilterViewUnit filterViewUnit) {
        if (context.imageHolder().getOriginalImage() == null) {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
            return;
        }
        components.progressPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        showOverlay(true);

        List<Setting<?>> options = filterViewUnit.getSettings();
        if (options == null) {
            acceptFilterToImageHolder(image, filterViewUnit);
        } else {
            SettingsDialogGenerator.generateAndShowDialog(options, () -> {
                acceptFilterToImageHolder(image, filterViewUnit);
            });
        }

    }

    private void acceptFilterToImageHolder(BufferedImage image, FilterViewUnit filterViewUnit) {
        filterViewUnit.applyFilter(image)
                .thenAccept(newImage -> {
                    context.imageHolder().setCurrentImage(Image.of(newImage));
                    fitCurrentImageToScreen().join();
                    updateCanvas(context.imageHolder().getResizedCurrentImage());
                    components.showOriginalImageButton().setSelected(false);
                    showOverlay(false);
                })
                .exceptionally(ex -> {
                    JOptionPane.showMessageDialog(this, "Error applying filter: " + ex.getMessage());
                    showOverlay(false);
                    return null;
                });
    }

    private CompletableFuture<Void> fitCurrentImageToScreen() {
        return fitFilterUnit
                .applyFilter(context.imageHolder().getCurrentImage())
                .thenAccept(newImage -> {
                    context.imageHolder().setResizedCurrentImage(newImage);
                })
                .thenAccept(e -> {
                    fitFilterUnit
                            .applyFilter(context.imageHolder().getOriginalImage())
                            .thenAccept(resizedOriginal -> {
                                context.imageHolder().setResizedOriginalImage(resizedOriginal);
                            })
                            .join();
                });
    }

    private void initFitFilter() {
        List<Setting<?>> options = fitFilterUnit.getSettings();
        if (options == null) {
            throw new IllegalStateException("There are no filter settings to fit the image to the screen.");
        }
        SettingsDialogGenerator.generateAndShowDialog(options, () -> {
            if (context.imageHolder().getOriginalImage() != null) {
                fitCurrentImageToScreen().join();
                updateCanvas(context.imageHolder().getResizedCurrentImage());
            }
        });
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

    private void onSwitchImagePressed(JToggleButton button) {
        if (context.imageHolder().getResizedOriginalImage() == null || context.imageHolder().getResizedCurrentImage() == null) {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
            button.setSelected(false);
            button.setEnabled(false);
            return;
        }
        if (button.isSelected()) {
            updateCanvas(context.imageHolder().getResizedOriginalImage());
        } else {
            updateCanvas(context.imageHolder().getResizedCurrentImage());
        }
    }

    private void updateLoader(float percent) {
        if (!components.progressPanel().progressBar().isVisible()) {
            components.progressPanel().progressBar().setVisible(true);
        }

        int progressValue = Math.round(percent * 100);
        components.progressPanel().progressBar().setValue(progressValue);

        if (!components.progressPanel().progressBar().isStringPainted()) {
            components.progressPanel().progressBar().setStringPainted(true);
        }
    }

    private void updateCanvas(BufferedImage image) {
        components.imageLabel().setIcon(new ImageIcon(image));
        resetUIAfterProcessing();
    }

    private void resetUIAfterProcessing() {
        components.progressPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.revalidate();
        components.progressPanel().progressBar().setValue(100);
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
            context.imageHolder().setCurrentImage(loadedImage);
            context.imageHolder().setOriginalImage(loadedImage);
            fitCurrentImageToScreen().join();
            updateCanvas(context.imageHolder().getResizedOriginalImage());
            components.showOriginalImageButton().setEnabled(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
        }
    }

    public void saveFileAs() {
        if (context.imageHolder().getOriginalImage() == null) {
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
                ImageIO.write(context.imageHolder().getCurrentImage(), "png", selectedFile);
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

                        components.imageLabel().scrollRectToVisible(view);
                        origin = e.getPoint();
                    }
                }
            }
        };

        pane.getViewport().addMouseListener(ma);
        pane.getViewport().addMouseMotionListener(ma);
    }
}
