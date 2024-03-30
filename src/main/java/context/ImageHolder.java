package context;

import java.awt.image.BufferedImage;


public class ImageHolder {
    private BufferedImage originalImage;
    private BufferedImage currentImage;
    private BufferedImage resizedOriginalImage;
    private BufferedImage resizedCurrentImage;


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

    public BufferedImage getResizedOriginalImage() {
        return resizedOriginalImage;
    }

    public void setResizedOriginalImage(BufferedImage resizedOriginalImage) {
        this.resizedOriginalImage = resizedOriginalImage;
    }

    public BufferedImage getResizedCurrentImage() {
        return resizedCurrentImage;
    }

    public void setResizedCurrentImage(BufferedImage resizedCurrentImage) {
        this.resizedCurrentImage = resizedCurrentImage;
    }
}
