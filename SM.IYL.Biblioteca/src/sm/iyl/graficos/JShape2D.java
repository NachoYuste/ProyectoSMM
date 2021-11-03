/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.iyl.graficos;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 * Clase JShape2D para que cada objeto Shape que se pinte tenga atributos propios
 * @author Ignacio Yuste López
 */
public class JShape2D {
    
    /**
     *  Creates new form JShape2D
     */
    
    protected Color colorRelleno1, colorRelleno2, colorBorde;
    protected GradientPaint gradiente;
    protected Stroke stroke;
    int grosor;
    protected Composite comp;
    protected RenderingHints suavizado;
    protected boolean relleno, isSuavizado, deg_vertical, deg_horizontal, bordes;
    
    Shape figura;
    Point p1, p2, deg_p1_ver, deg_p2_ver, deg_p1_hor, deg_p2_hor;
    
    /**
     * Constructor con atributos predeterminados 
     */
    public JShape2D(){
        stroke = new BasicStroke(2.0f);
        colorRelleno1 = Color.black;
        colorRelleno2 = Color.black;
        colorBorde = Color.black;
        suavizado = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
        relleno = false;
        deg_p1_ver = new Point(0,0);
        deg_p2_ver = new Point(0,0);
        deg_p1_hor = new Point(0,0);
        deg_p2_hor = new Point(0,0);
    }
    
    /**
     * Método que establece los atributos a la figura
     * @param c_relleno1
     * @param c_relleno2
     * @param s stroke
     * @param r renderinghits
     * @param com composite
     * @param rl  relleno
     */
    public void setAtributos(Color c_relleno1,Color c_relleno2, Stroke s, RenderingHints r, Composite com, boolean rl, boolean dv, boolean dh, boolean b){
        colorRelleno1 = c_relleno1;
        colorRelleno2 = c_relleno2;
        colorBorde = c_relleno2;
        stroke = s;
        suavizado = r;
        comp = com;
        relleno = rl;
        deg_vertical = dv;
        deg_horizontal = dh;
        bordes = b;
    }
    
    /**
     * Método que copia los atributos de otra figura
     * @param s 
     */
    public void copiaAtributos(JShape2D s){
        this.colorRelleno1 = s.getColor1();
        this.colorRelleno2 = s.getColor2();
        this.comp = s.getTransparencia();
        this.relleno = s.isRelleno();
        this.stroke = s.getStroke();
        this.suavizado = s.getSuavizado();       
    }

    /**
     * Devuelve el color de la figura
     * @return color
     */
    public Color getColor1() {
        return colorRelleno1;
    }

    /**
     * Establece el color de relleno de la figura
     * @param color 
     */
    public void setColor1(Color color) {
        this.colorRelleno1 = color;
    }

    /**
     * Devuelve el color de la figura para el degradado
     * @return color
     */
    public Color getColor2() {
        return colorRelleno2;
    }

    /**
     * Establece el color de relleno de la figura para el degradado
     * @param color 
     */
    public void setColor2(Color color) {
        this.colorRelleno2 = color;
    }

    
    /**
     * Devuelve el color del borde de la figura
     * @return colorBorde
     */
    public Color getColorBorde() {
        return colorBorde;
    }

    /**
     * Establece el color del borde de la figura
     * @param colorBorde 
     */
    public void setColorBorde(Color colorBorde) {
        this.colorBorde = colorBorde;
    }

    /**
     * Devuelve si la figura se pinta con degradado vertical
     * @return deg_vertical
     */
    public boolean isDeg_vertical() {
        return deg_vertical;
    }

    /**
     * Establece si la figura se pinta con degradado vertical
     * @param deg_vertical 
     */
    public void setDeg_vertical(boolean deg_vertical) {
        this.deg_vertical = deg_vertical;
    }

    /**
     * Devuelve si la figura se pinta con degradado horizontal
     * @return deg_horizontal
     */
    public boolean isDeg_horizontal() {
        return deg_horizontal;
    }

    /**
     * Establece si la figura se pinta con degradado vertical
     * @param deg_horizontal 
     */
    public void setDeg_horizontal(boolean deg_horizontal) {
        this.deg_horizontal = deg_horizontal;
    }

    /**
     * Devuelve si la figura se pinta con el color del borde distinto del relleno
     * @return bordes
     */
    public boolean isBordes() {
        return bordes;
    }

    /**
     * Establece si la figura se pinta con el color del borde distinto del relleno
     * @param bordes 
     */
    public void setBordes(boolean bordes) {
        this.bordes = bordes;
    }

    
    /**
     * Devuelve el stroke de la figura
     * @return stroke
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * Establece el stroke de la figura
     * @param stroke 
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    /**
     * Devuelve el grosor con el que se pinta la figura
     * @return grosor
     */
    public int getGrosor() {
        return grosor;
    }

    /**
     * Establece el grosor con el que se pinta la figura
     * @param grosor 
     */
    public void setGrosor(int grosor) {
        this.grosor = grosor;
    }
    
    /**
     * Devuelve si la figura se pinta con relleno
     * @return isRelleno
     */
    public boolean isRelleno() {
        return relleno;
    }

    /**
     * Establece si la figura se pinta con relleno
     * @param relleno 
     */
    public void setRelleno(boolean relleno) {
        this.relleno = relleno;
    }

    /**
     * Devuelve la transparencia de la figura
     * @return Composite transparencia
     */
    public Composite getTransparencia() {
        return comp;
    }

    /**
     * Establece la transparencia de la figura
     * @param transparencia 
     */
    public void setTransparencia(Composite transparencia) {
        this.comp = transparencia;
    }

    /**
     * Devuelve el suavizado de la figura
     * @return RenderingHints con valor de suavizado
     */
    public RenderingHints getSuavizado() {
        return suavizado;
    }

    /**
     * Establece el suavizado de la figura
     * @param suavizado 
     */
    public void setSuavizado(RenderingHints suavizado) {
        this.suavizado = suavizado;
    }

    /**
     * Devuelve si la figura se pinta con suavizado o no 
     * @return figura suavizada o no
     */
    public boolean isIsSuavizado() {
        return isSuavizado;
    }

    /**
     * Establece si la figura se pinta con suavizado o 
     * @param isSuavizado 
     */
    public void setIsSuavizado(boolean isSuavizado) {
        this.isSuavizado = isSuavizado;
    }
  
    /**
     * Devuelve el primer punto de la figura
     * @return p1
     */
    public Point2D getP1() {
        return p1;
    }

    /**
     * Establece el primer punto de la figura
     * @param p1 
     */
    public void setP1(Point p1) {
        this.p1 = p1;
    }

    /**
     * Devuelve el segundo punto de la figura
     * @return p2
     */
    public Point2D getP2() {
        return p2;
    }

    /**
     * Establece el segundo punto de la figura
     * @param p2 
     */
    public void setP2(Point p2) {
        this.p2 = p2;
    }
    
    /**
     * Método "abstracto" para implementar en todas las subclases
     * @param p punto a comprobar si pertenece a la figura
     * @return 
     */
    public boolean contains(Point p){
        return false;
    }
    
    /**
     * Método de dibujado de la figura con todos sus atributos
     * @param g2d 
     */
    public void paint(Graphics2D g2d){
        
        g2d.setStroke(stroke);
        g2d.setComposite(comp);
        g2d.setRenderingHints(suavizado);
        g2d.addRenderingHints(suavizado);
        g2d.setComposite(comp);
        
        if(deg_vertical){
            gradiente = new GradientPaint(deg_p2_ver, colorRelleno1, deg_p1_ver, colorRelleno2);
            g2d.setPaint(gradiente);
            g2d.fill(figura);
        }        
        else if(deg_horizontal){
            gradiente = new GradientPaint(deg_p2_hor, colorRelleno1, deg_p1_hor, colorRelleno2);
            g2d.setPaint(gradiente);
            g2d.fill(figura);
        }
        else if(bordes){
            g2d.setPaint(colorRelleno1);
            g2d.fill(figura);
            g2d.setColor(colorRelleno2);
            g2d.draw(figura);
        }
        else if(relleno){
            g2d.setColor(colorRelleno1);
            g2d.fill(figura);
        }
        else{
            g2d.setColor(colorRelleno1);
            g2d.draw(figura);
        }
        
    }
    
    /**
     * Método que establece los puntos para el dibujo con degradado
     */
    protected void setPuntosDegradado(){
        double x_centro = figura.getBounds2D().getCenterX();
        double y_centro = figura.getBounds2D().getCenterY();
        double x = figura.getBounds2D().getMinX();
        double x_max = figura.getBounds2D().getMaxX();
        double y = figura.getBounds2D().getMinY();
        double y_max = figura.getBounds2D().getMaxY();
        
        deg_p1_ver.setLocation(x_centro, y_max);
        deg_p2_ver.setLocation(x_centro, y);
        deg_p1_hor.setLocation(x, y_centro);
        deg_p2_hor.setLocation(x_max, y_centro);

    }
    
    
    
}
