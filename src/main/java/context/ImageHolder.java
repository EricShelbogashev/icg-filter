package context;

import java.awt.image.BufferedImage;


public class ImageHolder {
    private BufferedImage originalImage;
    private BufferedImage currentImage;
    private BufferedImage editedImage;

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

    public BufferedImage getEditedImage() {return editedImage;}

    public void setEditedImage(BufferedImage image) {
        this.editedImage = image;
    }

    public boolean isEditedImage() {
        return (currentImage == editedImage);
    }
}
