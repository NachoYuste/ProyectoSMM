package ProyectoFinal;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;

/**
 * Clase principal del proyecto, únicamente ejecuta una instancia de VentanaPrincipal
 * @author Ignacio Yuste López
 */
public class ProyectoFinal {

    /**
     * Main del proyecto, se comprueba primero si se tiene acceso a la biblioteca de VLC
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean ok = new NativeDiscovery().discover();
        
        VentanaPrincipal ventana = new VentanaPrincipal();
    }
    
}
