package dialogs;

import dao.AccesoTrabajador;
import exception.BDException;
import modelo.Empresa;
import modelo.Trabajador;
import utilidades.Utilidades;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Diálogo para visualizar los datos de los trabajadores en una tabla de solo lectura.
 * Incluye filtro de búsqueda por campo y botón para cerrar la ventana.
 *
 * @author usuario
 */
public class VerDialog extends JDialog implements ActionListener, ItemListener {


    JButton buscar; // Boton de buscar para filtrar
    JButton cancelar; // Boton de cancelar para salir;
    JPanel pBotonesArriba; // Aqui se van a almacenar los campos de arroba para filtrar;
    JPanel pBotones; // Aqui va a ir el boton de cancelar;

    JTable tabla; // Esto es la tabla en la que se muestran los resultados
    JTextField busqueda;
    JComboBox comboFiltro;
    String[][] datos;
    DefaultTableModel modelo;
    String[] columnas;
    Empresa empresa;
    List<Trabajador> trabajadores = new ArrayList<Trabajador>();


    public VerDialog(Empresa empresa) {
        this.empresa = empresa;

        // Impedimos que se pueda cambiar el tamaño de la ventana ModificaDialog
        setResizable(false);

        // Titulo del dialog
        setTitle("Buscar Trabajadores");

        // Tamaño
        setSize(750, 700);
        setLayout(new FlowLayout());

        // colocacion en el centro de la pantalla
        setLocationRelativeTo(null);

        pBotonesArriba = new JPanel();

        // lista desplegable para filtrar
        comboFiltro = new JComboBox();
        comboFiltro.addItem("DNI");
        comboFiltro.addItem("Nombre");
        comboFiltro.addItem("Apellidos");
        comboFiltro.addItem("Direccion");
        comboFiltro.addItem("Telefono");
        comboFiltro.addItem("Puesto");
        pBotonesArriba.add(comboFiltro);

        // Donde vas a escribir el nombre de lo que vas a filtrar
        busqueda = new JTextField(15);
        pBotonesArriba.add(busqueda); // Se añaden al JPanel

        // Creamos boton buscar y añadimos a JPanel
        buscar = new JButton("Buscar");
        buscar.addActionListener(this);
        pBotonesArriba.add(buscar);

        add(pBotonesArriba);


        // Crea un JTable, cada fila será un trabajador
        columnas = new String[]{"ID", "DNI", "Nombre", "Apellidos", "Direccion", "Telefono", "Puesto"};
        datos = empresa.listarTrabajadores();

        // Contiene los datos tanto filas como columnas de la tabla
        modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Bloquear todas las columnas
                return false;
            }
        };

        // Creamos un JTable
        tabla = new JTable(modelo);

        // Metodo para ordenar las columas al interatuar
        tabla.setAutoCreateRowSorter(true);

        // Ancho de todas las filas
        tabla.setRowHeight(30);

        // Mete la tabla en un JCrollPane
        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 560));
        add(jsp);


        // una JPanel para guardar los botones
        pBotones = new JPanel();

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
            // Asignamos el valor del JTextField
            String texto = busqueda.getText().trim();

            // Almacena el valor del combobox
            String seleccion = (String) comboFiltro.getSelectedItem();

            // Asigna a una variable el valor del combobox
            String campoBD = null;
            if (seleccion != null) {
                campoBD = switch (seleccion) {
                    case "DNI" -> "dni";
                    case "Nombre" -> "nombre";
                    case "Apellidos" -> "apellidos";
                    case "Direccion" -> "direccion";
                    case "Telefono" -> "telefono";
                    case "Puesto" -> "puesto";
                    default -> null;
                };
            }

            // Si es null no hace nada
            if (campoBD == null) return;

            try {
                if (texto.isEmpty()) { // Si esta vacio el texto muestra todo
                    datos = empresa.listarTrabajadores();
                } else {
                    // Creamos una lista con los datos filtrados
                    List<Trabajador> filtrados = AccesoTrabajador.obtenerTrabajadoresFiltrados(campoBD, texto);
                    datos = new String[filtrados.size()][7];
                    for (int i = 0; i < filtrados.size(); i++) {
                        Utilidades.creaFilasFiltradasTrabajadores(filtrados, i, datos);
                    }
                }

                // ESTO ELIMINA TODAS LAS TABLAS DEL MODELO PARA HACER ESPACIO A EL FILTRO
                modelo.setRowCount(0);

                JOptionPane.showMessageDialog(this,
                        "Resultados encontrados: " + datos.length,
                        "Busqueda", JOptionPane.INFORMATION_MESSAGE);

                // Inserta el giltrado a la tabla
                for (String[] fila : datos) {
                    modelo.addRow(fila);
                }

            } catch (BDException ex) {
                JOptionPane.showMessageDialog(null, "Error al filtrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == cancelar) {
            dispose();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}
