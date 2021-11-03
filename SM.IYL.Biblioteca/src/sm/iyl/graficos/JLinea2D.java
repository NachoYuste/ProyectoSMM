/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.iyl.graficos;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Clase Line2D propia, hija de JShape2D
 * @author Ignacio Yuste López
 */
public class JLinea2D extends JShape2D{

    /**
     * Create new form JLinea2D
     */
    
    /**
     * Constructor de clase, crea el objeto Line2D
     * @param p1
     * @param p2 
     */
    public JLinea2D(Point p1, Point p2){
        figura = new Line2D.Float(p1, p2);
    }
            
    /**
     * Método que dice si un punto está cerca de la línea 
     * @param p
     * @return está o no cerca
     */
    private boolean isNear(Point p){
        return ((Line2D)figura).ptLineDist(p) <= 2.0;
    }
    
    /**
     * Método que dice si un punto pertenece a la línea, en este caso se
     * considera dentro a un punto cercano
     * @param p
     * @return contiene o no al punto
     */
    @Override
    public boolean contains(Point p){
        return isNear(p);
    }
    
    /**
     * Sobrecarga de método setLine de la clase Line2D para la clase JLinea2D
     * @param p1
     * @param p2 
     */
    public void setLine(Point p1, Point p2){
        this.p1 = p1;
        this.p2 = p2;
        
        deg_p1_ver = deg_p1_hor = p1;
        deg_p2_ver = deg_p2_hor = p2;
        
        ((Line2D.Float)figura).setLine(p1, p2);
    }
    
    /**
     * Sobrecarga de método setLocation de la clase Line2D para la clase JLinea2D
     * @param pos 
     */
    public void setLocation(Point2D pos){
        double dx=pos.getX() - this.p1.getX();
        double dy=pos.getY() - this.p1.getY();
        Point2D newp2 = new Point2D.Double(this.p2.getX()+dx,this.p2.getY()+dy);
        deg_p1_ver = deg_p1_hor = (Point)pos;
        deg_p2_ver = deg_p2_hor = (Point)newp2;
        
        ((Line2D)figura).setLine(pos,newp2);
    }    
    
}
