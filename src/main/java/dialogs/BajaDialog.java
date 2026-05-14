/**
 * Paquete que contiene los diálogos de la interfaz gráfica.
 */
package dialogs;

// Importación de clases para la interfaz gráfica y manejo de eventos

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

// Importación de clases del modelo, DAO y excepciones
import dao.AccesoTrabajador;
import exception.BDException;
import modelo.Empresa;
import modelo.Trabajador;
import utilidades.Utilidades;

/**
 * Diálogo para gestionar la baja (eliminación) de trabajadores.
 * Muestra una tabla con los trabajadores y un checkbox en cada fila
 * para seleccionar cuáles eliminar. Incluye filtros de búsqueda.
 *
 * @author ach.dev
 */
public class BajaDialog extends JDialog implements ActionListener {

    // --- Componentes de la interfaz gráfica ---
    JButton eliminar;              // Botón para eliminar los trabajadores seleccionados
    JButton buscar;                // Botón para buscar/filtrar trabajadores
    JButton cancelar;              // Botón para cerrar el diálogo sin guardar
    JPanel panelFiltros;         // Panel superior con filtros y búsqueda
    JPanel panelBotones;               // Panel inferior con botones de acción
    JTable tabla;                  // Tabla que muestra los trabajadores
    JTextField busqueda;           // Campo de texto para escribir el término de búsqueda
    JComboBox comboFiltro;         // Desplegable para elegir el campo por el que filtrar
    Object[][] datos;              // Matriz que almacena los datos mostrados en la tabla
    DefaultTableModel modelo;      // Modelo de datos de la tabla
    Object[] columnas;             // Nombres de las columnas de la tabla

    Empresa empresa;                                    // Referencia a la empresa (compartida con la ventana principal)
    List<String> trabajadoresAEliminar = new ArrayList<>(); // Id de los trabajadores marcados para eliminar


    /**
     * Constructor del diálogo. Recibe la empresa, inicializa los componentes
     * y muestra la tabla con todos los trabajadores.
     *
     * @param empresa Objeto Empresa compartida con la ventana principal
     */
    public BajaDialog(Empresa empresa) {
        // Guarda la referencia a la empresa
        this.empresa = empresa;

        // Impedimos que se pueda cambiar el tamaño de la ventana
        setResizable(false);

        // Título del diálogo
        setTitle("Baja Trabajadores");

        // Tamaño de la ventana
        setSize(750, 700);

        // Layout de flujo para colocar los componentes en orden
        setLayout(new FlowLayout());

        // Centra la ventana en la pantalla
        setLocationRelativeTo(null);

        // --- Panel superior: filtro + búsqueda ---
        panelFiltros = new JPanel();

        // Combo box para seleccionar el campo de búsqueda
        comboFiltro = new JComboBox();
        for (String s : Arrays.asList("DNI", "Nombre", "Apellidos", "Dirección", "Teléfono", "Puesto")) {
            comboFiltro.addItem(s);
        }
        panelFiltros.add(comboFiltro);

        // Campo de texto para escribir el valor a buscar
        busqueda = new JTextField(15);
        panelFiltros.add(busqueda);

        // Botón "Buscar" que ejecuta el filtrado
        buscar = new JButton("Buscar");
        buscar.addActionListener(this);
        panelFiltros.add(buscar);

        // Añade el panel superior al diálogo
        add(panelFiltros);

        // --- Tabla de trabajadores ---
        // Define los nombres de las columnas (8 columnas, la última es el checkbox "Eliminar")
        columnas = new String[]{"ID", "DNI", "Nombre", "Apellidos", "Dirección", "Teléfono", "Puesto", "Eliminar"};
        // Obtiene los datos de todos los trabajadores desde la base de datos (con columna checkbox)
        datos = empresa.listarTrabajadoresCheckBox();

        // Crea el modelo de la tabla con los datos y las columnas
        modelo = new DefaultTableModel(datos, columnas) {

            // Define qué celdas son editables
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo se puede editar la columna 7 (Eliminar/checkbox)
                return column == 7;
            }

            // Define el tipo de dato de cada columna para que JTable renderice correctamente
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 7) {
                    // La columna 7 es Boolean -> JTable muestra un checkbox
                    return Boolean.class;
                }
                // El resto son String
                return String.class;
            }
        };

        // Crea el JTable con el modelo
        tabla = new JTable(modelo);

        // Permite ordenar las filas al hacer clic en los encabezados de columna
        tabla.setAutoCreateRowSorter(true);

        // Altura de cada fila
        tabla.setRowHeight(30);

        // Envuelve la tabla en un JScrollPane para poder desplazarse
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(700, 560));
        add(scrollTabla);

        // --- Listener para detectar cambios en la tabla ---
        tabla.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent evento) {

                // Solo procesa eventos de tipo UPDATE (modificación de celdas)
                if (evento.getType() != TableModelEvent.UPDATE) {
                    return;
                }

                // Obtiene la fila y columna modificada en coordenadas de MODELO
                int modelRow = evento.getFirstRow();
                int modelColumn = evento.getColumn();
                // Convierte la fila de modelos a vista (necesario cuando la tabla está ordenada)
                int viewRow = tabla.convertRowIndexToView(modelRow);

                // Si el nuevo valor es igual al original, no ha habido cambio real -> sale
                if (tabla.getValueAt(viewRow, evento.getColumn()).equals(datos[modelRow][modelColumn])) {
                    return;
                }

                // --- Lógica específica para la columna 7 (checkbox "Eliminar") ---
                if (modelColumn == 7) {
                    // Obtiene el estado del checkbox (marcado o desmarcado)
                    Boolean marcado = (Boolean) tabla.getValueAt(viewRow, 7);
                    // Obtiene el ID del trabajador de la fila actual
                    String id = (String) tabla.getValueAt(viewRow, 0);

                    if (marcado) {
                        // Si se marcó, añade el ID a la lista de eliminación (si no está ya)
                        if (!trabajadoresAEliminar.contains(id))
                            trabajadoresAEliminar.add(id);
                    } else {
                        // Si se desmarcó, quita el ID de la lista de eliminación
                        trabajadoresAEliminar.remove(id);
                    }
                    // Muestra en consola el estado actual de la lista
                    System.out.println(trabajadoresAEliminar);

                    // sincroniza la matriz datos con el nuevo estado del checkbox
                    datos[modelRow][modelColumn] = marcado;
                }

            }
        });

        // --- Panel inferior: botones de acción ---
        panelBotones = new JPanel();

        // Botón "Eliminar": elimina los trabajadores marcados
        eliminar = new JButton("Eliminar");
        eliminar.addActionListener(this);
        panelBotones.add(eliminar);

        // Botón "Cancelar": cierra el diálogo sin guardar cambios
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        panelBotones.add(cancelar);

        add(panelBotones);

        // Ajusta el ancho de las columnas al contenido
        Utilidades.ajustarAnchoColumnas(tabla);

        // Hace visible el diálogo
        setVisible(true);
        // Al cerrar, libera los recursos del diálogo
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    /**
     * Maneja los eventos de los botones (Buscar, Eliminar, Cancelar).
     *
     * @param evento Evento de acción
     */
    @Override
    public void actionPerformed(ActionEvent evento) {

        // --- Botón "Buscar" ---
        if (evento.getSource() == buscar) {
            // Obtiene el texto del campo de búsqueda sin espacios al inicio/final
            String texto = busqueda.getText().trim();

            // Obtiene el campo seleccionado en el combo de filtro
            String campoBD = getString();

            // Si el campo no es válido, no hace nada
            if (campoBD == null) return;

            try {
                if (texto.isEmpty()) {
                    // Si el campo de búsqueda está vacío, muestra todos los trabajadores
                    datos = empresa.listarTrabajadoresCheckBox();
                } else {
                    // Obtiene los trabajadores filtrados desde la base de datos
                    List<Trabajador> filtrados = AccesoTrabajador.obtenerTrabajadoresFiltrados(campoBD, texto);

                    // Crea una matriz de Object (8 columnas: 7 datos + checkbox)
                    datos = new Object[filtrados.size()][8];
                    for (int i = 0; i < filtrados.size(); i++) {
                        creaFilasFiltradasTrabajadores(filtrados, i, datos);
                        datos[i][7] = Boolean.FALSE; // Checkbox desmarcado por defecto
                    }
                }

                // Limpia todas las filas del modelo para insertar los nuevos datos
                modelo.setRowCount(0);

                // Muestra un mensaje con el número de resultados encontrados
                JOptionPane.showMessageDialog(this,
                        "Resultados encontrados: " + datos.length,
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);

                // Inserta cada fila de datos en el modelo de la tabla
                for (Object[] fila : datos) {
                    modelo.addRow(fila);
                }

                // Restaura los checkboxes marcados antes del filtrado
                // Recorre todas las filas del modelo y si el ID está en trabajadoresAEliminar, marca el checkbox
                for (int i = 0; i < modelo.getRowCount(); i++) {
                    String idFila = (String) modelo.getValueAt(i, 0);
                    if (trabajadoresAEliminar.contains(idFila)) {
                        modelo.setValueAt(Boolean.TRUE, i, 7);
                    }
                }

                // Ajusta el ancho de las columnas al contenido
                Utilidades.ajustarAnchoColumnas(tabla);

            } catch (BDException exception) {
                // Muestra error si falla la búsqueda en base de datos
                JOptionPane.showMessageDialog(null, "Error al filtrar: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            // --- Botón "Eliminar" ---
        } else if (evento.getSource() == eliminar) {

            // Si la tabla está en modo edición, fuerza la finalización para no perder datos
            if (tabla.isEditing()) {
                tabla.getCellEditor().stopCellEditing();
            }

            // Muestra un diálogo de confirmación con la cantidad de seleccionados
            int respuesta = JOptionPane.showConfirmDialog(null,
                    "¿Desea eliminar los " + trabajadoresAEliminar.size() + " seleccionados?",
                    "Eliminar", JOptionPane.YES_NO_OPTION);

            switch (respuesta) {
                case JOptionPane.YES_OPTION:
                    try {
                        // Recorre la lista de IDs a eliminar y los borra de la base de datos
                        AccesoTrabajador.eliminarTrabajadorId(trabajadoresAEliminar);

                        // Actualiza la lista interna de la empresa para reflejar los cambios
                        empresa.setTrabajadores(AccesoTrabajador.obtenerTrabajadoresBaseDatos());
                        // Muestra mensaje de éxito
                        JOptionPane.showMessageDialog(null, "Eliminados con éxito",
                                "", JOptionPane.INFORMATION_MESSAGE);
                        // Cierra el diálogo
                        dispose();

                    } catch (BDException exception) {
                        System.out.println(exception.getMessage());
                    }
                case JOptionPane.NO_OPTION:
                    // Si el usuario escoge "No", no hace nada
                    break;
            }

            // --- Botón "Cancelar" ---
        } else if (evento.getSource() == cancelar) {
            // Cierra el diálogo sin guardar cambios
            dispose();
        }
    }

    private String getString() {
        String seleccion = (String) comboFiltro.getSelectedItem();

        // Convierte el nombre visible del campo al nombre real de la columna en BD
        String campoBD = null;
        if (seleccion != null) {
            campoBD = switch (seleccion) {
                case "DNI" -> "dni";
                case "Nombre" -> "nombre";
                case "Apellidos" -> "apellidos";
                case "Dirección" -> "dirección";
                case "Teléfono" -> "teléfono";
                case "Puesto" -> "puesto";
                default -> null;
            };
        }
        return campoBD;
    }

    static void creaFilasFiltradasTrabajadores(List<Trabajador> filtrados, int i, Object[][] datos) {
        Trabajador trabajador = filtrados.get(i);
        datos[i][0] = Integer.toString(trabajador.getIdentificador());
        datos[i][1] = trabajador.getDni();
        datos[i][2] = trabajador.getNombre();
        datos[i][3] = trabajador.getApellidos();
        datos[i][4] = trabajador.getDireccion();
        datos[i][5] = trabajador.getTelefono();
        datos[i][6] = trabajador.getPuesto();
    }

    /**
     * Ajusta el ancho de cada columna de la tabla al tamaño del contenido
     * más ancho (incluyendo el encabezado y todas las celdas de datos).
     */

}
