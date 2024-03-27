import core.filter.DashedBorder;
import core.filter.Filter;
import core.filter.FilterExecutor;
import core.filter.Image;
import core.options.OptionsFactory;
import core.options.Setting;
import model.filter.boch.GammaFilter;
import model.filter.boch.MotionBlurFilter;
import model.filter.boch.SharpnessFilter;
import model.filter.darya.*;
import model.filter.eric.FitAlgorithm;
import model.filter.eric.LanczosResampling;
import model.filter.eric.VHSFilter;
import model.filter.leonid.*;
import model.filter.mikhail.MedianFilter;
import model.filter.mikhail.RotateImageFilter;
import model.filter.mikhail.WindFilter;
import model.options.SettingsDialogGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ImageFilterApp extends JFrame {
    private final Map<String, List<Setting<?>>> settings = new HashMap<>();
    int[] levels_kvant = {2, 2, 2};
    int window_size = 5;
    private JLabel imageLabel;
    private JProgressBar progressBar;
    private BufferedImage currentImage;
    private BufferedImage editedImage;
    boolean isOriginalImage;
    private BufferedImage originalImage;
    private JPanel overlayPanel;

    private File outputFile;

    private enum DitheringMethod {FLOYD_STEINBERG, ORDERED}

    public ImageFilterApp() {
        super("Image Filter Application");
        initializeUI();
        initSettings();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageFilterApp frame = new ImageFilterApp();
            frame.setVisible(true);
        });
    }

    private void initSettings() {
        settings.put("fit",
                List.of(
                        OptionsFactory.settingEnum(
                                FitAlgorithm.BILINEAR,
                                "choose algorythm",
                                "",
                                FitAlgorithm.class,
                                "fit_algo"
                        )
                )

        );

        settings.put("dithering",
                List.of(
                        OptionsFactory.settingInteger(
                                2,
                                "red colour level of quantization",
                                "",
                                2, 128,
                                "redDegree"
                        ),
                        OptionsFactory.settingInteger(
                                2,
                                "green colour level of quantization",
                                "",
                                2, 128,
                                "greenDegree"
                        ),
                        OptionsFactory.settingInteger(
                                2,
                                "blue colour level of quantization",
                                "",
                                2, 128,
                                "blueDegree"
                        ),
                        OptionsFactory.settingEnum(
                                DitheringMethod.ORDERED,
                                "Dithering method",
                                "",
                                DitheringMethod.class,
                                "ditherMethod"
                        )
                ));

        settings.put("bloom",
                List.of(
                        OptionsFactory.settingFloat(
                                0.3f,
                                "strength",
                                "",
                                0, 1,
                                "glowFactor"
                        ),
                        OptionsFactory.settingFloat(
                                0.7f,
                                "threshold",
                                "",
                                0, 1,
                                "threshold"
                        ),
                        OptionsFactory.settingInteger(
                                5,
                                "radius",
                                "", 1, 100,
                                "radius"
                        )
                ));

        settings.put("embossing",
                List.of(
                        OptionsFactory.settingEnum(
                                EmbossingFilter.Light.LEFT_TOP,
                                "Light source",
                                "",
                                EmbossingFilter.Light.class,
                                "light"
                        ),
                        OptionsFactory.settingInteger(
                                64,
                                "Brightness shift",
                                "",
                                0, 255,
                                "brightness"
                        )
                ));
        settings.put("watershed",
                List.of(
                        OptionsFactory.settingInteger(
                                2,
                                "red colour level of quantization",
                                "",
                                2, 128,
                                "redDegree"
                        ),
                        OptionsFactory.settingInteger(
                                2,
                                "green colour level of quantization",
                                "",
                                2, 128,
                                "greenDegree"
                        ),
                        OptionsFactory.settingInteger(
                                2,
                                "blue colour level of quantization",
                                "",
                                2, 128,
                                "blueDegree"
                        )
                ));

        settings.put("gamma",
                List.of(
                        OptionsFactory.settingInteger(
                                500,
                                "factor",
                                "",
                                1, 1000,
                                "gammaFactor"
                        )
                ));
        settings.put("gauss",
                List.of(
                        OptionsFactory.settingInteger(
                                3,
                                "gauss window",
                                "",
                                3, 11,
                                "window"
                        )
                ));
        settings.put("sobel",
                List.of(
                        OptionsFactory.settingInteger(
                                128,
                                "binarize",
                                "",
                                0, 254,
                                "binarize"
                        )
                ));
        settings.put("roberts",
                List.of(
                        OptionsFactory.settingInteger(
                                28,
                                "binarize",
                                "",
                                0, 254,
                                "binarize"
                        )
                ));
        settings.put("motionBlur",
                List.of(
                        OptionsFactory.settingInteger(
                                1,
                                "strength",
                                "",
                                0, 10,
                                "motionBlurStrength"
                        )
                ));

        settings.put("sharpness",
                List.of(
                        OptionsFactory.settingInteger(
                                1,
                                "strength",
                                "",
                                0, 10,
                                "sharpnessStrength"
                        )
                ));
        settings.put("wind",
                List.of(
                        OptionsFactory.settingEnum(
                                WindFilter.Direction.RIGHT,
                                "Wind direction",
                                "",
                                WindFilter.Direction.class,
                                "wind_direction"
                        ),
                        OptionsFactory.settingInteger(
                                3,
                                "Threshold",
                                "Higher values restrict the effect to fewer areas of the image.",
                                0, 50,
                                "wind_threshold"
                        ),
                        OptionsFactory.settingInteger(
                                5,
                                "Strength",
                                "Higher values increase the magnitude of the effect.",
                                0, 100,
                                "wind_strength"
                        )
                ));
        settings.put("rotate",
                List.of(
                        OptionsFactory.settingInteger(
                                90,
                                "Angle",
                                "Angle of rotate.",
                                -180, 180,
                                "rotate_angle"
                        )));
    }

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

    private void initializeUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setBorder(new DashedBorder(Color.black, 2));
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        isOriginalImage = false;
        createMenuBar();
        createToolbar();
        JScrollPane jScrollPane = new JScrollPane(imageLabel);
        addMouseDragFeature(jScrollPane);
        createOverlayPanel();
        add(jScrollPane, BorderLayout.CENTER);
        setMinimumSize(new Dimension(800, 600));
        pack();
    }

    private void fitImageToScreen() {
        if (currentImage != null) {
            Dimension screenSize = this.getSize();
            double widthRatio = screenSize.getWidth() / currentImage.getWidth();
            double heightRatio = screenSize.getHeight() / currentImage.getHeight();
            double ratio = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (currentImage.getWidth() * ratio);
            int newHeight = (int) (currentImage.getHeight() * ratio);

            final var s = settings.getOrDefault("fit", null);
            if (s == null) {
                final var filter = new LanczosResampling(newWidth, newHeight);
                applyFilters(filter);
            } else {
                final var algo = s.stream().filter(it -> it.getId().equals("fit_algo")).findFirst();
                if (algo.isEmpty()) {
                    final var filter = new LanczosResampling(newWidth, newHeight);
                    applyFilters(filter);
                    return;
                }
                FitAlgorithm algorithm = algo.get().value();
                applyFilters(algorithm.filter(newWidth, newHeight));
            }
        } else {
            JOptionPane.showMessageDialog(this, "No image loaded to fit to screen.");
        }
    }

    private void chooseMotionBlurArgs() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("motionBlur");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("motionBlur", prefs);
                parseMotionBlurArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseMotionBlurArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("motionBlur", null);

            // if filter didn't configured
            if (s == null) {
                applyMotionBlurEffect(1);
            } else {
                final int strength = s.stream().filter(it -> it.getId().equals("motionBlurStrength")).findFirst().get().value();
                applyMotionBlurEffect(strength);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyMotionBlurEffect(int strength) {
        if (editedImage != null) {
            MotionBlurFilter motionBlurFilter = new MotionBlurFilter(strength);
            applyFilters(motionBlurFilter);

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void chooseGammaArgs() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("gamma");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("gamma", prefs);
                parseGammaArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseGammaArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("gamma", null);

            // if filter didn't configured
            if (s == null) {
                applyGammaEffect(300);
            } else {
                final int gamma = s.stream().filter(it -> it.getId().equals("gammaFactor")).findFirst().get().value();
                applyGammaEffect(gamma);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyGammaEffect(int gamma) {
        if (editedImage != null) {
            GammaFilter gammaFilter = new GammaFilter(gamma);
            applyFilters(gammaFilter);

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void chooseSharpnessArgs() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("sharpness");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("sharpness", prefs);
                parseSharpnessArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseSharpnessArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("sharpness", null);

            // if filter didn't configured
            if (s == null) {
                applySharpnessEffect(1);
            } else {
                final int strength = s.stream().filter(it -> it.getId().equals("sharpnessStrength")).findFirst().get().value();
                applySharpnessEffect(strength);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applySharpnessEffect(int strength) {
        if (editedImage != null) {
            SharpnessFilter sharpnessFilter = new SharpnessFilter(strength);
            applyFilters(sharpnessFilter);

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void chooseBloomArgs() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("bloom");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("bloom", prefs);
                parseBloomArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseBloomArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("bloom", null);

            // if filter didn't configured
            if (s == null) {
                applyBloomEffect(0.3f, 0.7f, 5);
            } else {
                final float glowFactor = s.stream().filter(it -> it.getId().equals("glowFactor")).findFirst().get().value();
                final float threshold = s.stream().filter(it -> it.getId().equals("threshold")).findFirst().get().value();
                final int radius = s.stream().filter(it -> it.getId().equals("radius")).findFirst().get().value();
                applyBloomEffect(glowFactor, threshold, radius);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyBloomEffect(float glowFactor, float threshold, int radius) {
        if (editedImage != null) {
            BloomFilter bloomFilter = new BloomFilter(glowFactor, threshold);
            GaussianBlurFilter blurFilter = new GaussianBlurFilter(radius);
            MixFilter mixFilter = new MixFilter(new Image(editedImage));
            applyFilters(bloomFilter, blurFilter, mixFilter);

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void chooseEmbossingArgs() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("embossing");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("embossing", prefs);
                parseEmbossingArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseEmbossingArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("embossing", null);

            // if filter didn't configured
            if (s == null) {
                applyFilters(new EmbossingFilter(EmbossingFilter.Light.LEFT_TOP, 64));
            } else {
                final EmbossingFilter.Light light = s.stream().filter(it -> it.getId().equals("light")).findFirst().get().value();
                final int brightnessIncrease = s.stream().filter(it -> it.getId().equals("brightness")).findFirst().get().value();
                applyFilters(new EmbossingFilter(light, brightnessIncrease));
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyWaterShed() {
        if (editedImage != null) {
            WaterShedFilter waterShedFilter = new WaterShedFilter(levels_kvant);
            ColorStretchFilter colorStretchFilter = new ColorStretchFilter(levels_kvant);
            FillColorFilter fillColorFilter = new FillColorFilter();
            applyFilters(waterShedFilter, colorStretchFilter, fillColorFilter);

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void chooseWaterShedArgs() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("watershed");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("watershed", prefs);
                parseWaterShedArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseWaterShedArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("watershed", null);
            // if filter didn't configured
            if (s == null) {
                applyFilters(new WaterShedFilter(new int[]{2, 2, 2}));
            } else {
                levels_kvant[0] = s.stream().filter(it -> it.getId().equals("redDegree")).findFirst().get().value();
                levels_kvant[1] = s.stream().filter(it -> it.getId().equals("greenDegree")).findFirst().get().value();
                levels_kvant[2] = s.stream().filter(it -> it.getId().equals("blueDegree")).findFirst().get().value();
                applyWaterShed();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyGaussianBlur() {
        if (editedImage != null) {
            GaussianBlurFilter filter = new GaussianBlurFilter(window_size);
            applyFilters(filter);

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void chooseGaussianBlurArgs() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("gauss");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("gauss", prefs);
                parseGaussianBlurArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseGaussianBlurArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("gauss", null);
            // if filter didn't configured
            if (s == null) {
                applyFilters(new GaussianBlurFilter(3));
            } else {
                window_size = s.stream().filter(it -> it.getId().equals("window")).findFirst().get().value();
                applyGaussianBlur();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void chooseSobelArgs(){
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("sobel");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("sobel", prefs);
                parseSobelArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseSobelArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("sobel", null);
            // if filter didn't configured
            if (s == null) {
                applyFilters(new SobelFilter(128));
            } else {
                int binarize = s.stream().filter(it -> it.getId().equals("binarize")).findFirst().get().value();
                SobelFilter filter = new SobelFilter(binarize);
                applyFilters(filter);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }
    private void chooseRobertsArgs(){
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("roberts");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("roberts", prefs);
                parseRobertsArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseRobertsArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("roberts", null);
            // if filter didn't configured
            if (s == null) {
                applyFilters(new RobertsFilter(28));
            } else {
                int binarize = s.stream().filter(it -> it.getId().equals("binarize")).findFirst().get().value();
                RobertsFilter filter = new RobertsFilter(binarize);
                applyFilters(filter);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyDithering(DitheringMethod ditheringMethod, int redRank, int greenRank, int blueRank) {
        switch (ditheringMethod) {
            case FLOYD_STEINBERG -> {
                FSDithering filter = new FSDithering(redRank, greenRank, blueRank);
                applyFilters(filter);
            }
            case ORDERED -> {
                OrderedDithering filter = new OrderedDithering(redRank, greenRank, blueRank);
                applyFilters(filter);
            }
        }
    }

    private void chooseDitheringOrder() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("dithering");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("dithering", prefs);
                parseDitherArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseDitherArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("dithering", null);

            // if filter didn't configured
            if (s == null) {
                applyDithering(DitheringMethod.ORDERED, 2, 2, 2);
            } else {
                final int redRank = s.stream().filter(it -> it.getId().equals("redDegree")).findFirst().get().value();
                final int greenRank = s.stream().filter(it -> it.getId().equals("greenDegree")).findFirst().get().value();
                final int blueRank = s.stream().filter(it -> it.getId().equals("blueDegree")).findFirst().get().value();
                final DitheringMethod ditherMethod = s.stream().filter(it -> it.getId().equals("ditherMethod")).findFirst().get().value();

                applyDithering(ditherMethod, redRank, greenRank, blueRank);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void chooseWindArgs() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("wind");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("wind", prefs);
                parseWindArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseWindArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("wind", null);

            // if filter didn't configured
            if (s == null) {
                applyFilters(new WindFilter(WindFilter.Direction.RIGHT, 5, 3));
            } else {
                final WindFilter.Direction direction = s.stream().filter(it -> it.getId().equals("wind_direction")).findFirst().get().value();
                final int windThreshold = s.stream().filter(it -> it.getId().equals("wind_threshold")).findFirst().get().value();
                final int windStrength = s.stream().filter(it -> it.getId().equals("wind_strength")).findFirst().get().value();
                applyFilters(new WindFilter(direction, windStrength, windThreshold));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void chooseRotateArgs() {
        if (editedImage != null) {
            final List<Setting<?>> prefs = settings.get("rotate");
            SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
                settings.put("rotate", prefs);
                parseRotateArgs();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void parseRotateArgs() {
        if (editedImage != null) {
            final var s = settings.getOrDefault("rotate", null);

            // if filter didn't configured
            if (s == null) {
                applyFilters(new WindFilter(WindFilter.Direction.RIGHT, 5, 3));
            } else {
                final int rotateAngle = s.stream().filter(it -> it.getId().equals("rotate_angle")).findFirst().get().value();
                applyFilters(new RotateImageFilter(rotateAngle));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void applyWatercolor() {
        if (editedImage != null) {
            MedianFilter medianFilter = new MedianFilter(24);
            SharpnessFilter sharpnessFilter = new SharpnessFilter(200);
            applyFilters(medianFilter, sharpnessFilter);
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }
    }

    private void createToolbar() {
        JToolBar toolBar = new JToolBar("Image Tools");
        toolBar.setFloatable(false);



        JButton chooseImageButton = new JButton();
        chooseImageButton.addActionListener(e -> chooseImage());
        chooseImageButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("chooseIcon.png"))));
        chooseImageButton.setToolTipText("Choose image for editing");
        toolBar.add(chooseImageButton);

        JButton fitToScreenButton = new JButton();
        fitToScreenButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("fit.png"))));
        fitToScreenButton.addActionListener(e -> chooseFitAlgorithm());
        fitToScreenButton.setToolTipText("Fit image to screen size");
        toolBar.add(fitToScreenButton);

        JButton applyMonochromeButton = new JButton("");
        applyMonochromeButton.addActionListener(e -> applyFilters(new MonochromeFilter()));
        applyMonochromeButton.setToolTipText("Apply Monochrome filter");
        applyMonochromeButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("monochromeIcon.png"))));
        toolBar.add(applyMonochromeButton);

        JButton applyNegativeButton = new JButton("");
        applyNegativeButton.addActionListener(e -> applyFilters(new NegativeFilter()));
        applyNegativeButton.setToolTipText("Apply Negative (color invert) filter");
        applyNegativeButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("invertIcon.png"))));
        toolBar.add(applyNegativeButton);

        JButton applyGaussianBlur = new JButton("");
        applyGaussianBlur.addActionListener(e -> chooseGaussianBlurArgs());
        applyGaussianBlur.setToolTipText("Apply Gaussian Blur filter");
        applyGaussianBlur.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("blurIcon.png"))));

        toolBar.add(applyGaussianBlur);

        JButton applyVhs = new JButton();
        applyVhs.addActionListener(e -> applyFilters(new VHSFilter()));
        applyVhs.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("videoIcon.png"))));
        applyVhs.setToolTipText("Apply VHS filter");
        toolBar.add(applyVhs);

        JButton applySobel = new JButton();
        applySobel.addActionListener(e -> chooseSobelArgs());
        applySobel.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("sobel.png"))));
        applySobel.setToolTipText("Apply Sobel filter");
        toolBar.add(applySobel);

        JButton applyRoberts = new JButton();
        applyRoberts.addActionListener(e -> chooseRobertsArgs());
        applyRoberts.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("roberts.png"))));
        applyRoberts.setToolTipText("Apply Roberts filter");
        toolBar.add(applyRoberts);

        JButton applyBloom = new JButton();
        applyBloom.addActionListener(e -> chooseBloomArgs());
        applyBloom.setToolTipText("Apply Bloom filter");
        applyBloom.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("bloomIcon.png"))));
        toolBar.add(applyBloom);

        JButton applyWaterShedButton = new JButton();
        applyWaterShedButton.addActionListener(e -> chooseWaterShedArgs());
        applyWaterShedButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("waterIcon.png"))));
        applyWaterShedButton.setToolTipText("Apply Watershed filter");
        toolBar.add(applyWaterShedButton);

        JButton applyDitheringButton = new JButton();
        applyDitheringButton.addActionListener(e -> chooseDitheringOrder());
        applyDitheringButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("ditherIcon.png"))));
        applyDitheringButton.setToolTipText("Apply dithering");
        toolBar.add(applyDitheringButton);

        JButton applyEmbossingButton = new JButton();
        applyEmbossingButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("embossingIcon.png"))));
        applyEmbossingButton.addActionListener(e -> chooseEmbossingArgs());
        applyEmbossingButton.setToolTipText("Apply embossing");
        toolBar.add(applyEmbossingButton);

        JButton applyMotionBlurButton = new JButton();
        applyMotionBlurButton.addActionListener(e -> chooseMotionBlurArgs());
        applyMotionBlurButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("motionBlurIcon.png"))));
        //applyMotionBlurButton.addActionListener(e -> applyFilters(new MotionBlurFilter(1)));
        applyMotionBlurButton.setToolTipText("Apply motion blur");
        toolBar.add(applyMotionBlurButton);

        JButton applyGammaButton = new JButton();
        applyGammaButton.addActionListener(e -> chooseGammaArgs());
        applyGammaButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("blurIcon.png"))));
        //applyGammaButton.addActionListener(e -> applyFilters(new GammaFilter(5f)));
        applyGammaButton.setToolTipText("Apply gamma-correction");
        toolBar.add(applyGammaButton);

        JButton applySharpnessButton = new JButton();
        applySharpnessButton.addActionListener(e -> chooseSharpnessArgs());
        applySharpnessButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("sharpenIcon.png"))));
        applySharpnessButton.setToolTipText("Apply sharpness");
        toolBar.add(applySharpnessButton);

        JToggleButton switchImageButton = new JToggleButton();
        switchImageButton.addActionListener(e -> onSwitchImagePressed(switchImageButton));
        switchImageButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("exposeIcon.png"))));
        switchImageButton.setToolTipText("Switches image to original\\edited version");
        toolBar.add(switchImageButton);

        JButton applyWatercolorButton = new JButton();
        applyWatercolorButton.addActionListener(e -> applyWatercolor());
        applyWatercolorButton.setToolTipText("Apply Watercolor filter");
        applyWatercolorButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("paletteIcon.png"))));
        toolBar.add(applyWatercolorButton);

        JButton applyRotate = new JButton();

        RotateImageFilter rotateImageFilter = new RotateImageFilter(45);
        applyRotate.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("rotate.png"))));
        applyRotate.addActionListener(e -> applyFilters(rotateImageFilter));
        applyRotate.setToolTipText("Apply rotate");
        toolBar.add(applyRotate);

        JButton applyWindFilterButton = new JButton();
        applyWindFilterButton.addActionListener(e -> chooseWindArgs());
        applyWindFilterButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("wind.png"))));
        applyWindFilterButton.setToolTipText("Apply Wind filter");
        toolBar.add(applyWindFilterButton);

        add(toolBar, BorderLayout.NORTH);
    }

    private JMenu createFilterMenu() {
        JMenu filterMenu = new JMenu();
        filterMenu.setText("Filter");

        JMenuItem monochrome = new JMenuItem("Monochrome");
        monochrome.addActionListener(e -> applyFilters(new MonochromeFilter()));
        filterMenu.add(monochrome);

        JMenuItem negative = new JMenuItem("Negative");
        negative.addActionListener(e -> applyFilters(new NegativeFilter()));
        filterMenu.add(negative);

        JMenuItem blur = new JMenuItem("Gaussian blur");
        blur.addActionListener(e -> chooseGaussianBlurArgs());
        filterMenu.add(blur);

        JMenuItem bloom = new JMenuItem("Bloom");
        bloom.addActionListener(e -> chooseBloomArgs());
        filterMenu.add(bloom);

        JMenuItem embossing = new JMenuItem("Embossing");
        embossing.addActionListener(e -> chooseEmbossingArgs());
        filterMenu.add(embossing);

        JMenuItem vhs = new JMenuItem("VHS");
        vhs.addActionListener(e -> applyFilters(new VHSFilter()));
        filterMenu.add(vhs);

        JMenuItem gamma = new JMenuItem("Gamma");
        gamma.addActionListener(e -> chooseGammaArgs());
        filterMenu.add(gamma);

        JMenuItem motionBlur = new JMenuItem("Motion Blur");
        motionBlur.addActionListener(e -> chooseMotionBlurArgs());
        filterMenu.add(motionBlur);

        JMenuItem sharpness = new JMenuItem("Sharpness");
        sharpness.addActionListener(e -> chooseSharpnessArgs());
        filterMenu.add(sharpness);

        JMenuItem watershed = new JMenuItem("WaterShed");
        watershed.addActionListener(e -> chooseWaterShedArgs());
        filterMenu.add(watershed);

        JMenuItem sobel = new JMenuItem("sobel");
        sobel.addActionListener(e -> chooseSobelArgs());
        filterMenu.add(sobel);

        JMenuItem roberts = new JMenuItem("roberts");
        roberts.addActionListener(e -> chooseRobertsArgs());
        filterMenu.add(roberts);

        JMenuItem wind = new JMenuItem("Wind");
        wind.addActionListener(e -> chooseWindArgs());
        filterMenu.add(wind);

        JMenuItem watercolor = new JMenuItem("Watercolor");
        watercolor.addActionListener(e -> applyWatercolor());
        filterMenu.add(watercolor);

        JMenuItem rotate = new JMenuItem("Rotate");
        rotate.addActionListener(e -> chooseRotateArgs());
        filterMenu.add(rotate);

        return filterMenu;
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

    private JMenu createModifyMenu() {
        JMenu modifyMenu = new JMenu("Modify");
        JMenuItem fit = new JMenuItem("Fit image to screen");
        fit.addActionListener(e -> chooseFitAlgorithm());
        modifyMenu.add(fit);

        JMenuItem dithering = new JMenuItem("Dither");
        dithering.addActionListener(e -> chooseDitheringOrder());
        modifyMenu.add(dithering);

        return modifyMenu;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(e -> chooseImage());
        fileMenu.add(open);

        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(e -> saveFile());
        fileMenu.add(save);

        JMenuItem saveAs = new JMenuItem("Save as");
        saveAs.addActionListener(e -> saveFileAs());
        fileMenu.add(saveAs);

        return fileMenu;
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createHelpMenu());
        menuBar.add(createFileMenu());
        menuBar.add(createFilterMenu());
        menuBar.add(createModifyMenu());

        setJMenuBar(menuBar);
    }

    private void onSwitchImagePressed(JToggleButton button) {
        if (currentImage != null) {
            if (isOriginalImage) {
                isOriginalImage = false;
                updateCanvas(editedImage);
                button.setSelected(false);

            } else {
                isOriginalImage = true;
                updateCanvas(originalImage);
                button.setSelected(true);

            }
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an image first.");
        }

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
        this.currentImage = image;
        imageLabel.setIcon(new ImageIcon(image));
        resetUIAfterProcessing();
    }

    private void resetUIAfterProcessing() {
        overlayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.revalidate();
        progressBar.setValue(100);
    }

    private void chooseFitAlgorithm() {
        final List<Setting<?>> prefs = settings.get("fit");
        SettingsDialogGenerator.generateAndShowDialog(prefs, () -> {
            settings.put("fit", prefs);
            fitImageToScreen();
        });
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
            currentImage = ImageIO.read(imageFile);
            originalImage = Image.of(currentImage);
            editedImage = Image.of(currentImage);
            imageLabel.setIcon(new ImageIcon(currentImage));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
        }
    }

    public void saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            // Получаем выбранный файл
            File selectedFile = fileChooser.getSelectedFile();

            // Сохраняем BufferedImage в выбранный файл
            if (!selectedFile.getName().toLowerCase().endsWith(".png")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
            }
            outputFile = selectedFile;
            saveFile();
        }
    }

    public void saveFile() {
        if (outputFile == null) {
            saveFileAs();
        } else {
            try {
                ImageIO.write(currentImage, "png", outputFile);
                System.out.println("изображение успешно сохранено в " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении изображения: " + e.getMessage());
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
        if (currentImage == null) {
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
                .thenAccept(newImage -> {
                    editedImage = Image.of(newImage);
                    updateCanvas(editedImage);
                    showOverlay(false);
                })
                .exceptionally(ex -> {
                    JOptionPane.showMessageDialog(this, "Error applying filter: " + ex.getMessage());
                    showOverlay(false);
                    return null;
                });
    }
}
