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

    public static boolean actualizarTrabajador(Trabajador trabajador) throws BDException {
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

    public static void insertarListaTrabajadores(List<Trabajador> trabajadores) throws BDException, TrabajadorException {
        Connection conexion = null;
        int columnasInsertadas = 0;

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

            for (Trabajador trabajador : trabajadores) {
                String dni = trabajador.getDni();
                String nombre = trabajador.getNombre();
                String apellidos = trabajador.getApellidos();
                String direccion = trabajador.getDireccion();
                String telefono = trabajador.getTelefono();
                String puesto = trabajador.getPuesto();

                sentencia.setString(1, dni);
                sentencia.setString(2, nombre);
                sentencia.setString(3, apellidos);
                sentencia.setString(4, direccion);
                sentencia.setString(5, telefono);
                sentencia.setString(6, puesto);

                columnasInsertadas = sentencia.executeUpdate();

            }


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

    }

    public static List<Trabajador> obtenerListaTrabajadoresPorDni(String dniAux) throws TrabajadorException {
        List<Trabajador> TrabajadoresAux = new ArrayList<>();
        PreparedStatement ps;
        Connection conexion = null;

        try {
            conexion = ConfigMySql.abrirConexion();
            String sentenciaSelect = "SELECT * FROM trabajador WHERE dni LIKE ?";

            ps = conexion.prepareStatement(sentenciaSelect);

            ps.setString(1, "%" + dniAux + "%");

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

    public static int actualizarListaTrabajadoresPorDni(List<Trabajador> trabajadores) throws BDException, TrabajadorException {
        Connection conexion = null;
        int totalActualizados = 0;

        try {
            if (trabajadores == null) {
                throw new TrabajadorException(TrabajadorException.ERROR_LISTA_TRABAJADORES_VACIA);
            }

            conexion = ConfigMySql.abrirConexion();

            String sqlUpdate = "UPDATE trabajador SET nombre = ?, apellidos = ?, direccion = ?, telefono = ?, puesto = ? WHERE dni = ?";

            PreparedStatement sentencia = conexion.prepareStatement(sqlUpdate);

            for (Trabajador trabajador : trabajadores) {
                sentencia.setString(1, trabajador.getNombre());
                sentencia.setString(2, trabajador.getApellidos());
                sentencia.setString(3, trabajador.getDireccion());
                sentencia.setString(4, trabajador.getTelefono());
                sentencia.setString(5, trabajador.getPuesto());
                sentencia.setString(6, trabajador.getDni());

                int filasActualizadas = sentencia.executeUpdate();
                totalActualizados += filasActualizadas;

                if (filasActualizadas == 0) {
                    System.out.println("AVISO - No se encontró trabajador con DNI: " + trabajador.getDni());
                }
            }

            if (totalActualizados == 0) {
                throw new TrabajadorException(TrabajadorException.ERROR_NO_ACUALIZO_NADA);
            }

        } catch (SQLException e) {
            throw new BDException(BDException.ERROR_QUERY + e.getMessage());
        } finally {
            if (conexion != null) {
                ConfigMySql.cerrarConexion(conexion);
            }
        }

        return totalActualizados; // Devuelve cuántos trabajadores se actualizaron en total
    }

    public static void main(String[] args) {

        System.out.println("========== TEST 1: insertarTrabajador (nuevo) ==========");
        try {
            Trabajador t1 = new Trabajador(0, "12345678A", "Carlos", "García López", "Calle Mayor 1", "600111222", "Desarrollador");
            int resultado = insertarTrabajador(t1);
            if (resultado == 1) {
                System.out.println("OK - Trabajador insertado correctamente. Filas: " + resultado);
            } else {
                System.out.println("AVISO - Resultado inesperado: " + resultado);
            }
        } catch (BDException | TrabajadorException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 2: insertarTrabajador (duplicado con cambios) ==========");
        try {
            // Mismo DNI, datos distintos → debe actualizar (devuelve 2)
            Trabajador t2 = new Trabajador(0, "12345678A", "Carlos", "García López", "Calle Nueva 99", "600999888", "Analista");
            int resultado = insertarTrabajador(t2);
            if (resultado == 2) {
                System.out.println("OK - Trabajador existente actualizado. Filas: " + resultado);
            } else {
                System.out.println("AVISO - Resultado inesperado: " + resultado);
            }
        } catch (BDException | TrabajadorException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 3: insertarTrabajador (duplicado sin cambios → excepción) ==========");
        try {
            // Mismo DNI y exactamente los mismos datos → devuelve 0 → lanza TrabajadorException
            Trabajador t3 = new Trabajador(0, "12345678A", "Carlos", "García López", "Calle Nueva 99", "600999888", "Analista");
            int resultado = insertarTrabajador(t3);
            System.out.println("AVISO - Debería haber lanzado excepción. Resultado: " + resultado);
        } catch (TrabajadorException e) {
            System.out.println("OK - Excepción esperada: " + e.getMessage());
        } catch (BDException e) {
            System.out.println("ERROR BD - " + e.getMessage());
        }

        System.out.println("\n========== TEST 4: actualizarTrabajador ==========");
        try {
            Trabajador tActualizar = new Trabajador(0, "12345678A", "Carlos", "García Pérez", "Avenida Central 5", "611222333", "Tester");
            boolean actualizado = actualizarTrabajador(tActualizar);
            if (actualizado) {
                System.out.println("OK - Trabajador actualizado correctamente.");
            } else {
                System.out.println("AVISO - No se encontró el trabajador con ese DNI.");
            }
        } catch (BDException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 5: actualizarTrabajador (DNI inexistente) ==========");
        try {
            Trabajador tInexistente = new Trabajador(0, "00000000Z", "Fantasma", "Nadie", "Sin dirección", "000000000", "Ninguno");
            boolean actualizado = actualizarTrabajador(tInexistente);
            if (!actualizado) {
                System.out.println("OK - Correctamente no encontrado, devuelve false.");
            } else {
                System.out.println("AVISO - Se actualizó algo inesperado.");
            }
        } catch (BDException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 6: insertarListaTrabajadores ==========");
        try {
            List<Trabajador> lista = new ArrayList<>();
            lista.add(new Trabajador(0, "22222222B", "Ana", "Martínez Ruiz", "Calle Olmo 3", "622333444", "Diseñadora"));
            lista.add(new Trabajador(0, "33333333C", "Luis", "Sánchez Torres", "Plaza España 7", "633444555", "DevOps"));
            lista.add(new Trabajador(0, "44444444D", "Marta", "López Vega", "Ronda Norte 12", "644555666", "QA"));
            insertarListaTrabajadores(lista);
            System.out.println("OK - Lista de trabajadores insertada correctamente.");
        } catch (BDException | TrabajadorException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 7: obtenerTrabajadoresBaseDatos ==========");
        try {
            List<Trabajador> todos = obtenerTrabajadoresBaseDatos();
            if (!todos.isEmpty()) {
                System.out.println("OK - Total trabajadores obtenidos: " + todos.size());
                todos.forEach(t -> System.out.println("  → " + t.getDni() + " | " + t.getNombre() + " " + t.getApellidos() + " | " + t.getPuesto()));
            } else {
                System.out.println("AVISO - La base de datos está vacía.");
            }
        } catch (Exception e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 8: obtenerListaTrabajadoresPorDni (parcial) ==========");
        try {
            List<Trabajador> porDni = obtenerListaTrabajadoresPorDni("2222");
            if (!porDni.isEmpty()) {
                System.out.println("OK - Trabajadores encontrados con DNI que contiene '2222': " + porDni.size());
                porDni.forEach(t -> System.out.println("  → " + t.getDni() + " | " + t.getNombre()));
            } else {
                System.out.println("AVISO - No se encontraron trabajadores con ese fragmento de DNI.");
            }
        } catch (TrabajadorException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 9: obtenerListaTrabajadoresPorDni (sin resultados) ==========");
        try {
            List<Trabajador> porDni = obtenerListaTrabajadoresPorDni("XXXXXX");
            if (porDni.isEmpty()) {
                System.out.println("OK - Correctamente devuelve lista vacía para DNI inexistente.");
            } else {
                System.out.println("AVISO - Se encontraron resultados inesperados.");
            }
        } catch (TrabajadorException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 10: eliminarTrabajadorDni ==========");
        try {
            boolean eliminado = eliminarTrabajadorDni("44444444D");
            if (eliminado) {
                System.out.println("OK - Trabajador con DNI '44444444D' eliminado.");
            } else {
                System.out.println("AVISO - No se encontró el trabajador.");
            }
        } catch (BDException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 11: eliminarTrabajadorDni (DNI inexistente) ==========");
        try {
            boolean eliminado = eliminarTrabajadorDni("99999999Z");
            if (!eliminado) {
                System.out.println("OK - Correctamente devuelve false para DNI inexistente.");
            } else {
                System.out.println("AVISO - Se eliminó algo inesperado.");
            }
        } catch (BDException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 12: eliminarTrabajadorId ==========");
        try {
            // Primero obtenemos un ID real de la BD
            List<Trabajador> todos = obtenerTrabajadoresBaseDatos();
            if (!todos.isEmpty()) {
                int idAEliminar = todos.get(0).getIdentificador();
                boolean eliminado = eliminarTrabajadorId(idAEliminar);
                if (eliminado) {
                    System.out.println("OK - Trabajador con ID " + idAEliminar + " eliminado.");
                } else {
                    System.out.println("AVISO - No se encontró el trabajador con ese ID.");
                }
            } else {
                System.out.println("AVISO - No hay trabajadores en BD para probar eliminarPorId.");
            }
        } catch (BDException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("\n========== TEST 13: eliminarTrabajadorId (ID inexistente) ==========");
        try {
            boolean eliminado = eliminarTrabajadorId(-1);
            if (!eliminado) {
                System.out.println("OK - Correctamente devuelve false para ID inexistente.");
            } else {
                System.out.println("AVISO - Se eliminó algo inesperado.");
            }
        } catch (BDException e) {
            System.out.println("ERROR - " + e.getMessage());
        }

        System.out.println("========== TEST: actualizarListaTrabajadoresPorDni ==========");


// ── TEST 1: Lista con DNIs existentes → debe actualizar todos ────────────
        System.out.println("\n--- TEST 1: Todos los DNIs existen ---");
        try {
            List<Trabajador> listaValida = new ArrayList<>();
            listaValida.add(new Trabajador(0, "12345678A", "Carlos", "García Nuevo", "Calle Actualizada 1", "600000001", "CTO"));
            listaValida.add(new Trabajador(0, "22222222B", "Ana", "Martínez Nueva", "Calle Actualizada 2", "600000002", "CEO"));
            listaValida.add(new Trabajador(0, "33333333C", "Luis", "Sánchez Nuevo", "Calle Actualizada 3", "600000003", "CFO"));

            int actualizados = actualizarListaTrabajadoresPorDni(listaValida);
            System.out.println("OK - Trabajadores actualizados: " + actualizados + " de " + listaValida.size());

        } catch (BDException | TrabajadorException e) {
            System.out.println("ERROR - " + e.getMessage());
        }


// ── TEST 2: Lista con DNIs que NO existen → lanza TrabajadorException ────
        System.out.println("\n--- TEST 2: Ningún DNI existe ---");
        try {
            List<Trabajador> listaInexistente = new ArrayList<>();
            listaInexistente.add(new Trabajador(0, "00000000X", "Fantasma", "Nadie", "Sin dirección", "000000000", "Ninguno"));
            listaInexistente.add(new Trabajador(0, "99999999Z", "Otro", "Nadie", "Sin dirección", "000000001", "Ninguno"));

            int actualizados = actualizarListaTrabajadoresPorDni(listaInexistente);
            System.out.println("AVISO - Debería haber lanzado TrabajadorException. Resultado: " + actualizados);

        } catch (TrabajadorException e) {
            System.out.println("OK - Excepción esperada al no encontrar ningún DNI: " + e.getMessage());
        } catch (BDException e) {
            System.out.println("ERROR BD - " + e.getMessage());
        }


// ── TEST 3: Lista mixta (algunos DNIs existen, otros no) ─────────────────
        System.out.println("\n--- TEST 3: Lista mixta (DNIs válidos e inválidos) ---");
        try {
            List<Trabajador> listaMixta = new ArrayList<>();
            listaMixta.add(new Trabajador(0, "12345678A", "Carlos", "García Mix", "Calle Mix 1", "611000001", "Scrum Master")); // Existe
            listaMixta.add(new Trabajador(0, "55555555E", "Nadie", "Inventado", "Calle Mix 2", "611000002", "Ninguno"));      // No existe

            int actualizados = actualizarListaTrabajadoresPorDni(listaMixta);
            // Solo actualizará 1, el aviso del DNI no encontrado se imprime dentro del método
            System.out.println("OK - Actualizados: " + actualizados + " de " + listaMixta.size() + " (1 DNI no existía)");

        } catch (BDException | TrabajadorException e) {
            System.out.println("ERROR - " + e.getMessage());
        }


// ── TEST 4: Lista vacía → no entra al bucle, lanza TrabajadorException ───
        System.out.println("\n--- TEST 4: Lista vacía ---");
        try {
            List<Trabajador> listaVacia = new ArrayList<>();

            int actualizados = actualizarListaTrabajadoresPorDni(listaVacia);
            System.out.println("AVISO - Debería haber lanzado TrabajadorException. Resultado: " + actualizados);

        } catch (TrabajadorException e) {
            System.out.println("OK - Excepción esperada con lista vacía: " + e.getMessage());
        } catch (BDException e) {
            System.out.println("ERROR BD - " + e.getMessage());
        }


        System.out.println("\n========== FIN TEST actualizarListaTrabajadoresPorDni ==========");
    }

}