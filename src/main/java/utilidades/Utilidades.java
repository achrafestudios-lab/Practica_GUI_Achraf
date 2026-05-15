package utilidades;

import modelo.Trabajador;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class Utilidades {
    public static void ajustarAnchoColumnas(JTable tabla) {
        for (int col = 0; col < tabla.getColumnCount(); col++) {
            int ancho = 30;

            // Calcula el ancho del encabezado de la columna
            TableCellRenderer headerRenderer = tabla.getTableHeader().getDefaultRenderer();
            Component cabecera = headerRenderer.getTableCellRendererComponent(
                    tabla, tabla.getColumnModel().getColumn(col).getHeaderValue(),
                    false, false, 0, col);
            ancho = Math.max(ancho, cabecera.getPreferredSize().width + 4);

            // Calcula el ancho máximo entre todas las celdas de datos
            for (int row = 0; row < tabla.getRowCount(); row++) {
                TableCellRenderer renderer = tabla.getCellRenderer(row, col);
                Component componente = tabla.prepareRenderer(renderer, row, col);
                ancho = Math.max(ancho, componente.getPreferredSize().width + 4);
            }
            tabla.getColumnModel().getColumn(col).setPreferredWidth(ancho);
        }
    }
    
    public static void creaFilasFiltradasTrabajadores(List<Trabajador> filtrados, int i, Object[][] datos) {
        Trabajador trabajador = filtrados.get(i);
        datos[i][0] = Integer.toString(trabajador.getIdentificador());
        datos[i][1] = trabajador.getDni();
        datos[i][2] = trabajador.getNombre();
        datos[i][3] = trabajador.getApellidos();
        datos[i][4] = trabajador.getDireccion();
        datos[i][5] = trabajador.getTelefono();
        datos[i][6] = trabajador.getPuesto();
    }

}

