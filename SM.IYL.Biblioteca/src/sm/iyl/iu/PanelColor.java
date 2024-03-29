
package sm.iyl.iu;

import java.awt.Color;

/**
 * Clase JPanel para los paneles de colores a introducir en el comboBox de colores de la IU
 * @author Ignacio Yuste López
 */
public class PanelColor extends javax.swing.JPanel {

    /**
     * Creates new form PanelColor
     */
    
    /**
     * Constructor de la clase, crea un panel con el fondo de color pasado como argumento
     * @param color 
     */
    public PanelColor(Color color) {
        initComponents();
        b.setBackground(color);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        b = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(25, 25));
        setMinimumSize(new java.awt.Dimension(20, 20));
        setPreferredSize(new java.awt.Dimension(22, 22));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        b.setMargin(new java.awt.Insets(0, 0, 0, 0));
        b.setMaximumSize(new java.awt.Dimension(20, 20));
        b.setMinimumSize(new java.awt.Dimension(20, 20));
        b.setPreferredSize(new java.awt.Dimension(20, 20));
        add(b, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b;
    // End of variables declaration//GEN-END:variables
}
