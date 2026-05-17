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

/**
 * Diálogo para modificar los datos de trabajadores existentes.
 * Muestra una tabla editable donde se pueden cambiar nombre, apellidos,
 * dirección, teléfono y puesto, con validación de datos y filtro de búsqueda.
 *
 * @author ach.dev
 */
public class ModificaDialog extends JDialog implements ActionListener, ItemListener {
    /**
     * Elementos del JFrame
     */
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
    String[] columnas;


    /**
     * Variables a las que se pasara el contenido de los JTextField y del combo box
     */
    String dni = "";
    String nombre = "";
    String apellidos = "";
    String direccion = "";
    String telefono = "";
    String puesto = "";

    Empresa empresa;
    List<Trabajador> trabajadores = new ArrayList<>();
    boolean isReverting;


    public ModificaDialog(Empresa empresa) {
        this.empresa = empresa;

        // Impedimos que se pueda cambiar el tamaño de la ventana ModificaDialog
        setResizable(false);

        // Titulo del dialog
        setTitle("Modificado Trabajadores");

        // Tamaño
        setSize(750, 700);
        setLayout(new FlowLayout());

        // colocación en el centro de la pantalla
        setLocationRelativeTo(null);

        pBotonesArriba = new JPanel();

// lista desplegable para filtrar
        comboFiltro = new JComboBox();
        comboFiltro.addItem("DNI");
        comboFiltro.addItem("Nombre");
        comboFiltro.addItem("Apellidos");
        comboFiltro.addItem("Dirección");
        comboFiltro.addItem("Teléfono");
        comboFiltro.addItem("Puesto");
        pBotonesArriba.add(comboFiltro);


        busqueda = new JTextField(15);
        // Se añaden al JPanel
        pBotonesArriba.add(busqueda);

        // Creamos botón eliminar y añadimos a JPanel
        buscar = new JButton("Buscar");
        buscar.addActionListener(this);
        pBotonesArriba.add(buscar);


        add(pBotonesArriba);


        // Crea un JTable, cada fila será un trabajador
        columnas = new String[]{"ID", "DNI", "Nombre", "Apellidos", "Dirección", "Teléfono", "Puesto"};
        datos = empresa.listarTrabajadores();

        // Contiene los datos tanto filas como columnas de la tabla
        modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Bloquear la columna 0 (primera columna)
                return column != 0 && column != 1;
                // El resto de las celdas serán editables
            }
        };

        // Creamos un JTable
        tabla = new JTable(modelo);

        // Método para ordenar las columns al integrate
        tabla.setAutoCreateRowSorter(true);

        // Ancho de todas las filas
        tabla.setRowHeight(30);

        // Mete la tabla en un JCrollPane
        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 560));
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
            public void tableChanged(TableModelEvent evento) {

                // Solo detecta cambios de tipo UPDATE
                if (evento.getType() != TableModelEvent.UPDATE) {
                    return;
                }

                // Detectamos la fila y columna modificada
                int modelRow = evento.getFirstRow();
                int modelColumn = evento.getColumn();

                // Convierte la fila de modelos a vista (necesario cuando la tabla está ordenada)
                int viewRow = tabla.convertRowIndexToView(modelRow);

                // Este if comprueba si has hecho algún cambio para continuar, si no hay cambio se detiene
                if (tabla.getValueAt(viewRow, evento.getColumn()).equals(datos[modelRow][modelColumn])) {
                    isReverting = false;
                    return;
                }

                // Obtenemos los valores de toda la fila modificada
                dni = (String) tabla.getValueAt(viewRow, 1);
                nombre = (String) tabla.getValueAt(viewRow, 2);
                apellidos = (String) tabla.getValueAt(viewRow, 3);
                direccion = (String) tabla.getValueAt(viewRow, 4);
                telefono = (String) tabla.getValueAt(viewRow, 5);
                puesto = (String) tabla.getValueAt(viewRow, 6);


                // If que comprueba si hay que rebertir los cambios echos
                if (isReverting) {
                    isReverting = false;
                    return;
                }

                // Si no hay errores de sintaxsis permite pasar a lo sigiente
                if (comprobarErrores()) {

                    // Creamos un objeto Trabajador que almacene
                    Trabajador trabajador = new Trabajador(dni, nombre, apellidos, direccion, telefono, puesto);
                    System.out.println(trabajador);

                    // Lo añadimos a una lista de trabajadores a modificar
                    trabajadores.add(trabajador);
                    System.out.println(trabajadores);

                } else {
                    isReverting = true;
                    tabla.setValueAt(datos[modelRow][modelColumn], viewRow, evento.getColumn());//                    JOptionPane.showMessageDialog(null, "El dato no cumple...");
                }
            }
        });

        // una JPanel para guardar los botones
        pBotones = new JPanel();

        // Creamos boton eliminar y añadimos a JPanel
        aceptar = new JButton("Aceptar");
        aceptar.addActionListener(this);
        pBotones.add(aceptar);

        // Creamos boton cancelar y añadimos a JPanel
        cancelar = new JButton("Cancelar");
        cancelar.addActionListener(this);
        pBotones.add(cancelar);

        add(pBotones);

        Utilidades.ajustarAnchoColumnas(tabla);

        // Visible
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buscar) {
            String texto = busqueda.getText().trim();
            String campoBD = opcionBuscador();
            if (campoBD == null) return;

            try {
                datos = Utilidades.filtrarTrabajadores(empresa, texto, campoBD);
                Utilidades.actualizarTabla(modelo, datos, tabla, this);
            } catch (BDException ex) {
                JOptionPane.showMessageDialog(null, "Error al filtrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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
            JOptionPane.showMessageDialog(null, "Debe introducir la direcci�n del trabajador", "Error",
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

    private String opcionBuscador() {
        String seleccion = (String) comboFiltro.getSelectedItem();
        if (seleccion != null) {
            return Utilidades.comboToCampoBD(seleccion);
        }
        return null;
    }


}
