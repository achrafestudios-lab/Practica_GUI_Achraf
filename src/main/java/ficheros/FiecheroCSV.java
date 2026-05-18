package ficheros;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import modelo.Trabajador;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FiecheroCSV {

    public static void exportarFicheroCSV(String nombreFichero, List<Trabajador> trabajadores) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(nombreFichero))) {
            for (Trabajador t : trabajadores) {
                writer.writeNext(new String[]{
                        String.valueOf(t.getIdentificador()), t.getDni(), t.getNombre(),
                        t.getApellidos(), t.getDireccion(), t.getTelefono(), t.getPuesto()
                });
            }
        } catch (IOException e) {
            System.out.println("Error al escribir CSV: " + e.getMessage());
        }

    }

    public static List<Trabajador> importarFicheroTrabajadoresCSV(String nombreFichero) {
        List<Trabajador> lista = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(nombreFichero))) {
            String[] lineas;
            while ((lineas = reader.readNext()) != null) {
                lista.add(new Trabajador(
                        Integer.parseInt(lineas[0]), lineas[1], lineas[2],
                        lineas[3], lineas[4], lineas[5], lineas[6]
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al leer CSV: " + e.getMessage());
        }
        return lista;
    }

}
