package dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import dao.AccesoTrabajador;
import exception.BDException;
import ficheros.FicheroJSON;
import ficheros.FiecheroCSV;
import modelo.Empresa;
import modelo.Trabajador;
import utilidades.Utilidades;

/**
 * Diálogo para listar todos los trabajadores en una tabla de solo lectura.
 * Permite visualizar los datos de los trabajadores y cerrar la ventana.
 *
 * @author ach.dev
 */
public class ListarDialog extends JDialog implements ActionListener {

    JComboBox<String> comboPuesto;

    Empresa empresa;
    JTable tabla;
    String[][] datos;
    DefaultTableModel modelo;

    JButton buscar; // Boton de buscar para filtrar
    JButton cerrar;
    JButton exportar;
    JTextField busqueda;
    JPanel pBotonesArriba; // Aqui se van a almacenar los campos de arroba para filtrar;
    JPanel contentPane;

    JComboBox comboFiltro;

    List<Trabajador> trabajadores = new ArrayList<Trabajador>();

    String dni = "";
    String nombre = "";
    String apellidos = "";
    String direccion = "";
    String telefono = "";
    String puesto = "";

    JFileChooser fileChooser;

    public ListarDialog(Empresa empresa) {
        this.empresa = empresa;
        trabajadores = empresa.getTrabajadores();

        setResizable(false);
        // titulo del dialog
        setTitle("Listado Trabajadores");
        // tamaño
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
        String[] columnas = {"ID", "DNI", "Nombre", "Apellidos", "Direccion", "Telefono", "Puesto"};
        datos = empresa.listarTrabajadores();

        // Contiene los datos tanto filas como columnas de la tabla
        modelo = new DefaultTableModel(datos, columnas) {
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
        jsp.setPreferredSize(new Dimension(700, 560));
        add(jsp);

        contentPane = new JPanel();

        exportar = new JButton("Exportar");
        exportar.addActionListener(this);
        contentPane.add(exportar);

        cerrar = new JButton("Cerrar");
        cerrar.addActionListener(this);
        contentPane.add(cerrar);

        add(contentPane);


        Utilidades.ajustarAnchoColumnas(tabla);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
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
                    // Creamos una lista con los datos trabajadores
                    trabajadores = AccesoTrabajador.obtenerTrabajadoresFiltrados(campoBD, texto);
                    datos = new String[trabajadores.size()][7];
                    for (int i = 0; i < trabajadores.size(); i++) {
                        Utilidades.creaFilasFiltradasTrabajadores(trabajadores, i, datos);
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
        } else if (source == exportar) {
            String[] opciones = {"Exportación CSV", "Exportación JSON", "Cancelar"};
            int resp = JOptionPane.showOptionDialog(
                    this, "¿A que formato quieres exportar?", "Exportar",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[2]);
            if (resp == 0) {
                System.out.println(opciones[0]);
                fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("trabajadores.csv"));
                fileChooser.setFileFilter(new FileNameExtensionFilter(".csv", "csv"));
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    FiecheroCSV.exportarFicheroCSV(fileChooser.getSelectedFile().getAbsolutePath(), trabajadores);
                    JOptionPane.showMessageDialog(this,
                            "Se han exportado: " + trabajadores.size() + " trabajadores",
                            "Completado", JOptionPane.INFORMATION_MESSAGE);
                }


            } else if (resp == 1) {
                System.out.println(opciones[1]);
                fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("trabajadores.json"));
                fileChooser.setFileFilter(new FileNameExtensionFilter(".json", "json"));
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    FicheroJSON.exportarFicheroJSON(fileChooser.getSelectedFile().getAbsolutePath(), trabajadores);
                    JOptionPane.showMessageDialog(this,
                            "Se han exportado: " + trabajadores.size() + " trabajadores",
                            "Completado", JOptionPane.INFORMATION_MESSAGE);
                }


            } else if (resp == 2) {
                System.out.println(opciones[2]);
            }

        } else if (source == cerrar) {
            dispose();
            System.out.println("Cerrar");
        } else {
            System.out.println("Default");
        }
    }

}
