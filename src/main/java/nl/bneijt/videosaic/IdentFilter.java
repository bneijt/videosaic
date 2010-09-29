package nl.bneijt.videosaic;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import nl.bneijt.videosaic.IdentProducer;
/** Image filter that results in dientifiers

Probably should use something more generic here? Maybe Spring connector messages or Scala actors??
*/

class IdentFilter implements Runnable
{
    private IdentProducer idents;
    private ImageProducer imgSource;
    public IdentFilter(IdentProducer p, ImageProducer img)
    {
        idents = p;
        imgSource = img;
    }
    public void run()
    {
        while(imgSource.hasNext())
        {
            BufferedImage i = imgSource.next();
            String identity = idents.identify(i);
            ///yield identity???
        }
    }
}
