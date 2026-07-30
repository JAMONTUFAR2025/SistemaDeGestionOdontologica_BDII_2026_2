DROP DATABASE soe_odontologia_final;

CREATE DATABASE IF NOT EXISTS soe_odontologia_final;
USE soe_odontologia_final;

-- ==========================================
-- 1. TABLAS CATÁLOGO E INDEPENDIENTES
-- ==========================================

CREATE TABLE Especialidades (
    id_especialidades INT AUTO_INCREMENT,
    nombre_especialidad VARCHAR(100) NOT NULL,
    
    -- Restricciones
    CONSTRAINT PK_Especialidades PRIMARY KEY (id_especialidades)
);

CREATE TABLE Catalogo_Alergias (
    id_catalogo_alergias INT AUTO_INCREMENT,
    nombre_alergia VARCHAR(100) NOT NULL,
    
    -- Restricciones
    CONSTRAINT PK_Catalogo_Alergias PRIMARY KEY (id_catalogo_alergias)
);

CREATE TABLE Catalogo_Procedimientos (
    id_catalogo_procedimientos INT AUTO_INCREMENT,
    nombre_procedimiento VARCHAR(150) NOT NULL,
    precio_sugerido DECIMAL(10,2),
    
    -- Restricciones
    CONSTRAINT PK_Catalogo_Procedimientos PRIMARY KEY (id_catalogo_procedimientos),
    CONSTRAINT CHK_CatProc_Precio CHECK (precio_sugerido >= 0)
);

CREATE TABLE Egresos_Gastos (
    id_egresos_gastos INT AUTO_INCREMENT,
    fecha DATE NOT NULL,
    descripcion TEXT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    numero_comprobante VARCHAR(100),
    borrado ENUM('Si', 'No') DEFAULT 'No',
    fecha_borrado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Egresos_Gastos PRIMARY KEY (id_egresos_gastos),
    CONSTRAINT CHK_Egresos_Monto CHECK (monto >= 0)
);

-- ==========================================
-- 2. TABLAS DE USUARIOS Y PACIENTES
-- ==========================================

CREATE TABLE Personal_Medico (
    id_personal_medico INT AUTO_INCREMENT,
    identidad VARCHAR(20) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    id_especialidades INT,
    telefono VARCHAR(20),
    correo VARCHAR(100),
    borrado ENUM('Si', 'No') DEFAULT 'No',
    fecha_borrado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Personal_Medico PRIMARY KEY (id_personal_medico),
    CONSTRAINT UQ_PersonalMedico_Identidad UNIQUE (identidad),
    CONSTRAINT FK_PersMedico_Especialidad FOREIGN KEY (id_especialidades) REFERENCES Especialidades(id_especialidades)
);

CREATE TABLE Usuarios_Login (
    id_usuarios_login INT AUTO_INCREMENT,
    correo VARCHAR(100) NOT NULL,
    contrasenia VARCHAR(255) NOT NULL,
    id_personal_medico INT NULL,
    rol_sistema ENUM('Administrador', 'Recepcionista', 'Medico') NOT NULL,
    borrado ENUM('Si', 'No') DEFAULT 'No',
    fecha_borrado DATETIME NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT PK_Usuarios_Login PRIMARY KEY (id_usuarios_login),
    CONSTRAINT UQ_Usuarios_Correo UNIQUE (correo),
    CONSTRAINT FK_Usuarios_PersMedico FOREIGN KEY (id_personal_medico) REFERENCES Personal_Medico(id_personal_medico)
);

CREATE TABLE Pacientes (
    id_pacientes INT AUTO_INCREMENT,
    identidad VARCHAR(20),
    nombre_completo VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    genero ENUM('M', 'F'),
    ocupacion VARCHAR(100),
    domicilio VARCHAR(255),
    telefono VARCHAR(20),
    persona_responsable VARCHAR(100),
    telefono_responsable VARCHAR(20),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    borrado ENUM('Si', 'No') DEFAULT 'No',
    fecha_borrado DATETIME NULL,
    estado_civil VARCHAR(30),
    
    -- Restricciones
    CONSTRAINT PK_Pacientes PRIMARY KEY (id_pacientes),
    CONSTRAINT UQ_Pacientes_Identidad UNIQUE (identidad)
);

-- ==========================================
-- 3. TABLAS RELACIONALES (DEPENDIENTES)
-- ==========================================

CREATE TABLE Paciente_Alergias (
    id_paciente_alergias INT AUTO_INCREMENT,
    id_pacientes INT NOT NULL,
    id_catalogo_alergias INT NOT NULL,
    
    -- Restricciones
    CONSTRAINT PK_Paciente_Alergias PRIMARY KEY (id_paciente_alergias),
    CONSTRAINT UQ_PacAlergia_EvitarDuplicado UNIQUE (id_pacientes, id_catalogo_alergias),
    CONSTRAINT FK_PacAlergias_Paciente FOREIGN KEY (id_pacientes) REFERENCES Pacientes(id_pacientes),
    CONSTRAINT FK_PacAlergias_Alergia FOREIGN KEY (id_catalogo_alergias) REFERENCES Catalogo_Alergias(id_catalogo_alergias)
);

CREATE TABLE Citas (
    id_citas INT AUTO_INCREMENT,
    id_pacientes INT NOT NULL,
    id_personal_medico INT NULL,
    fecha_hora DATETIME NOT NULL,
    motivo_cita VARCHAR(255),
    estado ENUM('Programada', 'Confirmada', 'Completada', 'Cancelada', 'Ausente') DEFAULT 'Programada',
    
    -- Restricciones
    CONSTRAINT PK_Citas PRIMARY KEY (id_citas),
    CONSTRAINT FK_Citas_Paciente FOREIGN KEY (id_pacientes) REFERENCES Pacientes(id_pacientes),
    CONSTRAINT FK_Citas_Medico FOREIGN KEY (id_personal_medico) REFERENCES Personal_Medico(id_personal_medico)
);

CREATE TABLE Expediente_Base (
    id_expediente_base INT AUTO_INCREMENT,
    id_pacientes INT NOT NULL,
    remitido_por VARCHAR(100),
    antecedentes_patologicos TEXT,
    antecedentes_odontologicos TEXT,
    antecedentes_quirurgicos TEXT,
    antecedentes_ginecobstetros VARCHAR(255),
    habitos_toxicos VARCHAR(255),
    farmacos_uso_habitual TEXT,
    reaccion_anestesicos ENUM('Si', 'No'),
    especifique_anestesia TEXT,
    complicaciones_tratamientos_previos TEXT,
    habitos_bucales VARCHAR(100),
    frecuencia_cepillado VARCHAR(50),
    tipo_cepillo_cerdas ENUM('Duras', 'Suaves', 'Moderadas'),
    uso_hilo_dental ENUM('Si', 'A veces', 'No'),
    tipo_mordida ENUM('Clase I', 'Clase II', 'Clase III'),
    diagnostico_presuntivo TEXT,
    observaciones_generales TEXT,
    
    -- Restricciones
    CONSTRAINT PK_Expediente_Base PRIMARY KEY (id_expediente_base),
    CONSTRAINT UQ_Expediente_Paciente UNIQUE (id_pacientes),
    CONSTRAINT FK_Expediente_Paciente FOREIGN KEY (id_pacientes) REFERENCES Pacientes(id_pacientes)
);

CREATE TABLE Expediente_Archivos (
    id_expediente_archivos INT AUTO_INCREMENT,
    id_pacientes INT NOT NULL,
    tipo_archivo ENUM('Radiografia', 'Fotografia', 'Laboratorio', 'Otro') NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    observaciones TEXT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    borrado ENUM('Si', 'No') DEFAULT 'No',
    fecha_borrado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Expediente_Archivos PRIMARY KEY (id_expediente_archivos),
    CONSTRAINT FK_ExpArchivos_Paciente FOREIGN KEY (id_pacientes) REFERENCES Pacientes(id_pacientes)
);

CREATE TABLE Evolucion_Clinica (
    id_evolucion_clinica INT AUTO_INCREMENT,
    id_pacientes INT NOT NULL,
    id_expediente_base INT NOT NULL,
    id_citas INT NULL,
    id_personal_medico INT NOT NULL,
    numero_cita INT NOT NULL,
    fecha_consulta DATETIME NOT NULL,
    motivo_consulta TEXT,
    sintoma_principal TEXT,
    presion_arterial VARCHAR(20),
    pulso_cardiaco VARCHAR(20),
    temperatura VARCHAR(20),
    tejidos_blandos_observacion TEXT,
    diagnostico TEXT,
    id_catalogo_procedimientos INT,
    estado_odontograma LONGTEXT,
    pago_abono DECIMAL(10,2),
    observaciones TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Restricciones
    CONSTRAINT PK_Evolucion_Clinica PRIMARY KEY (id_evolucion_clinica),
    CONSTRAINT FK_EvoClinica_Paciente FOREIGN KEY (id_pacientes) REFERENCES Pacientes(id_pacientes),
    CONSTRAINT FK_EvoClinica_Expediente FOREIGN KEY (id_expediente_base) REFERENCES Expediente_Base(id_expediente_base),
    CONSTRAINT FK_EvoClinica_Cita FOREIGN KEY (id_citas) REFERENCES Citas(id_citas),
    CONSTRAINT FK_EvoClinica_Medico FOREIGN KEY (id_personal_medico) REFERENCES Personal_Medico(id_personal_medico),
    CONSTRAINT FK_EvoClinica_CatProc FOREIGN KEY (id_catalogo_procedimientos) REFERENCES Catalogo_Procedimientos(id_catalogo_procedimientos),
    CONSTRAINT CHK_EvoClinica_NumCita CHECK (numero_cita > 0),
    CONSTRAINT CHK_EvoClinica_Abono CHECK (pago_abono >= 0)
);

CREATE TABLE Consentimientos_Informados (
    id_consentimientos_informados INT AUTO_INCREMENT,
    id_evolucion_clinica INT NOT NULL,
    tipo_procedimiento ENUM('Cirugia Bucal', 'Endodoncia', 'Otro') NOT NULL,
    representante_legal VARCHAR(100),
    identidad_representante VARCHAR(20),
    fecha_firma DATE NOT NULL,
    
    -- Restricciones
    CONSTRAINT PK_Consentimientos PRIMARY KEY (id_consentimientos_informados),
    CONSTRAINT FK_Consent_Evolucion FOREIGN KEY (id_evolucion_clinica) REFERENCES Evolucion_Clinica(id_evolucion_clinica)
);

CREATE TABLE Constancias_Medicas (
    id_constancias_medicas INT AUTO_INCREMENT,
    id_evolucion_clinica INT NOT NULL,
    fecha_emision DATE NOT NULL,
    hora_emision TIME NOT NULL,
    tratamiento_realizado TEXT NOT NULL,
    
    -- Restricciones
    CONSTRAINT PK_Constancias_Medicas PRIMARY KEY (id_constancias_medicas),
    CONSTRAINT FK_Constancias_Evolucion FOREIGN KEY (id_evolucion_clinica) REFERENCES Evolucion_Clinica(id_evolucion_clinica)
);

CREATE TABLE Facturacion_Recibos (
    id_facturacion_recibos INT AUTO_INCREMENT,
    numero_recibo VARCHAR(50),
    id_pacientes INT NOT NULL,
    rtn_cliente VARCHAR(20),
    fecha_emision DATE NOT NULL,
    concepto TEXT NOT NULL,
    suma_neta DECIMAL(10,2),
    total_honorarios DECIMAL(10,2),
    total_retenido DECIMAL(10,2),
    total_neto_recibido DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM('Efectivo', 'Transferencia', 'POS') NOT NULL,
    borrado ENUM('Si', 'No') DEFAULT 'No',
    fecha_borrado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Facturacion_Recibos PRIMARY KEY (id_facturacion_recibos),
    CONSTRAINT FK_Facturacion_Paciente FOREIGN KEY (id_pacientes) REFERENCES Pacientes(id_pacientes),
    CONSTRAINT CHK_Facturacion_TotalNeto CHECK (total_neto_recibido >= 0)
);

-- ========================================== 
-- 4. DATOS INICIALES (INSERTS)
-- ==========================================

INSERT INTO Usuarios_Login (correo, contrasenia, rol_sistema, borrado) 
VALUES ('erickfernandochavezcardona@gmail.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador', 'No');

INSERT INTO Especialidades (nombre_especialidad) VALUES 
('Odontologa General'),
('Ortodoncia'),
('Endodoncia'),
('Periodoncia'),
('Cirugia Bucal / Maxilofacial'),
('Odontopediatra'),
('Prostodoncia / Rehabilitacion Oral');