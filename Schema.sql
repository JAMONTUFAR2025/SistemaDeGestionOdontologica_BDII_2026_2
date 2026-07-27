CREATE DATABASE IF NOT EXISTS soe_odontologia_final;
USE soe_odontologia_final;


CREATE TABLE Especialidades (
    id_especialidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre_especialidad VARCHAR(100) NOT NULL
);

CREATE TABLE Catalogo_Alergias (
    id_alergia INT AUTO_INCREMENT PRIMARY KEY,
    nombre_alergia VARCHAR(100) NOT NULL
);

CREATE TABLE Catalogo_Procedimientos (
    id_cat_procedimiento INT AUTO_INCREMENT PRIMARY KEY,
    nombre_procedimiento VARCHAR(150) NOT NULL,
    precio_sugerido DECIMAL(10,2)
);


CREATE TABLE Personal_Medico (
    id_medico INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    identidad VARCHAR(20) UNIQUE,
    rol ENUM('Administrador', 'Operativo', 'Asistente') NOT NULL,
    id_especialidad INT,
    telefono VARCHAR(20),
    correo VARCHAR(100),
    estado ENUM('Activo', 'Inactivo') DEFAULT 'Activo',
    FOREIGN KEY (id_especialidad) REFERENCES Especialidades(id_especialidad)
);

CREATE TABLE Usuarios_Login (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(100) UNIQUE NOT NULL,
    contrasenia VARCHAR(255) NOT NULL,
    id_medico INT NULL,
    rol_sistema ENUM('Administrador', 'Recepcionista', 'Medico') NOT NULL,
    estado ENUM('Activo', 'Inactivo') DEFAULT 'Activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_medico) REFERENCES Personal_Medico(id_medico)
);

CREATE TABLE Pacientes (
    identidad VARCHAR(20) PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    genero ENUM('M', 'F'),
    estado_civil VARCHAR(30),
    ocupacion VARCHAR(100),
    domicilio VARCHAR(255),
    telefono VARCHAR(20),
    persona_responsable VARCHAR(100),
    telefono_responsable VARCHAR(20),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Paciente_Alergias (
    identidad_paciente VARCHAR(20),
    id_alergia INT,
    PRIMARY KEY (identidad_paciente, id_alergia),
    FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad),
    FOREIGN KEY (id_alergia) REFERENCES Catalogo_Alergias(id_alergia)
);


CREATE TABLE Citas (
    id_cita INT AUTO_INCREMENT PRIMARY KEY,
    identidad_paciente VARCHAR(20),
    id_medico INT,
    fecha_hora DATETIME NOT NULL,
    motivo_cita VARCHAR(255),
    estado ENUM('Programada', 'Confirmada', 'Completada', 'Cancelada', 'Ausente') DEFAULT 'Programada',
    FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad),
    FOREIGN KEY (id_medico) REFERENCES Personal_Medico(id_medico)
);



CREATE TABLE Expediente_Base (
    id_expediente INT AUTO_INCREMENT PRIMARY KEY,
    identidad_paciente VARCHAR(20) UNIQUE NOT NULL,
    remitido_por VARCHAR(100),
    antecedentes_patologicos TEXT,
    antecedentes_odontologicos TEXT,
    antecedentes_quirurgicos TEXT,
    antecedentes_ginecobstetros VARCHAR(255),
    habitos_toxicos VARCHAR(255),
    farmacos_uso_habitual TEXT,
    reaccion_anestesicos BOOLEAN,
    especifique_anestesia TEXT,
    complicaciones_tratamientos_previos TEXT,
    habitos_bucales VARCHAR(100),
    frecuencia_cepillado VARCHAR(50),
    tipo_cepillo_cerdas ENUM('Duras', 'Suaves', 'Moderadas'),
    uso_hilo_dental ENUM('Si', 'A veces', 'No'),
    tipo_mordida ENUM('Clase I', 'Clase II', 'Clase III'),
    FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad)
);


CREATE TABLE Evolucion_Clinica (
    id_evolucion INT AUTO_INCREMENT PRIMARY KEY,
    identidad_paciente VARCHAR(20) NOT NULL,
    id_expediente INT NOT NULL,
    id_cita INT NULL,
    id_medico INT NOT NULL,

    numero_cita INT NOT NULL,

    -- Anamnesis y Signos Vitales
    fecha_consulta DATETIME NOT NULL,
    motivo_consulta TEXT,
    sintoma_principal TEXT,
    presion_arterial VARCHAR(20),
    pulso_cardiaco VARCHAR(20),
    temperatura VARCHAR(20),
    tejidos_blandos_observacion TEXT,

    -- Diagnostico y Tratamiento
    diagnostico TEXT,
    tratamiento_realizado TEXT,
    estado_odontograma LONGTEXT,

    -- Extraido de la hoja fisica de procedimientos
    pago_abono DECIMAL(10,2),

    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Integridad Referencial
    FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad),
    FOREIGN KEY (id_expediente) REFERENCES Expediente_Base(id_expediente),
    FOREIGN KEY (id_cita) REFERENCES Citas(id_cita),
    FOREIGN KEY (id_medico) REFERENCES Personal_Medico(id_medico)
);


CREATE TABLE Consentimientos_Informados (
    id_consentimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_evolucion INT NOT NULL, -- Amarrado a la consulta especifica
    tipo_procedimiento ENUM('Cirugia Bucal', 'Endodoncia', 'Otro') NOT NULL,
    representante_legal VARCHAR(100),
    identidad_representante VARCHAR(20),
    fecha_firma DATE NOT NULL,
    FOREIGN KEY (id_evolucion) REFERENCES Evolucion_Clinica(id_evolucion)
);

CREATE TABLE Constancias_Medicas (
    id_constancia INT AUTO_INCREMENT PRIMARY KEY,
    id_evolucion INT NOT NULL, -- Amarrado a la consulta especifica
    fecha_emision DATE NOT NULL,
    hora_emision TIME NOT NULL,
    tratamiento_realizado TEXT NOT NULL,
    FOREIGN KEY (id_evolucion) REFERENCES Evolucion_Clinica(id_evolucion)
);


CREATE TABLE Facturacion_Recibos (
    id_factura INT AUTO_INCREMENT PRIMARY KEY,
    numero_recibo VARCHAR(50),
    identidad_paciente VARCHAR(20),
    rtn_cliente VARCHAR(20),
    fecha_emision DATE NOT NULL,
    concepto TEXT NOT NULL,

    -- Desglose exacto extraido de la imagen del talonario SAR
    suma_neta DECIMAL(10,2),
    total_honorarios DECIMAL(10,2),
    total_retenido DECIMAL(10,2),
    total_neto_recibido DECIMAL(10,2) NOT NULL,

    metodo_pago ENUM('Efectivo', 'Transferencia', 'POS') NOT NULL,
    FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad)
);

CREATE TABLE Egresos_Gastos (
    id_egreso INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    descripcion TEXT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    numero_comprobante VARCHAR(100)
);

ALTER TABLE Evolucion_Clinica
DROP COLUMN tratamiento_realizado;


ALTER TABLE Evolucion_Clinica
ADD COLUMN id_cat_procedimiento INT AFTER diagnostico,
ADD FOREIGN KEY (id_cat_procedimiento) REFERENCES Catalogo_Procedimientos(id_cat_procedimiento);

ALTER TABLE Pacientes
DROP COLUMN estado_civil;

ALTER TABLE Pacientes
ADD COLUMN estado ENUM('Activo','Inactivo') DEFAULT 'Activo' AFTER fecha_registro;

ALTER TABLE Facturacion_Recibos
ADD COLUMN estado ENUM ('Valida','Anulada') DEFAULT 'Valida' AFTER metodo_pago;

ALTER TABLE Personal_Medico
ADD COLUMN fecha_inactivacion DATETIME NULL AFTER estado;

ALTER TABLE Usuarios_Login
ADD COLUMN fecha_inactivacion DATETIME NULL AFTER estado;

ALTER TABLE Pacientes
ADD COLUMN fecha_inactivacion DATETIME NULL AFTER fecha_registro;

ALTER TABLE Facturacion_Recibos
ADD COLUMN fecha_inactivacion DATETIME NULL AFTER metodo_pago;

-- ========================================== 
-- USUARIO ADMINISTRADOR POR DEFECTO 
-- INSTRUCCIONES: Cambia 'INGRESA_TU_CORREO_AQUI' por tu correo real de Gmail.
-- Esto te servira para probar la recuperacion de contrasenia.
-- ==========================================
INSERT INTO Usuarios_Login (correo, contrasenia, rol_sistema, estado) 
VALUES ('erickfernandochavezcardona@gmail.com', 'admin123', 'Administrador', 'Activo');

