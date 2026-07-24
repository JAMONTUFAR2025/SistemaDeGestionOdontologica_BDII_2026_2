<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Expediente Clínico Dental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { background-color: #f4f7f6; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .card-custom { border: none; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .header-bg { background: linear-gradient(135deg, #0d6efd, #0a58ca); color: white; border-radius: 12px 12px 0 0; }
        .badge-status { font-size: 0.85rem; padding: 6px 12px; border-radius: 20px; }
        .nav-pills .nav-link.active { background-color: #0d6efd; font-weight: 600; }
        .info-label { font-weight: 600; color: #6c757d; font-size: 0.85rem; text-transform: uppercase; }
        .info-value { font-size: 1rem; color: #212529; font-weight: 500; }
        .timeline-item { border-left: 3px solid #0d6efd; padding-left: 20px; position: relative; margin-bottom: 25px; }
        .timeline-item::before { content: ''; position: absolute; left: -8px; top: 0; width: 13px; height: 13px; border-radius: 50%; background: #0d6efd; }
    </style>
</head>
<body>

<div class="container py-4">
    <!-- Encabezado y Buscador -->
    <div class="row mb-4">
        <div class="col-md-8 mx-auto text-center">
            <h3 class="fw-bold text-primary mb-3"><i class="fa-solid fa-folder-open me-2"></i>Expediente Clínico del Paciente</h3>
            <div class="input-group input-group-lg shadow-sm">
                <input type="text" id="inputBuscar" class="form-control" placeholder="Ingrese Identidad o Nombre del Paciente..." autocomplete="off">
                <button class="btn btn-primary px-4" type="button" onclick="buscarExpediente()">
                    <i class="fa-solid fa-magnifying-glass me-2"></i>Buscar
                </button>
            </div>
        </div>
    </div>

    <!-- Contenedor del Expediente (Oculto al inicio) -->
    <div id="contenedorExpediente" style="display: none;">
        
        <!-- Tarjeta Principal Datos del Paciente -->
        <div class="card card-custom mb-4">
            <div class="card-header header-bg p-3 d-flex justify-content-between align-items-center">
                <h5 class="m-0 fw-bold"><i class="fa-solid fa-user-doctor me-2"></i><span id="lblNombrePaciente">---</span></h5>
                <span class="badge bg-light text-primary badge-status" id="lblIdentidad">ID: ---</span>
            </div>
            <div class="card-body p-4">
                <div class="row g-3">
                    <div class="col-md-3">
                        <div class="info-label">Fecha Nacimiento</div>
                        <div class="info-value" id="lblFechaNac">---</div>
                    </div>
                    <div class="col-md-3">
                        <div class="info-label">Teléfono</div>
                        <div class="info-value" id="lblTelefono">---</div>
                    </div>
                    <div class="col-md-3">
                        <div class="info-label">Género</div>
                        <div class="info-value" id="lblGenero">---</div>
                    </div>
                    <div class="col-md-3">
                        <div class="info-label">Tipo Sangre / Alergias</div>
                        <div class="info-value text-danger" id="lblAlergias">---</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Pestañas de Navegación -->
        <ul class="nav nav-pills mb-3 justify-content-center" id="pills-tab" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active rounded-pill px-4" id="tab-primera-cita" data-bs-toggle="pill" data-bs-target="#content-primera-cita" type="button">
                    <i class="fa-solid fa-file-invoice me-2"></i>Primera Cita (Historia Base)
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link rounded-pill px-4" id="tab-historial-citas" data-bs-toggle="pill" data-bs-target="#content-historial-citas" type="button">
                    <i class="fa-solid fa-clock-rotate-left me-2"></i>Historial de Consultas y Evolución
                </button>
            </li>
        </ul>

        <!-- Contenido de las Pestañas -->
        <div class="tab-content" id="pills-tabContent">
            
            <!-- PESTAÑA 1: PRIMERA CITA (HISTORIA CLÍNICA BASE) -->
            <div class="tab-pane fade show active" id="content-primera-cita" role="tabpanel">
                <div class="card card-custom p-4" id="cardHistoriaBase">
                    <h5 class="fw-bold text-primary mb-3"><i class="fa-solid fa-clipboard-list me-2"></i>Registro de Apertura / Primera Cita</h5>
                    <div class="row g-3" id="detalleHistoriaBase">
                        <!-- Se llena dinámicamente -->
                    </div>
                </div>
            </div>

            <!-- PESTAÑA 2: HISTORIAL DE CONSULTAS Y EVOLUCIÓN -->
            <div class="tab-pane fade" id="content-historial-citas" role="tabpanel">
                <div class="card card-custom p-4">
                    <h5 class="fw-bold text-primary mb-4"><i class="fa-solid fa-notes-medical me-2"></i>Cronología de Atenciones</h5>
                    <div id="listaConsultas">
                        <!-- Se llena dinámicamente -->
                    </div>
                </div>
            </div>

        </div>

    </div>

    <!-- Mensaje sin resultados -->
    <div id="msgNoResultado" class="text-center py-5 text-muted" style="display: none;">
        <i class="fa-solid fa-folder-open fa-3x mb-3 text-secondary"></i>
        <h5>No se encontraron datos para la búsqueda ingresada.</h5>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/bootstrap.bundle.min.js"></script>
<script>
function buscarExpediente() {
    let query = document.getElementById('inputBuscar').value.trim();
    if (!query) return;

    fetch(`obtener_expediente_ajax.php?identidad=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(data => {
            if (!data.exito) {
                document.getElementById('contenedorExpediente').style.display = 'none';
                document.getElementById('msgNoResultado').style.display = 'block';
                return;
            }

            document.getElementById('msgNoResultado').style.display = 'none';
            document.getElementById('contenedorExpediente').style.display = 'block';

            // 1. Llenar Paciente
            let p = data.paciente;
            document.getElementById('lblNombrePaciente').innerText = `${p.nombre || ''} ${p.apellido || ''}`;
            document.getElementById('lblIdentidad').innerText = `ID: ${p.identidad || ''}`;
            document.getElementById('lblFechaNac').innerText = p.fecha_nacimiento || 'N/A';
            document.getElementById('lblTelefono').innerText = p.telefono || 'N/A';
            document.getElementById('lblGenero').innerText = p.genero || 'N/A';

            // 2. Llenar Historia Base (Primera Cita)
            let h = data.historia;
            let contenedorH = document.getElementById('detalleHistoriaBase');
            if (h) {
                document.getElementById('lblAlergias').innerText = h.alergias || 'Ninguna';
                contenedorH.innerHTML = `
                    <div class="col-md-4"><div class="info-label">Fecha Apertura</div><div class="info-value">${h.fecha_apertura}</div></div>
                    <div class="col-md-4"><div class="info-label">Remitido Por</div><div class="info-value">${h.remitido_por || 'N/A'}</div></div>
                    <div class="col-md-4"><div class="info-label">Médico Tratante</div><div class="info-value">ID: ${h.id_medico_tratante || 'N/A'}</div></div>
                    <div class="col-md-6 mt-3"><div class="info-label">Motivo Consulta Inicial</div><div class="info-value">${h.motivo_consulta}</div></div>
                    <div class="col-md-6 mt-3"><div class="info-label">Síntoma Principal</div><div class="info-value">${h.sintoma_principal}</div></div>
                    <div class="col-md-12 mt-3"><div class="info-label">Historia Enfermedad Actual</div><div class="info-value">${h.historia_enfermedad_actual}</div></div>
                    <div class="col-md-6 mt-3"><div class="info-label">Antecedentes Patológicos</div><div class="info-value">${h.antecedentes_patologicos || 'Sin hallazgos'}</div></div>
                    <div class="col-md-6 mt-3"><div class="info-label">Diagnóstico Presuntivo Inicial</div><div class="info-value text-primary fw-bold">${h.diagnostico_presuntivo || 'N/A'}</div></div>
                `;
            } else {
                contenedorH.innerHTML = `<div class="alert alert-warning">El paciente no posee registro de Historia Clínica Inicial.</div>`;
            }

            // 3. Llenar Evoluciones / Consultas Posteriores
            let listaC = document.getElementById('listaConsultas');
            if (data.consultas && data.consultas.length > 0) {
                let htmlC = '';
                data.consultas.forEach(c => {
                    htmlC += `
                        <div class="timeline-item">
                            <div class="d-flex justify-content-between align-items-center mb-1">
                                <h6 class="fw-bold text-dark m-0">Consulta - ${c.fecha_consulta || 'Sin Fecha'}</h6>
                                <span class="badge bg-secondary">${c.hora || ''}</span>
                            </div>
                            <p class="mb-1"><strong>Tratamiento/Evolución:</strong> ${c.diagnostico_tratamiento || c.descripcion || ''}</p>
                            <small class="text-muted">Observaciones: ${c.observaciones || 'Ninguna'}</small>
                        </div>
                    `;
                });
                listaC.innerHTML = htmlC;
            } else {
                listaC.innerHTML = `<p class="text-muted">No hay citas de evolución o subsecuentes registradas aún.</p>`;
            }
        });
}

// Permitir presionar Enter en el buscador
document.getElementById('inputBuscar').addEventListener('keypress', function (e) {
    if (e.key === 'Enter') buscarExpediente();
});
</script>
</body>
</html>