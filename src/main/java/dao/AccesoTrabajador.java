package dao;

import config.ConfigMySql;
import exception.BDException;
import exception.TrabajadorException;
import modelo.Empresa;
import modelo.Trabajador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccesoTrabajador {
    public static int insertarTrabajador(Trabajador trabajador) throws BDException, TrabajadorException {
        Connection conexion = null;
        int columnasInsertadas = 0;

        String dni = trabajador.getDni();
        String nombre = trabajador.getNombre();
        String apellidos = trabajador.getApellidos();
        String direccion = trabajador.getDireccion();
        String telefono = trabajador.getTelefono();
        String puesto = trabajador.getPuesto();

        try {
            conexion = ConfigMySql.abrirConexion();

            String sentenciaInsertarDept = "INSERT INTO trabajador(dni, nombre, apellidos, direccion, telefono, puesto) VALUES(?,?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "nombre = VALUES(nombre)," +
                    "apellidos = VALUES(apellidos)," +
                    "direccion = VALUES(direccion)," +
                    "telefono = VALUES(telefono)," +
                    "puesto = VALUES(puesto)";

            PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsertarDept);

            sentencia.setString(1, dni);
            sentencia.setString(2, nombre);
            sentencia.setString(3, apellidos);
            sentencia.setString(4, direccion);
            sentencia.setString(5, telefono);
            sentencia.setString(6, puesto);

            columnasInsertadas = sentencia.executeUpdate();

            if (columnasInsertadas == 0) {
                throw new TrabajadorException(TrabajadorException.ERROR_EXISTE_EL_TRABAJADOR);
            }

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }

        /**
         * Si devuelve 0 No se realizaron cambios. La fila ya existía y los valores que se intentaron actualizar eran exactamente iguales
         * Si devuelve 1 solo inseta usuario nuevo
         * Si devuelve 2 ussuario existe pero se ha actualizado
         */
        return columnasInsertadas;
    }

    public static boolean actualizarTrabajador(Trabajador trabajador) throws TrabajadorException {
        Connection conexion = null;
        int columnasActualizadas = 0;

        String dni = trabajador.getDni();
        String nombre = trabajador.getNombre();
        String apellidos = trabajador.getApellidos();
        String direccion = trabajador.getDireccion();
        String telefono = trabajador.getTelefono();
        String puesto = trabajador.getPuesto();

        try {
            conexion = ConfigMySql.abrirConexion();

            String sentenciaInsertarDept = "UPDATE trabajador SET nombre = ?, apellidos = ?, direccion = ?, telefono = ?, puesto = ? WHERE dni = ?";

            PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsertarDept);

            sentencia.setString(1, nombre);
            sentencia.setString(2, apellidos);
            sentencia.setString(3, direccion);
            sentencia.setString(4, telefono);
            sentencia.setString(5, puesto);
            sentencia.setString(6, dni);

            columnasActualizadas = sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }

        return columnasActualizadas > 0;

    }

    public static boolean eliminarTrabajadorDni(String dni) throws BDException {
        Connection conexion = null;
        int columnasEliminadas = 0;


        try {
            conexion = ConfigMySql.abrirConexion();

            String sentenciaInsertarDept = "DELETE FROM trabajador WHERE dni = ?";

            PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsertarDept);

            sentencia.setString(1, dni);

            columnasEliminadas = sentencia.executeUpdate();


        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }

        return columnasEliminadas > 0;

    }

    public static boolean eliminarTrabajadorId(int id) throws BDException {
        Connection conexion = null;
        int columnasEliminadas = 0;


        try {
            conexion = ConfigMySql.abrirConexion();

            String sentenciaInsertarDept = "DELETE FROM trabajador WHERE id = ?";

            PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsertarDept);

            sentencia.setInt(1, id);

            columnasEliminadas = sentencia.executeUpdate();


        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }

        return columnasEliminadas > 0;

    }


    public static List<Trabajador> obtenerTrabajadoresBaseDatos() {
        List<Trabajador> TrabajadoresAux = new ArrayList<>();
        PreparedStatement ps;
        Connection conexion = null;

        try {
            conexion = ConfigMySql.abrirConexion();
            String sentenciaSelect = "SELECT * FROM trabajador";

            ps = conexion.prepareStatement(sentenciaSelect);

            ResultSet resultados = ps.executeQuery();

            while (resultados.next()) {
                int id = resultados.getInt("id");
                String dni = resultados.getString("dni");
                String nombre = resultados.getString("nombre");
                String apellidos = resultados.getString("apellidos");
                String direccion = resultados.getString("direccion");
                String telefono = resultados.getString("telefono");
                String puesto = resultados.getString("puesto");

                Trabajador trabajador = new Trabajador(id, dni, nombre, apellidos, direccion, telefono, puesto);
                TrabajadoresAux.add(trabajador);

            }
        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } catch (BDException e) {
            throw new BDException(BDException.ERROR_ABRIR_CONEXION + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }

        return TrabajadoresAux;

    }

    public static void insertarTrabajadores(List<Trabajador> trabajadores) throws BDException, TrabajadorException {
        Connection conexion = null;
        String sentenciaInsertarDept = "INSERT INTO trabajador(dni, nombre, apellidos, direccion, telefono, puesto) VALUES(?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "nombre = VALUES(nombre)," +
                "apellidos = VALUES(apellidos)," +
                "direccion = VALUES(direccion)," +
                "telefono = VALUES(telefono)," +
                "puesto = VALUES(puesto)";

        try {
            if (trabajadores != null) {
                throw new TrabajadorException(TrabajadorException.ERROR_LISTA_TRABAJADORES_VACIA);
            }

            conexion = ConfigMySql.abrirConexion();

            PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsertarDept);


            for (Trabajador trabajador : trabajadores) {
                sentencia.setString(1, trabajador.getDni());
                sentencia.setString(2, trabajador.getNombre());
                sentencia.setString(3, trabajador.getApellidos());
                sentencia.setString(4, trabajador.getDireccion());
                sentencia.setString(5, trabajador.getTelefono());
                sentencia.setString(6, trabajador.getPuesto());

                sentencia.executeBatch();

            }

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }

    }

    static void main() {
        Trabajador trabajador = new Trabajador(0, "12345678X", "Achraf", "Pepo", "Zaragoza", "631014166", "Programador");

        Trabajador trabajador2 = new Trabajador(0, "12345678Z", "Achraf", "Pepo", "Zaragoza", "631014166", "Programador");

        Trabajador trabajador3 = new Trabajador(0, "12345678Z", "Pepe", "Pepo", "Zaragoza", "631014166", "Programador");

        List<Trabajador> trabajadores = new ArrayList<>();

        trabajadores.add(trabajador);
        trabajadores.add(trabajador2);
        trabajadores.add(trabajador3);

        Empresa empresa = new Empresa(trabajadores);

        try {

            insertarTrabajadores(trabajadores);

//            insertarTrabajador(trabajador);
//            insertarTrabajador(trabajador2);
//            actualizarTrabajador(trabajador3);
//
//
//            eliminarTrabajador("12345678X");
//            eliminarTrabajador("12345678Z");

        } catch (TrabajadorException e) {
            System.out.println(e.getMessage());
            ;
        }

    }
}