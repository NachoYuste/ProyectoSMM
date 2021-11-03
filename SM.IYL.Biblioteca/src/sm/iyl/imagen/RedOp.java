
package sm.iyl.imagen;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;

/**
 * Clase que implementa el operador proprio RedOp
 * @author Ignacio Yuste López
 */
public class RedOp extends BufferedImageOpAdapter{

    /**
     * Create new form RedOp
     */
    
    private int umbral;
    
    /**
     * Constructor de la clase, indicando el umbral de color donde se aplicará el efecto
     * @param umbral 
     */
    public RedOp(int umbral){
        this.umbral = umbral;
    }
    
    /**
     * Sobrecarga del método filter, crea y aplica el filtro para los píxeles de la imagen
     * cuyo valor RGB supera el umbral definido
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
        int[] pixelComp = new int[srcRaster.getNumBands()];
        int[] pixelCompDest = new int[destRaster.getNumBands()];
        float diferencia;
        int media;
        
        for(int x=0; x<src.getWidth(); x++){
            for(int y=0; y<src.getHeight(); y++){                
                diferencia = srcRaster.getPixel(x, y, pixelComp)[0]-srcRaster.getPixel(x, y, pixelComp)[1]-srcRaster.getPixel(x, y, pixelComp)[2];
                
                if(diferencia < umbral){
                    media = (srcRaster.getPixel(x, y, pixelComp)[0]+srcRaster.getPixel(x, y, pixelComp)[1]+srcRaster.getPixel(x, y, pixelComp)[2])/3;
                    pixelCompDest[0] = media;
                    pixelCompDest[1] = media;
                    pixelCompDest[2] = media;
                }
                else{
                    pixelCompDest[0] =  srcRaster.getPixel(x, y, pixelComp)[0];
                    pixelCompDest[1] =  srcRaster.getPixel(x, y, pixelComp)[1];
                    pixelCompDest[2] =  srcRaster.getPixel(x, y, pixelComp)[2];
                }
                
                destRaster.setPixel(x, y, pixelCompDest);
            }
        }
        
      return dest;  
    }
    
}
