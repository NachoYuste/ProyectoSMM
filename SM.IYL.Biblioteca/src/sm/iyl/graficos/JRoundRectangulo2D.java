/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.iyl.graficos;

import java.awt.Point;
import java.awt.geom.RoundRectangle2D;

/**
 *  Clase RoundRectangle2D propia, hija de JShape2D
 * @author Ignacio Yuste López
 */
public class JRoundRectangulo2D extends JShape2D{
    
    /**
     * Create new form JRoundRectangulo2D
     */
    
     /**
     * Constructor de clase, crea el objeto RoundRectangle2D
     * @param p1 
     */
    public JRoundRectangulo2D(Point p1){
        this.p1 = p1;        
        figura = new RoundRectangle2D.Float();
        ((RoundRectangle2D)figura).setRoundRect(p1.x, p1.y, 0, 0, 20, 20);
    }
    
    /**
     * Sobrecarga del método setLocation de la clase RoundRectangle2D para la clase JRoundRectangulo2D
     * @param p2
     */
    public void setFrameFromDiagonal(Point p2){
        this.p2 = p2;
        ((RoundRectangle2D)figura).setFrameFromDiagonal(p1.x, p1.y, this.p2.x, this.p2.y);
        
        this.setPuntosDegradado();
    }

    /**
     * Sobrecarga del método setFrame de la clase RoundRectangle2D para la clase JRoundRectangulo2D
     * @param pAux 
     */
    public void setFrame(Point pAux){
        ((RoundRectangle2D)figura).setFrame(pAux.x, pAux.y, Math.abs(p2.x-p1.x), Math.abs(p2.y-p1.y));
        
        this.setPuntosDegradado();
    }
    
    /**
     * Sobrecarga del método contains que dice si un punto está contenido en el rectángulo
     * @param pAux
     * @return pAux está contenido o no en el rectángulo
     */
    @Override
    public boolean contains(Point pAux){
        return ((RoundRectangle2D)figura).contains(pAux);
    }
    
    
}
