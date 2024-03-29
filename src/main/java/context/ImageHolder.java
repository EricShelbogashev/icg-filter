package context;

import java.awt.image.BufferedImage;


public class ImageHolder {
    private BufferedImage originalImage;
    private BufferedImage currentImage;

    public void rollBack() {
        currentImage = originalImage;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(BufferedImage currentImage) {
        this.currentImage = currentImage;
    }
}
