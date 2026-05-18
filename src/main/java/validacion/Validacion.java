package validacion;

import exception.BDException;
import modelo.Empresa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static utilidades.Utilidades.*;

public class Validacion {

    private static final String LETRAS_DNI = "TRWAGMYFPDXBNJZSQVHLCKE";

    /**
     * Verifica el DNI.
     *
     * @param dni DNI a validar (8 dígitos + letra)
     * @return 0 si es válido, 1 si es null/vacío, 2 si longitud != 9,
     * 3 si los primeros 8 caracteres no son dígitos, 4 si la letra no coincide
     */
    public static int verificarDni(String dni) {
        if (dni == null || dni.isEmpty()) {
            return 1;
        }

        if (dni.length() != 9) {
            return 2;
        }

        String numeroStr = dni.substring(0, 8);
        char letra = Character.toUpperCase(dni.charAt(8));

        int numero;
        try {
            numero = Integer.parseInt(numeroStr);
        } catch (Exception e) {
            return 3;
        }

        int resto = numero % 23;
        char letraEsperada = LETRAS_DNI.charAt(resto);

        if (letra != letraEsperada) {
            return 4;
        }
        return 0;
    }

    /**
     * Valida un número de teléfono de 9 dígitos.
     *
     * @param telefono Teléfono a validar
     * @return 0 si es válido, 1 si es null, 2 si está vacío,
     * 3 si longitud != 9, 4 si contiene caracteres no numéricos
     */
    public static int validarTelefono(String telefono) {
        if (telefono == null) {
            return 1;
        }

        if (telefono.isEmpty()) {
            return 2;
        }

        if (telefono.length() != 9) {
            return 3;
        }

        for (int i = 0; i < telefono.length(); i++) {
            if (!Character.isDigit(telefono.charAt(i))) {
                return 4;
            }
        }

        return 0;
    }

    public static boolean comprobarErroresAlta(JTextField areaDni, JTextField areaNombre, JTextField areaApellidos,
                                               JTextField areaDireccion, JTextField areaTelefono, JComboBox comboPuesto,
                                               String dni, String nombre, String apellidos, String direccion,
                                               String telefono, String puesto) {

        areaDni.setBackground(Color.WHITE);
        areaNombre.setBackground(Color.WHITE);
        areaApellidos.setBackground(Color.WHITE);
        areaDireccion.setBackground(Color.WHITE);
        areaTelefono.setBackground(Color.WHITE);
        comboPuesto.setBackground(Color.WHITE);

        int resDni = Validacion.verificarDni(dni);
        switch (resDni) {
            case 1:
                areaDni.setBackground(Color.RED);
                JOptionPane.showMessageDialog(null, "El DNI no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 2:
                areaDni.setBackground(Color.RED);
                JOptionPane.showMessageDialog(null, "El DNI debe tener longitud 9", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 3:
                areaDni.setBackground(Color.RED);
                JOptionPane.showMessageDialog(null, "El DNI debe contener 8 dígitos y una letra", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 4:
                areaDni.setBackground(Color.RED);
                JOptionPane.showMessageDialog(null, "La letra del DNI no es correcta", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
        }

        if (nombre.isEmpty()) {
            areaNombre.setBackground(Color.RED);
            JOptionPane.showMessageDialog(null, "Debe introducir el nombre del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (apellidos.isEmpty()) {
            areaApellidos.setBackground(Color.RED);
            JOptionPane.showMessageDialog(null, "Debe introducir los apellidos del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (direccion.isEmpty()) {
            areaDireccion.setBackground(Color.RED);
            JOptionPane.showMessageDialog(null, "Debe introducir la direcci�n del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int resTel = Validacion.validarTelefono(telefono);
        switch (resTel) {
            case 1:
                areaTelefono.setBackground(Color.RED);
                JOptionPane.showMessageDialog(null, "El teléfono no puede ser nulo", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 2:
                areaTelefono.setBackground(Color.RED);
                JOptionPane.showMessageDialog(null, "El teléfono no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 3:
                areaTelefono.setBackground(Color.RED);
                JOptionPane.showMessageDialog(null, "El teléfono debe tener longitud 9", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 4:
                areaTelefono.setBackground(Color.RED);
                JOptionPane.showMessageDialog(null, "El teléfono solo debe contener dígitos", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
        }

        if (puesto.isEmpty()) {
            comboPuesto.setBackground(Color.RED);
            JOptionPane.showMessageDialog(null, "Debe introducir el puesto del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }


    public static boolean comprobarErroresModificar(String dni, String nombre, String apellidos, String direccion,
                                                    String telefono, String puesto) {

        int resDni = Validacion.verificarDni(dni);
        switch (resDni) {
            case 1:
                JOptionPane.showMessageDialog(null, "El DNI no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 2:
                JOptionPane.showMessageDialog(null, "El DNI debe tener longitud 9", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 3:
                JOptionPane.showMessageDialog(null, "El DNI debe contener 8 dígitos y una letra", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 4:
                JOptionPane.showMessageDialog(null, "La letra del DNI no es correcta", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
        }

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe introducir el nombre del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (apellidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe introducir los apellidos del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (direccion.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe introducir la dirección del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int resTel = Validacion.validarTelefono(telefono);
        switch (resTel) {
            case 1:
                JOptionPane.showMessageDialog(null, "El teléfono no puede ser nulo", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 2:
                JOptionPane.showMessageDialog(null, "El teléfono no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 3:
                JOptionPane.showMessageDialog(null, "El teléfono debe tener longitud 9", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            case 4:
                JOptionPane.showMessageDialog(null, "El teléfono solo debe contener dígitos", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
        }

        if (puesto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe introducir el puesto del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static String[][] realizarBusqueda(
            JTextField busqueda, JComboBox<String> comboFiltro,
            Empresa empresa, DefaultTableModel modelo, JTable tabla,
            Component parent
    ) {
        String texto = busqueda.getText().trim();
        String seleccion = (String) comboFiltro.getSelectedItem();

        String campoBD = null;
        if (seleccion != null) {
            campoBD = comboToCampoBD(seleccion);
        }
        if (campoBD == null) return null;

        try {
            String[][] datos = filtrarTrabajadores(empresa, texto, campoBD);
            actualizarTabla(modelo, datos, tabla, parent);
            return datos;
        } catch (BDException ex) {
            JOptionPane.showMessageDialog(null, "Error al filtrar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

}
