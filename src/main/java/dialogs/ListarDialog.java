package dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import ficheros.FicheroJSON;
import ficheros.FiecheroCSV;
import modelo.Empresa;
import modelo.Trabajador;
import utilidades.Utilidades;

import static utilidades.Utilidades.*;
import static validacion.Validacion.realizarBusqueda;

// Diálogo para listar todos los trabajadores en una tabla de solo lectura con opción de exportar
public class ListarDialog extends JDialog implements ActionListener {
    Empresa empresa;
    JTable tabla;
    String[][] datos;
    DefaultTableModel modelo;
    JButton buscar;
    JButton cerrar;
    JButton exportar;
    JTextField busqueda;
    JPanel pBotonesArriba;
    JPanel contentPane;
    JComboBox comboFiltro;
    List<Trabajador> trabajadores;
    JFileChooser fileChooser;

    // Constructor: configura la ventana, la tabla de trabajadores y los botones
    public ListarDialog(Empresa empresa) {
        this.empresa = empresa;
        trabajadores = empresa.getTrabajadores();
        setResizable(false);
        setTitle("Listado Trabajadores");
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

        // --- Tabla de solo lectura con los datos de los trabajadores ---
        datos = empresa.listarTrabajadores();
        modelo = new DefaultTableModel(datos, COLUMNAS_TRABAJADORES) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ninguna celda es editable
            }
        };
        tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(30);
        JScrollPane jsp = new JScrollPane(tabla);
        jsp.setPreferredSize(new Dimension(700, 560));
        add(jsp);

        // --- Panel inferior: botones de exportación y cierre ---
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

    // Gestiona las acciones: buscar, exportar (CSV o JSON) y cerrar
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // --- Lógica del botón Buscar: filtra la tabla según el criterio ---
        if (e.getSource() == buscar) {
            String[][] resultado = realizarBusqueda(busqueda, comboFiltro, empresa, modelo, tabla, this);
            if (resultado != null) {
                datos = resultado;
            }

            // --- Lógica del botón Exportar: exporta a CSV o JSON según elección ---
        } else if (source == exportar) {
            String[] opciones = {"Exportación CSV", "Exportación JSON", "Cancelar"};
            int resp = JOptionPane.showOptionDialog(
                    this, "¿A que formato quieres exportar?", "Exportar",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[2]);

            // Exportar a CSV
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

                // Exportar a JSON
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
            }

            // --- Lógica del botón Cerrar: cierra el diálogo ---
        } else if (source == cerrar) {
            dispose();
        }
    }
}
