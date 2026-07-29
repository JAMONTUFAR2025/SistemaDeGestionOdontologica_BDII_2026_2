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

ALTER TABLE Egresos_Gastos
ADD COLUMN estado ENUM('Activo','Inactivo') DEFAULT 'Activo' AFTER numero_comprobante;

ALTER TABLE Egresos_Gastos
ADD COLUMN fecha_inactivacion DATETIME NULL AFTER estado;

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
VALUES ('erickfernandochavezcardona@gmail.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador', 'Activo');

ALTER TABLE Egresos_Gastos
ADD COLUMN estado ENUM('Activo','Inactivo') DEFAULT 'Activo' AFTER numero_comprobante;

ALTER TABLE Egresos_Gastos
ADD COLUMN fecha_inactivacion DATETIME NULL AFTER estado;


-- ========================================== 
-- CAT�LOGO DE ESPECIALIDADES M�DICAS 
-- (Precargadas para que aparezcan al registrar doctores)
-- ==========================================
INSERT INTO Especialidades (nombre_especialidad) VALUES 
('Odontologa General'),
('Ortodoncia'),
('Endodoncia'),
('Periodoncia'),
('Cirugia Bucal / Maxilofacial'),
('Odontopediatra'),
('Prostodoncia / Rehabilitacion Oral');


-- ==========================================
-- ACTUALIZACIONES RECIENTES (AGREGADAS AL FINAL)
-- ==========================================

ALTER TABLE Pacientes ADD COLUMN estado_civil varchar(30);


ALTER TABLE Egresos_Gastos
ADD COLUMN estado ENUM('Activo','Inactivo') DEFAULT 'Activo' AFTER numero_comprobante;

ALTER TABLE Egresos_Gastos
ADD COLUMN fecha_inactivacion DATETIME NULL AFTER estado;

ALTER TABLE Expediente_Base
ADD COLUMN diagnostico_presuntivo TEXT AFTER tipo_mordida,
ADD COLUMN observaciones_generales TEXT AFTER diagnostico_presuntivo;

CREATE TABLE Expediente_Archivos (
    id_archivo INT AUTO_INCREMENT PRIMARY KEY,
    identidad_paciente VARCHAR(20) NOT NULL,
    tipo_archivo ENUM('Radiografia', 'Fotografia', 'Laboratorio', 'Otro') NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL, -- Aquí se guardará la ruta de la carpeta local (ej: C:\SOE_Archivos\...)
    observaciones TEXT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('Activo', 'Inactivo') DEFAULT 'Activo',
    fecha_inactivacion DATETIME NULL,
    FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad)
);

-- ==========================================
-- ACTUALIZACIONES: MODIFICAR PERSONAL_MEDICO Y PACIENTES
-- ==========================================

-- Nota: Reemplazar los nombres de las llaves foraneas (ibfk) por los reales de la base de datos si son diferentes.
ALTER TABLE Usuarios_Login DROP FOREIGN KEY Usuarios_Login_ibfk_1;
ALTER TABLE Citas DROP FOREIGN KEY Citas_ibfk_2;
ALTER TABLE Evolucion_Clinica DROP FOREIGN KEY Evolucion_Clinica_ibfk_4;

ALTER TABLE Personal_Medico MODIFY id_medico INT NOT NULL;
ALTER TABLE Personal_Medico DROP PRIMARY KEY;
ALTER TABLE Personal_Medico DROP COLUMN id_medico;
ALTER TABLE Personal_Medico DROP COLUMN rol;
ALTER TABLE Personal_Medico ADD PRIMARY KEY (identidad);

ALTER TABLE Usuarios_Login CHANGE id_medico identidad_medico VARCHAR(20) NULL;
ALTER TABLE Usuarios_Login ADD FOREIGN KEY (identidad_medico) REFERENCES Personal_Medico(identidad);

ALTER TABLE Citas CHANGE id_medico identidad_medico VARCHAR(20) NULL;
ALTER TABLE Citas ADD FOREIGN KEY (identidad_medico) REFERENCES Personal_Medico(identidad);

ALTER TABLE Evolucion_Clinica CHANGE id_medico identidad_medico VARCHAR(20) NOT NULL;
ALTER TABLE Evolucion_Clinica ADD FOREIGN KEY (identidad_medico) REFERENCES Personal_Medico(identidad);

-- Modificar Paciente: Mover columna estado antes de fecha_inactivacion
ALTER TABLE Pacientes MODIFY COLUMN estado ENUM('Activo','Inactivo') DEFAULT 'Activo' AFTER fecha_registro;

-- 1. Eliminar llaves foráneas que referencian a Pacientes(identidad)
ALTER TABLE Paciente_Alergias DROP FOREIGN KEY Paciente_Alergias_ibfk_1;
ALTER TABLE Citas DROP FOREIGN KEY Citas_ibfk_1;
ALTER TABLE Expediente_Base DROP FOREIGN KEY Expediente_Base_ibfk_1;
ALTER TABLE Evolucion_Clinica DROP FOREIGN KEY Evolucion_Clinica_ibfk_1;
ALTER TABLE Facturacion_Recibos DROP FOREIGN KEY Facturacion_Recibos_ibfk_1;

-- 2. Modificar Pacientes para que id_paciente sea la clave primaria e identidad sea UNIQUE
ALTER TABLE Pacientes DROP PRIMARY KEY;
ALTER TABLE Pacientes ADD COLUMN id_paciente INT AUTO_INCREMENT PRIMARY KEY FIRST;
ALTER TABLE Pacientes ADD UNIQUE (identidad);

-- 3. Recrear las llaves foráneas apuntando a Pacientes(identidad) como campo UNIQUE
ALTER TABLE Paciente_Alergias ADD FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad);
ALTER TABLE Citas ADD FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad);
ALTER TABLE Expediente_Base ADD FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad);
ALTER TABLE Evolucion_Clinica ADD FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad);
ALTER TABLE Facturacion_Recibos ADD FOREIGN KEY (identidad_paciente) REFERENCES Pacientes(identidad);
