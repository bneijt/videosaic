
package nl.bneijt.videosaic;
import java.awt.image.BufferedImage;
import java.util.List;

interface IdentProducer
{
    /** Produce an ident string from an image
    */
    public List<String> identify(BufferedImage img);
}

