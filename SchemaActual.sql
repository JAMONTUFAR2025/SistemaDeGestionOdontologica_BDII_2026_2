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

-- 1. TABLA CAJA_SESIONES (Con usuarios de apertura y cierre)
CREATE TABLE Caja_Sesiones (
    id_caja_sesion INT AUTO_INCREMENT,
    id_usuario_apertura INT NOT NULL,         -- Cajero que abre la sesión
    id_usuario_cierre INT NULL,             -- Usuario/Supervisor que realiza el cierre
    monto_apertura DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_cierre_real DECIMAL(10,2) NULL,     -- Dinero físico contado
    diferencia DECIMAL(10,2) NULL,           -- (monto_cierre_real - monto_cierre_esperado)
    estado ENUM('Abierta', 'Cerrada') NOT NULL DEFAULT 'Abierta',
    fecha_apertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre DATETIME NULL,
    observaciones TEXT NULL,

    -- Restricciones
    CONSTRAINT PK_Caja_Sesiones PRIMARY KEY (id_caja_sesion),
    CONSTRAINT FK_Caja_Usuario_Apertura FOREIGN KEY (id_usuario_apertura) REFERENCES Usuarios_Login(id_usuarios_login),
    CONSTRAINT FK_Caja_Usuario_Cierre FOREIGN KEY (id_usuario_cierre) REFERENCES Usuarios_Login(id_usuarios_login),
    CONSTRAINT CHK_Caja_Apertura CHECK (monto_apertura >= 0)
);

-- 2. TABLA FACTURACION (Con trazabilidad de quién cobró)
CREATE TABLE Facturacion (
    id_facturacion_recibos INT AUTO_INCREMENT,
    numero_recibo VARCHAR(50),
    id_pacientes INT NOT NULL,
    id_caja_sesion INT NOT NULL,
    id_usuario INT NOT NULL,                  -- Usuario/Recepcionista que procesó la factura
    rtn_cliente VARCHAR(20),
    fecha_emision DATE NOT NULL,
    concepto TEXT NOT NULL,
    suma_neta DECIMAL(10,2),
    total_honorarios DECIMAL(10,2),
    total_retenido DECIMAL(10,2),
    total_neto_recibido DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM('Efectivo', 'Transferencia', 'POS') NOT NULL,
    anulado ENUM('Si', 'No') DEFAULT 'No',
    fecha_anulado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Facturacion_Recibos PRIMARY KEY (id_facturacion_recibos),
    CONSTRAINT FK_Facturacion_Paciente FOREIGN KEY (id_pacientes) REFERENCES Pacientes(id_pacientes),
    CONSTRAINT FK_Facturacion_Caja FOREIGN KEY (id_caja_sesion) REFERENCES Caja_Sesiones(id_caja_sesion) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT FK_Facturacion_Usuario FOREIGN KEY (id_usuario) REFERENCES Usuarios_Login(id_usuarios_login),
    CONSTRAINT CHK_Facturacion_TotalNeto CHECK (total_neto_recibido >= 0)
);

-- 3. TABLA EGRESOS_GASTOS (Con trazabilidad de quién entregó o autorizó el dinero)
CREATE TABLE Egresos_Gastos (
    id_egresos_gastos INT AUTO_INCREMENT,
    id_caja_sesion INT NULL,
    id_usuario INT NOT NULL,                  -- Usuario que registró el gasto
    fecha DATE NOT NULL,
    descripcion TEXT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM('Efectivo', 'Transferencia', 'POS') NOT NULL DEFAULT 'Efectivo',
    numero_comprobante VARCHAR(100),
    anulado ENUM('Si', 'No') DEFAULT 'No',
    fecha_anulado DATETIME NULL,
    
    -- Restricciones
    CONSTRAINT PK_Egresos_Gastos PRIMARY KEY (id_egresos_gastos),
    CONSTRAINT FK_Egresos_Caja FOREIGN KEY (id_caja_sesion) REFERENCES Caja_Sesiones(id_caja_sesion) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT FK_Egresos_Usuario FOREIGN KEY (id_usuario) REFERENCES Usuarios_Login(id_usuarios_login),
    CONSTRAINT CHK_Egresos_Monto CHECK (monto >= 0)
);

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

-- ========================================== 
-- 4. DATOS INICIALES (INSERTS)
-- ==========================================

INSERT INTO Usuarios_Login (correo, contrasenia, rol_sistema, borrado) 
VALUES ('erickfernandochavezcardona@gmail.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador', 'No');

INSERT INTO Especialidades (nombre_especialidad) VALUES 
('Odontologo General'),
('Ortodoncia'),
('Endodoncia'),
('Periodoncia'),
('Cirugia Bucal / Maxilofacial'),
('Odontopediatra'),
('Prostodoncia / Rehabilitacion Oral');

-- OTROS DATOS
USE soe_odontologia_final;

-- ==========================================
-- 1. TABLAS CATÁLOGO E INDEPENDIENTES
-- ==========================================

INSERT INTO Catalogo_Alergias (nombre_alergia) VALUES 
('Penicilina'),
('Látex'),
('Aspirina / AINEs'),
('Anestésicos Locales (Lidocaína)'),
('Yodo');

INSERT INTO Catalogo_Procedimientos (nombre_procedimiento, precio_sugerido) VALUES 
('Limpieza Dental y Detartraje Ultrasónico', 800.00),
('Obturación con Resina Fotocurable', 600.00),
('Extracción Dental Simple', 700.00),
('Tratamiento de Conducto (Endodoncia)', 3500.00),
('Blanqueamiento Dental LED', 2500.00);

-- ==========================================
-- 2. USUARIOS Y PACIENTES
-- ==========================================

-- Asumiendo que Especialidades ya tiene datos creados previamente (IDs 1 al 7)
INSERT INTO Personal_Medico (identidad, nombre_completo, id_especialidades, telefono, correo) VALUES 
('0801199012345', 'Dra. María José López', 1, '99887766', 'mlopez@clini-dental.hn'),
('0501198556789', 'Dr. Carlos Roberto Suazo', 3, '88776655', 'csuazo@clini-dental.hn'),
('0801199234567', 'Dra. Ana Lucía Hernández', 2, '97654321', 'ahernandez@clini-dental.hn'),
('1601198898765', 'Dr. Juan Fernando Gómez', 5, '33221100', 'jgomez@clini-dental.hn'),
('0501199543210', 'Dra. Sofía Isabel Ramírez', 6, '88112233', 'sramirez@clini-dental.hn');

INSERT INTO Usuarios_Login (correo, contrasenia, id_personal_medico, rol_sistema) VALUES 
('recepcion@clini-dental.hn', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', NULL, 'Recepcionista'),
('mlopez@clini-dental.hn', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1, 'Medico'),
('csuazo@clini-dental.hn', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 2, 'Medico'),
('ahernandez@clini-dental.hn', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 3, 'Medico'),
('recepcion2@clini-dental.hn', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', NULL, 'Recepcionista');

INSERT INTO Pacientes (identidad, nombre_completo, fecha_nacimiento, genero, ocupacion, domicilio, telefono, persona_responsable, telefono_responsable, estado_civil) VALUES 
('0801200001122', 'José Luis Martínez', '2000-05-14', 'M', 'Ingeniero Agrónomo', 'Col. Palmira, Tegucigalpa', '98765432', NULL, NULL, 'Soltero'),
('0501199503344', 'Elena Beatríz Castillo', '1995-11-20', 'F', 'Docente', 'Barrio El Centro, San Pedro Sula', '87654321', NULL, NULL, 'Casada'),
('1601201505566', 'Kevin Alejandro Paz', '2015-03-10', 'M', 'Estudiante', 'Bo. Las Flores, Santa Bárbara', '99112233', 'Marta Paz (Madre)', '99112244', 'Soltero'),
('0801198807788', 'Rosa Amelia Alvarado', '1988-08-02', 'F', 'Contadora', 'Col. El Prado, Tegucigalpa', '33445566', NULL, NULL, 'Soltera'),
('0501200209900', 'David Eduardo Rivera', '2002-01-25', 'M', 'Estudiante Universitario', 'Col. Trejo, San Pedro Sula', '95500112', 'Eduardo Rivera (Padre)', '95500113', 'Soltero');

-- ==========================================
-- 3. CAJA Y OPERACIONES FINANCIERAS
-- ==========================================

INSERT INTO Caja_Sesiones (id_usuario_apertura, id_usuario_cierre, monto_apertura, monto_cierre_real, diferencia, estado, fecha_apertura, fecha_cierre, observaciones) VALUES 
(2, 2, 500.00, 2600.00, 0.00, 'Cerrada', '2026-08-04 08:00:00', '2026-08-04 17:00:00', 'Caja cerrada sin novedades'),
(2, 2, 500.00, 1900.00, 0.00, 'Cerrada', '2026-08-05 08:00:00', '2026-08-05 17:00:00', 'Cuadro exacto'),
(6, 6, 500.00, 4200.00, 0.00, 'Cerrada', '2026-08-06 08:00:00', '2026-08-06 17:00:00', 'Se pagó laboratorio en efectivo'),
(2, 2, 500.00, 1100.00, -50.00, 'Cerrada', '2026-08-07 08:00:00', '2026-08-07 17:00:00', 'Faltante de L 50 por cambio errado'),
(2, NULL, 500.00, NULL, NULL, 'Abierta', '2026-08-08 08:00:00', NULL, 'Sesión de caja actual');

INSERT INTO Facturacion (numero_recibo, id_pacientes, id_caja_sesion, id_usuario, rtn_cliente, fecha_emision, concepto, suma_neta, total_honorarios, total_retenido, total_neto_recibido, metodo_pago) VALUES 
('REC-0001', 1, 1, 2, '08012000011221', '2026-08-04', 'Limpieza Dental y Detartraje Ultrasónico', 800.00, 0.00, 0.00, 800.00, 'Efectivo'),
('REC-0002', 2, 1, 2, '05011995033442', '2026-08-04', 'Obturación con Resina Fotocurable (2 piezas)', 1200.00, 0.00, 0.00, 1200.00, 'POS'),
('REC-0003', 3, 2, 2, 'ND', '2026-08-05', 'Extracción Dental Simple', 700.00, 0.00, 0.00, 700.00, 'Efectivo'),
('REC-0004', 4, 3, 6, '08011988077885', '2026-08-06', 'Abono Inicial Endodoncia', 2000.00, 0.00, 0.00, 2000.00, 'Transferencia'),
('REC-0005', 5, 5, 2, '05012002099009', '2026-08-08', 'Blanqueamiento Dental LED', 2500.00, 0.00, 0.00, 2500.00, 'Efectivo');

INSERT INTO Egresos_Gastos (id_caja_sesion, id_usuario, fecha, descripcion, monto, metodo_pago, numero_comprobante) VALUES 
(1, 2, '2026-08-04', 'Compra de garrafón de agua destilada para autoclave', 150.00, 'Efectivo', 'REC-AGUA-01'),
(2, 2, '2026-08-05', 'Pago de mensajería flete de insumos', 100.00, 'Efectivo', 'COMP-0852'),
(3, 6, '2026-08-06', 'Pago a Laboratorio Protésico Dental (Anticipo Corona)', 800.00, 'Efectivo', 'LAB-9921'),
(NULL, 1, '2026-08-07', 'Pago mensual de Internet de la clínica', 1150.00, 'Transferencia', 'TR-882910'),
(5, 2, '2026-08-08', 'Compra urgente de cajas de guantes de nitrilo', 350.00, 'Efectivo', 'FAC-DEP-441');

-- ==========================================
-- 4. CLINICAS, EXPEDIENTES Y CLINICA EVALUATION
-- ==========================================

INSERT INTO Paciente_Alergias (id_pacientes, id_catalogo_alergias) VALUES 
(1, 1), -- José Martínez es alérgico a la Penicilina
(2, 2), -- Elena Castillo es alérgica al Látex
(3, 3), -- Kevin Paz es alérgico a Aspirinas
(4, 4), -- Rosa Alvarado es alérgica a la Lidocaína
(5, 5); -- David Rivera es alérgico al Yodo

INSERT INTO Expediente_Base (id_pacientes, remitido_por, antecedentes_patologicos, antecedentes_odontologicos, antecedentes_quirurgicos, habitos_toxicos, reaccion_anestesicos, habitos_bucales, frecuencia_cepillado, tipo_cepillo_cerdas, uso_hilo_dental, tipo_mordida, diagnostico_presuntivo) VALUES 
(1, 'Particular', 'Ninguno', 'Limpiezas previas anuales', 'Ninguna', 'Ninguno', 'No', 'Ninguno', '2 veces al día', 'Moderadas', 'A veces', 'Clase I', 'Gingivitis leve asociada a placa'),
(2, 'Dra. María López', 'Hipertensión controlada', 'Extracción previa de cordales', 'Apendicectomía (2018)', 'Ninguno', 'No', 'Bruxismo nocturno', '3 veces al día', 'Suaves', 'Si', 'Clase I', 'Caries dental profunda en pieza 16'),
(3, 'Pediatra de cabecera', 'Asma bronquial', 'Primera visita al odontólogo', 'Ninguna', 'Ninguno', 'No', 'Onicofagia (morderse las uñas)', '2 veces al día', 'Suaves', 'No', 'Clase I', 'Caries de la infancia temprana'),
(4, 'Dr. Carlos Suazo', 'Ninguno', 'Tratamiento de ortodoncia previo', 'Ninguna', 'Consumo ocasional de café', 'No', 'Ninguno', '3 veces al día', 'Moderadas', 'Si', 'Clase II', 'Periodontitis apical irreversible pieza 24'),
(5, 'Particular', 'Ninguno', 'Restauraciones previas', 'Amigdalectomía', 'Fumador social', 'No', 'Ninguno', '2 veces al día', 'Duras', 'A veces', 'Clase I', 'Pigmentaciones extrínsecas por tabaco/café');

INSERT INTO Expediente_Archivos (id_pacientes, tipo_archivo, nombre_archivo, ruta_archivo, observaciones) VALUES 
(1, 'Fotografia', 'Foto_Intraoral_Inicial_Jose.jpg', '/uploads/pacientes/1/foto1.jpg', 'Fotografía clínica inicial'),
(2, 'Radiografia', 'Rx_Periapical_Pieza16_Elena.png', '/uploads/pacientes/2/rx_16.png', 'Muestra zona radiolúcida cerca de cámara pulpar'),
(3, 'Radiografia', 'Panoramic_Kevin.png', '/uploads/pacientes/3/panoramica.png', 'Evolución de dentición mixta'),
(4, 'Radiografia', 'Rx_Periapical_Pieza24_Rosa.png', '/uploads/pacientes/4/rx_24.png', 'Proceso osteolítico periapical'),
(5, 'Fotografia', 'Foto_Sonrisa_David.jpg', '/uploads/pacientes/5/sonrisa.jpg', 'Registro previo a blanqueamiento');

-- ==========================================
-- INSERCIÓN DE 20 CITAS DE PRUEBA
-- ==========================================

INSERT INTO Citas (id_pacientes, id_personal_medico, fecha_hora, motivo_cita, estado) VALUES 
-- Citas completadas (Histórico reciente)
(1, 1, '2026-08-01 08:30:00', 'Evaluación general y profilaxis', 'Completada'),
(2, 2, '2026-08-01 10:00:00', 'Revisión de Endodoncia pieza 16', 'Completada'),
(3, 3, '2026-08-02 09:00:00', 'Ajuste de brackets de Ortodoncia', 'Completada'),
(4, 4, '2026-08-02 11:30:00', 'Valoración para extracción de cordales', 'Completada'),
(5, 5, '2026-08-03 14:00:00', 'Consulta Odontopediatría primera vez', 'Completada'),
(1, 1, '2026-08-04 09:00:00', 'Limpieza y revisión general', 'Completada'),
(2, 1, '2026-08-04 10:30:00', 'Dolor en molar superior derecho', 'Completada'),
(3, 4, '2026-08-05 14:00:00', 'Evaluación de pieza retenida', 'Completada'),
(4, 2, '2026-08-06 11:00:00', 'Tratamiento de conducto (Sesión 1)', 'Completada'),

-- Citas de la semana actual (Confirmadas / Canceladas / Ausentes)
(5, 3, '2026-08-07 09:00:00', 'Consulta de seguimiento Ortodoncia', 'Ausente'),
(1, 2, '2026-08-07 15:30:00', 'Sensibilidad dental extrema', 'Cancelada'),
(2, 1, '2026-08-08 08:30:00', 'Obturación con resina', 'Completada'),
(3, 5, '2026-08-08 10:00:00', 'Aplicación de flúor y sellantes', 'Confirmada'),
(4, 2, '2026-08-08 14:00:00', 'Tratamiento de conducto (Sesión 2)', 'Confirmada'),

-- Citas futuras (Programadas para los próximos días)
(5, 3, '2026-08-10 09:00:00', 'Consulta inicial de Ortodoncia', 'Programada'),
(1, 1, '2026-08-10 11:00:00', 'Revisión posterior a profilaxis', 'Programada'),
(2, 4, '2026-08-11 08:00:00', 'Cirugía de tercera molar', 'Programada'),
(3, 2, '2026-08-11 10:30:00', 'Evaluación de perno y corona', 'Programada'),
(4, 1, '2026-08-12 14:00:00', 'Blanqueamiento dental sesión 1', 'Programada'),
(5, 5, '2026-08-13 15:00:00', 'Control de higiene infantil', 'Programada');

INSERT INTO Evolucion_Clinica (id_pacientes, id_expediente_base, id_citas, id_personal_medico, numero_cita, fecha_consulta, motivo_consulta, sintoma_principal, presion_arterial, pulso_cardiaco, temperatura, tejidos_blandos_observacion, diagnostico, id_catalogo_procedimientos, estado_odontograma, pago_abono, observaciones) VALUES 
(1, 1, 1, 1, 1, '2026-08-04 09:30:00', 'Limpieza general', 'Ninguno', '120/80', '72 bpm', '36.5 °C', 'Encías con ligero eritema marginal', 'Gingivitis marginal', 1, '{"piezas_afectadas": []}', 800.00, 'Paciente tolera bien el procedimiento'),
(2, 2, 2, 1, 1, '2026-08-04 11:00:00', 'Dolor al masticar', 'Sensibilidad al frío y dulce', '130/85', '78 bpm', '36.6 °C', 'Tejidos blandos normales', 'Caries de III Grado pieza 16', 2, '{"pieza_16": "caries_oclusal"}', 1200.00, 'Se colocó base protectora y resina'),
(3, 3, 3, 4, 1, '2026-08-05 14:30:00', 'Evaluación de molestia', 'Dolor a la presión', '110/70', '85 bpm', '36.4 °C', 'Loma alveolar inflamada zona molar', 'Pieza decidua hiperreabsorbida', 3, '{"pieza_75": "indicada_extraccion"}', 700.00, 'Extracción exitosa sin complicaciones'),
(4, 4, 4, 2, 1, '2026-08-06 11:30:00', 'Dolor pulsátil e intenso', 'Dolor espontáneo nocturno', '125/80', '80 bpm', '36.7 °C', 'Sin fístula visible', 'Pulpitis Irreversible pieza 24', 4, '{"pieza_24": "endodoncia_iniciada"}', 2000.00, 'Se realizó biopulpectomía y conductometría'),
(5, 5, NULL, 3, 1, '2026-08-08 09:30:00', 'Estética dental', 'Dientes amarillentos', '118/75', '70 bpm', '36.5 °C', 'Mucosa sana', 'Manchas extrínsecas', 5, '{"piezas_anteriores": "blanqueamiento"}', 2500.00, 'Aclaramiento de 3 tonos en escala Vita');

INSERT INTO Consentimientos_Informados (id_evolucion_clinica, tipo_procedimiento, representante_legal, identidad_representante, fecha_firma) VALUES 
(1, 'Otro', NULL, NULL, '2026-08-04'),
(2, 'Otro', NULL, NULL, '2026-08-04'),
(3, 'Cirugia Bucal', 'Marta Paz', '0501198001122', '2026-08-05'),
(4, 'Endodoncia', NULL, NULL, '2026-08-06'),
(5, 'Otro', NULL, NULL, '2026-08-08');

INSERT INTO Constancias_Medicas (id_evolucion_clinica, fecha_emision, hora_emision, tratamiento_realizado) VALUES 
(1, '2026-08-04', '10:15:00', 'Se realizó detartraje ultrasónico y profiilaxis dental.'),
(2, '2026-08-04', '12:00:00', 'Se realizó preparación cavitaria y restauración con resina fotocurable en pieza 16.'),
(3, '2026-08-05', '15:15:00', 'Se realizó extracción quirúrgica simple de pieza temporal bajo anestesia local.'),
(4, '2026-08-06', '12:45:00', 'Se inició tratamiento de conducto (Endodoncia) en pieza 24. Se indica reposo por 24 horas.'),
(5, '2026-08-08', '11:00:00', 'Se realizó sesión de blanqueamiento dental clínico con luz LED.');