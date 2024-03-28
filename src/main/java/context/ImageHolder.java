package context;

import java.awt.image.BufferedImage;

public class ImageHolder {
    private BufferedImage originalImage;
    private BufferedImage editedImage = null;

    private BufferedImage currentImage;

    public void commitChanges(BufferedImage editedImage) {
        this.editedImage = editedImage;
    }

    public void rollBack() {
        currentImage = originalImage;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    public BufferedImage getEditedImage() {
        return editedImage;
    }

    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(BufferedImage currentImage) {
        this.currentImage = currentImage;
    }
}
