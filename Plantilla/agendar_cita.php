<?php
session_start();
if (!isset($_SESSION['usuario_id'])) {
    header("Location: login.php");
    exit();
}
include('conexion.php');

$mensaje = "";
$tipo_mensaje = "";

// Cargar Pacientes y Médicos para los desplegables
$pacientes = $conexion->query("SELECT identidad, nombre_completo, telefono FROM Pacientes ORDER BY nombre_completo ASC");
$medicos   = $conexion->query("SELECT * FROM Personal_Medico WHERE estado = 'Activo' ORDER BY nombre_completo ASC");

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_paciente = $_POST['id_paciente'];
    $id_medico   = $_POST['id_medico'];
    $fecha_cita  = $_POST['fecha_cita'];
    $hora_cita   = $_POST['hora_cita'];
    $motivo      = trim($_POST['motivo']);
    $detalle     = trim($_POST['detalle']);
    $prioridad   = $_POST['prioridad'];

    $stmt = $conexion->prepare("INSERT INTO Citas (id_paciente, id_medico, fecha_cita, hora_cita, motivo, detalle, prioridad) VALUES (?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("sisssss", $id_paciente, $id_medico, $fecha_cita, $hora_cita, $motivo, $detalle, $prioridad);

    if ($stmt->execute()) {
        $mensaje = "¡Cita agendada correctamente!";
        $tipo_mensaje = "exito";
    } else {
        $mensaje = "Error al agendar la cita: " . $conexion->error;
        $tipo_mensaje = "error";
    }
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Agenda y Citas - SOE Odontología</title>
    <!-- jQuery y Select2 -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <style>
        body { font-family: Arial, sans-serif; background-image: url('fondo-clinica.jpg'); background-size: cover; background-position: center; background-attachment: fixed; margin: 0; padding: 20px; }
        .container { background: rgba(255, 255, 255, 0.95); max-width: 950px; margin: 0 auto; padding: 25px; border-radius: 12px; box-shadow: 0 8px 25px rgba(0,0,0,0.2); }
        .layout { display: grid; grid-template-columns: 220px 1fr; gap: 20px; }
        
        /* Panel Lateral estilo comunicación */
        .sidebar-menu { background: #f8f9fa; border: 1px solid #e0e0e0; border-radius: 8px; padding: 15px; }
        .sidebar-menu h3 { color: #007bf0; margin-top: 0; font-size: 16px; border-bottom: 2px solid #007bf0; padding-bottom: 8px; }
        .btn-canal { display: flex; align-items: center; gap: 10px; width: 100%; padding: 10px; margin-bottom: 8px; border: 1px solid #ddd; background: white; border-radius: 6px; cursor: pointer; text-decoration: none; color: #333; font-weight: bold; font-size: 13px; box-sizing: border-box; }
        .btn-canal:hover { background: #eef6ff; border-color: #007bf0; }
        .btn-ws { border-color: #25D366; color: #128C7E; }
        .btn-ws:hover { background: #e8f8ef; }

        /* Formulario */
        .form-panel { background: #fff; padding: 20px; border-radius: 8px; border: 1px solid #e0e0e0; }
        .quick-days { display: flex; gap: 5px; margin-bottom: 15px; flex-wrap: wrap; }
        .quick-days button { background: #f0f0f0; border: 1px solid #ccc; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 12px; }
        .quick-days button:hover { background: #007bf0; color: white; border-color: #007bf0; }
        
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 15px; }
        .form-group { display: flex; flex-direction: column; margin-bottom: 12px; }
        label { font-size: 12px; font-weight: bold; margin-bottom: 5px; color: #444; }
        input, select, textarea { padding: 9px; border: 1px solid #ccc; border-radius: 6px; font-size: 13px; }
        textarea { height: 70px; resize: vertical; }

        .btn-guardar { background: #007bf0; color: white; border: none; padding: 12px 25px; border-radius: 6px; font-weight: bold; cursor: pointer; float: right; font-size: 14px; }
        .btn-guardar:hover { background: #0056b3; }
        .alert { padding: 10px; border-radius: 6px; margin-bottom: 15px; font-size: 13px; text-align: center; font-weight: bold; }
        .alert-exito { background: #d4edda; color: #155724; }
        .alert-error { background: #f8d7da; color: #721c24; }
    </style>
</head>
<body>

<div class="container">
    <div style="margin-bottom: 15px;">
        <a href="menu.php" style="text-decoration: none; color: #007bf0; font-weight: bold; font-size: 14px;">← Volver al Menú Principal</a>
    </div>

    <?php if ($mensaje != ""): ?>
        <div class="alert alert-<?php echo $tipo_mensaje; ?>"><?php echo $mensaje; ?></div>
    <?php endif; ?>

    <div class="layout">
        <!-- Panel Izquierdo: Comunicación -->
        <div class="sidebar-menu">
            <h3>📢 Comunicación</h3>
            <a href="#" onclick="enviarWhatsApp()" class="btn-canal btn-ws">
                <span>💬</span> WhatsApp Directo
            </a>
            <div class="btn-canal"><span>📧</span> Correo Electrónico</div>
            <div class="btn-canal"><span>📱</span> SMS</div>
            <div class="btn-canal"><span>📞</span> Llamada Registrada</div>
        </div>

        <!-- Panel Derecho: Formulario de Cita -->
        <div class="form-panel">
            <h2 style="margin-top:0; color:#007bf0; font-size:20px;">📅 Agendar Nueva Cita</h2>
            
            <!-- Botones de sumar días rápidamente -->
            <div class="quick-days">
                <span style="font-size:12px; align-self:center; font-weight:bold; margin-right:5px;">Sumar días:</span>
                <button type="button" onclick="sumarDias(0)">Hoy</button>
                <button type="button" onclick="sumarDias(1)">+1 día</button>
                <button type="button" onclick="sumarDias(2)">+2 días</button>
                <button type="button" onclick="sumarDias(3)">+3 días</button>
                <button type="button" onclick="sumarDias(5)">+5 días</button>
                <button type="button" onclick="sumarDias(7)">+7 días</button>
            </div>

            <form action="agendar_cita.php" method="POST">
                <div class="grid-2">
                    <!-- Selector de Paciente con Botón Modal -->
<div class="form-group">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px;">
        <label style="margin-bottom: 0;">Paciente:*</label>
        <button type="button" onclick="abrirModalPaciente();" style="color: #28a745; background: #e8f5e9; border: 1px solid #28a745; font-size: 12px; font-weight: bold; padding: 3px 10px; border-radius: 4px; cursor: pointer;">
            + Nuevo Paciente
        </button>
    </div>
    <select id="id_paciente" name="id_paciente" required style="width: 100%;">
        <option value="">-- Buscar o seleccionar paciente --</option>
        <?php while($p = $pacientes->fetch_assoc()): ?>
            <option value="<?php echo $p['identidad']; ?>" data-telefono="<?php echo $p['telefono']; ?>">
                <?php echo $p['nombre_completo'] . " (" . $p['identidad'] . ")"; ?>
            </option>
        <?php endwhile; ?>
    </select>
</div>



                    <div class="form-group">
                        <label>Doctor / Especialista:*</label>
                        <select name="id_medico" required>
                            <option value="">-- Seleccionar Doctor --</option>
                            <?php while($m = $medicos->fetch_assoc()): 
                                $id_med = reset($m); 
                            ?>
                                <option value="<?php echo $id_med; ?>">
                                    <?php echo $m['nombre_completo'] . " (" . $m['especialidad'] . ")"; ?>
                                </option>
                            <?php endwhile; ?>
                        </select>
                    </div>
                </div>

                <div class="grid-2">
                    <div class="form-group">
                        <label>Fecha de la Cita:*</label>
                        <input type="date" id="fecha_cita" name="fecha_cita" required>
                    </div>

                    <div class="form-group">
                        <label>Hora:*</label>
                        <input type="time" id="hora_cita" name="hora_cita" required>
                    </div>
                </div>

                <div class="grid-2">
                    <div class="form-group">
                        <label>Motivo de Consulta:*</label>
                        <input type="text" name="motivo" placeholder="Ej: Limpieza, Ortodoncia, Extracción" required>
                    </div>

                    <div class="form-group">
                        <label>Prioridad:*</label>
                        <div>
                            <label style="font-weight:normal;"><input type="radio" name="prioridad" value="Estandar" checked> Estándar</label>
                            <label style="font-weight:normal; margin-left:10px;"><input type="radio" name="prioridad" value="Alta"> Alta</label>
                            <label style="font-weight:normal; margin-left:10px;"><input type="radio" name="prioridad" value="Maxima"> Máxima</label>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label>Detalles / Observaciones:</label>
                    <textarea name="detalle" placeholder="Notas adicionales sobre la cita..."></textarea>
                </div>

                <button type="submit" class="btn-guardar">Guardar Cita</button>
            </form>
        </div>
    </div>
</div>

<script>
// Fecha de hoy por defecto
document.getElementById('fecha_cita').valueAsDate = new Date();

// Sumar días
function sumarDias(dias) {
    let fecha = new Date();
    fecha.setDate(fecha.getDate() + dias);
    document.getElementById('fecha_cita').valueAsDate = fecha;
}

// Abrir WhatsApp Web con mensaje predeterminado
function enviarWhatsApp() {
    let selectPaciente = document.getElementById('id_paciente');
    let option = selectPaciente.options[selectPaciente.selectedIndex];
    let telefono = option.getAttribute('data-telefono');
    let nombre = option.text;
    let fecha = document.getElementById('fecha_cita').value;
    let hora = document.getElementById('hora_cita').value;

    if (!selectPaciente.value) {
        alert("Por favor selecciona un paciente primero.");
        return;
    }

    if (!telefono) {
        alert("El paciente seleccionado no tiene un teléfono registrado.");
        return;
    }

    let numLimpio = telefono.replace(/[^0-9]/g, '');
    if(numLimpio.length === 8) {
        numLimpio = '504' + numLimpio;
    }

    let mensaje = `Hola ${nombre.trim()}, le saludamos de SOE Odontología. Le recordamos su cita agendada para el día ${fecha} a las ${hora}. Por favor confirmarnos su asistencia.`;
    let url = `https://wa.me/${numLimpio}?text=${encodeURIComponent(mensaje)}`;
    
    window.open(url, '_blank');
}
</script>

<script>
$(document).ready(function() {
    $('#id_paciente').select2({
        placeholder: "-- Buscar o seleccionar paciente --",
        allowClear: true
    });
});
</script>

<script>
$(document).ready(function() {
    // Inicializar Select2
    $('#id_paciente').select2({
        placeholder: "-- Buscar o seleccionar paciente --",
        allowClear: true
    });

    // Guardar nuevo paciente por AJAX sin recargar
    $('#formNuevoPacienteModal').on('submit', function(e) {
        e.preventDefault();
        $('#btnGuardarModal').prop('disabled', true).text('Guardando...');
        
        $.ajax({
            url: 'guardar_paciente_ajax.php',
            type: 'POST',
            data: $(this).serialize(),
            dataType: 'json',
            success: function(response) {
                if(response.exito) {
                    // Crear la nueva opción en Select2 y seleccionarla
                    var newOption = new Option(response.nombre + " (" + response.identidad + ")", response.identidad, true, true);
                    $(newOption).attr('data-telefono', response.telefono);
                    $('#id_paciente').append(newOption).trigger('change');
                    
                    // Limpiar y cerrar modal
                    $('#formNuevoPacienteModal')[0].reset();
                    cerrarModalPaciente();
                    alert('¡Paciente registrado con éxito y seleccionado en la cita!');
                } else {
                    $('#msgModalPaciente').css({'background': '#f8d7da', 'color': '#721c24'}).html(response.mensaje).show();
                }
            },
            error: function() {
                $('#msgModalPaciente').css({'background': '#f8d7da', 'color': '#721c24'}).html('Error al procesar la solicitud.').show();
            },
            complete: function() {
                $('#btnGuardarModal').prop('disabled', false).text('Guardar Paciente');
            }
        });
    });
});

function abrirModalPaciente() {
    $('#msgModalPaciente').hide();
    $('#modalPaciente').css('display', 'flex');
}

function cerrarModalPaciente() {
    $('#modalPaciente').hide();
}

function calcularEdadModal() {
    var fechaNac = document.getElementById('modal_fecha_nac').value;
    if (fechaNac) {
        var hoy = new Date();
        var nacimiento = new Date(fechaNac);
        var edad = hoy.getFullYear() - nacimiento.getFullYear();
        var mes = hoy.getMonth() - nacimiento.getMonth();
        if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) {
            edad--;
        }
        document.getElementById('modal_edad').value = edad >= 0 ? edad : 0;
    } else {
        document.getElementById('modal_edad').value = '';
    }
}
</script>

<!-- VENTANA MODAL PARA REGISTRAR NUEVO PACIENTE -->
<div id="modalPaciente" style="display: none; position: fixed; z-index: 9999; left: 0; top: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.6); align-items: center; justify-content: center; overflow-y: auto; padding: 20px 0;">
    <div style="background: #fff; border-radius: 8px; width: 95%; max-width: 750px; padding: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.3); position: relative; margin: auto; box-sizing: border-box; max-height: 90vh; overflow-y: auto;">
        <span onclick="cerrarModalPaciente();" style="position: absolute; right: 15px; top: 10px; font-size: 24px; font-weight: bold; cursor: pointer; color: #888;">&times;</span>
        <h3 style="margin-top: 0; color: #007bf0; border-bottom: 2px solid #007bf0; padding-bottom: 8px;">➕ Registrar Nuevo Paciente</h3>
        
        <div id="msgModalPaciente" style="display: none; padding: 10px; border-radius: 4px; margin-bottom: 15px; font-size: 13px;"></div>

        <form id="formNuevoPacienteModal">
            <!-- SECCIÓN 1: DATOS PERSONALES -->
            <div style="background: #e9ecef; padding: 8px 12px; font-weight: bold; font-size: 14px; color: #495057; border-radius: 4px; margin-bottom: 15px;">
                Datos Personales
            </div>

            <!-- Identidad y Nombre Completo -->
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 12px;">
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Identidad (DNI):*</label>
                    <input type="text" 
       id="modal_identidad" 
       name="identidad" 
       required 
       autocomplete="off" 
       list="none" 
       placeholder="Ej: 0801-1990-12345" 
       style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Nombre Completo:*</label>
                    <input type="text" name="nombre_completo" required style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
            </div>

            <!-- Fecha Nacimiento, Edad y Género -->
            <div style="display: grid; grid-template-columns: 1fr 0.6fr 1fr; gap: 15px; margin-bottom: 12px;">
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Fecha de Nacimiento:</label>
                    <input type="date" id="modal_fecha_nac" name="fecha_nacimiento" onchange="calcularEdadModal();" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Edad:</label>
                    <input type="number" id="modal_edad" name="edad" readonly placeholder="Años" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; background: #e9ecef; box-sizing: border-box;">
                </div>
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Género:</label>
                    <select name="genero" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                        <option value="Masculino">Masculino</option>
                        <option value="Femenino">Femenino</option>
                        <option value="Otro">Otro</option>
                    </select>
                </div>
            </div>

            <!-- Estado Civil, Ocupación y Teléfono -->
            <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px; margin-bottom: 12px;">
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Estado Civil:</label>
                    <input type="text" name="estado_civil" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Ocupación:</label>
                    <input type="text" name="ocupacion" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Teléfono:*</label>
                    <input type="text" name="telefono" required style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
            </div>

            <!-- Domicilio / Dirección -->
            <div style="margin-bottom: 15px;">
                <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Domicilio / Dirección:</label>
                <textarea name="direccion" rows="2" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; resize: vertical; box-sizing: border-box;"></textarea>
            </div>

            <!-- SECCIÓN 2: MENOR DE EDAD / RESPONSABLE -->
            <div style="background: #e9ecef; padding: 8px 12px; font-weight: bold; font-size: 14px; color: #495057; border-radius: 4px; margin-bottom: 15px;">
                En caso de Menor de Edad / Persona Responsable
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 20px;">
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Nombre del Responsable:</label>
                    <input type="text" name="nombre_responsable" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
                <div>
                    <label style="font-size: 12px; font-weight: bold; display: block; margin-bottom: 4px;">Teléfono del Responsable:</label>
                    <input type="text" name="telefono_responsable" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
            </div>

            <!-- BOTONES -->
            <div style="display: flex; justify-content: flex-end; gap: 10px;">
                <button type="button" onclick="cerrarModalPaciente();" style="background: #6c757d; color: white; border: none; padding: 9px 18px; border-radius: 4px; cursor: pointer;">Cancelar</button>
                <button type="submit" id="btnGuardarModal" style="background: #28a745; color: white; border: none; padding: 9px 18px; border-radius: 4px; cursor: pointer; font-weight: bold;">Guardar Paciente</button>
            </div>
        </form>
    </div>
</div>

</body>
</html>