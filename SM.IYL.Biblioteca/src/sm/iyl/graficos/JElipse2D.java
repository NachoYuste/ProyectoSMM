
package sm.iyl.graficos;

import java.awt.Point;
import java.awt.geom.Ellipse2D;

/**
 * Clase Ellipse2D propia, hija de JShape2D 
 * @author Ignacio Yuste López
 */
public class JElipse2D extends JShape2D{

    
    /**
     * Create new form JElipse2D
     */
    
    /**
     * Constructor de la clase, crea el objeto Ellipse2D.Float
     * @param p1 primer punto de la elipse
     */
    public JElipse2D(Point p1){
        this.p1 = p1;
        figura = new Ellipse2D.Float(p1.x,p1.y,p1.x,p1.y);
    }
    
    
    /**
     * Sobrecarga del método setFrame de la clase Ellipse2D para la clase JElipse2D
     * @param pAux
     */
    public void setFrame(Point pAux){
        ((Ellipse2D.Float)figura).setFrame(pAux.x, pAux.y, ((Ellipse2D.Float)figura).getWidth(), ((Ellipse2D.Float)figura).getHeight());
        
        this.setPuntosDegradado();
    }
    
    
    /**
     *  Sobrecarga del método setFrameFromDiagonal de la clase Ellipse2D para la clase JElipse2D
     * @param p2 segundo punto de la elipse
     */
    public void setFrameFromDiagonal(Point p2){
        this.p2 = p2;
        ((Ellipse2D.Float)figura).setFrameFromDiagonal(p1, p2);
        
        this.setPuntosDegradado();
    }
    
    /**
     * Sobrecarga del método contains que dice si un punto está contenido en la elipse
     * @param pAux
     * @return pAux está contenido en la elipse
     */
    @Override
    public boolean contains(Point pAux){
        return ((Ellipse2D.Float)figura).contains(pAux);
    }
    
}
