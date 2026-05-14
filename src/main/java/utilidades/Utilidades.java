package utilidades;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

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

}
