package dialogs;

import dao.AccesoTrabajador;
import exception.BDException;
import exception.TrabajadorException;
import modelo.Empresa;
import modelo.Trabajador;

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

import static dao.AccesoTrabajador.actualizarTrabajador;

public class ModificaDialog extends JDialog implements ActionListener, ItemListener {
    /**
     * Elementos del JFrame
     */
    JComboBox comboPuesto;
    JButton aceptar;
    JButton cancelar;
    JPanel pBotones;
    JTable tabla;

    /**
     * Variables a las que se pasara el contenido de los JTextField y del combo box
     */
    int id = 0;
    String dni = "";
    String nombre = "";
    String apellidos = "";
    String direccion = "";
    String telefono = "";
    String puesto = "";

    Empresa empresa;
    List<Trabajador> trabajadores = new ArrayList<Trabajador>();


    public ModificaDialog(Empresa empresa) {
        this.empresa = empresa;

        // Impedimos que se pueda cambiar el tamaño de la ventana ModificaDialog
        setResizable(false);

        // Titulo del dialog
        setTitle("Modificado Trabajadores");

        // Tamaño
        setSize(750, 700);
        setLayout(new FlowLayout());

        // colocacion en el centro de la pantalla
        setLocationRelativeTo(null);

        // Crea un JTable, cada fila será un trabajador
        String[] columnas = {"Identificador", "DNI", "Nombre", "Apellidos", "Direccion", "Telefono", "Puesto"};
        String[][] datos = empresa.listarTrabajadores();

        // Contiene los datos tanto filas como columnas de la tabla
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Bloquear la columna 0 (primera columna)
                if (column == 0 || column == 1) {
                    return false;
                }
                // El resto de las celdas serán editables
                return true;
            }
        };

        // Creamos un JTable
        tabla = new JTable(modelo);

        // Metodo para ordenar las columas al interatuar
        tabla.setAutoCreateRowSorter(true);

        // Mete la tabla en un JCrollPane
        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 600));
        add(jsp);

        // lista desplegable
        comboPuesto = new JComboBox();
        comboPuesto.addItem("Programador");
        comboPuesto.addItem("Analista");
        comboPuesto.addItem("Arquitecto");
        comboPuesto.addItem("Jefe de Proyecto");

        // Marcamos la columna 6 Puesto como comboBox
        TableColumn columnaRol = tabla.getColumnModel().getColumn(6);
        columnaRol.setCellEditor(new DefaultCellEditor(comboPuesto));

        tabla.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {

                // Solo detecta cambios de tipo UPDATE
                if (e.getType() != TableModelEvent.UPDATE) {
                    return;
                }

                // Detectamos la fila y columna modificada
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Este if comprueba si has echo algun cambio para continuar, si no hay cambio se detiene
//                if (tabla.getValueAt(row, column).equals(tabla.getModel().getValueAt(row, column).toString())) {
//                    return;
//                }

                // Obtenemos los valores de toda la fila modificada
                dni = (String) tabla.getValueAt(row, 1);
                nombre = (String) tabla.getValueAt(row, 2);
                apellidos = (String) tabla.getValueAt(row, 3);
                direccion = (String) tabla.getValueAt(row, 4);
                telefono = (String) tabla.getValueAt(row, 5);
                puesto = (String) tabla.getValueAt(row, 6);


                // Si no hay errores de sintaxsis permite pasar a lo sigiente
                if (comprobarErrores()) {

                    // Creamos un objeto Trabajador que almacene
                    Trabajador trabajador = new Trabajador(0, dni, nombre, apellidos, direccion, telefono, puesto);
                    System.out.println(trabajador);

                    // Lo añadimos a una lista de trabajadores a modificar
                    trabajadores.add(trabajador);
                    System.out.println(trabajadores);

                } else {
                    JOptionPane.showMessageDialog(null, "El dato no cumple con el formato NO se modificara ", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // una JPanel para guardar los botones
        pBotones = new JPanel();

        // Creamos boton aceptar y añadimos a JPanel
        aceptar = new JButton("Aceptar");
        aceptar.addActionListener(this);
        pBotones.add(aceptar);

        // Creamos boton cancelar y añadimos a JPanel
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        pBotones.add(cancelar);

        add(pBotones);

        // Visible
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == aceptar) {

            // Finaliza la edición activa y confirma los cambios para evitar la pérdida del ultimo dato ingresado.
            if (tabla.isEditing()) {
                tabla.getCellEditor().stopCellEditing();
            }

            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea guardar los cambios modificados?", "Guardar",
                    JOptionPane.YES_NO_OPTION);

            switch (respuesta) {
                case JOptionPane.YES_OPTION:
                    try {
                        AccesoTrabajador.actualizarListaTrabajadoresPorDni(trabajadores);
                        JOptionPane.showMessageDialog(null, "Cambios guardados con exito ", "", JOptionPane.INFORMATION_MESSAGE);

                    } catch (TrabajadorException | BDException ex) {
                        System.out.println(ex.getMessage());
                    }
                case JOptionPane.NO_OPTION:
                    // Operaciones en caso negativo no hacer nada
                    break;
            }

            // Al tocar cancelar se hace un dispose()
        } else if (e.getSource() == cancelar) {
            dispose();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        puesto = comboPuesto.getSelectedItem().toString();
    }

    public boolean comprobarErrores() {

        if (dni.equals("") || dni.length() != 9) {
            JOptionPane.showMessageDialog(null, "El DNI debe tener longitud 9", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (nombre.equals("")) {
            JOptionPane.showMessageDialog(null, "Debe introducir el nombre del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (apellidos.equals("")) {
            JOptionPane.showMessageDialog(null, "Debe introducir los apellidos del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (direccion.equals("")) {
            JOptionPane.showMessageDialog(null, "Debe introducir la direcci�n del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (telefono.equals("") || telefono.length() != 9) {
            JOptionPane.showMessageDialog(null, "El telefono debe tener longitud 9", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (puesto.equals("")) {
            JOptionPane.showMessageDialog(null, "Debe introducir el puesto del trabajador", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

}
