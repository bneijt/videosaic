
package nl.bneijt.videosaic;
import nl.bneijt.videosaic.IdentProducer;
import java.awt.image.BufferedImage;

/** Mean of quadrant ident producer
*/
class MeanIdentProducer implements IdentProducer
{
    /** Return string based ident of BufferedImage
        This will return 4 strings based on the intensity
        of their 4 quadrants
    */
    public String identify(BufferedImage i)
    {
        //Split the image into 4 quadrants
        //Find the mean intensity of each quadrant
        char levels[] = {254,0,18,2}; //Resulting intensities
        //Return the value as a string
        return "";//(new String(".")).join(levels);
    }
}


