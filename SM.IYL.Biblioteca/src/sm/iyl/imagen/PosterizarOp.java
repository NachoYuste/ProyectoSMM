
package sm.iyl.imagen;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;


/**
 * Clase que implementa el operador proprio PosterizarOp
 * @author Ignacio Yuste López
 */
public class PosterizarOp extends BufferedImageOpAdapter{

    /**
     * Create new form PosterizarOp
     */
    
    private int niveles;
    
    /**
     * Constructor de la clase, indicanddo los niveles para posterizar
     * @param niveles 
     */
    public PosterizarOp(int niveles){
        this.niveles = niveles;
    }
    
    /**
     * Sobrecarga del método filter, crea y aplica el filtro de posterización
     * según los niveles de la clase a una imagen
     * @param src Imagen fuente
     * @param dest Imagen destino
     * @return dest Imagen destino
     */
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if(src == null){
            throw new NullPointerException("src image is null");
        }
        if(dest == null){
            dest = createCompatibleDestImage(src, null);
        }
        
        WritableRaster srcRaster = src.getRaster();
        WritableRaster destRaster = dest.getRaster();
        int sample;
        float k = 256f/niveles;
        
        for(int i=0; i<src.getWidth(); i++){
            for(int j=0; j<src.getHeight(); j++){
                for(int band=0; band<srcRaster.getNumBands(); band++){
                    sample = srcRaster.getSample(i, j, band);
                    sample = (int) (k * ((int)(sample/k)));
                    destRaster.setSample(i, j, band, sample);
                }
            }
        }
        
        return dest;
    }
}
