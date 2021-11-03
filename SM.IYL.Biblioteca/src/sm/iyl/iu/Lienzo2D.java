/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.iyl.iu;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import static java.awt.BasicStroke.*;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import sm.iyl.graficos.JLinea2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import sm.iyl.graficos.JElipse2D;
import sm.iyl.graficos.JRectangulo2D;
import sm.iyl.graficos.JRoundRectangulo2D;
import sm.iyl.graficos.JShape2D;

/**
 * Clase Lienzo2D, uso de la clase Graphics2D y demás para pintar formas e imágenes
 * @author Ignacio Yuste López
 */
public class Lienzo2D extends javax.swing.JPanel {

    /**
     *  Creates new form Lienzo2D
     */
    
    public enum Figura{PUNTO, LINEA, RECTANGULO, ELIPSE, ROUNDRECTANGULO, NADA};
    private Figura figuraActual;
    
    private Point p1, p2, pAux;
    private List<JShape2D> vShape;
    private JShape2D shapeEditar, shapeCreada;    //Variable para guardar una forma a modificar
    
    private Color color1, color2;
    private Stroke stroke;
    int grosor;
    private RenderingHints rh;
    private Composite comp;
    
    private BufferedImage imagen;    
    
    Boolean relleno, mover, transparencia, suavizado, conBordes, trazoDiscontinuo, deg_vertical, deg_horizontal;
    
    private ArrayList<LienzoListener> lienzoEventListeners = new ArrayList();
    
    /**
     * Constructor, inicializa las variables a un estado predeterminado
     */
    public Lienzo2D() {
        initComponents();
        this.setVisible(true);
        relleno = mover = transparencia = suavizado = deg_vertical = deg_horizontal = conBordes = trazoDiscontinuo = false;  
        figuraActual = Figura.NADA;
        stroke = new BasicStroke(2.0f);
        color1 = color2 = Color.black;
        rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
        vShape = new ArrayList();
    }
    
    /**
     * Método paint para pintar el vector de figuras
     * @param g 
     */
    @Override
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if(imagen!=null)    g2d.drawImage(imagen,0,0,this);
        
        for(JShape2D s: vShape)
            if(s!=null) s.paint(g2d);     
        
        repaint();
    }
    
    /**
     * Método que devuelve la figura en la que se encuentra un punto del lienzo
     * @param p punto de la figura
     * @return figura donde se encuentra el punto
     */
    private JShape2D getSelectedShape(Point p) {
        for (JShape2D s : vShape) {
            if (s.contains(p)) {
                setFiguraActual(s);
                return s;
            }
        }
        return null;
    }
    
    /**
     * Método llamado cada vez que se desea crear una figura nueva,
     * crea la figura seleccionada en figuraActual
     * @return 
     */
    public JShape2D createShape(){
        
        JShape2D creada = null;
        switch (figuraActual) {
            case PUNTO:
                creada = new JLinea2D(p1, p1);
                creada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
            break;

            case LINEA:
                creada = new JLinea2D(p1, p1);
                creada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
            break;

            case RECTANGULO:
                creada = new JRectangulo2D(p1);
                creada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
            break;

            case ELIPSE:
                creada = new JElipse2D(p1);
                creada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
            break;
            
            case ROUNDRECTANGULO:
                creada = new JRoundRectangulo2D(p1);
                creada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
            break;
            
            case NADA:
        }
        
        vShape.add(creada);
        this.notifyShapeAddedEvent(new LienzoEvent(this,creada, grosor, transparencia, trazoDiscontinuo));
        
        return creada;
    }
      
    /**
     * Selecciona la figuraActual dependiendo de la clase a la que pertenezca el objeto JShape2D
     * @param fig objeto JShape2D del que obtener la figura
     */
    void setFiguraActual(JShape2D fig){
        if(fig instanceof JLinea2D){
            if(((JLinea2D) fig).getP1().getX() == ((JLinea2D) fig).getP2().getX())  figuraActual = Figura.PUNTO;
            else                                            figuraActual = Figura.LINEA;
        }
        else if(fig instanceof JRectangulo2D)   figuraActual = Figura.RECTANGULO;
        else if(fig instanceof JElipse2D)   figuraActual = Figura.ELIPSE;
        else if(fig instanceof JRoundRectangulo2D)   figuraActual = Figura.ROUNDRECTANGULO;
    }
    
    /**
     * Método utilizado cada vez que se desea cambiar alguna propiedad de una figura
     */
    public void updateShape(){

        if (mover)  setFiguraActual(shapeEditar);

        switch (figuraActual) {
            case PUNTO:
                if(mover){
                    if(shapeEditar!=null) ((JLinea2D)shapeEditar).setLine(pAux, pAux);
                }
                else
                    if(shapeCreada!=null) shapeCreada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
            break;

            case LINEA:
                if(mover){
                    if(shapeEditar!=null)  ((JLinea2D)shapeEditar).setLocation(pAux);
                }
                else{
                    if(shapeCreada!=null){
                        ((JLinea2D)shapeCreada).setLine(p1,p2);
                        shapeCreada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
                    }
                }
            break;

            case RECTANGULO:
                if(mover){
                    if(shapeEditar!=null) ((JRectangulo2D)shapeEditar).setLocation(pAux);
                }
                else{
                    if(shapeCreada!=null){
                        ((JRectangulo2D)shapeCreada).setFrameFromDiagonal(p2);
                        shapeCreada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
                    }
                }
            break;

            case ELIPSE:
                if(mover){           
                    if(shapeEditar!=null) ((JElipse2D)shapeEditar).setFrame(pAux);
                }
                else{
                    if(shapeCreada!=null){
                        ((JElipse2D)shapeCreada).setFrameFromDiagonal(p2);
                        shapeCreada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
                    }
                }
            break;
            
            case ROUNDRECTANGULO:
                if(mover){           
                    if(shapeEditar!=null) ((JRoundRectangulo2D)shapeEditar).setFrame(pAux);
                }
                else{
                    if(shapeCreada!=null){
                        ((JRoundRectangulo2D)shapeCreada).setFrameFromDiagonal(p2);
                        shapeCreada.setAtributos(color1, color2, stroke, rh, comp, relleno, deg_vertical, deg_horizontal, conBordes);
                    }
                }
            break;
            
            
            case NADA:
        }
        
        if(mover)
            this.notifyShapeSelectedEvent(new LienzoEvent(this,shapeEditar, grosor, transparencia, trazoDiscontinuo));
        else
            this.notifyPropertyChangeEvent(new LienzoEvent(this,shapeCreada, grosor, transparencia, trazoDiscontinuo));
            
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(50, 50));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Método que crea y devuelve la imagen actual en el Lienzo2D, con las posibles modificaciones
     * @param drawVector booleando para indicar si se desea obtener la imagen modificada o no
     * @return 
     */
    public BufferedImage getImagen(boolean drawVector) {
        
        if(drawVector){
            BufferedImage imgout = new BufferedImage(imagen.getWidth(), imagen.getHeight(), imagen.getType());
            boolean opacoActual = this.isOpaque();
            if (imagen.getColorModel().hasAlpha()) {
                this.setOpaque(false);
            }
            this.paint(imgout.createGraphics());
            this.setOpaque(opacoActual);
            return imgout;
        }       
        else
            return imagen;
    }
    
    /**
     * Establece una imagen en el Lienzo2D
     * @param imagen a establecer
     */
    public void setImagen(BufferedImage imagen) {
        this.imagen = imagen;
        
        if(imagen!=null){
            setPreferredSize(new Dimension(imagen.getWidth(), imagen.getHeight()));
        }
    }

    /**
     * Devuelve la figura actual
     * @return 
     */
    public Figura getFiguraActual() {
        return figuraActual;
    }
    
    /**
     * Establece la figura actual
     * @param figuraActual figura a establecer 
     */
    public void setFiguraActual(Figura figuraActual) {
        this.figuraActual = figuraActual;
    }

    /**
     * Devuelve el color de la figura seleccionada
     * @return Color de la figura seleccionada
     */
    public Color getColor1() {
        Color ret = color1;
        
        if(mover)
            if(shapeEditar!=null)   ret = shapeEditar.getColor1();
        else
            if(shapeCreada!=null)   ret = shapeCreada.getColor1();
        
        return ret;
    }    

    /**
     * Establece el color a la figura seleccionada
     * @param color a establecer
     */
    public void setColor1(Color color) {
        this.color1 = color;
        
        if(mover)
            if(shapeEditar!=null)   shapeEditar.setColor1(color);
        else
            if(shapeCreada!=null)   shapeCreada.setColor1(color);
        
        repaint();
    }

    /**
     * Devuelve el color de la figura seleccionada para el degradado
     * @return Color de la figura seleccionada
     */
    public Color getColor2() {
        Color ret = color2;
        
        if(mover)
            if(shapeEditar!=null)   ret = shapeEditar.getColor2();
        else
            if(shapeCreada!=null)   ret = shapeCreada.getColor2();
        
        return ret;
    }    

    /**
     * Establece el color a la figura seleccionada para el degradado
     * @param color a establecer
     */
    public void setColor2(Color color) {
        this.color2 = color;
        
        if(mover)
            if(shapeEditar!=null)   shapeEditar.setColor2(color);
        else
            if(shapeCreada!=null)   shapeCreada.setColor2(color);
        
        repaint();
    }
    
    /**
     * Devuelve si se pinta un trazo discontinuo
     * @return 
     */
    public Boolean getTrazoDiscontinuo() {
        return trazoDiscontinuo;
    }

    /**
     * Establece si se pinta un trazo discontinuo
     * @param trazoDiscontinuo 
     */
    public void setTrazoDiscontinuo(Boolean trazoDiscontinuo) {
        this.trazoDiscontinuo = trazoDiscontinuo;
    }
    
    
    /**
     * Devuelve el atributo Stoke de la figura seleccionada
     * @return grosor del trazo
     */
    public Stroke getStroke() {
        Stroke ret = stroke;
        
        if(mover)
            if(shapeEditar!=null)   ret = shapeEditar.getStroke();
        else
            if(shapeCreada!=null)   ret = shapeCreada.getStroke();
        
        return ret;
    }

    
    /**
     * Establece el valor del grosor a la figura seleccionada
     * @param valor del grosor a establecer
     */
    public void setStroke(int valor) {
        
        if(trazoDiscontinuo){
            float []dash={6f,2.0f,6.0f};
            this.stroke = new BasicStroke(5.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,1.0f,dash,0.0f);
        }
        else    this.stroke = new BasicStroke((float)valor);
        
        grosor = valor;
        
        if(mover){
            if(shapeCreada!=null){
                shapeEditar.setStroke(stroke);
                shapeEditar.setGrosor(valor);
            }
        }
        else{
            if(shapeCreada!=null){
                shapeCreada.setStroke(stroke);
                shapeCreada.setGrosor(valor);
            }
        }
        

        repaint();
    }
    
    
    /**
     * Devuelve el valor del grosor de la figura seleccionada
     * @return valor del grosor
     */
    public int getGrosor(){
        int ret=grosor;
        
        if(mover)
            if(shapeEditar!=null)   ret = shapeEditar.getGrosor();
        else
            if(shapeCreada!=null)   ret = shapeCreada.getGrosor();
        
        return ret;
    }

    /**
     * Devuelve si la figura actual se pinta con relleno o no
     * @return relleno
     */
    public Boolean getRelleno() {
        boolean ret=relleno;
        
        if(mover)
            if(shapeEditar!=null)   ret = shapeEditar.isRelleno();
        else
            if(shapeCreada!=null)   ret = shapeCreada.isRelleno();
        
        return ret;
    }

    /**
     * Establece si la figura actual se pinta con relleno o no
     * @param relleno 
     */
    public void setRelleno(Boolean relleno) {
        this.relleno = relleno;
        
        if(mover)
            if(shapeEditar!=null)   shapeEditar.setRelleno(relleno);
        else
            if(shapeCreada!=null)   shapeCreada.setRelleno(relleno);
        
        repaint();
    }

    /**
     * Devuelve si la figura se pinta con los bordes de un color distinto al del relleno
     * @return 
     */
    public Boolean getConBordes() {
        boolean ret=conBordes;
        
        if(mover)
            if(shapeEditar!=null)   ret = shapeEditar.isBordes();
        else
            if(shapeCreada!=null)   ret = shapeCreada.isBordes();
        
        return ret;
    }

    /**
     * Establece si la figura se pinta con los bordes de un color distinto al del relleno
     * @param conBordes 
     */
    public void setConBordes(Boolean conBordes) {
        this.conBordes = conBordes;
        
        if(mover){
            System.out.println(conBordes);
            if(shapeEditar!=null)   shapeEditar.setBordes(conBordes);
        }
        else{
            if(shapeCreada!=null){
                shapeCreada.setBordes(conBordes);
            }
        }

    }

    /**
     * Devuelve si la figura se pinta con degradado vertical 
     * @return deg_vertical
     */
    public Boolean getDeg_vertical() {
        boolean ret=deg_vertical;
        
        if(mover)
            if(shapeEditar!=null)   ret = shapeEditar.isDeg_vertical();
        else
            if(shapeCreada!=null)   ret = shapeCreada.isDeg_vertical();
        
        return ret;
    }

    /**
     * Establece si la figura se pinta con degradado vertical 
     * @param deg_vertical 
     */
    public void setDeg_vertical(Boolean deg_vertical) {
        this.deg_vertical = deg_vertical;
        
        if(mover)
            if(shapeEditar!=null)   shapeEditar.setDeg_vertical(deg_vertical);
        else
            if(shapeCreada!=null)   shapeCreada.setDeg_vertical(deg_vertical);
    }

    /**
     * Devuelve si la figura se pinta con degradado horizontal 
     * @return deg_horizontal
     */
    public Boolean getDeg_horizontal() {
        boolean ret=deg_horizontal;
        
        if(mover)
            if(shapeEditar!=null)   ret = shapeEditar.isDeg_horizontal();
        else
            if(shapeCreada!=null)   ret = shapeCreada.isDeg_horizontal();
        
        return ret;
    }

    /**
     * Establece si la figura se pinta con degradado horizontal
     * @param deg_horizontal 
     */
    public void setDeg_horizontal(Boolean deg_horizontal) {
        this.deg_horizontal = deg_horizontal;
        
        if(mover)
            if(shapeEditar!=null)   shapeEditar.setDeg_horizontal(deg_horizontal);
        else
            if(shapeCreada!=null)   shapeCreada.setDeg_horizontal(deg_horizontal);
    }
    
    /**
     * Devuelve si el Lienzo2D está en modo editar, con el que se pueden mover las figuras
     * @return mover
     */
    public Boolean getMover() {
        return mover;
    }

    /**
     * Establece si el Lienzo2D está en modo editar, con el que se pueden mover las figuras
     * @param mover 
     */
    public void setMover(Boolean mover) {
        this.mover = mover;
    }

    /**
     * Devuelve si la figura actual se pinta con transparencia
     * @return transparencia
     */
    public Boolean getTransparencia() {
        return transparencia;
    }

    /**
     * Establece si la figura actual se pinta con transparencia
     * @param transparencia 
     */
    public void setTransparencia(Boolean transparencia) {
        this.transparencia = transparencia;
        
        if(transparencia){
            if(mover)
                if(shapeEditar!=null)   shapeEditar.setTransparencia(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            else
                if(shapeCreada!=null)   shapeCreada.setTransparencia(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        else{
            if(mover)
                if(shapeEditar!=null)   shapeEditar.setTransparencia(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            else
                if(shapeCreada!=null)   shapeCreada.setTransparencia(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        repaint();
    }

    /**
     * Devuelve si la figura actual se pinta con suavizado
     * @return suavizado
     */
    public Boolean getSuavizado() {
        boolean ret=suavizado;
        
        if(mover)
            if(shapeEditar!=null)   ret = shapeEditar.isIsSuavizado();
        else
            if(shapeCreada!=null)   ret = shapeCreada.isIsSuavizado();
        
        return ret;
    }

    /**
     * Establece si la figura actual se pinta con suavizado o no
     * @param suavizado 
     */
    public void setSuavizado(Boolean suavizado) {
        this.suavizado = suavizado;
        
        if (suavizado){
            rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            if(mover)
                if(shapeEditar!=null)   shapeEditar.setSuavizado(rh);
            else
                if(shapeCreada!=null)   shapeCreada.setSuavizado(rh);
        }
        else {
            rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            
            if(mover)
                if(shapeEditar!=null)   shapeEditar.setSuavizado(rh);
            else
                if(shapeCreada!=null)   shapeCreada.setSuavizado(rh);
        }
        
        repaint();
    } 

    /**
     * Manejador de evento de pulsar click.
     * Crea una figura nueva o mueve una figura ya pintada
     * @param evt 
     */
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        pAux = evt.getPoint();
        
        if(mover){
            shapeEditar = getSelectedShape(pAux);
        }
        else{
            p1 = pAux;
            shapeCreada = createShape();
        }
    }//GEN-LAST:event_formMousePressed

    /**
     * Actualiza el valor del segundo punto de la figura que se está pintando
     * o atualiza la ubicación de la figura que se está moviendo
     * @param evt 
     */
    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        pAux = evt.getPoint();
        
        if(!mover)  p2 = pAux;
                
        updateShape();    
        repaint();
    }//GEN-LAST:event_formMouseDragged

    /**
     * Actualiza el valor del segundo punto de la figura que se está pintando
     * o actualiza la ubicación de la figura que se está moviendo
     * @param evt 
     */
    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        pAux = evt.getPoint();
        
        if(!mover)  p2 = pAux;
        
        updateShape();
        this.formMouseDragged(evt);
        repaint();
    }//GEN-LAST:event_formMouseReleased
    
    /**
     * Clase de eventos de Lienzo2D
     * Crea eventos con todos los atributos del objeto que lo genera
     */
    public class LienzoEvent extends EventObject{
        private JShape2D forma;
        private Color color1, color2;
        private Stroke stroke;
        int grosor;
        private RenderingHints rh;
        private Composite comp;
        Boolean relleno, transparencia, suavizado, conBordes, deg_vertical, deg_horizontal, discontinuo;
        
        public LienzoEvent(Object source, JShape2D forma, int grosor, boolean transparencia, boolean discontinuo){
            super(source);
            if(forma!=null){
                this.forma = forma;
                this.color1 = forma.getColor1();
                this.color2 = forma.getColor2();
                this.stroke = forma.getStroke();
                this.grosor = grosor;
                this.rh = forma.getSuavizado();
                this.comp = forma.getTransparencia();
                this.relleno = forma.isRelleno();
                this.conBordes = forma.isBordes();
                this.transparencia = transparencia;
                this.suavizado = forma.isIsSuavizado();
                this.deg_vertical = forma.isDeg_vertical();
                this.deg_horizontal = forma.isDeg_horizontal();
                this.discontinuo = discontinuo;
            }
        }
        
        public JShape2D getForma(){
            return forma;
        }
        
        public Color getColor1(){
            return color1;
        }

        public Color getColor2() {
            return color2;
        }

        public Stroke getStroke() {
            return stroke;
        }

        public int getGrosor() {
            return grosor;
        }

        public RenderingHints getRh() {
            return rh;
        }

        public Composite getComp() {
            return comp;
        }

        public Boolean getRelleno() {
            return relleno;
        }

        public Boolean getMover() {
            return mover;
        }

        public Boolean getTransparencia() {
            return transparencia;
        }

        public Boolean getSuavizado() {
            return suavizado;
        }

        public Boolean getConBordes() {
            return conBordes;
        }

        public Boolean getDeg_vertical() {
            return deg_vertical;
        }

        public Boolean getDeg_horizontal() {
            return deg_horizontal;
        }

        public Boolean getDiscontinuo() {
            return discontinuo;
        }
        
        public Figura getFigura(){
            Figura ret=Figura.NADA;
            if(forma instanceof JLinea2D){
                if(((JLinea2D) forma).getP1().getX() == ((JLinea2D) forma).getP2().getX())  ret = Figura.PUNTO;
                else                                                                        ret = Figura.LINEA;
            }
            else if(forma instanceof JRectangulo2D)      ret = Figura.RECTANGULO;
            else if(forma instanceof JElipse2D)          ret = Figura.ELIPSE;
            else if(forma instanceof JRoundRectangulo2D) ret = Figura.ROUNDRECTANGULO;
            
            return ret;
         }
        
    };
    
    /**
     * Clase manejadora de eventos LienzoEvent
     * Interfaz para implementar los métodos que interesan para el proyecto
     */
    public interface LienzoListener extends EventListener{
        public void shapeAdded(LienzoEvent evt);
        public void propertyChange(LienzoEvent evt);
        public void shapeSelected(LienzoEvent evt);
    };

    /**
     * Añade un manejador de eventos del lienzo al array lienzoEvetnListeners
     * @param listener 
     */
    public void addLienzoListener(LienzoListener listener){
        if(listener!=null)  this.lienzoEventListeners.add(listener);
    }
    
    /**
     * Añade el evento de creación de una figura a los listeners
     * @param evt 
     */
    private void notifyShapeAddedEvent(LienzoEvent evt) {
        if (!lienzoEventListeners.isEmpty()) {
            for (LienzoListener listener : lienzoEventListeners) {
                listener.shapeAdded(evt);
            }
        }
    }

    /**
     * Añade el evento de modificación de una figura a los listeners
     * @param evt 
     */
    private void notifyPropertyChangeEvent(LienzoEvent evt) {
        if (!lienzoEventListeners.isEmpty()) {
            for (LienzoListener listener : lienzoEventListeners) {
                listener.propertyChange(evt);
            }
        }
    }
    
    /**
     * Añade el evento de selección de una figura a los listeners
     * @param evt 
     */
    private void notifyShapeSelectedEvent(LienzoEvent evt){
       if (!lienzoEventListeners.isEmpty()) {
            for (LienzoListener listener : lienzoEventListeners) {
                listener.shapeSelected(evt);
            }
        } 
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
