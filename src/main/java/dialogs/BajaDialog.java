package dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import dao.AccesoTrabajador;
import exception.BDException;
import modelo.Empresa;
import modelo.Trabajador;
import utilidades.Utilidades;

import static utilidades.Utilidades.comboToCampoBD;
import static utilidades.Utilidades.crearComboFiltro;

// Diálogo para gestionar la baja (eliminación) de trabajadores mediante tabla con checkboxes
public class BajaDialog extends JDialog implements ActionListener {

    // --- Componentes de la interfaz ---
    JButton eliminar;
    JButton buscar;
    JButton cancelar;
    JPanel panelFiltros;
    JPanel panelBotones;
    JTable tabla;
    JTextField busqueda;
    JComboBox comboFiltro;
    Object[][] datos;
    DefaultTableModel modelo;
    Object[] columnas;
    Empresa empresa;
    List<String> trabajadoresAEliminar = new ArrayList<>();

    // Constructor: monta la ventana con filtros, tabla de trabajadores y checkboxes de selección
    public BajaDialog(Empresa empresa) {
        this.empresa = empresa;
        setResizable(false);
        setTitle("Baja Trabajadores");
        setSize(750, 700);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        // --- Panel superior: filtro de búsqueda y campo de texto ---
        panelFiltros = new JPanel();
        comboFiltro = crearComboFiltro();
        panelFiltros.add(comboFiltro);
        busqueda = new JTextField(15);
        panelFiltros.add(busqueda);
        buscar = new JButton("Buscar");
        buscar.addActionListener(this);
        panelFiltros.add(buscar);
        add(panelFiltros);

        // --- Configuración de la tabla: columnas y modelo ---
        columnas = new String[]{"ID", "DNI", "Nombre", "Apellidos", "Dirección", "Teléfono", "Puesto", "Eliminar"};
        datos = empresa.listarTrabajadoresCheckBox();
        modelo = new DefaultTableModel(datos, columnas) {

            // Solo la columna 7 (checkbox "Eliminar") es editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }

            // La columna 7 es Boolean para que JTable la renderice como checkbox
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 7) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(30);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(700, 560));
        add(scrollTabla);

        // --- Listener: detecta cambios en los checkboxes de la tabla ---
        tabla.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent evento) {
                if (evento.getType() != TableModelEvent.UPDATE) return;

                int modelRow = evento.getFirstRow();
                int modelColumn = evento.getColumn();
                int viewRow = tabla.convertRowIndexToView(modelRow);

                // Ignora si el valor no ha cambiado realmente
                if (tabla.getValueAt(viewRow, evento.getColumn()).equals(datos[modelRow][modelColumn])) return;

                // Cuando se marca/desmarca el checkbox de la columna 7, actualiza la lista de IDs a eliminar
                if (modelColumn == 7) {
                    Boolean marcado = (Boolean) tabla.getValueAt(viewRow, 7);
                    String id = (String) tabla.getValueAt(viewRow, 0);
                    if (marcado) {
                        if (!trabajadoresAEliminar.contains(id)) trabajadoresAEliminar.add(id);
                    } else {
                        trabajadoresAEliminar.remove(id);
                    }
                    datos[modelRow][modelColumn] = marcado;
                }
            }
        });

        // --- Panel inferior: botones de acción ---
        panelBotones = new JPanel();
        eliminar = new JButton("Eliminar");
        eliminar.addActionListener(this);
        panelBotones.add(eliminar);
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        panelBotones.add(cancelar);
        add(panelBotones);

        Utilidades.ajustarAnchoColumnas(tabla);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    // Gestiona las acciones de los botones: buscar, eliminar y cancelar
    @Override
    public void actionPerformed(ActionEvent evento) {

        // --- Lógica del botón Buscar: filtra trabajadores según criterio ---
        if (evento.getSource() == buscar) {
            String texto = busqueda.getText().trim();
            String seleccion = (String) comboFiltro.getSelectedItem();
            String campoBD = null;
            if (seleccion != null) {
                campoBD = comboToCampoBD(seleccion);
            }
            if (campoBD == null) return;

            try {
                if (texto.isEmpty()) {
                    datos = empresa.listarTrabajadoresCheckBox();
                } else {
                    List<Trabajador> filtrados = AccesoTrabajador.obtenerTrabajadoresFiltrados(campoBD, texto);
                    datos = new Object[filtrados.size()][8];
                    for (int i = 0; i < filtrados.size(); i++) {
                        Utilidades.creaFilasFiltradasTrabajadores(filtrados, i, datos);
                        datos[i][7] = Boolean.FALSE;
                    }
                }
                modelo.setRowCount(0);
                JOptionPane.showMessageDialog(this, "Resultados encontrados: " + datos.length, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                // Inserta los datos filtrados en la tabla
                for (Object[] fila : datos) {
                    modelo.addRow(fila);
                }
                // Restaura los checkboxes marcados antes del filtrado
                for (int i = 0; i < modelo.getRowCount(); i++) {
                    String idFila = (String) modelo.getValueAt(i, 0);
                    if (trabajadoresAEliminar.contains(idFila)) {
                        modelo.setValueAt(Boolean.TRUE, i, 7);
                    }
                }
                Utilidades.ajustarAnchoColumnas(tabla);
            } catch (BDException exception) {
                JOptionPane.showMessageDialog(null, "Error al filtrar: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            // --- Lógica del botón Eliminar: borra los trabajadores seleccionados ---
        } else if (evento.getSource() == eliminar) {
            if (tabla.isEditing()) {
                tabla.getCellEditor().stopCellEditing();
            }
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea eliminar los " + trabajadoresAEliminar.size() + " seleccionados?", "Eliminar", JOptionPane.YES_NO_OPTION);
            switch (respuesta) {
                case JOptionPane.YES_OPTION:
                    try {
                        AccesoTrabajador.eliminarTrabajadorId(trabajadoresAEliminar);
                        empresa.setTrabajadores(AccesoTrabajador.obtenerTrabajadoresBaseDatos());
                        JOptionPane.showMessageDialog(null, "Eliminados con éxito", "", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } catch (BDException exception) {
                        System.out.println(exception.getMessage());
                    }
                case JOptionPane.NO_OPTION:
            }

            // --- Lógica del botón Cancelar: cierra el diálogo ---
        } else if (evento.getSource() == cancelar) {
            dispose();
        }
    }
}
