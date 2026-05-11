package dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import dao.AccesoTrabajador;
import exception.BDException;
import exception.TrabajadorException;
import modelo.Empresa;
import modelo.Trabajador;

/**
 *
 * @author usuario
 *
 */
public class ListarDialog extends JDialog implements ActionListener {

    JComboBox<String> comboPuesto;

    Empresa empresa;
    JTable tabla;
    JButton cerrar;
    JButton aceptar;

    List<Trabajador> trabajadores = new ArrayList<Trabajador>();

    String dni = "";
    String nombre = "";
    String apellidos = "";
    String direccion = "";
    String telefono = "";
    String puesto = "";

    public ListarDialog(Empresa empresa) {
        this.empresa = empresa;


        setResizable(false);
        // titulo del dialog
        setTitle("Listado Trabajadores");
        // tamaño
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

                return false;
            }
        };
        tabla = new JTable(modelo);

        // Metodo para ordenar las columas al interatuar
        tabla.setAutoCreateRowSorter(true);

        // Ancho de todas las filas
        tabla.setRowHeight(30);

        // Mete la tabla en un JCrollPane
        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 600));
        add(jsp);

        cerrar = new JButton("Cerrar");
        cerrar.addActionListener(this);
        add(cerrar);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == aceptar) {
            if (tabla.isEditing()) {
                tabla.getCellEditor().stopCellEditing();
            }

            int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea guardar los cambios trabajador?", "Guardar",
                    JOptionPane.YES_NO_OPTION);
            switch (respuesta) {
                case JOptionPane.YES_OPTION:
                    try {
                        AccesoTrabajador.insertarListaTrabajadores(trabajadores);
                        JOptionPane.showMessageDialog(null, "Cambios guardados con exito", "", JOptionPane.INFORMATION_MESSAGE);

                    } catch (TrabajadorException | BDException ex) {
                        System.out.println(ex.getMessage());
                    }
                case JOptionPane.NO_OPTION:
                    // Operaciones en caso negativo
                    break;
            }

            System.out.println("Aceptar");
        } else if (source == cerrar) {
            dispose();
            System.out.println("Cerrar");
        } else {
            System.out.println("Default");
        }
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
            JOptionPane.showMessageDialog(null, "El tel�fono debe tener longitud 9", "Error",
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
