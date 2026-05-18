package dialogs;

import dao.AccesoTrabajador;
import exception.BDException;
import exception.TrabajadorException;
import modelo.Empresa;
import modelo.Trabajador;
import utilidades.Utilidades;
import validacion.Validacion;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import static utilidades.Utilidades.*;
import static validacion.Validacion.realizarBusqueda;

// Diálogo para modificar datos de trabajadores existentes mediante una tabla editable
public class ModificaDialog extends JDialog implements ActionListener, ItemListener {
    JComboBox comboPuesto;
    JButton aceptar;
    JButton buscar;
    JButton cancelar;
    JPanel pBotonesArriba;
    JPanel pBotones;
    JTable tabla;
    JTextField busqueda;
    JComboBox comboFiltro;
    String[][] datos;
    DefaultTableModel modelo;
    String dni = "";
    String nombre = "";
    String apellidos = "";
    String direccion = "";
    String telefono = "";
    String puesto = "";
    Empresa empresa;
    List<Trabajador> trabajadores = new ArrayList<>();
    boolean isReverting;

    // Constructor: configura la ventana con tabla editable, filtros y listeners
    public ModificaDialog(Empresa empresa) {
        this.empresa = empresa;
        setResizable(false);
        setTitle("Modificado Trabajadores");
        setSize(750, 700);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        // --- Panel superior: filtro de búsqueda ---
        pBotonesArriba = new JPanel();
        comboFiltro = crearComboFiltro();
        pBotonesArriba.add(comboFiltro);
        busqueda = new JTextField(15);
        pBotonesArriba.add(busqueda);
        buscar = new JButton("Buscar");
        buscar.addActionListener(this);
        pBotonesArriba.add(buscar);
        add(pBotonesArriba);

        // --- Tabla editable: se puede modificar nombre, apellidos, dirección, teléfono y puesto ---
        datos = empresa.listarTrabajadores();
        modelo = new DefaultTableModel(datos, COLUMNAS_TRABAJADORES) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 1; // ID y DNI no editables
            }
        };
        tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(30);
        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 560));
        add(jsp);

        // La columna 6 (Puesto) usa un JComboBox como editor para elegir el puesto
        comboPuesto = crearComboPuestos();
        TableColumn columnaRol = tabla.getColumnModel().getColumn(6);
        columnaRol.setCellEditor(new DefaultCellEditor(comboPuesto));

        // --- Listener: captura los cambios en la tabla y los acumula para guardarlos ---
        tabla.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent evento) {
                if (evento.getType() != TableModelEvent.UPDATE) return;

                int modelRow = evento.getFirstRow();
                int modelColumn = evento.getColumn();
                int viewRow = tabla.convertRowIndexToView(modelRow);

                // Ignora si el valor no ha cambiado realmente
                if (tabla.getValueAt(viewRow, evento.getColumn()).equals(datos[modelRow][modelColumn])) {
                    isReverting = false;
                    return;
                }

                dni = (String) tabla.getValueAt(viewRow, 1);
                nombre = (String) tabla.getValueAt(viewRow, 2);
                apellidos = (String) tabla.getValueAt(viewRow, 3);
                direccion = (String) tabla.getValueAt(viewRow, 4);
                telefono = (String) tabla.getValueAt(viewRow, 5);
                puesto = (String) tabla.getValueAt(viewRow, 6);

                // Si se está revirtiendo un cambio inválido, no hacer nada
                if (isReverting) {
                    isReverting = false;
                    return;
                }

                // Valida los datos y acumula el trabajador modificado en la lista
                if (Validacion.comprobarErroresModificar(dni, nombre, apellidos, direccion, telefono, puesto)) {
                    Trabajador trabajador = new Trabajador(dni, nombre, apellidos, direccion, telefono, puesto);
                    System.out.println(trabajador);
                    trabajadores.add(trabajador);
                    System.out.println(trabajadores);
                } else {
                    isReverting = true;
                    tabla.setValueAt(datos[modelRow][modelColumn], viewRow, evento.getColumn());
                }
            }
        });

        // --- Panel inferior: botones de acción ---
        pBotones = new JPanel();
        aceptar = new JButton("Aceptar");
        aceptar.addActionListener(this);
        pBotones.add(aceptar);
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        pBotones.add(cancelar);
        add(pBotones);

        Utilidades.ajustarAnchoColumnas(tabla);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    // Gestiona las acciones: buscar, guardar cambios y cancelar
    @Override
    public void actionPerformed(ActionEvent e) {

        // --- Lógica del botón Buscar: filtra la tabla ---
        if (e.getSource() == buscar) {
            String[][] resultado = realizarBusqueda(busqueda, comboFiltro, empresa, modelo, tabla, this);
            if (resultado != null) {
                datos = resultado;
            }

        // --- Lógica del botón Aceptar: guarda todos los cambios acumulados en BD ---
        } else if (e.getSource() == aceptar) {
            if (tabla.isEditing()) {
                tabla.getCellEditor().stopCellEditing();
            }

            if (trabajadores.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay cambios que guardar", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(null,
                    "Se van a modificar " + trabajadores.size() + " trabajadores. ¿Desea guardar los cambios?",
                    "Guardar", JOptionPane.YES_NO_OPTION);
            switch (respuesta) {
                case JOptionPane.YES_OPTION:
                    try {
                        AccesoTrabajador.actualizarListaTrabajadoresPorDni(trabajadores);
                        JOptionPane.showMessageDialog(null, "Cambios guardados con éxito", "", JOptionPane.INFORMATION_MESSAGE);
                    } catch (TrabajadorException | BDException ex) {
                        System.out.println(ex.getMessage());
                    }
                case JOptionPane.NO_OPTION:
                    break;
            }

        // --- Lógica del botón Cancelar: cierra el diálogo ---
        } else if (e.getSource() == cancelar) {
            dispose();
        }
    }

    // Actualiza el puesto seleccionado en el combo
    @Override
    public void itemStateChanged(ItemEvent e) {
        puesto = (String) comboPuesto.getSelectedItem();
    }
}
