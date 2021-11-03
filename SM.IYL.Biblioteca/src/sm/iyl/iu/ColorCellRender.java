
package sm.iyl.iu;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Clase para código propio del comboBox de colores de la IU
 * @author Ignacio Yuste López
 */
public class ColorCellRender implements ListCellRenderer<Color>{

    /**
     * Create new form ColorCellRender 
     */
    
    /**
     * Crea y devuelve la lista de PanelColor propia con los colores para el dibujado
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return b lista de paneles de colores
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends Color> list, Color value, int index, boolean isSelected, boolean cellHasFocus) {
        PanelColor b = new PanelColor(value);
        return b;
    }
    
}
