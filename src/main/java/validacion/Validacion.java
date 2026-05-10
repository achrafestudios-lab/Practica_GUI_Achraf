package validacion;

public class Validacion {

    private static final String LETRAS_DNI = "TRWAGMYFPDXBNJZSQVHLCKE";

    /**
     * Verifica el DNI.
     *
     * @param dni DNI a validar (8 dígitos + letra)
     * @return 0 si es válido, 1 si es null/vacío, 2 si longitud != 9,
     * 3 si los primeros 8 caracteres no son dígitos, 4 si la letra no coincide
     */
    public static int verificarDni(String dni) {
        if (dni == null || dni.isEmpty()) {
            return 1;
        }

        if (dni.length() != 9) {
            return 2;
        }

        String numeroStr = dni.substring(0, 8);
        char letra = Character.toUpperCase(dni.charAt(8));

        int numero;
        try {
            numero = Integer.parseInt(numeroStr);
        } catch (Exception e) {
            return 3;
        }

        int resto = numero % 23;
        char letraEsperada = LETRAS_DNI.charAt(resto);

        if (letra != letraEsperada) {
            return 4;
        }
        return 0;
    }

    /**
     * Valida un número de teléfono de 9 dígitos.
     *
     * @param telefono Teléfono a validar
     * @return 0 si es válido, 1 si es null, 2 si está vacío,
     * 3 si longitud != 9, 4 si contiene caracteres no numéricos
     */
    public static int validarTelefono(String telefono) {
        if (telefono == null) {
            return 1;
        }

        if (telefono.isEmpty()) {
            return 2;
        }

        if (telefono.length() != 9) {
            return 3;
        }

        for (int i = 0; i < telefono.length(); i++) {
            if (!Character.isDigit(telefono.charAt(i))) {
                return 4;
            }
        }

        return 0;
    }
}
