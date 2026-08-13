DROP DATABASE IF EXISTS soe_odontologia_final;
CREATE DATABASE IF NOT EXISTS soe_odontologia_final;
USE soe_odontologia_final;

-- ==========================================
-- 1. TABLAS CATÁLOGO E INDEPENDIENTES
-- ==========================================

CREATE TABLE Especialidades (
    id_especialidad INT AUTO_INCREMENT,
    nombre_especialidad VARCHAR(100) NOT NULL,
    
    -- Restricciones
    CONSTRAINT PK_Especialidades PRIMARY KEY (id_especialidad),
    CONSTRAINT UQ_Especialidad_Nombre UNIQUE (nombre_especialidad)
);

CREATE TABLE Catalogo_Alergias (
    id_catalogo_alergia INT AUTO_INCREMENT,
    nombre_alergia VARCHAR(100) NOT NULL,
    
    -- Restricciones
    CONSTRAINT PK_Catalogo_Alergias PRIMARY KEY (id_catalogo_alergia),
    CONSTRAINT UQ_Alergia_Nombre UNIQUE (nombre_alergia)
);

CREATE TABLE Catalogo_Procedimientos (
    id_catalogo_procedimiento INT AUTO_INCREMENT,
    nombre_procedimiento VARCHAR(150) NOT NULL,
    precio_sugerido DECIMAL(10,2),
    
    -- Restricciones
    CONSTRAINT PK_Catalogo_Procedimientos PRIMARY KEY (id_catalogo_procedimiento),
    CONSTRAINT UQ_Procedimiento_Nombre UNIQUE (nombre_procedimiento),
    CONSTRAINT CHK_CatProc_Precio CHECK (precio_sugerido >= 0)
);

-- ==========================================
-- 2. TABLAS DE USUARIOS, RESPONSABLES Y PACIENTES
-- ==========================================

CREATE TABLE Personal_Medico (
    id_personal_medico INT AUTO_INCREMENT,
    identidad VARCHAR(20) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    id_especialidad INT NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    borrado BOOLEAN DEFAULT FALSE,
    fecha_borrado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Personal_Medico PRIMARY KEY (id_personal_medico),
    CONSTRAINT UQ_PersonalMedico_Identidad UNIQUE (identidad),
    CONSTRAINT UQ_PersonalMedico_Correo UNIQUE (correo),
    CONSTRAINT FK_PersMedico_Especialidad FOREIGN KEY (id_especialidad) REFERENCES Especialidades(id_especialidad)
);

CREATE TABLE Usuarios_Login (
    id_usuario_login INT AUTO_INCREMENT,
    nombre_usuario VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    contrasenia VARCHAR(255) NOT NULL,
    id_personal_medico INT NULL,
    rol_sistema ENUM('Administrador', 'Recepcionista', 'Medico') NOT NULL,
    borrado BOOLEAN DEFAULT FALSE,
    fecha_borrado DATETIME NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Restricciones
    CONSTRAINT PK_Usuarios_Login PRIMARY KEY (id_usuario_login),
    CONSTRAINT UQ_Usuarios_NombreUsuario UNIQUE (nombre_usuario),
    CONSTRAINT UQ_Usuarios_Correo UNIQUE (correo),
    CONSTRAINT UQ_Usuarios_PersonalMedico UNIQUE (id_personal_medico),
    CONSTRAINT FK_Usuarios_PersMedico FOREIGN KEY (id_personal_medico) REFERENCES Personal_Medico(id_personal_medico)
);

CREATE TABLE Responsables (
    id_responsable INT AUTO_INCREMENT,
    identidad VARCHAR(20),
    nombre_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(100),
    parentesco ENUM('Padre', 'Madre', 'Tutor Legal', 'Conyuge', 'Otro') NOT NULL,
    borrado BOOLEAN DEFAULT FALSE,
    fecha_borrado DATETIME NULL,

    -- Restricciones
    CONSTRAINT PK_Responsables PRIMARY KEY (id_responsable),
    CONSTRAINT UQ_Responsable_Identidad UNIQUE (identidad),
    CONSTRAINT UQ_Responsable_Correo UNIQUE (correo)
);

CREATE TABLE Pacientes (
    id_paciente INT AUTO_INCREMENT,
    nombre_completo VARCHAR(100) NOT NULL,
    genero ENUM('Masculino','Femenino') NOT NULL,
    estado_civil VARCHAR(30) NOT NULL,
    ocupacion VARCHAR(100),
    domicilio VARCHAR(255),
    fecha_nacimiento DATE NOT NULL,
    identidad VARCHAR(20),
    telefono VARCHAR(20),
    id_responsable INT NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    borrado BOOLEAN DEFAULT FALSE,
    fecha_borrado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Pacientes PRIMARY KEY (id_paciente),
    CONSTRAINT UQ_Pacientes_Identidad UNIQUE (identidad),
    CONSTRAINT FK_Pacientes_Responsable FOREIGN KEY (id_responsable) REFERENCES Responsables(id_responsable)
);

-- ==========================================
-- 3. TABLAS RELACIONALES (DEPENDIENTES)
-- ==========================================

CREATE TABLE Caja_Sesiones (
    id_caja_sesion INT AUTO_INCREMENT,
    id_usuario_apertura INT NOT NULL,
    id_usuario_cierre INT NOT NULL,
    monto_apertura DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_cierre_real DECIMAL(10,2) NOT NULL,
    diferencia DECIMAL(10,2) NOT NULL,
    estado ENUM('Abierta', 'Cerrada') NOT NULL DEFAULT 'Abierta',
    fecha_apertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre DATETIME NOT NULL,
    observaciones TEXT NULL,

    -- Restricciones
    CONSTRAINT PK_Caja_Sesiones PRIMARY KEY (id_caja_sesion),
    CONSTRAINT FK_Caja_Usuario_Apertura FOREIGN KEY (id_usuario_apertura) REFERENCES Usuarios_Login(id_usuario_login),
    CONSTRAINT FK_Caja_Usuario_Cierre FOREIGN KEY (id_usuario_cierre) REFERENCES Usuarios_Login(id_usuario_login),
    CONSTRAINT CHK_Caja_Apertura CHECK (monto_apertura >= 0)
);

CREATE TABLE Facturacion (
    id_facturacion_recibo INT AUTO_INCREMENT,
    numero_recibo VARCHAR(50) NOT NULL,
    id_paciente INT NOT NULL,
    id_caja_sesion INT NOT NULL,
    id_usuario_login INT NOT NULL,
    rtn_cliente VARCHAR(20),
    fecha_emision DATE NOT NULL,
    concepto TEXT NOT NULL,
    suma_neta DECIMAL(10,2) NOT NULL,
    total_honorarios DECIMAL(10,2) NOT NULL,
    total_retenido DECIMAL(10,2) NOT NULL,
    total_neto_recibido DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM('Efectivo', 'Transferencia', 'POS') NOT NULL,
    anulado BOOLEAN DEFAULT FALSE,
    fecha_anulado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Facturacion_Recibos PRIMARY KEY (id_facturacion_recibo),
    CONSTRAINT UQ_Facturacion_Recibo UNIQUE (numero_recibo),
    CONSTRAINT CHK_Facturacion_Formato_SAR CHECK (numero_recibo REGEXP '^[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{8}$'),
    CONSTRAINT FK_Facturacion_Paciente FOREIGN KEY (id_paciente) REFERENCES Pacientes(id_paciente),
    CONSTRAINT FK_Facturacion_Caja FOREIGN KEY (id_caja_sesion) REFERENCES Caja_Sesiones(id_caja_sesion) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT FK_Facturacion_Usuario FOREIGN KEY (id_usuario_login) REFERENCES Usuarios_Login(id_usuario_login),
    CONSTRAINT CHK_Facturacion_TotalNeto CHECK (total_neto_recibido >= 0)
);

CREATE TABLE Egresos_Gastos (
    id_egreso_gasto INT AUTO_INCREMENT,
    id_caja_sesion INT NULL,
    id_usuario_login INT NOT NULL,
    fecha DATE NOT NULL,
    descripcion TEXT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM('Efectivo', 'Transferencia', 'POS') NOT NULL DEFAULT 'Efectivo',
    numero_comprobante VARCHAR(100),
    anulado BOOLEAN DEFAULT FALSE,
    fecha_anulado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Egresos_Gastos PRIMARY KEY (id_egreso_gasto),
    CONSTRAINT FK_Egresos_Caja FOREIGN KEY (id_caja_sesion) REFERENCES Caja_Sesiones(id_caja_sesion) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT FK_Egresos_Usuario FOREIGN KEY (id_usuario_login) REFERENCES Usuarios_Login(id_usuario_login),
    CONSTRAINT CHK_Egresos_Monto CHECK (monto >= 0)
);

CREATE TABLE Paciente_Alergias (
    id_paciente_alergia INT AUTO_INCREMENT,
    id_paciente INT NOT NULL,
    id_catalogo_alergia INT NOT NULL,
    
    -- Restricciones
    CONSTRAINT PK_Paciente_Alergias PRIMARY KEY (id_paciente_alergia),
    CONSTRAINT UQ_PacAlergia_EvitarDuplicado UNIQUE (id_paciente, id_catalogo_alergia),
    CONSTRAINT FK_PacAlergias_Paciente FOREIGN KEY (id_paciente) REFERENCES Pacientes(id_paciente),
    CONSTRAINT FK_PacAlergias_Alergia FOREIGN KEY (id_catalogo_alergia) REFERENCES Catalogo_Alergias(id_catalogo_alergia)
);

CREATE TABLE Citas (
    id_cita INT AUTO_INCREMENT,
    id_paciente INT NOT NULL,
    id_personal_medico INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    motivo_cita VARCHAR(255) NOT NULL,
    estado ENUM('Programada', 'Confirmada', 'Completada', 'Cancelada', 'Ausente') NOT NULL DEFAULT 'Programada',
    
    -- Restricciones
    CONSTRAINT PK_Citas PRIMARY KEY (id_cita),
    CONSTRAINT FK_Citas_Paciente FOREIGN KEY (id_paciente) REFERENCES Pacientes(id_paciente),
    CONSTRAINT FK_Citas_Medico FOREIGN KEY (id_personal_medico) REFERENCES Personal_Medico(id_personal_medico)
);

CREATE TABLE Expediente_Base (
    id_expediente_base INT AUTO_INCREMENT,
    id_paciente INT NOT NULL,
    remitido_por VARCHAR(100) NOT NULL,
    antecedentes_patologicos TEXT NOT NULL,
    antecedentes_odontologicos TEXT NOT NULL,
    antecedentes_quirurgicos TEXT NOT NULL,
    antecedentes_ginecobstetros VARCHAR(255) NOT NULL,
    habitos_toxicos VARCHAR(255) NOT NULL,
    farmacos_uso_habitual TEXT NOT NULL,
    reaccion_anestesicos BOOLEAN DEFAULT FALSE,
    especifique_anestesia TEXT NOT NULL,
    complicaciones_tratamientos_previos TEXT NOT NULL,
    habitos_bucales VARCHAR(100) NOT NULL,
    frecuencia_cepillado VARCHAR(50) NOT NULL,
    tipo_cepillo_cerdas ENUM('Duras', 'Suaves', 'Moderadas') NOT NULL,
    uso_hilo_dental ENUM('Si', 'A veces', 'No') NOT NULL,
    tipo_mordida ENUM('Clase I', 'Clase II', 'Clase III') NOT NULL,
    
    -- Restricciones
    CONSTRAINT PK_Expediente_Base PRIMARY KEY (id_expediente_base),
    CONSTRAINT UQ_Expediente_Paciente UNIQUE (id_paciente),
    CONSTRAINT FK_Expediente_Paciente FOREIGN KEY (id_paciente) REFERENCES Pacientes(id_paciente)
);

CREATE TABLE Expediente_Archivos (
    id_expediente_archivo INT AUTO_INCREMENT,
    id_paciente INT NOT NULL,
    tipo_archivo ENUM('Radiografia', 'Fotografia', 'Laboratorio', 'Otro') NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    observaciones TEXT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    borrado BOOLEAN DEFAULT FALSE,
    fecha_borrado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Expediente_Archivos PRIMARY KEY (id_expediente_archivo),
    CONSTRAINT UQ_ExpArchivos_Ruta UNIQUE (ruta_archivo),
    CONSTRAINT FK_ExpArchivos_Paciente FOREIGN KEY (id_paciente) REFERENCES Pacientes(id_paciente)
);

CREATE TABLE Evolucion_Clinica (
    id_evolucion_clinica INT AUTO_INCREMENT,
    id_expediente_base INT NOT NULL,
    id_cita INT NOT NULL,
    id_personal_medico INT NOT NULL,
    numero_cita INT NOT NULL,
    motivo_consulta TEXT NOT NULL,
    sintoma_principal TEXT NOT NULL,
    historia_enfermedad_actual TEXT NOT NULL,
    presion_sistolica INT NOT NULL,
    presion_diastolica INT NOT NULL,
    pulso_cardiaco_bpm INT NOT NULL,
    temperatura_celsius DECIMAL(4,2) NOT NULL,
    tejidos_blandos_observacion TEXT NOT NULL,
    diagnostico TEXT NOT NULL,
    estado_odontograma JSON NOT NULL,
    observaciones TEXT NOT NULL,
    id_catalogo_procedimiento INT NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Restricciones
    CONSTRAINT PK_Evolucion_Clinica PRIMARY KEY (id_evolucion_clinica),
    CONSTRAINT FK_EvoClinica_Expediente FOREIGN KEY (id_expediente_base) REFERENCES Expediente_Base(id_expediente_base),
    CONSTRAINT FK_EvoClinica_Cita FOREIGN KEY (id_cita) REFERENCES Citas(id_cita),
    CONSTRAINT FK_EvoClinica_Medico FOREIGN KEY (id_personal_medico) REFERENCES Personal_Medico(id_personal_medico),
    CONSTRAINT FK_EvoClinica_CatProc FOREIGN KEY (id_catalogo_procedimiento) REFERENCES Catalogo_Procedimientos(id_catalogo_procedimiento),
    CONSTRAINT CHK_EvoClinica_NumCita CHECK (numero_cita > 0)
);

-- DATOS INICIALES
INSERT INTO Usuarios_Login (nombre_usuario, correo, contrasenia, rol_sistema, borrado) 
VALUES ('erikchavez', 'erickfernandochavezcardona@gmail.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador', False);

-- ==========================================
-- 1. INSERTS DE TABLAS CATÁLOGO E INDEPENDIENTES
-- ==========================================

INSERT INTO Especialidades (nombre_especialidad) VALUES
('Odontología General'),
('Ortodoncia'),
('Endodoncia'),
('Periodoncia'),
('Cirugía Maxilofacial');

INSERT INTO Catalogo_Alergias (nombre_alergia) VALUES
('Penicilina'),
('Látex'),
('Aspirina / AINEs'),
('Anestésicos Locales'),
('Ibuprofeno');

INSERT INTO Catalogo_Procedimientos (nombre_procedimiento, precio_sugerido) VALUES
('Limpieza Dental y Profilaxis', 800.00),
('Extracción Dental Simple', 1000.00),
('Obturación con Resina (Empaste)', 1200.00),
('Blanqueamiento LED', 2500.00),
('Tratamiento de Conducto (Endodoncia)', 3500.00);

-- ==========================================
-- 2. INSERTS DE USUARIOS, RESPONSABLES Y PACIENTES
-- ==========================================

INSERT INTO Personal_Medico (identidad, nombre_completo, id_especialidad, telefono, correo, borrado) VALUES
('0801-1980-11111', 'Dr. Carlos Mendoza', 1, '9988-1111', 'cmendoza@clinica.hn', FALSE),
('0801-1985-22222', 'Dra. Ana Suazo', 2, '9988-2222', 'asuazo@clinica.hn', FALSE),
('0501-1990-33333', 'Dr. Luis Ramos', 3, '9988-3333', 'lramos@clinica.hn', FALSE),
('0501-1982-44444', 'Dra. María Paz', 4, '9988-4444', 'mpaz@clinica.hn', FALSE),
('1601-1988-55555', 'Dr. Jorge Valle', 5, '9988-5555', 'jvalle@clinica.hn', FALSE);

INSERT INTO Usuarios_Login (nombre_usuario, correo, contrasenia, id_personal_medico, rol_sistema, borrado) VALUES
('admin_sistema', 'admin@clinica.hn', 'hash_secreto_1', NULL, 'Administrador', FALSE),
('recepcion_01', 'recepcion@clinica.hn', 'hash_secreto_2', NULL, 'Recepcionista', FALSE),
('doc_cmendoza', 'cmendoza@clinica.hn', 'hash_secreto_3', 1, 'Medico', FALSE),
('doc_asuazo', 'asuazo@clinica.hn', 'hash_secreto_4', 2, 'Medico', FALSE),
('doc_lramos', 'lramos@clinica.hn', 'hash_secreto_5', 3, 'Medico', FALSE);

INSERT INTO Responsables (identidad, nombre_completo, telefono, correo, parentesco, borrado) VALUES
('0801-1970-66666', 'Marta López', '8877-1111', 'mlopez@gmail.com', 'Madre', FALSE),
('0501-1975-77777', 'Pedro Martínez', '8877-2222', 'pmartinez@gmail.com', 'Padre', FALSE),
('1601-1980-88888', 'Lucía Fernández', '8877-3333', 'lfernandez@gmail.com', 'Tutor Legal', FALSE),
('0801-1990-99999', 'José Castro', '8877-4444', 'jcastro@gmail.com', 'Conyuge', FALSE),
('0501-1965-00000', 'Rosa Valle', '8877-5555', 'rvalle@gmail.com', 'Otro', FALSE);

INSERT INTO Pacientes (identidad, nombre_completo, fecha_nacimiento, genero, ocupacion, domicilio, telefono, id_responsable, estado_civil, borrado) VALUES
('0801-2005-12345', 'Kevin López', '2005-10-15', 'Masculino', 'Estudiante', 'Col. Kennedy, Bloque 4', '3344-1111', 1, 'Soltero', FALSE),
('0501-2010-23456', 'Sofía Martínez', '2010-05-20', 'Femenino', 'Estudiante', 'Col. Trejo, 3ra calle', '3344-2222', 2, 'Soltero', FALSE),
('1601-2012-34567', 'Diego Fernández', '2012-08-08', 'Masculino', 'Estudiante', 'Bo. El Centro', '3344-3333', 3, 'Soltero', FALSE),
('0801-1995-45678', 'María Castro', '1995-02-14', 'Femenino', 'Contadora', 'Res. Plaza', '3344-4444', 4, 'Casado', FALSE),
('0501-2000-56789', 'Carlos Valle', '2000-11-30', 'Masculino', 'Ingeniero', 'Col. Los Andes', '3344-5555', 5, 'Soltero', FALSE);

-- ==========================================
-- 3. INSERTS DE TABLAS RELACIONALES
-- ==========================================

INSERT INTO Caja_Sesiones (id_usuario_apertura, id_usuario_cierre, monto_apertura, monto_cierre_real, diferencia, estado, fecha_apertura, fecha_cierre, observaciones) VALUES
(2, 2, 500.00, 2500.00, 0.00, 'Cerrada', '2026-08-10 08:00:00', '2026-08-10 17:00:00', 'Cierre exacto'),
(2, 2, 500.00, 3000.00, 0.00, 'Cerrada', '2026-08-11 08:00:00', '2026-08-11 17:00:00', 'Sin novedades'),
(2, 2, 500.00, 1500.00, -50.00, 'Cerrada', '2026-08-12 08:00:00', '2026-08-12 17:00:00', 'Faltante de L50.00 reportado'),
(2, 2, 500.00, 4000.00, 10.00, 'Cerrada', '2026-08-13 08:00:00', '2026-08-13 17:00:00', 'Sobrante de L10.00'),
(2, NULL, 500.00, NULL, NULL, 'Abierta', '2026-08-14 08:00:00', NULL, 'Sesión actual abierta');

INSERT INTO Facturacion (numero_recibo, id_paciente, id_caja_sesion, id_usuario_login, rtn_cliente, fecha_emision, concepto, suma_neta, total_honorarios, total_retenido, total_neto_recibido, metodo_pago) VALUES
('000-001-01-00000001', 1, 1, 2, '08012005123451', '2026-08-10', 'Limpieza Dental', 800.00, 0, 0, 800.00, 'Efectivo'),
('000-001-01-00000002', 2, 2, 2, '05012010234561', '2026-08-11', 'Extracción Dental Simple', 1000.00, 0, 0, 1000.00, 'Efectivo'),
('000-001-01-00000003', 3, 3, 2, '16012012345671', '2026-08-12', 'Obturación con Resina', 1200.00, 0, 0, 1200.00, 'POS'),
('000-001-01-00000004', 4, 4, 2, '08011995456781', '2026-08-13', 'Blanqueamiento LED', 2500.00, 0, 0, 2500.00, 'Transferencia'),
('000-001-01-00000005', 5, 5, 2, '05012000567891', '2026-08-14', 'Abono a Tratamiento de Conducto', 1500.00, 0, 0, 1500.00, 'Efectivo');

INSERT INTO Egresos_Gastos (id_caja_sesion, id_usuario_login, fecha, descripcion, monto, metodo_pago, numero_comprobante) VALUES
(1, 2, '2026-08-10', 'Compra de Guantes de Nitrilo', 300.00, 'Efectivo', 'FAC-PROV-101'),
(2, 2, '2026-08-11', 'Compra de botellones de agua', 150.00, 'Efectivo', 'FAC-PROV-102'),
(3, 2, '2026-08-12', 'Papelería y clips para recepción', 200.00, 'Efectivo', 'FAC-PROV-103'),
(4, 2, '2026-08-13', 'Pago de Internet Clínica', 1000.00, 'Transferencia', 'TRANSF-99801'),
(5, 2, '2026-08-14', 'Mantenimiento de unidad dental', 500.00, 'Efectivo', 'RECIBO-MANT-55');

INSERT INTO Paciente_Alergias (id_paciente, id_catalogo_alergia) VALUES
(1, 1), 
(2, 2), 
(3, 3), 
(4, 4), 
(5, 5);

INSERT INTO Citas (id_paciente, id_personal_medico, fecha_hora, motivo_cita, estado) VALUES
(1, 1, '2026-08-10 09:00:00', 'Chequeo General Anual', 'Completada'),
(2, 2, '2026-08-11 10:00:00', 'Evaluación para Brackets', 'Completada'),
(3, 3, '2026-08-12 14:00:00', 'Dolor Fuerte en Molar', 'Completada'),
(4, 4, '2026-08-13 11:00:00', 'Limpieza Profunda', 'Completada'),
(5, 5, '2026-08-14 15:00:00', 'Consulta Inicial Odontológica', 'Confirmada');

INSERT INTO Expediente_Base (id_paciente, remitido_por, antecedentes_patologicos, antecedentes_odontologicos, antecedentes_quirurgicos, antecedentes_ginecobstetros, habitos_toxicos, farmacos_uso_habitual, reaccion_anestesicos, especifique_anestesia, complicaciones_tratamientos_previos, habitos_bucales, frecuencia_cepillado, tipo_cepillo_cerdas, uso_hilo_dental, tipo_mordida) VALUES
(1, 'Particular', 'Ninguno', 'Limpieza en 2024', 'Ninguno', 'N/A', 'Ninguno', 'Ninguno', FALSE, '', 'Ninguna', 'Ninguno', '3 veces al día', 'Suaves', 'Si', 'Clase I'),
(2, 'Pediatra de Cabecera', 'Asma Leve', 'Ninguno', 'Amigdalectomía', 'N/A', 'Ninguno', 'Salbutamol SOS', FALSE, '', 'Ninguna', 'Respirador bucal', '2 veces al día', 'Moderadas', 'A veces', 'Clase II'),
(3, 'Familiar', 'Ninguno', 'Extracción decidua', 'Ninguno', 'N/A', 'Ninguno', 'Ninguno', FALSE, '', 'Ninguna', 'Bruxismo nocturno', '2 veces al día', 'Suaves', 'No', 'Clase I'),
(4, 'Dr. Carlos Mendoza', 'Hipertensión Controlada', 'Ortodoncia previa', 'Cesárea', '2 Embarazos a término', 'Consumo alto de café', 'Enalapril', FALSE, '', 'Ninguna', 'Onicofagia', '3 veces al día', 'Moderadas', 'Si', 'Clase I'),
(5, 'Redes Sociales', 'Diabetes Tipo 2', 'Coronas previas', 'Apendicectomía', 'N/A', 'Fumador social', 'Metformina', FALSE, '', 'Sangrado excesivo previo', 'Ninguno', '1 vez al día', 'Duras', 'No', 'Clase III');

INSERT INTO Expediente_Archivos (id_paciente, tipo_archivo, nombre_archivo, ruta_archivo, observaciones) VALUES
(1, 'Radiografia', 'panoramica_klopez.jpg', 'C:/ClinicaData/Uploads/Pacientes/1/panoramica_klopez.jpg', 'Panorámica inicial'),
(2, 'Fotografia', 'foto_perfil_smartinez.jpg', 'C:/ClinicaData/Uploads/Pacientes/2/foto_perfil.jpg', 'Perfil lateral ortodoncia'),
(3, 'Radiografia', 'periapical_dfernandez.jpg', 'C:/ClinicaData/Uploads/Pacientes/3/periapical_36.jpg', 'Radiolucidez en pieza 36'),
(4, 'Fotografia', 'sonrisa_mcastro.jpg', 'C:/ClinicaData/Uploads/Pacientes/4/sonrisa_antes.jpg', 'Tono inicial previo al blanqueamiento'),
(5, 'Laboratorio', 'hemograma_cvalle.pdf', 'C:/ClinicaData/Uploads/Pacientes/5/hemograma_2026.pdf', 'Exámenes prequirúrgicos regulares');

INSERT INTO Evolucion_Clinica (id_expediente_base, id_cita, id_personal_medico, numero_cita, motivo_consulta, sintoma_principal, historia_enfermedad_actual, presion_sistolica, presion_diastolica, pulso_cardiaco_bpm, temperatura_celsius, tejidos_blandos_observacion, diagnostico, estado_odontograma, observaciones, id_catalogo_procedimiento) VALUES
(1, 1, 1, 1, 'Chequeo de rutina', 'Ninguno', 'Paciente acude a revisión rutinaria anual, completamente asintomático.', 120, 80, 75, 36.5, 'Encías sanas color rosa coral', 'Salud bucal óptima', '{"pieza_11":"sana"}', 'Paciente muy colaborador con la higiene.', 1),
(2, 2, 2, 1, 'Ortodoncia estética', 'Dientes apiñados', 'Paciente refiere inconformidad estética por apiñamiento anteroinferior.', 110, 70, 80, 36.6, 'Ligera inflamación marginal', 'Apiñamiento moderado', '{"apiñamiento":"arco_inferior"}', 'Se toman modelos de estudio y fotografías.', NULL),
(3, 3, 3, 1, 'Dolor intenso', 'Dolor punzante', 'Refiere dolor espontáneo y nocturno intenso de 3 días de evolución.', 115, 75, 90, 37.0, 'Fístula observable en pieza 36', 'Pulpitis irreversible asintomática', '{"pieza_36":"caries_profunda"}', 'Se apertura la cámara pulpar para drenaje.', 5),
(4, 4, 4, 1, 'Limpieza', 'Manchas superficiales', 'Paciente desea remover pigmentaciones adquiridas por café y té.', 130, 85, 72, 36.4, 'Borde gingival inflamado', 'Gingivitis leve generalizada', '{"sarro":"caras_linguales_inferiores"}', 'Procedimiento profiláctico sin dolor.', 1),
(5, 5, 5, 1, 'Consulta de urgencia', 'Diente quebrado', 'Trauma masticatorio accidental ocurrido hace 24 horas.', 140, 90, 85, 36.8, 'Laceración leve en labio inferior', 'Fractura coronal sin exposición', '{"pieza_21":"fracturada"}', 'Se pule borde incisal afilado para evitar laceración.', 3);