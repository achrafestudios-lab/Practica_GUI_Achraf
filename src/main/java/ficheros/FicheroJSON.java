package ficheros;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import modelo.Trabajador;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FicheroJSON {
    public static void exportarFicheroJSON(String nombreFichero, List<Trabajador> trabajadores) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(nombreFichero), trabajadores);
        } catch (IOException e) {
            System.out.println("Error al escribir JSON: " + e.getMessage());
        }
    }

    public static List<Trabajador> importarFicheroJSON(String nombre) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File(nombre), new TypeReference<List<Trabajador>>() {
            });
        } catch (IOException e) {
            System.out.println("Error al leer JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
