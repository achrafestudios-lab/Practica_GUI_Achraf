CREATE TABLE trabajador (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            dni VARCHAR(20) NOT NULL UNIQUE,
                            nombre VARCHAR(50) NOT NULL,
                            apellidos VARCHAR(100) NOT NULL,
                            direccion VARCHAR(255) NOT NULL,
                            telefono VARCHAR(20) NOT NULL,
                            puesto VARCHAR(50) NOT NULL
);