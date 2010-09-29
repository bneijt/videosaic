
/** Image filter that results in dientifiers

Probably should use something more generic here? Maybe Spring connector messages or Scala actors??
*/

class IdentFilter implements Runnable
{
    private IdentityProducer idents;
    private ImageProducer imgSource;
    public IdentFilter(IdentityProducer p, ImageProducer img)
    {
        idents = p;
        imgSource = img;
    }
    public void run()
    {
        while(imgSource.hasNext())
        {
            BufferedImage i = imgSource.next();
            String identity = idents.identifyImage(i);
            ///yield identity???
        }
    }
}
