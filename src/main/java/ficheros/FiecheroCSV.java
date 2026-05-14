package ficheros;

import modelo.Trabajador;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FiecheroCSV {
    public static void exportarFicheroCSV(String nombreFichero, List<Trabajador> trabajadores) {
        BufferedWriter bw = null;
        try {
            File fichero = new File(nombreFichero);
            bw = new BufferedWriter(new FileWriter(fichero, false));
            for (Trabajador t : trabajadores) {
                bw.write(t.toStringWithSeparatorsCSV());
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

    private String formatearCSV(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    public static List<Trabajador> importarFicheroTrabajadores(String nombreFichero) {

        List<Trabajador> trajadores = new ArrayList<Trabajador>();
        BufferedReader br = null;
        try {
            // Abre fichero de trajadores en modo lectura
            br = new BufferedReader(new FileReader(new File(nombreFichero)));

            // Lectura linea por línea del fichero de trajadores
            String linea = br.readLine();
            while (linea != null) {
                // Construye alumno a partir de la linea
                Trabajador trabajador = new Trabajador(linea);
                // Inserta el alumno en el Array
                trajadores.add(trabajador);
                linea = br.readLine();
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("Error al abrir el fichero: " + fnfe.getMessage());
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Error al leer del fichero: " + ioe.getMessage());
            ioe.printStackTrace();
        } catch (NumberFormatException nfe) {
            System.out.println("Error al convertir de cadena a n�mero: " + nfe.getMessage());
            nfe.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error al cerrar el fichero: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        }

        return trajadores;
    }
}
