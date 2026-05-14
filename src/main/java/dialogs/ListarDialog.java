package dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import ficheros.FicheroDatos;
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
    JButton cerrar;
    JButton aceptar;
    JButton exportar;
    JPanel contentPane;

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
        String[] columnas = {"ID", "DNI", "Nombre", "Apellidos", "Direccion", "Telefono", "Puesto"};
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
        if (source == exportar) {
            String[] opciones = {"Exportación CSV", "Exportación JSON", "Cancelar"};
            int resp = JOptionPane.showOptionDialog(
                    this, "¿A que formato quieres exportar?", "Exportar",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[2]);
            if (resp == 0) {
                System.out.println(opciones[0]);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("trabajadores.csv"));
                fileChooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    FiecheroCSV.exportarFicheroCSV(fileChooser.getSelectedFile().getAbsolutePath(), empresa.getTrabajadores());
                }
                JOptionPane.showMessageDialog(this,
                        "Se han exportado: " + empresa.getTrabajadores().size() + " trabajadores",
                        "Completado", JOptionPane.INFORMATION_MESSAGE);

            } else if (resp == 1) {
                System.out.println(opciones[1]);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("trabajadores.json"));
                fileChooser.setFileFilter(new FileNameExtensionFilter("JSON", "json"));
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    FicheroJSON.exportarFicheroJSON(fileChooser.getSelectedFile().getAbsolutePath(), empresa.getTrabajadores());
                }
                JOptionPane.showMessageDialog(this,
                        "Se han exportado: " + empresa.getTrabajadores().size() + " trabajadores",
                        "Completado", JOptionPane.INFORMATION_MESSAGE);

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
