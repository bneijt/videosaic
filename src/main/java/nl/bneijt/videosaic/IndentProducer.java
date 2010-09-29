
package nl.bneijt.videosaic;
import java.awt.image.BufferedImage;

interface IdentProducer
{
    /** Produce an ident string from an image
    */
    public String identify(BufferedImage img);
}

