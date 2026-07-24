<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Historia Clínica - Clínica SOE</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { 
            background: linear-gradient(135deg, #e3f2fd 0%, #f4f7f6 100%);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            min-height: 100vh;
        }
        .main-container {
            background-color: #ffffff;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            padding: 30px;
            margin-top: 20px;
            margin-bottom: 20px;
        }
        
        /* Botones Módulo Principal */
        .btn-tab-main { font-weight: 600; padding: 12px 24px; border-radius: 30px; border: none; background: #e9ecef; color: #495057; }
        .btn-tab-main.active { background-color: #0d6efd; color: white; }
        
        /* Sub-pestañas Formulario */
        .btn-subtab { font-weight: 600; color: #495057; background-color: #f8f9fa; border: 1px solid #dee2e6; padding: 10px 18px; border-radius: 8px; }
        .btn-subtab.active { background-color: #0d6efd; color: white; }
        
        /* Formato de Visualización del Expediente */
        .info-label { font-weight: 700; color: #6c757d; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; }
        .info-value { font-size: 0.95rem; color: #1a252f; font-weight: 500; background: #f8f9fa; padding: 8px 12px; border-radius: 6px; border-left: 3px solid #0d6efd; margin-top: 3px; }
        
        .seccion-panel, .subpanel-paso { display: none; }
        .seccion-panel.active, .subpanel-paso.active { display: block; }
        .seccion-bloque { background: #ffffff; padding: 15px; border-radius: 10px; border: 1px solid #e9ecef; }
    </style>
</head>
<body>

<div class="container my-4">
    <div class="main-container">

        <!-- Enlace Volver -->
        <div class="mb-3">
            <a href="menu.php" class="text-decoration-none fw-bold text-primary">
                <i class="fa-solid fa-arrow-left me-2"></i>Volver al Menú Principal
            </a>
        </div>

        <!-- PESTAÑAS PRINCIPALES DEL MÓDULO -->
        <div class="d-flex justify-content-center mb-4 bg-light p-2 rounded-pill shadow-sm mx-auto" style="max-width: 650px;">
            <button class="btn-tab-main active me-2" id="btnTab1" onclick="cambiarPestañaPrincipal('panel-expedientes', this)">
                <i class="fa-solid fa-folder-open me-2"></i>1. Expedientes y Consultas
            </button>
            <button class="btn-tab-main" id="btnTab2" onclick="cambiarPestañaPrincipal('panel-nuevo-registro', this)">
                <i class="fa-solid fa-clipboard-user me-2"></i>2. Registrar Nueva Historia
            </button>
        </div>

        <!-- ========================================== -->
        <!-- MÓDULO 1: BUSCADOR Y VISOR COMPLETO DE EXPEDIENTE -->
        <!-- ========================================== -->
        <div id="panel-expedientes" class="seccion-panel active">
            <div class="row mb-4 py-2">
                <div class="col-md-8 mx-auto text-center">
                    <h3 class="fw-bold text-primary mb-3"><i class="fa-solid fa-magnifying-glass me-2"></i>Buscar Expediente de Paciente</h3>
                    <div class="input-group input-group-lg shadow-sm">
                        <input type="text" id="inputBuscar" class="form-control" placeholder="Ingrese Identidad (Ej: 1601-2010-00271)...">
                        <button class="btn btn-primary px-4" type="button" onclick="buscarExpediente()">
                            <i class="fa-solid fa-magnifying-glass me-2"></i>Buscar
                        </button>
                    </div>
                </div>
            </div>

            <!-- VISOR DE RESULTADOS DEL EXPEDIENTE COMPLETO -->
            <div id="contenedorExpediente" style="display: none;">
                <div class="card border-primary shadow-sm mb-4">
                    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center py-3">
                        <h5 class="m-0 fw-bold"><i class="fa-solid fa-id-card me-2"></i><span id="lblNombrePaciente">---</span></h5>
                        <span class="badge bg-light text-primary fs-6 px-3 py-2" id="lblIdentidad">ID: ---</span>
                    </div>
                    <div class="card-body p-4">
                        <div class="row g-4" id="detalleHistoriaBase">
                            <!-- Todo el expediente se renderiza dinámicamente aquí -->
                        </div>
                    </div>
                </div>
            </div>

            <div id="msgNoResultado" class="text-center py-5 text-muted" style="display: none;">
                <i class="fa-solid fa-folder-open fa-3x mb-3 text-secondary"></i>
                <h5 id="txtMensajeError">No se encontró ningún expediente para esta identidad.</h5>
            </div>
        </div>

        <!-- ========================================== -->
        <!-- MÓDULO 2: FORMULARIO DE REGISTRO EN 4 PASOS -->
        <!-- ========================================== -->
        <div id="panel-nuevo-registro" class="seccion-panel">
            <h3 class="fw-bold text-primary mb-3"><i class="fa-solid fa-clipboard-list me-2"></i>Registro de Historia Clínica</h3>
            
            <form id="formHistoriaClinica">
                
                <!-- BOTONES DE NAVEGACIÓN DE SUB-PESTAÑAS -->
                <div class="d-flex flex-wrap gap-2 mb-4 border-bottom pb-3">
                    <button type="button" class="btn-subtab active" onclick="cambiarSubPaso('paso-anamnesis', this)">1. Anamnesis</button>
                    <button type="button" class="btn-subtab" onclick="cambiarSubPaso('paso-antecedentes', this)">2. Antecedentes</button>
                    <button type="button" class="btn-subtab" onclick="cambiarSubPaso('paso-examen', this)">3. Examen Oral</button>
                    <button type="button" class="btn-subtab" onclick="cambiarSubPaso('paso-signos', this)">4. Signos y Diagnóstico</button>
                </div>

                <!-- PASO 1: ANAMNESIS -->
                <div id="paso-anamnesis" class="subpanel-paso active">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Identidad del Paciente:*</label>
                            <input type="text" name="identidad_paciente" class="form-control" placeholder="Ej: 1601-2010-00271" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">ID Médico Tratante:</label>
                            <input type="number" name="id_medico_tratante" class="form-control" placeholder="ID del Médico">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Fecha de Apertura:*</label>
                            <input type="date" name="fecha_apertura" class="form-control" value="<?php echo date('Y-m-d'); ?>" required>
                        </div>
                        <div class="col-md-12">
                            <label class="form-label fw-bold">Remitido Por:</label>
                            <input type="text" name="remitido_por" class="form-control" placeholder="Nombre del Dr. o Clínica que remite">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Motivo de Consulta:</label>
                            <textarea name="motivo_consulta" class="form-control" rows="3" placeholder="Razón principal por la que acude..."></textarea>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Síntoma Principal:</label>
                            <textarea name="sintoma_principal" class="form-control" rows="3" placeholder="Descripción detallada del síntoma..."></textarea>
                        </div>
                        <div class="col-md-12">
                            <label class="form-label fw-bold">Historia de la Enfermedad Actual:</label>
                            <textarea name="historia_enfermedad_actual" class="form-control" rows="3" placeholder="Evolución, tiempo, intensidad del dolor..."></textarea>
                        </div>
                    </div>
                </div>

                <!-- PASO 2: ANTECEDENTES -->
                <div id="paso-antecedentes" class="subpanel-paso">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Antecedentes Patológicos:</label>
                            <textarea name="antecedentes_patologicos" class="form-control" rows="2" placeholder="Diabetes, Hipertensión, etc."></textarea>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Antecedentes Odontológicos:</label>
                            <textarea name="antecedentes_odontologicos" class="form-control" rows="2" placeholder="Tratamientos previos, experiencias negativas..."></textarea>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Hábitos Tóxicos:</label>
                            <input type="text" name="habitos_toxicos" class="form-control" placeholder="Tabaquismo, Alcohol, etc.">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Alergias:</label>
                            <input type="text" name="alergias" class="form-control" placeholder="Medicamentos, látex, alimentos...">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Antecedentes Gineco-Obstétricos:</label>
                            <input type="text" name="antecedentes_gineco_obstetricos" class="form-control" placeholder="Solo si aplica (embarazo, semanas...)">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Antecedentes Quirúrgicos / Hospitalarios:</label>
                            <input type="text" name="antecedentes_quirurgicos" class="form-control" placeholder="Cirugías previas, hospitalizaciones...">
                        </div>
                        <div class="col-md-12">
                            <label class="form-label fw-bold">Fármacos de Uso Habitual:</label>
                            <textarea name="farmacos_uso_habitual" class="form-control" rows="2" placeholder="Medicamentos que consume actualmente..."></textarea>
                        </div>
                        
                        <div class="col-md-12 p-3 bg-warning bg-opacity-10 rounded border border-warning">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">¿Reacción a Anestésicos?</label>
                                    <select name="reaccion_anestesicos" class="form-select">
                                        <option value="No">No</option>
                                        <option value="Si">Sí</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Especifique Reacción de Anestesia:</label>
                                    <input type="text" name="especifique_reaccion_anestesia" class="form-control" placeholder="Detalle si presentó alergia o choque...">
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12">
                            <label class="form-label fw-bold">Complicaciones en Tratamientos Previos:</label>
                            <textarea name="complicaciones_tratamientos_previos" class="form-control" rows="2"></textarea>
                        </div>
                    </div>
                </div>

                <!-- PASO 3: EXAMEN ORAL -->
                <div id="paso-examen" class="subpanel-paso">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Hábitos Bucales:</label>
                            <input type="text" name="habitos_bucales" class="form-control" placeholder="Bruxismo, onicofagia, respirador bucal...">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Frecuencia de Cepillado:</label>
                            <input type="text" name="frecuencia_cepillado" class="form-control" placeholder="Ej: 3 veces al día">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Tipo de Cerdas del Cepillo:</label>
                            <select name="tipo_cerdas_cepillo" class="form-select">
                                <option value="">-- Seleccionar --</option>
                                <option value="Suave">Suave</option>
                                <option value="Media">Media</option>
                                <option value="Dura">Dura</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Uso de Hilo Dental:</label>
                            <select name="uso_hilo_dental" class="form-select">
                                <option value="">-- Seleccionar --</option>
                                <option value="Diario">Diario</option>
                                <option value="Ocasional">Ocasional</option>
                                <option value="Nunca">Nunca</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Tipo de Mordida:</label>
                            <select name="tipo_mordida" class="form-select">
                                <option value="">-- Seleccionar --</option>
                                <option value="Normoclusión (Clase I)">Normoclusión (Clase I)</option>
                                <option value="Distoclusión (Clase II)">Distoclusión (Clase II)</option>
                                <option value="Mesioclusión (Clase III)">Mesioclusión (Clase III)</option>
                                <option value="Mordida Cruzada">Mordida Cruzada</option>
                                <option value="Mordida Abierta">Mordida Abierta</option>
                            </select>
                        </div>
                        <div class="col-md-12">
                            <label class="form-label fw-bold">Observación de Tejidos Blandos:</label>
                            <textarea name="observacion_tejidos_blandos" class="form-control" rows="3" placeholder="Estado de encías, lengua, paladar, mucosa..."></textarea>
                        </div>
                    </div>
                </div>

                <!-- PASO 4: SIGNOS Y DIAGNÓSTICO -->
                <div id="paso-signos" class="subpanel-paso">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Presión Arterial:</label>
                            <input type="text" name="presion_arterial" class="form-control" placeholder="Ej: 120/80 mmHg">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Pulso Cardíaco:</label>
                            <input type="text" name="pulso_cardiaco" class="form-control" placeholder="Ej: 75 bpm">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold">Temperatura (°C):</label>
                            <input type="text" name="temperatura" class="form-control" placeholder="Ej: 36.5 °C">
                        </div>
                        <div class="col-md-12">
                            <label class="form-label fw-bold">Estado Odontograma (JSON / Data):</label>
                            <textarea name="estado_odontograma" class="form-control" rows="2" placeholder="Registro técnico de piezas dentales..."></textarea>
                        </div>
                        <div class="col-md-12">
                            <label class="form-label fw-bold">Diagnóstico Presuntivo:</label>
                            <textarea name="diagnostico_presuntivo" class="form-control" rows="2" placeholder="Diagnóstico inicial del paciente..."></textarea>
                        </div>
                        <div class="col-md-12">
                            <label class="form-label fw-bold">Observaciones Generales:</label>
                            <textarea name="observaciones_generales" class="form-control" rows="2" placeholder="Notas adicionales del expediente..."></textarea>
                        </div>
                    </div>
                </div>

                <!-- Botón de Guardado -->
                <div class="text-end mt-4 pt-3 border-top">
                    <button type="submit" class="btn btn-primary btn-lg px-5 shadow-sm fw-bold">
                        <i class="fa-solid fa-floppy-disk me-2"></i>Guardar Historia Clínica
                    </button>
                </div>
            </form>
        </div>

    </div>
</div>

<!-- ========================================================== -->
<!-- MODAL PARA NUEVA EVOLUCIÓN (CITA 2, 3...)                  -->
<!-- (AHORA ESTÁ ARRIBA DE LOS SCRIPTS COMO DEBE SER)           -->
<!-- ========================================================== -->
<div class="modal fade" id="modalNuevaConsulta" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header bg-success text-white">
        <h5 class="modal-title"><i class="fa-solid fa-notes-medical me-2"></i>Registrar Evolución / Nueva Cita</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <form id="formNuevaConsulta">
        <div class="modal-body">
            <input type="hidden" name="identidad_paciente" id="modalIdentidadPaciente">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label fw-bold">Fecha y Hora de Cita:*</label>
                    <input type="datetime-local" name="fecha_consulta" class="form-control" required value="<?php echo date('Y-m-d\TH:i'); ?>">
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-bold">ID Médico Tratante:*</label>
                    <input type="number" name="id_medico" class="form-control" required placeholder="Ej: 1">
                </div>
                
                <div class="col-12"><hr class="my-1"></div>

                <!-- Signos Vitales de la Cita -->
                <div class="col-md-4">
                    <label class="form-label fw-bold">Presión Arterial:</label>
                    <input type="text" name="presion_arterial" class="form-control" placeholder="120/80 mmHg">
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-bold">Pulso Cardíaco:</label>
                    <input type="text" name="pulso_cardiaco" class="form-control" placeholder="75 bpm">
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-bold">Temperatura:</label>
                    <input type="text" name="temperatura" class="form-control" placeholder="36.5 °C">
                </div>

                <div class="col-12"><hr class="my-1"></div>

                <div class="col-12">
                    <label class="form-label fw-bold">Motivo de Consulta:*</label>
                    <textarea name="motivo_consulta" class="form-control" rows="2" required placeholder="Razón de la visita de hoy..."></textarea>
                </div>
                <div class="col-12">
                    <label class="form-label fw-bold">Síntoma Principal:</label>
                    <textarea name="sintoma_principal" class="form-control" rows="2" placeholder="Detalles del dolor o problema..."></textarea>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-bold">Diagnóstico:</label>
                    <textarea name="diagnostico" class="form-control" rows="3"></textarea>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-bold">Tratamiento Realizado:</label>
                    <textarea name="tratamiento_realizado" class="form-control" rows="3"></textarea>
                </div>
                <div class="col-12"><hr class="my-1"></div>
                <div class="col-12">
                    <h6 class="text-success fw-bold m-0"><i class="fa-solid fa-tooth me-2"></i>Detalle de Procedimientos (Opcional)</h6>
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-bold">Pieza Dental:</label>
                    <input type="text" name="pieza_dental" class="form-control" placeholder="Ej: 18, 21 o N/A">
                </div>
                <div class="col-md-8">
                    <label class="form-label fw-bold">Procedimiento Específico:</label>
                    <input type="text" name="descripcion_procedimiento" class="form-control" placeholder="Ej: Extracción simple / Resina compuesta">
                </div>
                <div class="col-12">
                    <label class="form-label fw-bold">Observaciones / Recomendaciones:</label>
                    <textarea name="observaciones" class="form-control" rows="2"></textarea>
                </div>
            </div>
        </div>
        <div class="modal-footer bg-light">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" class="btn btn-success"><i class="fa-solid fa-save me-2"></i>Guardar Evolución</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- LIBRERÍA DE BOOTSTRAP -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
// 1. Cambiar entre Módulo 1 (Expedientes) y Módulo 2 (Registro)
function cambiarPestañaPrincipal(panelId, btnElement) {
    document.querySelectorAll('.seccion-panel').forEach(panel => panel.classList.remove('active'));
    document.querySelectorAll('.btn-tab-main').forEach(btn => btn.classList.remove('active'));

    document.getElementById(panelId).classList.add('active');
    btnElement.classList.add('active');
}

// 2. Cambiar entre las 4 Sub-Pestañas del Registro
function cambiarSubPaso(pasoId, btnElement) {
    document.querySelectorAll('.subpanel-paso').forEach(paso => paso.classList.remove('active'));
    document.querySelectorAll('.btn-subtab').forEach(btn => btn.classList.remove('active'));

    document.getElementById(pasoId).classList.add('active');
    btnElement.classList.add('active');
}

// 3. BUSCADOR COMPLETO Y DESPLIEGUE TOTAL DE INFORMACIÓN
function buscarExpediente() {
    let query = document.getElementById('inputBuscar').value.trim();
    if (!query) {
        alert("Por favor ingrese un número de identidad.");
        return;
    }

    fetch(`obtener_expediente_ajax.php?identidad=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(data => {
            if (!data.exito) {
                document.getElementById('contenedorExpediente').style.display = 'none';
                document.getElementById('msgNoResultado').style.display = 'block';
                document.getElementById('txtMensajeError').innerText = data.mensaje || "No se encontró el expediente.";
                return;
            }

            document.getElementById('msgNoResultado').style.display = 'none';
            document.getElementById('contenedorExpediente').style.display = 'block';

            // Datos Cabecera Paciente
            let p = data.paciente;
            document.getElementById('lblNombrePaciente').innerText = `${p.nombre || ''} ${p.apellido || ''}`.trim() || 'Paciente Registrado';
            document.getElementById('lblIdentidad').innerText = `ID: ${p.identidad || query}`;

            // Datos Historia Clínica Completa
            let h = data.historia;
            let contenedorH = document.getElementById('detalleHistoriaBase');
            
            if (h) {
                contenedorH.innerHTML = `
                    <!-- BLOQUE 1: ANAMNESIS -->
                    <div class="col-12">
                        <div class="seccion-bloque shadow-sm">
                            <h5 class="fw-bold text-primary border-bottom pb-2 mb-3">
                                <i class="fa-solid fa-notes-medical me-2"></i>1. Anamnesis
                            </h5>
                            <div class="row g-3">
                                <div class="col-md-3"><div class="info-label">Fecha Apertura</div><div class="info-value">${h.fecha_apertura || 'N/A'}</div></div>
                                <div class="col-md-3"><div class="info-label">ID Médico Tratante</div><div class="info-value">${h.id_medico_tratante || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Remitido Por</div><div class="info-value">${h.remitido_por || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Motivo de Consulta</div><div class="info-value">${h.motivo_consulta || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Síntoma Principal</div><div class="info-value">${h.sintoma_principal || 'N/A'}</div></div>
                                <div class="col-12"><div class="info-label">Historia de Enfermedad Actual</div><div class="info-value">${h.historia_enfermedad_actual || 'N/A'}</div></div>
                            </div>
                        </div>
                    </div>

                    <!-- BLOQUE 2: ANTECEDENTES -->
                    <div class="col-12">
                        <div class="seccion-bloque shadow-sm">
                            <h5 class="fw-bold text-primary border-bottom pb-2 mb-3">
                                <i class="fa-solid fa-file-medical me-2"></i>2. Antecedentes Médicos y Odontológicos
                            </h5>
                            <div class="row g-3">
                                <div class="col-md-6"><div class="info-label">Antecedentes Patológicos</div><div class="info-value">${h.antecedentes_patologicos || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Antecedentes Odontológicos</div><div class="info-value">${h.antecedentes_odontologicos || 'N/A'}</div></div>
                                <div class="col-md-4"><div class="info-label">Hábitos Tóxicos</div><div class="info-value">${h.habitos_toxicos || 'N/A'}</div></div>
                                <div class="col-md-4"><div class="info-label">Alergias</div><div class="info-value text-danger fw-bold">${h.alergias || 'Ninguna'}</div></div>
                                <div class="col-md-4"><div class="info-label">Gineco-Obstétricos</div><div class="info-value">${h.antecedentes_gineco_obstetricos || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Antecedentes Quirúrgicos</div><div class="info-value">${h.antecedentes_quirurgicos || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Fármacos Habituales</div><div class="info-value">${h.farmacos_uso_habitual || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">¿Reacción Anestésicos?</div><div class="info-value">${h.reaccion_anestesicos || 'No'} (${h.especifique_reaccion_anestesia || 'Sin detalle'})</div></div>
                                <div class="col-md-6"><div class="info-label">Complicaciones Previas</div><div class="info-value">${h.complicaciones_tratamientos_previos || 'N/A'}</div></div>
                            </div>
                        </div>
                    </div>

                    <!-- BLOQUE 3: EXAMEN ORAL -->
                    <div class="col-12">
                        <div class="seccion-bloque shadow-sm">
                            <h5 class="fw-bold text-primary border-bottom pb-2 mb-3">
                                <i class="fa-solid fa-tooth me-2"></i>3. Examen Oral
                            </h5>
                            <div class="row g-3">
                                <div class="col-md-4"><div class="info-label">Hábitos Bucales</div><div class="info-value">${h.habitos_bucales || 'N/A'}</div></div>
                                <div class="col-md-4"><div class="info-label">Frecuencia Cepillado</div><div class="info-value">${h.frecuencia_cepillado || 'N/A'}</div></div>
                                <div class="col-md-4"><div class="info-label">Tipo Cerdas / Hilo</div><div class="info-value">${h.tipo_cerdas_cepillo || 'N/A'} / ${h.uso_hilo_dental || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Tipo de Mordida</div><div class="info-value">${h.tipo_mordida || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Tejidos Blandos</div><div class="info-value">${h.observacion_tejidos_blandos || 'N/A'}</div></div>
                            </div>
                        </div>
                    </div>

                    <!-- BLOQUE 4: SIGNOS Y DIAGNÓSTICO -->
                    <div class="col-12">
                        <div class="seccion-bloque shadow-sm">
                            <h5 class="fw-bold text-primary border-bottom pb-2 mb-3">
                                <i class="fa-solid fa-heart-pulse me-2"></i>4. Signos Vitales y Diagnóstico
                            </h5>
                            <div class="row g-3">
                                <div class="col-md-4"><div class="info-label">Presión Arterial</div><div class="info-value">${h.presion_arterial || 'N/A'}</div></div>
                                <div class="col-md-4"><div class="info-label">Pulso Cardíaco</div><div class="info-value">${h.pulso_cardiaco || 'N/A'}</div></div>
                                <div class="col-md-4"><div class="info-label">Temperatura</div><div class="info-value">${h.temperatura || 'N/A'} °C</div></div>
                                <div class="col-md-12"><div class="info-label">Estado Odontograma</div><div class="info-value">${h.estado_odontograma || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Diagnóstico Presuntivo</div><div class="info-value fw-bold text-dark">${h.diagnostico_presuntivo || 'N/A'}</div></div>
                                <div class="col-md-6"><div class="info-label">Observaciones Generales</div><div class="info-value">${h.observaciones_generales || 'N/A'}</div></div>
                            </div>
                        </div>
                    </div>

                    <!-- BLOQUE 5: HISTORIAL DE EVOLUCIÓN CLÍNICA (DATOS COMPLETOS) -->
                    <div class="col-12 mt-4">
                        <div class="seccion-bloque shadow-sm border-success">
                            <div class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-3">
                                <h5 class="fw-bold text-success m-0">
                                    <i class="fa-solid fa-truck-medical me-2"></i>5. Historial de Evolución Clínica
                                </h5>
                                <button class="btn btn-sm btn-success fw-bold" onclick="document.getElementById('modalIdentidadPaciente').value = '${p.identidad || query}';" data-bs-toggle="modal" data-bs-target="#modalNuevaConsulta">
                                    <i class="fa-solid fa-plus me-1"></i> Añadir Evolución
                                </button>
                            </div>
                            <div class="row g-3" id="listaConsultas">
                                ${data.consultas && data.consultas.length > 0 
                                    ? data.consultas.map(c => `
                                        <div class="col-12">
                                            <div class="card bg-light border-0 shadow-sm">
                                                <div class="card-body p-3">
                                                    <div class="d-flex justify-content-between border-bottom pb-2 mb-2">
                                                        <h6 class="text-primary fw-bold m-0"><i class="fa-regular fa-calendar-check me-2"></i>Cita del ${c.fecha_consulta}</h6>
                                                        <span class="badge bg-secondary">Dr. ID: ${c.id_medico || 'N/A'}</span>
                                                    </div>
                                                    <div class="row text-muted small mb-2">
                                                        <div class="col-4"><strong>PA:</strong> ${c.presion_arterial || 'N/A'}</div>
                                                        <div class="col-4"><strong>Pulso:</strong> ${c.pulso_cardiaco || 'N/A'}</div>
                                                        <div class="col-4"><strong>Temp:</strong> ${c.temperatura || 'N/A'}</div>
                                                    </div>
                                                    <p class="mb-1"><strong>Motivo:</strong> ${c.motivo_consulta}</p>
                                                    <p class="mb-1"><strong>Síntoma Principal:</strong> ${c.sintoma_principal || 'N/A'}</p>
                                                    <p class="mb-1"><strong>Diagnóstico:</strong> ${c.diagnostico || 'N/A'}</p>
                                                    <div class="mt-2 pt-2 border-top">
                                <strong>Procedimiento(s):</strong> 
                                ${c.procedimientos && c.procedimientos.length > 0 
                                    ? c.procedimientos.map(pr => `<span class="badge bg-info text-dark me-1">Pieza: ${pr.pieza_dental || 'N/A'} - ${pr.descripcion_procedimiento}</span>`).join('')
                                    : '<span class="text-muted small">Ninguno registrado</span>'
                                }
                            </div>
                                                    <p class="mb-1 text-success fw-bold"><strong>Tratamiento Realizado:</strong> ${c.tratamiento_realizado || 'N/A'}</p>
                                                    <p class="mb-0 small text-muted"><strong>Observaciones:</strong> ${c.observaciones || 'Ninguna'}</p>
                                                </div>
                                            </div>
                                        </div>
                                      `).join('') 
                                    : `<div class="col-12 text-center text-muted py-3">No hay evoluciones clínicas registradas aún.</div>`
                                }
                            </div>
                        </div>
                    </div>
                `;
            } else {
                contenedorH.innerHTML = `<div class="alert alert-warning col-12 text-center fs-6">El paciente existe pero no tiene Historia Clínica registrada aún.</div>`;
            }
        })
        .catch(err => {
            console.error("Error:", err);
            alert("Error al intentar realizar la búsqueda. Comprueba tu conexión con el servidor.");
        });
}

// Búsqueda al pulsar la tecla Enter
document.getElementById('inputBuscar').addEventListener('keypress', function (e) {
    if (e.key === 'Enter') buscarExpediente();
});

// Guardar Registro Base
document.getElementById('formHistoriaClinica').addEventListener('submit', function(e) {
    e.preventDefault();
    fetch('guardar_historia_ajax.php', { method: 'POST', body: new FormData(this) })
    .then(r => r.json())
    .then(data => {
        alert(data.mensaje);
        if(data.exito) this.reset();
    });
});

// Guardar Nueva Evolución Clínica
document.getElementById('formNuevaConsulta').addEventListener('submit', function(e) {
    e.preventDefault(); // Esto evita que la página se recargue
    let formData = new FormData(this);

    fetch('guardar_evolucion_ajax.php', { method: 'POST', body: formData })
    .then(r => r.json())
    .then(data => {
        alert(data.mensaje); // Ahora SÍ mostrará el mensaje
        if(data.exito) {
            let modal = bootstrap.Modal.getInstance(document.getElementById('modalNuevaConsulta'));
            modal.hide();
            this.reset();
            buscarExpediente(); // Recarga las tarjetas
        }
    })
    .catch(err => {
        console.error("Error al guardar evolución:", err);
        alert("Hubo un error de conexión al intentar guardar.");
    });
});
</script>
</body>
</html>