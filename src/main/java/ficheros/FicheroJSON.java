package ficheros;

import modelo.Trabajador;

import java.io.*;
import java.util.ArrayList;
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

    public static List<Trabajador> importarFicheroJSON(String nombre) {
        List<Trabajador> trabajadores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombre))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                // Extraer valores del JSON: {"identificador":"X","dni":"Y",...}
                String[] partes = linea.replaceAll("[{}\"]", "").split(",");
                String[] valores = new String[7];
                for (int i = 0; i < partes.length; i++) {
                    valores[i] = partes[i].split(":")[1];
                }

                Trabajador t = new Trabajador(
                        Integer.parseInt(valores[0]),
                        valores[1], valores[2], valores[3],
                        valores[4], valores[5], valores[6]
                );
                trabajadores.add(t);
            }
        } catch (IOException e) {
            System.out.println("Error al leer JSON: " + e.getMessage());
        }
        return trabajadores;
    }
}
