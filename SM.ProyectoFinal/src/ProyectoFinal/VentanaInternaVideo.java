package ProyectoFinal;


import java.io.File;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Clase JInternalFrame que contiene un EmbeddedMediaPlayer para reproducir vídeo
 * @author Ignacio Yuste López
 */
public class VentanaInternaVideo extends javax.swing.JInternalFrame {

       
    
    
    private EmbeddedMediaPlayer vlcplayer = null;
    private File fMedia;
    
    /**
     * Creates new form VentanaInternaVideo
     */
    
    /**
     * Constructor privado por si hay un fallo al abrir el vídeo que no se abra la ventana
     * Asocia un archivo de vídeo al reproductor
     * @param f archivo a reproducir
     */
    private VentanaInternaVideo(File f) {
        initComponents();
        
        fMedia = f;
        EmbeddedMediaPlayerComponent aVisual = new EmbeddedMediaPlayerComponent();
        
        getContentPane().add(aVisual, java.awt.BorderLayout.CENTER);
        vlcplayer = aVisual.getMediaPlayer();
        
        this.setSize(400,400);
    }

    /**
     * Método que crea y devuelve una instancia de la clase
     * @param f arhivo a reproducir
     * @return instancia de clase
     */
    public static VentanaInternaVideo getInstance(File f){
        VentanaInternaVideo v = new VentanaInternaVideo(f);
        return (v.vlcplayer!=null?v:null);
    }
    
    /**
     * Método para reproducir el vídeo
     */
    public void play(){
        
        if(vlcplayer != null){
            
            if(vlcplayer.isPlayable())
                vlcplayer.play();
            else
                vlcplayer.playMedia(fMedia.getAbsolutePath());                                
        }
    }
    
    /**
     * Método para parar el vídeo
     */
    public void stop(){
        
         if(vlcplayer != null){
            
            if(vlcplayer.isPlayable())
                vlcplayer.pause();
            else
                vlcplayer.stop();                               
        }
    }
    
    /**
     * Método para asociar un manejador de vídeo
     * @param ml manejador de vídeo
     */
    public void addMediaPlayerEventListener(MediaPlayerEventListener ml) {
        if (vlcplayer != null) {
            vlcplayer.addMediaPlayerEventListener(ml);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Método para manejar el cierre de la ventana. 
     * Para el vídeo que se esté reproduciendo
     * @param evt 
     */
    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        if(vlcplayer!=null){
            vlcplayer.stop();
            vlcplayer = null;
        }
    }//GEN-LAST:event_formInternalFrameClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
