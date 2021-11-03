
package sm.iyl.graficos;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Clase Rectangle propia, hija de JShape2D 
 * @author Ignacio Yuste López
 */
public class JRectangulo2D extends JShape2D{
    
    /**
     * Create new form JRectangulo2D
     */
    
    /**
     * Constructor de clase, crea el objeto Rectangle
     * @param p1 
     */
    public JRectangulo2D(Point p1){
        this.p1 = p1;        
        figura = new Rectangle(p1);
    }
    
    /**
     * Sobrecarga del método setLocation de la clase Rectangle para la clase JRectangulo2D
     * @param pAux 
     */
    public void setLocation(Point pAux){
        ((Rectangle)figura).setLocation(pAux);
        
        this.setPuntosDegradado();
    }
    
    /**
     * Sobrecarga del método setFrameFromDiagonal de la clase Rectangle para la clase JRectangulo2D
     * @param p2 
     */
    public void setFrameFromDiagonal(Point p2){
        this.p2 = p2;
        ((Rectangle)figura).setFrameFromDiagonal(this.p1,p2);
        
        this.setPuntosDegradado();
    }
    
    /**
     * Sobrecarga del método contains que dice si un punto está contenido en el rectángulo
     * @param pAux
     * @return pAux está contenido o no en el rectángulo
     */
    @Override
    public boolean contains(Point pAux){
        return ((Rectangle)figura).contains(pAux);
    }
    
}
