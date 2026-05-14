package ficheros;

import modelo.Trabajador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FicheroJSON {
    public static void exportarFicheroJSON(String nombreFichero, List<Trabajador> trabajadores) {
        BufferedWriter bw = null;
        try {
            File fichero = new File(nombreFichero);
            bw = new BufferedWriter(new FileWriter(fichero, false));
            for (Trabajador t : trabajadores) {
                bw.write(t.toStringWithSeparatorsJSON());
                bw.newLine();
            }
        } catch (IOException ioe) {
            System.out.println("Error al escribir en el fichero: " + ioe.getMessage());
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error al cerrar el fichero: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        }
    }

}
