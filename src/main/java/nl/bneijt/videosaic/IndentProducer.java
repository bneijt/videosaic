
package nl.bneijt.videosaic;
import java.awt.image.BufferedImage;

interface IdentProducer
{
    /** Produce an ident string from an image
    */
    public Identity identify(BufferedImage img);
}

