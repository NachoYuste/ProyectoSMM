/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProyectoFinal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.image.EqualizationOp;
import sm.image.ImageTools;
import sm.image.KernelProducer;
import sm.image.LookupTableProducer;
import sm.image.SepiaOp;
import sm.image.TintOp;
import sm.iyl.imagen.PosterizarOp;
import sm.iyl.imagen.PropiaOp;
import sm.iyl.imagen.RedOp;
import sm.iyl.iu.ColorCellRender;
import sm.iyl.iu.Lienzo2D.Figura;
import sm.iyl.iu.Lienzo2D.LienzoEvent;
import sm.iyl.iu.Lienzo2D.LienzoListener;
import sm.sound.SMClipPlayer;
import sm.sound.SMSoundRecorder;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * Clase de la ventana principal donde se gestionan todos los eventos relacionados con la IU
 * @author Ignacio Yuste López
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    /**
     *  Creates new form VentanaPrincipal
     */
    
    private BufferedImage imgFuente = null;
    private SMClipPlayer player = null;
    private SMSoundRecorder recorder = null;
    private File audioActual = null;
    
    
    /**
     * Constructor por defecto
     */
    public VentanaPrincipal() {
        initComponents();
        this.setVisible(true);
        this.setSize(1400, 800);
    }
    
    
    /**
     * Clase manejadora propia para sistema de audio
     */
    class ManejadorAudio implements LineListener {
        
        /**
         * Método que actualiza el estado de los botones de 
         * reproducción/stop cuando se reproduce un audio
         * @param event 
         */
        @Override
        public void update(LineEvent event) {
            if (event.getType() == LineEvent.Type.START) {
                botonPlay.setEnabled(false);
            }
            if (event.getType() == LineEvent.Type.STOP) {
                botonPlay.setEnabled(true);
            }
            if (event.getType() == LineEvent.Type.CLOSE) {
            }
        }
    }
    
    /**
     * Clase manejadora propia para actualizar el estado de los botones de dibujo
     * dependiendo del objeto seleccionado
     */
    public class MiManejadorLienzo implements LienzoListener{
        
        @Override
        public void shapeSelected(LienzoEvent evt){
          
            bPanelAlisar.setSelected(evt.getSuavizado_evt());
            bPanelTransparencia.setSelected(evt.getTransparencia_evt());
            spinnerGrosor.setValue(Integer.valueOf(evt.getGrosor_evt()));
            comboColores1.setSelectedItem(evt.getColor1_evt());
            comboColores2.setSelectedItem(evt.getColor2_evt());
            botonTrazoDiscontinuo.setSelected(evt.getDiscontinuo_evt());
            
            opcionesFiguras.clearSelection();            
            switch (evt.getFigura()) {
                case PUNTO -> opcionesFiguras.setSelected(botonPunto.getModel(), true);
                case LINEA -> opcionesFiguras.setSelected(botonLinea.getModel(), true);
                case RECTANGULO -> opcionesFiguras.setSelected(botonRectangulo.getModel(), true);
                case ELIPSE -> opcionesFiguras.setSelected(botonElipse.getModel(), true);
                case ROUNDRECTANGULO -> opcionesFiguras.setSelected(botonRoundRectangulo.getModel(), true);
            }
            
            if(evt.getRelleno_evt())        comboRelleno.setSelectedIndex(1);
            else if(evt.getConBordes_evt()) comboRelleno.setSelectedIndex(2);
            else if(evt.getDeg_horizontal_evt()) comboRelleno.setSelectedIndex(3);
            else if(evt.getDeg_vertical_evt()) comboRelleno.setSelectedIndex(4);
            else                           comboRelleno.setSelectedIndex(0);
 
        }

        @Override
        public void shapeAdded(LienzoEvent evt) {
        }

        @Override
        public void propertyChange(LienzoEvent evt) {
        }

        @Override
        public void lienzoCreated(LienzoEvent evt) {
            bPanelAlisar.setSelected(evt.getSuavizado_evt());
            bPanelTransparencia.setSelected(evt.getTransparencia_evt());
            spinnerGrosor.setValue(Integer.valueOf(evt.getGrosor_evt()));
            comboColores1.setSelectedItem(evt.getColor1_evt());
            comboColores2.setSelectedItem(evt.getColor2_evt());
            botonTrazoDiscontinuo.setSelected(evt.getDiscontinuo_evt());
            
            opcionesFiguras.clearSelection();            
                        
            if(evt.getRelleno_evt())        comboRelleno.setSelectedIndex(1);
            else if(evt.getConBordes_evt()) comboRelleno.setSelectedIndex(2);
            else if(evt.getDeg_horizontal_evt()) comboRelleno.setSelectedIndex(3);
            else if(evt.getDeg_vertical_evt()) comboRelleno.setSelectedIndex(4);
            else                           comboRelleno.setSelectedIndex(0);
        }
    }
    
    /**
     * Clase manejadora de eventos para la clase VentanaInternaImagen
     */
    public class MiManejadorVII extends InternalFrameAdapter{
        
        VentanaInternaImagen vi;
        
        @Override
        public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt){
            if(evt.getInternalFrame() instanceof VentanaInternaImagen){
                vi = (VentanaInternaImagen)evt.getInternalFrame();
                bPanelAlisar.setSelected(vi.getLienzo2D().getSuavizado());
                bPanelTransparencia.setSelected(vi.getLienzo2D().getTransparencia());
                spinnerGrosor.setValue(Integer.valueOf(vi.getLienzo2D().getGrosor()));
                comboColores1.setSelectedItem(vi.getLienzo2D().getColor1());
                comboColores2.setSelectedItem(vi.getLienzo2D().getColor2());
                botonTrazoDiscontinuo.setSelected(vi.getLienzo2D().getTrazoDiscontinuo());

                opcionesFiguras.clearSelection();            
                switch (vi.getLienzo2D().getFiguraActual()) {
                    case PUNTO -> opcionesFiguras.setSelected(botonPunto.getModel(), true);
                    case LINEA -> opcionesFiguras.setSelected(botonLinea.getModel(), true);
                    case RECTANGULO -> opcionesFiguras.setSelected(botonRectangulo.getModel(), true);
                    case ELIPSE -> opcionesFiguras.setSelected(botonElipse.getModel(), true);
                    case ROUNDRECTANGULO -> opcionesFiguras.setSelected(botonRoundRectangulo.getModel(), true);
                }

                if(vi.getLienzo2D().getRelleno())        comboRelleno.setSelectedIndex(1);
                else if(vi.getLienzo2D().getConBordes()) comboRelleno.setSelectedIndex(2);
                else if(vi.getLienzo2D().getDeg_horizontal()) comboRelleno.setSelectedIndex(3);
                else if(vi.getLienzo2D().getDeg_vertical()) comboRelleno.setSelectedIndex(4);
                else                           comboRelleno.setSelectedIndex(0);
            }
        }
    }
    
    /**
     * Clase manejadora propia para sistema de vídeo
     */        
    private class VideoListener extends MediaPlayerEventAdapter{
        
        /**
         * Actualiza el estado de los botones de 
         * reprocción/stop cuando se está reproduciendo el vídeo
         * @param mediaPlayer 
         */
        @Override
        public void playing(MediaPlayer mediaPlayer) {
            botonStop.setEnabled(true);
            botonPlay.setEnabled(false);
        }

        /**
         * Actualiza el estado de los botones de 
         * reprocción/stop cuando se pausa el vídeo
         * @param mediaPlayer 
         */
        @Override
        public void paused(MediaPlayer mediaPlayer) {
            botonStop.setEnabled(false);
            botonPlay.setEnabled(true);
        }

        /**
         * Actualiza el estado de los botones de 
         * reprocción/stop cuando se para el vídeo
         * @param mediaPlayer 
         */
        @Override
        public void finished(MediaPlayer mediaPlayer) {
            this.paused(mediaPlayer);
        }
    };

     /**
     * Método que crea una LookupTable para el filtro cuadrático
     * @param m indica donde se hace cero la función
     * @return LookupTable creada para el filtro cuadrático 
     */
    private LookupTable cuadratica(double m){
        double max;
        
        if(m>=128)
            max = ((1.0 / 100.0 )* Math.pow((0 - m), 2));
        else
            max = ((1.0 / 100.0 )* Math.pow((255 - m), 2));
            
        double k = 255.0/max; // Cte de normalizacion
        byte funcionT[] = new byte[256];
        
        for (int x=0; x<256; x++){
            funcionT[x] = (byte) (k * Math.pow((x-m), 2));
            
            if(funcionT[x]>255) funcionT[x]=(byte)255;
            if(funcionT[x]<0)   funcionT[x]=0;
        }
        
        LookupTable tabla = new ByteLookupTable(0, funcionT);
        
        return tabla;
    } 
    
    /**
     * Función LookUp cuya operación es la función negativo
     * @return tabla
     */
    private LookupTable negativo(){
        byte funcionT[] = new byte[256];
        
        for(int x=0; x<256; x++)
            funcionT[x] = (byte)(255-x);
        
        LookupTable tabla = new ByteLookupTable(0, funcionT);
        return tabla;
        
    }
    
    /**
     * Funcion LookUp cuya operación es la función polinomica de grado N. El grado
     * de la función viene dado por el valor del spinnner del grosor
     * @param n Grado de la función polinómica. 
     * @return tabla
     */
    private LookupTable polinomicaOrdenN(int n){
        byte[] funcionT = new byte[256];
        double valor_polinomico;
        
        for (int l = 0; l <= 255; ++l){ 
            valor_polinomico = 0;
            for(int i=1; i<=n; i++)
                valor_polinomico += l^i;
            
            funcionT[l] = (byte) valor_polinomico;
        }
        LookupTable tabla = new ByteLookupTable(0, funcionT);
        return tabla;
    }
    

    /**
     * Aplica una rotación a la imagen de la ventana interna
     * El valor a rotar viene dado por el spinner de rotacion
     * @param grados número de grados a rotar
     */
    private void aplicarRotacion(int grados){
        VentanaInternaImagen vi;
        
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
            BufferedImage img = vi.getLienzo2D().getImagen(false);
        
            if (img != null) {
                try {
                    AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(grados), img.getWidth() / 2, img.getHeight() / 2);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo2D().setImagen(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }
    
    /**
     * Método que aplica cualquier filtro LookupTable a la imagen de la ventana interna seleccionada
     * @param tabla tabla con el filtro a aplicar
     */
    private void aplicarLookup(LookupTable tabla){
        VentanaInternaImagen vi;
        
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
            BufferedImage img = vi.getLienzo2D().getImagen(false);
           
            if (img != null) {
                try {
                    LookupOp lop = new LookupOp(tabla, null);
                    //Misma imagen origen/destino
                    lop.filter(img, img);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }
    
    /**
     * Aplica un escalado a la imagen de la ventana interna seleccionada
     * @param x valor de escalado en eje X
     * @param y valor de escalado en eje Y
     */
    private void aplicarEscalado(double x, double y){
        VentanaInternaImagen vi; 
        
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    AffineTransform at = AffineTransform.getScaleInstance(x, y);
                                
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo2D().setImagen(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        } 
    }
            
    /**
     * Método que crea un filtro Kernel dependiendo del valor seleccionado en la IU
     * @param seleccion índice de seleccionMascara que indica el filtro seleccionado
     * @return Kernel del filtro seleccionado
     */
    private Kernel getKernel(int seleccion){
        Kernel k = null;
        
        switch(seleccion){
                case 0:
                    float filtro_media[] = KernelProducer.MASCARA_MEDIA_3x3;
                    k = new Kernel(3, 3, filtro_media);
                    break;
                
                case 1: //Media 5x5
                    float filtro_media5x5[] = new float[25];
                    
                    for(int i=0; i<25; i++)
                        filtro_media5x5[i] = 1/25f;
                    
                    k = new Kernel(5, 5, filtro_media5x5);
                    break;
                    
                case 2: //Media 7x7
                    float filtro_media7x7[] = new float[49];
                    
                    for(int i=0; i<49; i++)
                        filtro_media7x7[i] = 1/49f;
                    
                    k = new Kernel(7, 7, filtro_media7x7);
                    break;
                    
                case 3: // Binomial
                    float filtro_binomial[] = KernelProducer.MASCARA_BINOMIAL_3x3;
                    k = new Kernel(3, 3, filtro_binomial);
                    break;
                
                case 4: // Enfoque
                    float filtro_enfoque[] = KernelProducer.MASCARA_ENFOQUE_3x3;
                    k = new Kernel(3, 3, filtro_enfoque);
                    break;
                    
                case 5: // Relieve
                    float filtro_relieve[] = KernelProducer.MASCARA_RELIEVE_3x3;
                    k = new Kernel(3, 3, filtro_relieve);
                    break;
                    
                case 6: // Laplaciano
                    float filtro_laplaciano[] = KernelProducer.MASCARA_LAPLACIANA_3x3;
                    k = new Kernel(3, 3, filtro_laplaciano);
                    break;
                    
                case 7: // Emborronamiento horizontal
                    float filtro_emborHorizontal [] = {0.2f, 0.2f, 0.2f, 0.2f, 0.2f};
                    k = new Kernel(5, 1, filtro_emborHorizontal);
                    break;
                    
                case 8: // Emborronamiento diagonal
                    float filtro_emborDiagonal [] = {1/3f, 1f, 1f,
                                                     1f, 1/3f, 1f,
                                                     1f, 1f, 1/3f};
                    k = new Kernel(3,3, filtro_emborDiagonal);
                    break;
            }
        
        return k;
    }
    
    /**
     * Crea una copia de la imagen img usando sólo la banda indicada
     * @param img imagen de la que obtener la banda
     * @param banda banda a obtener
     * @return copia de la imagen original sólo con la banda indicada
     */
    private BufferedImage getImageBand(BufferedImage img, int banda) { 
        //Creamos el modelo de color de la nueva imagen basado en un espcio de color GRAY   
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ComponentColorModel cm = new ComponentColorModel(cs, false, false,
                                                            Transparency.OPAQUE,
                                                            DataBuffer.TYPE_BYTE); 

        //Creamos el nuevo raster a partir del raster de la imagen original   
        int vband[] = {banda};
        WritableRaster bRaster = (WritableRaster) img.getRaster().createWritableChild(0, 0, img.getWidth(), img.getHeight(), 0, 0, vband); 

        //Creamos una nueva imagen que contiene como raster el correspondiente a la banda   
        return new BufferedImage(cm, bRaster, false, null);
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        opcionesFiguras = new javax.swing.ButtonGroup();
        panelPrincipal = new javax.swing.JPanel();
        panelInferior = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        panelBrillo = new javax.swing.JPanel();
        sliderBrillo = new javax.swing.JSlider();
        panelFiltro = new javax.swing.JPanel();
        seleccionMascara = new javax.swing.JComboBox<>();
        panelContraste = new javax.swing.JPanel();
        botonContraste = new javax.swing.JButton();
        botonIluminacion = new javax.swing.JButton();
        botonOscurecer = new javax.swing.JButton();
        panelCuadratica = new javax.swing.JPanel();
        botonCuadratica = new javax.swing.JButton();
        botonCombinar = new javax.swing.JButton();
        botonTintar = new javax.swing.JButton();
        botonSepia = new javax.swing.JButton();
        botonEcualizar = new javax.swing.JButton();
        botonRojo = new javax.swing.JButton();
        sliderPosterizar = new javax.swing.JSlider();
        panelColor = new javax.swing.JPanel();
        botonExtraerBandas = new javax.swing.JButton();
        seleccionEspaciosColor = new javax.swing.JComboBox<>();
        panelRotacion = new javax.swing.JPanel();
        sliderRotacion = new javax.swing.JSlider();
        boton90Gr = new javax.swing.JButton();
        boton180Gr = new javax.swing.JButton();
        boton270Gr = new javax.swing.JButton();
        panelEscalado = new javax.swing.JPanel();
        botonAumentar = new javax.swing.JButton();
        botonReducir = new javax.swing.JButton();
        panelFiguras = new javax.swing.JPanel();
        botonPanelNuevo = new javax.swing.JButton();
        botonPanelAbrir = new javax.swing.JButton();
        botonPanelGuardar = new javax.swing.JButton();
        botonPunto = new javax.swing.JToggleButton();
        botonLinea = new javax.swing.JToggleButton();
        botonRectangulo = new javax.swing.JToggleButton();
        botonElipse = new javax.swing.JToggleButton();
        botonRoundRectangulo = new javax.swing.JToggleButton();
        botonTrazoDiscontinuo = new javax.swing.JToggleButton();
        bPanelEditar = new javax.swing.JToggleButton();
        spinnerGrosor = new javax.swing.JSpinner();
        Color colores1[] = {Color.BLACK, Color.RED, Color.BLUE,Color.GREEN, Color.WHITE, Color.YELLOW};
        comboColores1 = new javax.swing.JComboBox<>(colores1);
        Color colores2[] = {Color.BLACK, Color.RED, Color.BLUE,Color.GREEN, Color.WHITE, Color.YELLOW};
        comboColores2 = new javax.swing.JComboBox<>(colores2);
        comboRelleno = new javax.swing.JComboBox<>();
        bPanelTransparencia = new javax.swing.JToggleButton();
        bPanelAlisar = new javax.swing.JToggleButton();
        botonPlay = new javax.swing.JButton();
        botonStop = new javax.swing.JButton();
        listaReproduccion = new javax.swing.JComboBox<>();
        botonGrabar = new javax.swing.JButton();
        botonCamara = new javax.swing.JButton();
        botonCaptura = new javax.swing.JButton();
        escritorio = new javax.swing.JDesktopPane();
        panelActividad = new javax.swing.JPanel();
        textoActividad = new javax.swing.JLabel();
        Menu = new javax.swing.JMenuBar();
        botonMenuArchivo = new javax.swing.JMenu();
        botonNuevo = new javax.swing.JMenuItem();
        botonAbrir = new javax.swing.JMenuItem();
        botonGuardar = new javax.swing.JMenuItem();
        botonMenuEditar = new javax.swing.JMenu();
        botonEstadoOnOff = new javax.swing.JCheckBoxMenuItem();
        botonMenuImagen = new javax.swing.JMenu();
        botonRescaleOp = new javax.swing.JMenuItem();
        botonConvolveOp = new javax.swing.JMenuItem();
        botonAffineTransportOp = new javax.swing.JMenuItem();
        botonLookupOp = new javax.swing.JMenuItem();
        botonBandCombineOp = new javax.swing.JMenuItem();
        botonColorConvertOp = new javax.swing.JMenuItem();
        botonDuplicar = new javax.swing.JMenuItem();
        botonNegativo = new javax.swing.JMenuItem();
        botonPolinomicaOrdenN = new javax.swing.JMenuItem();
        botonPixelPixelPropio = new javax.swing.JMenuItem();
        botonIluminacionLookup = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        botonAyuda = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelPrincipal.setToolTipText("");
        panelPrincipal.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panelPrincipal.setLayout(new java.awt.BorderLayout());

        panelInferior.setBackground(new java.awt.Color(204, 204, 204));
        panelInferior.setPreferredSize(new java.awt.Dimension(400, 60));
        panelInferior.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setPreferredSize(new java.awt.Dimension(1348, 40));

        panelBrillo.setBackground(new java.awt.Color(204, 204, 204));
        panelBrillo.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelBrillo.setToolTipText("");
        panelBrillo.setMaximumSize(new java.awt.Dimension(150, 48));
        panelBrillo.setMinimumSize(new java.awt.Dimension(150, 48));
        panelBrillo.setPreferredSize(new java.awt.Dimension(150, 50));
        panelBrillo.setLayout(new java.awt.BorderLayout());

        sliderBrillo.setBackground(new java.awt.Color(204, 204, 204));
        sliderBrillo.setToolTipText("Brillo");
        sliderBrillo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBrilloStateChanged(evt);
            }
        });
        sliderBrillo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderBrilloFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderBrilloFocusLost(evt);
            }
        });
        panelBrillo.add(sliderBrillo, java.awt.BorderLayout.CENTER);

        jPanel1.add(panelBrillo);

        panelFiltro.setBackground(new java.awt.Color(204, 204, 204));
        panelFiltro.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelFiltro.setMaximumSize(new java.awt.Dimension(150, 90));
        panelFiltro.setMinimumSize(new java.awt.Dimension(150, 48));
        panelFiltro.setPreferredSize(new java.awt.Dimension(75, 50));
        panelFiltro.setLayout(new java.awt.BorderLayout());

        seleccionMascara.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Media", "Media 5x5", "Media 7x7", "Binomial", "Enfoque", "Relieve", "Laplaciano", "Emb. Horizontal", "Emb. Diagonal", " ", " " }));
        seleccionMascara.setToolTipText("Máscaras");
        seleccionMascara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionMascaraActionPerformed(evt);
            }
        });
        panelFiltro.add(seleccionMascara, java.awt.BorderLayout.CENTER);

        jPanel1.add(panelFiltro);

        panelContraste.setBackground(new java.awt.Color(204, 204, 204));
        panelContraste.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelContraste.setMinimumSize(new java.awt.Dimension(55, 41));
        panelContraste.setPreferredSize(new java.awt.Dimension(173, 50));

        botonContraste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/contraste.png"))); // NOI18N
        botonContraste.setToolTipText("Constraste");
        botonContraste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonContrasteActionPerformed(evt);
            }
        });
        panelContraste.add(botonContraste);

        botonIluminacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/iluminar.png"))); // NOI18N
        botonIluminacion.setToolTipText("Iluminar");
        botonIluminacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonIluminacionActionPerformed(evt);
            }
        });
        panelContraste.add(botonIluminacion);

        botonOscurecer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/oscurecer.png"))); // NOI18N
        botonOscurecer.setToolTipText("Oscurecer");
        botonOscurecer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonOscurecerActionPerformed(evt);
            }
        });
        panelContraste.add(botonOscurecer);

        jPanel1.add(panelContraste);

        panelCuadratica.setBackground(new java.awt.Color(204, 204, 204));
        panelCuadratica.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelCuadratica.setMaximumSize(new java.awt.Dimension(70, 50));
        panelCuadratica.setMinimumSize(new java.awt.Dimension(0, 0));
        panelCuadratica.setPreferredSize(new java.awt.Dimension(400, 50));

        botonCuadratica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/cuadratica.png"))); // NOI18N
        botonCuadratica.setToolTipText("Cuadrática");
        botonCuadratica.setMaximumSize(new java.awt.Dimension(50, 30));
        botonCuadratica.setPreferredSize(new java.awt.Dimension(49, 25));
        botonCuadratica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCuadraticaActionPerformed(evt);
            }
        });
        panelCuadratica.add(botonCuadratica);

        botonCombinar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/combinar.png"))); // NOI18N
        botonCombinar.setToolTipText("Combinar bandas");
        botonCombinar.setPreferredSize(new java.awt.Dimension(49, 25));
        botonCombinar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCombinarActionPerformed(evt);
            }
        });
        panelCuadratica.add(botonCombinar);

        botonTintar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/tintar.png"))); // NOI18N
        botonTintar.setToolTipText("Tintar");
        botonTintar.setPreferredSize(new java.awt.Dimension(49, 25));
        botonTintar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTintarActionPerformed(evt);
            }
        });
        panelCuadratica.add(botonTintar);

        botonSepia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/sepia.png"))); // NOI18N
        botonSepia.setToolTipText("Sepia");
        botonSepia.setPreferredSize(new java.awt.Dimension(49, 25));
        botonSepia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSepiaActionPerformed(evt);
            }
        });
        panelCuadratica.add(botonSepia);

        botonEcualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/ecualizar.png"))); // NOI18N
        botonEcualizar.setToolTipText("Ecualizar");
        botonEcualizar.setPreferredSize(new java.awt.Dimension(49, 25));
        botonEcualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEcualizarActionPerformed(evt);
            }
        });
        panelCuadratica.add(botonEcualizar);

        botonRojo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/bRojo.png"))); // NOI18N
        botonRojo.setToolTipText("Rojo");
        botonRojo.setPreferredSize(new java.awt.Dimension(49, 25));
        botonRojo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRojoActionPerformed(evt);
            }
        });
        panelCuadratica.add(botonRojo);

        sliderPosterizar.setBackground(new java.awt.Color(204, 204, 204));
        sliderPosterizar.setMajorTickSpacing(2);
        sliderPosterizar.setMaximum(20);
        sliderPosterizar.setMinimum(1);
        sliderPosterizar.setPaintTicks(true);
        sliderPosterizar.setToolTipText("Posterizar");
        sliderPosterizar.setValue(1);
        sliderPosterizar.setAlignmentX(0.0F);
        sliderPosterizar.setPreferredSize(new java.awt.Dimension(60, 30));
        sliderPosterizar.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderPosterizarStateChanged(evt);
            }
        });
        sliderPosterizar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderPosterizarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderPosterizarFocusLost(evt);
            }
        });
        panelCuadratica.add(sliderPosterizar);

        jPanel1.add(panelCuadratica);

        panelColor.setBackground(new java.awt.Color(204, 204, 204));
        panelColor.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelColor.setPreferredSize(new java.awt.Dimension(130, 50));
        panelColor.setLayout(new javax.swing.BoxLayout(panelColor, javax.swing.BoxLayout.LINE_AXIS));

        botonExtraerBandas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/bandas.png"))); // NOI18N
        botonExtraerBandas.setToolTipText("Extraer bandas");
        botonExtraerBandas.setPreferredSize(new java.awt.Dimension(50, 50));
        botonExtraerBandas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonExtraerBandasActionPerformed(evt);
            }
        });
        panelColor.add(botonExtraerBandas);

        seleccionEspaciosColor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RGB", "YCC", "GREY", " " }));
        seleccionEspaciosColor.setToolTipText("Espacios de color");
        seleccionEspaciosColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionEspaciosColorActionPerformed(evt);
            }
        });
        panelColor.add(seleccionEspaciosColor);

        jPanel1.add(panelColor);

        panelRotacion.setBackground(new java.awt.Color(204, 204, 204));
        panelRotacion.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelRotacion.setPreferredSize(new java.awt.Dimension(275, 50));
        panelRotacion.setLayout(new javax.swing.BoxLayout(panelRotacion, javax.swing.BoxLayout.LINE_AXIS));

        sliderRotacion.setBackground(new java.awt.Color(204, 204, 204));
        sliderRotacion.setForeground(new java.awt.Color(0, 0, 0));
        sliderRotacion.setMajorTickSpacing(90);
        sliderRotacion.setMaximum(360);
        sliderRotacion.setPaintTicks(true);
        sliderRotacion.setToolTipText("Rotación");
        sliderRotacion.setValue(0);
        sliderRotacion.setPreferredSize(new java.awt.Dimension(125, 31));
        sliderRotacion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderRotacionStateChanged(evt);
            }
        });
        sliderRotacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sliderRotacionFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sliderRotacionFocusLost(evt);
            }
        });
        panelRotacion.add(sliderRotacion);

        boton90Gr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/rotacion90.png"))); // NOI18N
        boton90Gr.setToolTipText("Rotar 90º");
        boton90Gr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton90GrActionPerformed(evt);
            }
        });
        panelRotacion.add(boton90Gr);

        boton180Gr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/rotacion180.png"))); // NOI18N
        boton180Gr.setToolTipText("Rotar 180º");
        boton180Gr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton180GrActionPerformed(evt);
            }
        });
        panelRotacion.add(boton180Gr);

        boton270Gr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/rotacion270.png"))); // NOI18N
        boton270Gr.setToolTipText("Rotar 270º");
        boton270Gr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton270GrActionPerformed(evt);
            }
        });
        panelRotacion.add(boton270Gr);

        jPanel1.add(panelRotacion);

        panelEscalado.setBackground(new java.awt.Color(204, 204, 204));
        panelEscalado.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelEscalado.setPreferredSize(new java.awt.Dimension(105, 50));
        panelEscalado.setLayout(new javax.swing.BoxLayout(panelEscalado, javax.swing.BoxLayout.LINE_AXIS));

        botonAumentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/aumentar.png"))); // NOI18N
        botonAumentar.setToolTipText("Aumentar");
        botonAumentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAumentarActionPerformed(evt);
            }
        });
        panelEscalado.add(botonAumentar);

        botonReducir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/disminuir.png"))); // NOI18N
        botonReducir.setToolTipText("Disminuir");
        botonReducir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonReducirActionPerformed(evt);
            }
        });
        panelEscalado.add(botonReducir);

        jPanel1.add(panelEscalado);

        panelInferior.add(jPanel1, java.awt.BorderLayout.WEST);

        panelPrincipal.add(panelInferior, java.awt.BorderLayout.SOUTH);

        panelFiguras.setBackground(new java.awt.Color(204, 204, 204));
        panelFiguras.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelFiguras.setPreferredSize(new java.awt.Dimension(400, 50));
        panelFiguras.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        botonPanelNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/nuevo.png"))); // NOI18N
        botonPanelNuevo.setToolTipText("Nuevo");
        botonPanelNuevo.setPreferredSize(new java.awt.Dimension(40, 40));
        botonPanelNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPanelNuevoActionPerformed(evt);
            }
        });
        panelFiguras.add(botonPanelNuevo);

        botonPanelAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/abrir.png"))); // NOI18N
        botonPanelAbrir.setToolTipText("Abrir");
        botonPanelAbrir.setPreferredSize(new java.awt.Dimension(40, 40));
        botonPanelAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPanelAbrirActionPerformed(evt);
            }
        });
        panelFiguras.add(botonPanelAbrir);

        botonPanelGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/guardar.png"))); // NOI18N
        botonPanelGuardar.setToolTipText("Guardar");
        botonPanelGuardar.setMinimumSize(new java.awt.Dimension(0, 0));
        botonPanelGuardar.setPreferredSize(new java.awt.Dimension(40, 40));
        botonPanelGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPanelGuardarActionPerformed(evt);
            }
        });
        panelFiguras.add(botonPanelGuardar);

        opcionesFiguras.add(botonPunto);
        botonPunto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/punto.png"))); // NOI18N
        botonPunto.setToolTipText("Punto");
        botonPunto.setMaximumSize(new java.awt.Dimension(40, 40));
        botonPunto.setPreferredSize(new java.awt.Dimension(40, 40));
        botonPunto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPuntoActionPerformed(evt);
            }
        });
        panelFiguras.add(botonPunto);

        opcionesFiguras.add(botonLinea);
        botonLinea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/linea.png"))); // NOI18N
        botonLinea.setToolTipText("Línea");
        botonLinea.setMaximumSize(new java.awt.Dimension(40, 40));
        botonLinea.setPreferredSize(new java.awt.Dimension(40, 40));
        botonLinea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLineaActionPerformed(evt);
            }
        });
        panelFiguras.add(botonLinea);

        opcionesFiguras.add(botonRectangulo);
        botonRectangulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/rectangulo.png"))); // NOI18N
        botonRectangulo.setToolTipText("Rectángulo");
        botonRectangulo.setMaximumSize(new java.awt.Dimension(40, 40));
        botonRectangulo.setPreferredSize(new java.awt.Dimension(40, 40));
        botonRectangulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRectanguloActionPerformed(evt);
            }
        });
        panelFiguras.add(botonRectangulo);

        opcionesFiguras.add(botonElipse);
        botonElipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/elipse.png"))); // NOI18N
        botonElipse.setToolTipText("Elipse");
        botonElipse.setMaximumSize(new java.awt.Dimension(40, 40));
        botonElipse.setPreferredSize(new java.awt.Dimension(40, 40));
        botonElipse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonElipseActionPerformed(evt);
            }
        });
        panelFiguras.add(botonElipse);

        opcionesFiguras.add(botonRoundRectangulo);
        botonRoundRectangulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/roundrectangulo.png"))); // NOI18N
        botonRoundRectangulo.setToolTipText("RoundRectangulo");
        botonRoundRectangulo.setPreferredSize(new java.awt.Dimension(40, 40));
        botonRoundRectangulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRoundRectanguloActionPerformed(evt);
            }
        });
        panelFiguras.add(botonRoundRectangulo);

        botonTrazoDiscontinuo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/discontinuo.png"))); // NOI18N
        botonTrazoDiscontinuo.setToolTipText("Trazo Discontinuo");
        botonTrazoDiscontinuo.setPreferredSize(new java.awt.Dimension(40, 40));
        botonTrazoDiscontinuo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTrazoDiscontinuoActionPerformed(evt);
            }
        });
        panelFiguras.add(botonTrazoDiscontinuo);

        bPanelEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/seleccion.png"))); // NOI18N
        bPanelEditar.setToolTipText("Selección");
        bPanelEditar.setPreferredSize(new java.awt.Dimension(40, 40));
        bPanelEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPanelEditarActionPerformed(evt);
            }
        });
        panelFiguras.add(bPanelEditar);

        spinnerGrosor.setToolTipText("Grosor");
        spinnerGrosor.setPreferredSize(new java.awt.Dimension(30, 30));
        spinnerGrosor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerGrosorStateChanged(evt);
            }
        });
        panelFiguras.add(spinnerGrosor);

        comboColores1.setToolTipText("Color 1");
        comboColores1.setRenderer(new ColorCellRender());
        comboColores1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboColores1ActionPerformed(evt);
            }
        });
        panelFiguras.add(comboColores1);

        comboColores2.setToolTipText("Color 2");
        comboColores2.setRenderer(new ColorCellRender());
        comboColores2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboColores2ActionPerformed(evt);
            }
        });
        panelFiguras.add(comboColores2);

        comboRelleno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hueco", "Plano", "Bordes", "Deg. Horizontal", "Deg. Vertical" }));
        comboRelleno.setToolTipText("Modo de Relleno");
        comboRelleno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboRellenoActionPerformed(evt);
            }
        });
        panelFiguras.add(comboRelleno);

        bPanelTransparencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/transparencia.png"))); // NOI18N
        bPanelTransparencia.setToolTipText("Transparencia");
        bPanelTransparencia.setPreferredSize(new java.awt.Dimension(40, 40));
        bPanelTransparencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPanelTransparenciaActionPerformed(evt);
            }
        });
        panelFiguras.add(bPanelTransparencia);

        bPanelAlisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/alisar.png"))); // NOI18N
        bPanelAlisar.setToolTipText("Alisar");
        bPanelAlisar.setPreferredSize(new java.awt.Dimension(40, 40));
        bPanelAlisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPanelAlisarActionPerformed(evt);
            }
        });
        panelFiguras.add(bPanelAlisar);

        botonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/Play.png"))); // NOI18N
        botonPlay.setToolTipText("Play");
        botonPlay.setPreferredSize(new java.awt.Dimension(40, 40));
        botonPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPlayActionPerformed(evt);
            }
        });
        panelFiguras.add(botonPlay);

        botonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/Stop.png"))); // NOI18N
        botonStop.setToolTipText("Stop");
        botonStop.setPreferredSize(new java.awt.Dimension(40, 40));
        botonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonStopActionPerformed(evt);
            }
        });
        panelFiguras.add(botonStop);

        listaReproduccion.setToolTipText("Lista de reproducción");
        listaReproduccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listaReproduccionActionPerformed(evt);
            }
        });
        panelFiguras.add(listaReproduccion);

        botonGrabar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/record24x24.png"))); // NOI18N
        botonGrabar.setToolTipText("Grabar");
        botonGrabar.setPreferredSize(new java.awt.Dimension(40, 40));
        botonGrabar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGrabarActionPerformed(evt);
            }
        });
        panelFiguras.add(botonGrabar);

        botonCamara.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/Camara.png"))); // NOI18N
        botonCamara.setToolTipText("WebCam");
        botonCamara.setPreferredSize(new java.awt.Dimension(40, 40));
        botonCamara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCamaraActionPerformed(evt);
            }
        });
        panelFiguras.add(botonCamara);

        botonCaptura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Iconos/Capturar.png"))); // NOI18N
        botonCaptura.setToolTipText("Instantánea");
        botonCaptura.setPreferredSize(new java.awt.Dimension(40, 40));
        botonCaptura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCapturaActionPerformed(evt);
            }
        });
        panelFiguras.add(botonCaptura);

        panelPrincipal.add(panelFiguras, java.awt.BorderLayout.NORTH);

        escritorio.setPreferredSize(new java.awt.Dimension(1374, 306));

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1374, Short.MAX_VALUE)
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 316, Short.MAX_VALUE)
        );

        panelPrincipal.add(escritorio, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelPrincipal, java.awt.BorderLayout.CENTER);

        panelActividad.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelActividad.setPreferredSize(new java.awt.Dimension(100, 20));
        panelActividad.setLayout(new java.awt.BorderLayout());

        textoActividad.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        textoActividad.setText("Actividad");
        textoActividad.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        panelActividad.add(textoActividad, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(panelActividad, java.awt.BorderLayout.SOUTH);

        Menu.setBackground(java.awt.SystemColor.controlHighlight);

        botonMenuArchivo.setText("Archivo");

        botonNuevo.setText("Nuevo");
        botonNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonNuevoActionPerformed(evt);
            }
        });
        botonMenuArchivo.add(botonNuevo);

        botonAbrir.setText("Abrir");
        botonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAbrirActionPerformed(evt);
            }
        });
        botonMenuArchivo.add(botonAbrir);

        botonGuardar.setText("Guardar");
        botonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarActionPerformed(evt);
            }
        });
        botonMenuArchivo.add(botonGuardar);

        Menu.add(botonMenuArchivo);

        botonMenuEditar.setText("Editar");

        botonEstadoOnOff.setSelected(true);
        botonEstadoOnOff.setText("Ver barra de estado");
        botonEstadoOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEstadoOnOffActionPerformed(evt);
            }
        });
        botonMenuEditar.add(botonEstadoOnOff);

        Menu.add(botonMenuEditar);

        botonMenuImagen.setText("Imagen");

        botonRescaleOp.setText("Operador RescaleOp");
        botonRescaleOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRescaleOpActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonRescaleOp);

        botonConvolveOp.setText("Operador ConvolveOp");
        botonConvolveOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonConvolveOpActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonConvolveOp);

        botonAffineTransportOp.setText("Operador AffineTrasnformOp");
        botonAffineTransportOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAffineTransportOpActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonAffineTransportOp);

        botonLookupOp.setText("Operador LookupOp");
        botonLookupOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLookupOpActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonLookupOp);

        botonBandCombineOp.setText("Operador BandCombineOp");
        botonBandCombineOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBandCombineOpActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonBandCombineOp);

        botonColorConvertOp.setText("Operador ColorConvertOp");
        botonColorConvertOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonColorConvertOpActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonColorConvertOp);

        botonDuplicar.setText("Duplicar");
        botonDuplicar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDuplicarActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonDuplicar);

        botonNegativo.setText("Operador Negativo");
        botonNegativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonNegativoActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonNegativo);

        botonPolinomicaOrdenN.setText("Operador Polinomica Orden N");
        botonPolinomicaOrdenN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPolinomicaOrdenNActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonPolinomicaOrdenN);

        botonPixelPixelPropio.setText("Operador Pixel a Pixel Propio");
        botonPixelPixelPropio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPixelPixelPropioActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonPixelPixelPropio);

        botonIluminacionLookup.setText("Operador Lookup Iluminación");
        botonIluminacionLookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonIluminacionLookupActionPerformed(evt);
            }
        });
        botonMenuImagen.add(botonIluminacionLookup);

        Menu.add(botonMenuImagen);

        jMenu1.setText("Ayuda");

        botonAyuda.setText("Acerca De");
        botonAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAyudaActionPerformed(evt);
            }
        });
        jMenu1.add(botonAyuda);

        Menu.add(jMenu1);

        setJMenuBar(Menu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAbrirActionPerformed
        JFileChooser dlg = new JFileChooser();
        
        dlg.setFileFilter(new FileNameExtensionFilter("Imagen JPEG (*.jpg, *.jpeg)", "jpg", "jpeg"));
        dlg.setFileFilter(new FileNameExtensionFilter("Imagen PNG (*.png)", "png"));          
        dlg.setFileFilter(new FileNameExtensionFilter("Imagen GIF (*.gif)", "gif"));
 
        
        dlg.setFileFilter(new FileNameExtensionFilter("Sonido WAV (*.wav)", "wav"));
        dlg.setFileFilter(new FileNameExtensionFilter("Sonido AU (*.au)", "au"));
        dlg.setFileFilter(new FileNameExtensionFilter("Sonido MP3 (*.mp3)", "mp3"));
        
        dlg.setFileFilter(new FileNameExtensionFilter("Todos los tipos de imagenes (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif")); 
        dlg.setFileFilter(new FileNameExtensionFilter("Todos los tipos de sonido (*.wav, *.au, *.mp3)", "wav", "au", "mp3"));
        dlg.setFileFilter(new FileNameExtensionFilter("Todos los ficheros permitidos", "wav", "au", "mp3", "jpg", "jpeg", "png", "gif"));
        
        int resp = dlg.showOpenDialog(this);
        if(resp == JFileChooser.APPROVE_OPTION){
            try {
                File f = dlg.getSelectedFile();
                
                if(f.getName().endsWith(".wav") || f.getName().endsWith(".au") || f.getName().endsWith(".mp3")){
                    f = new File(dlg.getSelectedFile().getAbsolutePath()){
                        @Override
                        public String toString(){
                            return this.getName();
                        }
                    };
                    
                    this.listaReproduccion.addItem(f);
                    this.listaReproduccion.setSelectedItem(f);
                }
                else{
                    BufferedImage img = ImageIO.read(f);
                    VentanaInternaImagen vi = new VentanaInternaImagen();
                    MiManejadorLienzo ml = new MiManejadorLienzo();
                    MiManejadorVII mvii = new MiManejadorVII();
                    vi.getLienzo2D().addLienzoListener(ml);
                    vi.addInternalFrameListener(mvii);
                    vi.getLienzo2D().setImagen(img);
                    this.escritorio.add(vi);
                    vi.setTitle(f.getName());
                    vi.setVisible(true);
                }
            } catch (Exception ex) {
                System.err.println("Error al leer la imagen");
            }
        }
    }//GEN-LAST:event_botonAbrirActionPerformed

    private void botonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGuardarActionPerformed
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            VentanaInternaImagen vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
            BufferedImage img = vi.getLienzo2D().getImagen(true);
            if (img != null) {
                JFileChooser dlg = new JFileChooser();
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = dlg.getSelectedFile();
                        ImageIO.write(img, "jpg", f);
                        vi.setTitle(f.getName());
                    } catch (Exception ex) {
                        System.err.println("Error al guardar la imagen");
                    }
                }
            }
        }
    }//GEN-LAST:event_botonGuardarActionPerformed

    private void botonEstadoOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEstadoOnOffActionPerformed
        if(this.botonEstadoOnOff.isSelected())
            this.textoActividad.setVisible(true);
        else
            this.textoActividad.setVisible(false);
    }//GEN-LAST:event_botonEstadoOnOffActionPerformed

    private void spinnerGrosorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerGrosorStateChanged
        VentanaInternaImagen vi;
        int valor = (Integer) spinnerGrosor.getValue(); 
        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen){
            vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
            vi.getLienzo2D().setStroke(valor);
            vi.getLienzo2D().updateShape();
        }
    }//GEN-LAST:event_spinnerGrosorStateChanged

    private void botonElipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonElipseActionPerformed
        VentanaInternaImagen vi=(VentanaInternaImagen) escritorio.getSelectedFrame();
        
        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen)    vi.getLienzo2D().setFiguraActual(Figura.ELIPSE);
        if(!this.bPanelEditar.isSelected())     textoActividad.setText("Dibujando Elipse");
    }//GEN-LAST:event_botonElipseActionPerformed

    private void botonRectanguloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRectanguloActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) escritorio.getSelectedFrame();

        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen)    vi.getLienzo2D().setFiguraActual(Figura.RECTANGULO);
        if(!this.bPanelEditar.isSelected())     textoActividad.setText("Dibujando Rectángulo");
    }//GEN-LAST:event_botonRectanguloActionPerformed

    private void botonLineaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLineaActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) escritorio.getSelectedFrame();

        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen)    vi.getLienzo2D().setFiguraActual(Figura.LINEA);
        if(!this.bPanelEditar.isSelected())     textoActividad.setText("Dibujando Línea");
    }//GEN-LAST:event_botonLineaActionPerformed

    private void botonPuntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPuntoActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen)    vi.getLienzo2D().setFiguraActual(Figura.PUNTO);
        if(!this.bPanelEditar.isSelected())     textoActividad.setText("Dibujando Punto");
    }//GEN-LAST:event_botonPuntoActionPerformed

    private void botonPanelAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPanelAbrirActionPerformed
        JFileChooser dlg = new JFileChooser();
        
        dlg.setFileFilter(new FileNameExtensionFilter("Imagen JPEG (*.jpg, *.jpeg)", "jpg", "jpeg"));
        dlg.setFileFilter(new FileNameExtensionFilter("Imagen PNG (*.png)", "png"));          
        dlg.setFileFilter(new FileNameExtensionFilter("Imagen GIF (*.gif)", "gif"));
 
        
        dlg.setFileFilter(new FileNameExtensionFilter("Sonido WAV (*.wav)", "wav"));
        dlg.setFileFilter(new FileNameExtensionFilter("Sonido AU (*.au)", "au"));
        dlg.setFileFilter(new FileNameExtensionFilter("Sonido MP3 (*.mp3)", "mp3"));
        
        dlg.setFileFilter(new FileNameExtensionFilter("Video MP4 (*.mp4)", "mp4"));
        dlg.setFileFilter(new FileNameExtensionFilter("Video MKV (*.mkv)", "mkv"));
        dlg.setFileFilter(new FileNameExtensionFilter("Video MPG (*.mpg)", "mpg"));
        dlg.setFileFilter(new FileNameExtensionFilter("Sonido AVI (*.avi)", "avi"));
        
        
        dlg.setFileFilter(new FileNameExtensionFilter("Todos los tipos de imagenes (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif")); 
        dlg.setFileFilter(new FileNameExtensionFilter("Todos los tipos de sonido (*.wav, *.au, *.mp3)", "wav", "au", "mp3"));
        dlg.setFileFilter(new FileNameExtensionFilter("Todos los tipos de video (*.mp4, *.mpg, *.avi, *mkv)", "mp4", "mpg", "avi", "mkv"));
        dlg.setFileFilter(new FileNameExtensionFilter("Todos los ficheros permitidos", "wav", "au", "mp3", "jpg", "jpeg", "png", "gif", "mp4", "mpg", "avi", "mkv"));
        
        int resp = dlg.showOpenDialog(this);
        if(resp == JFileChooser.APPROVE_OPTION){
            
            File f = dlg.getSelectedFile();

            if (f.getName().endsWith(".wav") || f.getName().endsWith(".au") || f.getName().endsWith(".mp3")) {
                try{
                    f = new File(dlg.getSelectedFile().getAbsolutePath()) {
                        @Override
                        public String toString() {
                            return this.getName();
                        }
                    };

                    this.listaReproduccion.addItem(f);
                    this.listaReproduccion.setSelectedItem(f);
                } catch (Exception ex) {
                    System.err.println("Error al abrir el archivo de audio");
                }
            } else if (f.getName().endsWith(".mp4") || f.getName().endsWith(".mkv") || f.getName().endsWith(".mpg") || f.getName().endsWith(".avi")) {
                try{
                    f = new File(dlg.getSelectedFile().getAbsolutePath()) {
                        @Override
                        public String toString() {
                            return this.getName();
                        }
                    };

                    VentanaInternaVideo vv = VentanaInternaVideo.getInstance(f);
                    vv.addMediaPlayerEventListener(new VideoListener());

                    escritorio.add(vv);
                    vv.setTitle(f.getName());
                    vv.setVisible(true);
                    vv.play();
                    } catch (Exception ex) {
                       System.err.println(ex.getMessage());
                    }
            } else {
                try{
                    BufferedImage img = ImageIO.read(f);
                    VentanaInternaImagen vi = new VentanaInternaImagen();
                    MiManejadorLienzo ml = new MiManejadorLienzo();
                    MiManejadorVII mvii = new MiManejadorVII();
                    vi.addInternalFrameListener(mvii);
                    vi.getLienzo2D().addLienzoListener(ml);
                    vi.getLienzo2D().setImagen(img);
                    this.escritorio.add(vi);
                    vi.setTitle(f.getName());
                    vi.setVisible(true);
                } catch (Exception ex) {
                    System.err.println("Error al archivo de imagen");
                }
            }

        }
    }//GEN-LAST:event_botonPanelAbrirActionPerformed

    private void botonPanelNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPanelNuevoActionPerformed
        VentanaInternaImagen vi = new VentanaInternaImagen();
        MiManejadorLienzo ml = new MiManejadorLienzo();
        MiManejadorVII mvii = new MiManejadorVII();
        vi.addInternalFrameListener(mvii);
        vi.getLienzo2D().addLienzoListener(ml);
        escritorio.add(vi);
        vi.setVisible(true);
        BufferedImage img;
        img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d;
        g2d = img.createGraphics();
        Rectangle rect = new Rectangle();
        rect.setFrameFromDiagonal(new Point(0,0), new Point(img.getWidth(), img.getHeight()));
        g2d.setPaint(new Color(255,255,255));
        g2d.fill(rect);
        g2d.draw(rect);
        
        vi.getLienzo2D().setImagen(img);
    }//GEN-LAST:event_botonPanelNuevoActionPerformed

    private void botonPanelGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPanelGuardarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(true);
            if (img != null) {
                JFileChooser dlg = new JFileChooser();
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = dlg.getSelectedFile();
                        ImageIO.write(img, "jpg", f);
                        vi.setTitle(f.getName());
                    } catch (Exception ex) {
                        System.err.println("Error al guardar la imagen");
                    }
                }
            }
        }
    }//GEN-LAST:event_botonPanelGuardarActionPerformed

    private void bPanelEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPanelEditarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen)
            vi.getLienzo2D().setMover(bPanelEditar.isSelected());
        
    }//GEN-LAST:event_bPanelEditarActionPerformed

    private void bPanelTransparenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPanelTransparenciaActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen){
            vi.getLienzo2D().setTransparencia(bPanelTransparencia.isSelected());
            vi.getLienzo2D().updateShape();
        }
    }//GEN-LAST:event_bPanelTransparenciaActionPerformed

    private void bPanelAlisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPanelAlisarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen){
            vi.getLienzo2D().setSuavizado(bPanelAlisar.isSelected());
            vi.getLienzo2D().updateShape();
        }
    }//GEN-LAST:event_bPanelAlisarActionPerformed

    private void comboColores1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboColores1ActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen){
            vi.getLienzo2D().setColor1((Color)comboColores1.getSelectedItem());
            vi.getLienzo2D().updateShape();
        }
    }//GEN-LAST:event_comboColores1ActionPerformed

    private void botonConvolveOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonConvolveOpActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(true);
            if (img != null) {
                try {
                    float filtro[] = {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};
                    Kernel k = new Kernel(3, 3, filtro);
                    ConvolveOp cop = new ConvolveOp(k);
                    
                    BufferedImage imgdest = cop.filter(img, null);
                    vi.getLienzo2D().setImagen(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonConvolveOpActionPerformed

    private void botonRescaleOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRescaleOpActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    RescaleOp rop = new RescaleOp(1.0F, 100.0F, null);
                    rop.filter(img, img);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonRescaleOpActionPerformed

    private void sliderBrilloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBrilloFocusGained
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            ColorModel cm = vi.getLienzo2D().getImagen(false).getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImagen(false).copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImagen(false).isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_sliderBrilloFocusGained

    private void sliderBrilloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderBrilloFocusLost
        imgFuente = null;
        this.sliderBrillo.setValue(0);
    }//GEN-LAST:event_sliderBrilloFocusLost

    private void sliderBrilloStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBrilloStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    int brillo = this.sliderBrillo.getValue();
                    RescaleOp rop = new RescaleOp(1.0F, brillo, null);
                    rop.filter(imgFuente, img);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderBrilloStateChanged

    private void seleccionMascaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionMascaraActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);

            Kernel k = getKernel(this.seleccionMascara.getSelectedIndex());
            
            if (img != null && k!=null) {
                try {
                    ConvolveOp cop = new ConvolveOp(k);
                    
                    BufferedImage imgdest = cop.filter(img, null);
                    vi.getLienzo2D().setImagen(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_seleccionMascaraActionPerformed

    private void botonLookupOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLookupOpActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    byte funcionT[] = new byte[256];
                    for (int x = 0; x < 256; x++) {
                        funcionT[x] = (byte) (255 - x);  // Negativo
                    }
                    
                    LookupTable tabla = new ByteLookupTable(0, funcionT);
                    LookupOp lop = new LookupOp(tabla, null);                    
                    
                    img = ImageTools.convertImageType(img, BufferedImage.TYPE_INT_ARGB);
                    
                    BufferedImage imgdest = lop.filter(img, null);
                    vi.getLienzo2D().setImagen(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonLookupOpActionPerformed

    private void botonAffineTransportOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAffineTransportOpActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    AffineTransform at = AffineTransform.getScaleInstance(1.5, 1.5);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo2D().setImagen(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonAffineTransportOpActionPerformed

    private void botonContrasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonContrasteActionPerformed
        LookupTable tabla = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_SFUNCION);
        aplicarLookup(tabla);            
    }//GEN-LAST:event_botonContrasteActionPerformed

    private void botonIluminacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonIluminacionActionPerformed
        LookupTable tabla = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_LOGARITHM);
        aplicarLookup(tabla);          
    }//GEN-LAST:event_botonIluminacionActionPerformed

    private void botonOscurecerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonOscurecerActionPerformed
        LookupTable tabla = LookupTableProducer.createLookupTable(LookupTableProducer.TYPE_POWER);
        aplicarLookup(tabla);
    }//GEN-LAST:event_botonOscurecerActionPerformed

    private void botonCuadraticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCuadraticaActionPerformed
        int m = 128;
        LookupTable tabla = cuadratica(m);
        aplicarLookup(tabla);
    }//GEN-LAST:event_botonCuadraticaActionPerformed

    private void sliderRotacionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderRotacionFocusGained
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            this.imgFuente = vi.getLienzo2D().getImagen(false);
        }
    }//GEN-LAST:event_sliderRotacionFocusGained

    private void sliderRotacionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderRotacionFocusLost
        this.imgFuente = null;
        this.sliderRotacion.setValue(0);
    }//GEN-LAST:event_sliderRotacionFocusLost

    private void sliderRotacionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderRotacionStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            if(imgFuente!=null){
                try{
                    AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(this.sliderRotacion.getValue()), imgFuente.getWidth() / 2, imgFuente.getHeight() / 2);
                    AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(imgFuente, null);
                    vi.getLienzo2D().setImagen(imgdest);
                    vi.getLienzo2D().repaint();
                }catch(IllegalArgumentException e){
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderRotacionStateChanged

    private void boton90GrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton90GrActionPerformed
        aplicarRotacion(90);
    }//GEN-LAST:event_boton90GrActionPerformed

    private void boton180GrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton180GrActionPerformed
        aplicarRotacion(180);
    }//GEN-LAST:event_boton180GrActionPerformed

    private void boton270GrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton270GrActionPerformed
        aplicarRotacion(270);
    }//GEN-LAST:event_boton270GrActionPerformed

    private void botonAumentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAumentarActionPerformed
        double factor = 1.25;
        aplicarEscalado(factor,factor);
    }//GEN-LAST:event_botonAumentarActionPerformed

    private void botonReducirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonReducirActionPerformed
        double factor = 0.75;
        aplicarEscalado(factor,factor);
    }//GEN-LAST:event_botonReducirActionPerformed

    private void botonBandCombineOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBandCombineOpActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    float[][] matriz = {{1.0F, 0.0F, 0.0F}, 
                                        {0.0F, 0.0F, 1.0F}, 
                                        {0.0F, 1.0F, 0.0F}};
                    BandCombineOp bcop = new BandCombineOp(matriz, null);
                    bcop.filter(img.getRaster(), img.getRaster());
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonBandCombineOpActionPerformed

    private void botonColorConvertOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonColorConvertOpActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);
                    ColorConvertOp op = new ColorConvertOp(cs, null);
                    BufferedImage imgdest = op.filter(img, null);
                    vi.getLienzo2D().setImagen(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonColorConvertOpActionPerformed

    private void botonExtraerBandasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonExtraerBandasActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame()); 
        
        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen){
            BufferedImage img = vi.getLienzo2D().getImagen(false);

            if(img!=null){
                
                for(int i=0; i<img.getRaster().getNumBands(); i++){
                    vi = new VentanaInternaImagen();
                    MiManejadorLienzo ml = new MiManejadorLienzo();
                    MiManejadorVII mvii = new MiManejadorVII();
                    vi.addInternalFrameListener(mvii);
                    vi.getLienzo2D().addLienzoListener(ml);
                    BufferedImage imgBanda = getImageBand(img, i);
                    vi.getLienzo2D().setImagen(imgBanda);
                    vi.setTitle("[Banda "+i+"]");
                    escritorio.add(vi);
                    vi.setVisible(true);
                }
            }
        }        
    }//GEN-LAST:event_botonExtraerBandasActionPerformed

    private void seleccionEspaciosColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionEspaciosColorActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                
                int seleccion = this.seleccionEspaciosColor.getSelectedIndex();
                ColorSpace cs = null;
                
                switch (seleccion){
                    case 0:
                         cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                         vi.setTitle("[RGB]");
                        break;
                    
                    case 1:
                         cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);
                         vi.setTitle("[YCC]");
                        break;
                    
                    case 2:
                         cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                         vi.setTitle("[GRAY]");
                        break;
                    
                };
                
                try {
                    ColorConvertOp op = new ColorConvertOp(cs, null);
                    BufferedImage imgdest = op.filter(img, null);
                    vi.getLienzo2D().setImagen(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_seleccionEspaciosColorActionPerformed

    private void botonCombinarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCombinarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    float[][] matriz = {{0.0F, 1/2F, 1/2F}, 
                                        {1/2F, 0.0F, 1/2F}, 
                                        {1/2F, 1/2F, 0.0F}};
                    BandCombineOp bcop = new BandCombineOp(matriz, null);
                    bcop.filter(img.getRaster(), img.getRaster());
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonCombinarActionPerformed

    private void botonTintarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTintarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    TintOp tintado = new TintOp((Color)this.comboColores1.getSelectedItem(), 0.5f);
                    tintado.filter(img,img);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonTintarActionPerformed

    private void botonSepiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSepiaActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    SepiaOp sepia = new SepiaOp();
                    sepia.filter(img,img);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonSepiaActionPerformed

    private void botonEcualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEcualizarActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    EqualizationOp ecualizacion = new EqualizationOp();
                    ecualizacion.filter(img, img);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonEcualizarActionPerformed

    private void sliderPosterizarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderPosterizarFocusGained
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            ColorModel cm = vi.getLienzo2D().getImagen(false).getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImagen(false).copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImagen(false).isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_sliderPosterizarFocusGained

    private void sliderPosterizarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sliderPosterizarFocusLost
        imgFuente = null;
        this.sliderPosterizar.setValue(0);
    }//GEN-LAST:event_sliderPosterizarFocusLost

    private void sliderPosterizarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderPosterizarStateChanged
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    int niveles = this.sliderPosterizar.getValue();
                    PosterizarOp pop = new PosterizarOp(niveles);
                    pop.filter(imgFuente, img);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_sliderPosterizarStateChanged

    private void botonRojoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRojoActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    int umbral = 10;
                    RedOp rop = new RedOp(umbral);
                    rop.filter(img, img);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonRojoActionPerformed

    private void listaReproduccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listaReproduccionActionPerformed
        // Parar si cambias de audio durante la reproduccion
        if (audioActual != null) {
            if (player != null) {
                player.stop();
                player = null;
            }

        }
        //Seleccionamos el audio de la lista
        audioActual = (File) listaReproduccion.getSelectedItem();
        if (audioActual != null) {
            
            try {
                player = new SMClipPlayer(audioActual);
                player.addLineListener(new ManejadorAudio());
                player.stop();
            }catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_listaReproduccionActionPerformed

    private void botonNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonNuevoActionPerformed
        VentanaInternaImagen vi = new VentanaInternaImagen();
        MiManejadorLienzo ml = new MiManejadorLienzo();
        MiManejadorVII mvii = new MiManejadorVII();
        vi.addInternalFrameListener(mvii);
        vi.getLienzo2D().addLienzoListener(ml);
        escritorio.add(vi);
        vi.setVisible(true);
        BufferedImage img;
        img = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d;
        g2d = img.createGraphics();
        Rectangle rect = new Rectangle();
        rect.setFrameFromDiagonal(new Point(0,0), new Point(img.getWidth(), img.getHeight()));
        g2d.setPaint(new Color(255,255,255));
        g2d.fill(rect);
        g2d.draw(rect);

        vi.getLienzo2D().setImagen(img);
    }//GEN-LAST:event_botonNuevoActionPerformed

    private void botonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonStopActionPerformed
        if(player!=null){
            player.stop();
            player = null;
        }
        if(recorder!=null){
            recorder.stop();
            recorder = null;
        }
    }//GEN-LAST:event_botonStopActionPerformed

    private void botonGrabarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGrabarActionPerformed
        JFileChooser dlg = new JFileChooser();
        int resp = dlg.showSaveDialog(this);
        
        if(resp == JFileChooser.APPROVE_OPTION){
            try {
                File f = dlg.getSelectedFile();
                recorder = new SMSoundRecorder(f);
                
                if(recorder!=null)
                    recorder.record();
            
            }catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_botonGrabarActionPerformed

    private void botonPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPlayActionPerformed

        if(audioActual!=null){
            if(player!=null){
                player.play();
            }
        }
    }//GEN-LAST:event_botonPlayActionPerformed

    private void botonCamaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCamaraActionPerformed
        VentanaInternaCamara vc = VentanaInternaCamara.getInstance(true);
        
        if(vc!=null){
            escritorio.add(vc);
            vc.setVisible(true);
        }
        
    }//GEN-LAST:event_botonCamaraActionPerformed

    private void botonCapturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCapturaActionPerformed
        JInternalFrame [] frames = escritorio.getAllFrames();
        
        //Compruebo si ya hay una instancia de cámara abierta
        VentanaInternaCamara vc = null;
        
        for(int i=0; i<frames.length; i++){
            if( frames[i] instanceof VentanaInternaCamara)
                vc = (VentanaInternaCamara)frames[i];
        }
        
        // Si no hay ninguna instancia creo una
        if(vc == null)  vc = VentanaInternaCamara.getInstance(false);
                
        VentanaInternaImagen vi = new VentanaInternaImagen();
        MiManejadorLienzo ml = new MiManejadorLienzo();
        MiManejadorVII mvii = new MiManejadorVII();
        vi.addInternalFrameListener(mvii);
        escritorio.add(vi);
        escritorio.setSelectedFrame(vi);
        vi.getLienzo2D().addLienzoListener(ml);
        vi.setVisible(true);
        BufferedImage img = vc.getImage();

        Graphics2D g2d;
        g2d = img.createGraphics();
        
        vi.getLienzo2D().setImagen(img);
    }//GEN-LAST:event_botonCapturaActionPerformed

    private void botonRoundRectanguloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRoundRectanguloActionPerformed
        VentanaInternaImagen vi;
        vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen)    vi.getLienzo2D().setFiguraActual(Figura.ROUNDRECTANGULO);
        textoActividad.setText("Dibujando Round Rectángulo");
    }//GEN-LAST:event_botonRoundRectanguloActionPerformed

    private void comboRellenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboRellenoActionPerformed
        VentanaInternaImagen vi;
        vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
      
        if(escritorio.getSelectedFrame() instanceof VentanaInternaImagen){
        
            switch(comboRelleno.getSelectedIndex()){
                case 0: //Figura hueca
                    vi.getLienzo2D().setRelleno(false);
                    vi.getLienzo2D().setConBordes(false);
                    vi.getLienzo2D().setDeg_horizontal(false);
                    vi.getLienzo2D().setDeg_vertical(false);
                    break;
                
                case 1: //Figura plana
                    vi.getLienzo2D().setRelleno(true);
                    vi.getLienzo2D().setConBordes(false);
                    vi.getLienzo2D().setDeg_horizontal(false);
                    vi.getLienzo2D().setDeg_vertical(false);
                    break;
                
                case 2: //Figura con bordes de color distinto
                    vi.getLienzo2D().setRelleno(false);
                    vi.getLienzo2D().setConBordes(true);
                    vi.getLienzo2D().setDeg_horizontal(false);
                    vi.getLienzo2D().setDeg_vertical(false);
                    break;
                    
                case 3: //Figura con degradado horizontal
                    vi.getLienzo2D().setRelleno(false);
                    vi.getLienzo2D().setConBordes(false);
                    vi.getLienzo2D().setDeg_horizontal(true);
                    vi.getLienzo2D().setDeg_vertical(false);
                    break;
                    
                case 4: //Figura con degradado vertical
                    vi.getLienzo2D().setRelleno(false);
                    vi.getLienzo2D().setConBordes(false);
                    vi.getLienzo2D().setDeg_horizontal(false);
                    vi.getLienzo2D().setDeg_vertical(true);
                    break;

            }
            vi.getLienzo2D().updateShape();
        }
    }//GEN-LAST:event_comboRellenoActionPerformed

    private void comboColores2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboColores2ActionPerformed
        VentanaInternaImagen vi;
        vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen){
            vi.getLienzo2D().setColor2((Color)comboColores2.getSelectedItem());
            vi.getLienzo2D().updateShape();
        }
    }//GEN-LAST:event_comboColores2ActionPerformed

    private void botonTrazoDiscontinuoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTrazoDiscontinuoActionPerformed
        VentanaInternaImagen vi;
        vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen){
            vi.getLienzo2D().setTrazoDiscontinuo(this.botonTrazoDiscontinuo.isSelected());
            vi.getLienzo2D().setStroke(vi.getLienzo2D().getGrosor());
        }
    }//GEN-LAST:event_botonTrazoDiscontinuoActionPerformed

    private void botonDuplicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonDuplicarActionPerformed
        VentanaInternaImagen vi;
        vi = (VentanaInternaImagen) escritorio.getSelectedFrame();
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen){
            
            ColorModel cm = vi.getLienzo2D().getImagen(true).getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImagen(true).copyData(null);
            boolean duplicado = vi.getLienzo2D().getImagen(true).isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm,raster,duplicado,null);
            
            VentanaInternaImagen nuevaVi = new VentanaInternaImagen();
            MiManejadorLienzo ml = new MiManejadorLienzo();
            MiManejadorVII mvii = new MiManejadorVII();
            vi.addInternalFrameListener(mvii);
            vi.getLienzo2D().addLienzoListener(ml);
            nuevaVi.getLienzo2D().setImagen(imgFuente);
            escritorio.add(nuevaVi);
            nuevaVi.setVisible(true);
        }
    }//GEN-LAST:event_botonDuplicarActionPerformed

    private void botonNegativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonNegativoActionPerformed
        LookupTable neg = negativo();
        aplicarLookup(neg);
    }//GEN-LAST:event_botonNegativoActionPerformed

    private void botonPolinomicaOrdenNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPolinomicaOrdenNActionPerformed
        int valor = (int)spinnerGrosor.getValue();
        if(valor <= 0)  valor = 1;
        LookupTable polinomica = polinomicaOrdenN(valor);
        aplicarLookup(polinomica);
    }//GEN-LAST:event_botonPolinomicaOrdenNActionPerformed

    private void botonPixelPixelPropioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPixelPixelPropioActionPerformed
        VentanaInternaImagen vi = (VentanaInternaImagen) (escritorio.getSelectedFrame());
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    Random r = new Random();
                    PropiaOp pop = new PropiaOp(r.nextInt(3), r.nextInt(256));
                    pop.filter(img, img);
                    escritorio.repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_botonPixelPixelPropioActionPerformed

    private void botonAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAyudaActionPerformed
        JOptionPane.showMessageDialog(this,"Proyecto Sistemas Multimedia\nV1.2\nPor Ignacio Yuste López" );
    }//GEN-LAST:event_botonAyudaActionPerformed

    private void botonIluminacionLookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonIluminacionLookupActionPerformed
        
        if (escritorio.getSelectedFrame() instanceof VentanaInternaImagen) {
            VentanaInternaImagen vi = (VentanaInternaImagen)escritorio.getSelectedFrame();
            BufferedImage img = vi.getLienzo2D().getImagen(false);
            if (img != null) {
                try {
                    byte[] funcionT = new byte[256];
                    
                    for (int l = 0; l <= 255; ++l) {
                            
                        if(l<128) funcionT[l] = (byte) l;
                        else{
                            funcionT[l] = (byte)(l^2);
                            if(funcionT[l]>255) funcionT[l] = (byte) (255/128 * Math.pow((l-128), 2));
                        }
    
                    }
                    
                    LookupTable tabla = new ByteLookupTable (0, funcionT);
                    aplicarLookup(tabla);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }

        
    }//GEN-LAST:event_botonIluminacionLookupActionPerformed

 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar Menu;
    private javax.swing.JToggleButton bPanelAlisar;
    private javax.swing.JToggleButton bPanelEditar;
    private javax.swing.JToggleButton bPanelTransparencia;
    private javax.swing.JButton boton180Gr;
    private javax.swing.JButton boton270Gr;
    private javax.swing.JButton boton90Gr;
    private javax.swing.JMenuItem botonAbrir;
    private javax.swing.JMenuItem botonAffineTransportOp;
    private javax.swing.JButton botonAumentar;
    private javax.swing.JMenuItem botonAyuda;
    private javax.swing.JMenuItem botonBandCombineOp;
    private javax.swing.JButton botonCamara;
    private javax.swing.JButton botonCaptura;
    private javax.swing.JMenuItem botonColorConvertOp;
    private javax.swing.JButton botonCombinar;
    private javax.swing.JButton botonContraste;
    private javax.swing.JMenuItem botonConvolveOp;
    private javax.swing.JButton botonCuadratica;
    private javax.swing.JMenuItem botonDuplicar;
    private javax.swing.JButton botonEcualizar;
    private javax.swing.JToggleButton botonElipse;
    private javax.swing.JCheckBoxMenuItem botonEstadoOnOff;
    private javax.swing.JButton botonExtraerBandas;
    private javax.swing.JButton botonGrabar;
    private javax.swing.JMenuItem botonGuardar;
    private javax.swing.JButton botonIluminacion;
    private javax.swing.JMenuItem botonIluminacionLookup;
    private javax.swing.JToggleButton botonLinea;
    private javax.swing.JMenuItem botonLookupOp;
    private javax.swing.JMenu botonMenuArchivo;
    private javax.swing.JMenu botonMenuEditar;
    private javax.swing.JMenu botonMenuImagen;
    private javax.swing.JMenuItem botonNegativo;
    private javax.swing.JMenuItem botonNuevo;
    private javax.swing.JButton botonOscurecer;
    private javax.swing.JButton botonPanelAbrir;
    private javax.swing.JButton botonPanelGuardar;
    private javax.swing.JButton botonPanelNuevo;
    private javax.swing.JMenuItem botonPixelPixelPropio;
    private javax.swing.JButton botonPlay;
    private javax.swing.JMenuItem botonPolinomicaOrdenN;
    private javax.swing.JToggleButton botonPunto;
    private javax.swing.JToggleButton botonRectangulo;
    private javax.swing.JButton botonReducir;
    private javax.swing.JMenuItem botonRescaleOp;
    private javax.swing.JButton botonRojo;
    private javax.swing.JToggleButton botonRoundRectangulo;
    private javax.swing.JButton botonSepia;
    private javax.swing.JButton botonStop;
    private javax.swing.JButton botonTintar;
    private javax.swing.JToggleButton botonTrazoDiscontinuo;
    private javax.swing.JComboBox<Color> comboColores1;
    private javax.swing.JComboBox<Color> comboColores2;
    private javax.swing.JComboBox<String> comboRelleno;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox<File> listaReproduccion;
    private javax.swing.ButtonGroup opcionesFiguras;
    private javax.swing.JPanel panelActividad;
    private javax.swing.JPanel panelBrillo;
    private javax.swing.JPanel panelColor;
    private javax.swing.JPanel panelContraste;
    private javax.swing.JPanel panelCuadratica;
    private javax.swing.JPanel panelEscalado;
    private javax.swing.JPanel panelFiguras;
    private javax.swing.JPanel panelFiltro;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelRotacion;
    private javax.swing.JComboBox<String> seleccionEspaciosColor;
    private javax.swing.JComboBox<String> seleccionMascara;
    private javax.swing.JSlider sliderBrillo;
    private javax.swing.JSlider sliderPosterizar;
    private javax.swing.JSlider sliderRotacion;
    private javax.swing.JSpinner spinnerGrosor;
    private javax.swing.JLabel textoActividad;
    // End of variables declaration//GEN-END:variables
}
