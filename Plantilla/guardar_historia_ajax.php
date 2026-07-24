<?php
ob_start();
header('Content-Type: application/json; charset=utf-8');

try {
    if (!file_exists('conexion.php')) {
        throw new Exception("El archivo conexion.php no existe.");
    }
    include('conexion.php');

    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception('Método de solicitud no permitido.');
    }

    $identidad_paciente                     = isset($_POST['identidad_paciente']) ? trim($_POST['identidad_paciente']) : '';
    $id_medico_tratante                     = !empty($_POST['id_medico_tratante']) ? intval($_POST['id_medico_tratante']) : NULL;
    $fecha_apertura                         = !empty($_POST['fecha_apertura']) ? $_POST['fecha_apertura'] : date('Y-m-d');
    $remitido_por                           = isset($_POST['remitido_por']) ? trim($_POST['remitido_por']) : '';
    $motivo_consulta                        = isset($_POST['motivo_consulta']) ? trim($_POST['motivo_consulta']) : '';
    $sintoma_principal                      = isset($_POST['sintoma_principal']) ? trim($_POST['sintoma_principal']) : '';
    $historia_enfermedad_actual             = isset($_POST['historia_enfermedad_actual']) ? trim($_POST['historia_enfermedad_actual']) : '';
    $antecedentes_patologicos               = isset($_POST['antecedentes_patologicos']) ? trim($_POST['antecedentes_patologicos']) : '';
    $antecedentes_odontologicos             = isset($_POST['antecedentes_odontologicos']) ? trim($_POST['antecedentes_odontologicos']) : '';
    $habitos_toxicos                        = isset($_POST['habitos_toxicos']) ? trim($_POST['habitos_toxicos']) : '';
    $alergias                               = isset($_POST['alergias']) ? trim($_POST['alergias']) : '';
    $antecedentes_ginecobstetros            = isset($_POST['antecedentes_ginecobstetros']) ? trim($_POST['antecedentes_ginecobstetros']) : '';
    $antecedentes_quirurgicos_hospitalarios   = isset($_POST['antecedentes_quirurgicos_hospitalarios']) ? trim($_POST['antecedentes_quirurgicos_hospitalarios']) : '';
    $farmacos_uso_habitual                  = isset($_POST['farmacos_uso_habitual']) ? trim($_POST['farmacos_uso_habitual']) : '';
    $reaccion_anestesicos                   = isset($_POST['reaccion_anestesicos']) ? intval($_POST['reaccion_anestesicos']) : 0;
    $especifique_anestesia                  = isset($_POST['especifique_anestesia']) ? trim($_POST['especifique_anestesia']) : '';
    $complicaciones_tratamientos_previos    = isset($_POST['complicaciones_tratamientos_previos']) ? trim($_POST['complicaciones_tratamientos_previos']) : '';
    $habitos_bucales                        = isset($_POST['habitos_bucales']) ? trim($_POST['habitos_bucales']) : '';
    $frecuencia_cepillado                   = isset($_POST['frecuencia_cepillado']) ? trim($_POST['frecuencia_cepillado']) : '';
    $tipo_cepillo_cerdas                    = !empty($_POST['tipo_cepillo_cerdas']) ? $_POST['tipo_cepillo_cerdas'] : NULL;
    $uso_hilo_dental                        = !empty($_POST['uso_hilo_dental']) ? $_POST['uso_hilo_dental'] : NULL;
    $presion_arterial                       = isset($_POST['presion_arterial']) ? trim($_POST['presion_arterial']) : '';
    $pulso_cardiaco                         = isset($_POST['pulso_cardiaco']) ? trim($_POST['pulso_cardiaco']) : '';
    $temperatura                            = isset($_POST['temperatura']) ? trim($_POST['temperatura']) : '';
    $tejidos_blandos_observacion            = isset($_POST['tejidos_blandos_observacion']) ? trim($_POST['tejidos_blandos_observacion']) : '';
    $tipo_mordida                           = !empty($_POST['tipo_mordida']) ? $_POST['tipo_mordida'] : NULL;
    $raw_odontograma = isset($_POST['estado_odontograma']) ? trim($_POST['estado_odontograma']) : '';

// Validar si el texto enviado es un JSON válido; si no lo es, envolverlo en formato JSON
if (empty($raw_odontograma)) {
    $estado_odontograma = json_encode(['notacion' => 'Sin registrar']);
} else {
    // Verificar si ya viene como JSON
    json_decode($raw_odontograma);
    if (json_last_error() === JSON_ERROR_NONE) {
        $estado_odontograma = $raw_odontograma;
    } else {
        // Si ingresaron texto normal (ej: "No se realizo"), se formatea como JSON válido
        $estado_odontograma = json_encode(['detalle' => $raw_odontograma]);
    }
}
    $diagnostico_presuntivo                 = isset($_POST['diagnostico_presuntivo']) ? trim($_POST['diagnostico_presuntivo']) : '';
    $observaciones                          = isset($_POST['observaciones']) ? trim($_POST['observaciones']) : '';

    if (empty($identidad_paciente)) {
        throw new Exception('La identidad del paciente es un campo obligatorio.');
    }

    $sql = "INSERT INTO historia_clinica (
        identidad_paciente, id_medico_tratante, fecha_apertura, remitido_por, motivo_consulta,
        sintoma_principal, historia_enfermedad_actual, antecedentes_patologicos, antecedentes_odontologicos,
        habitos_toxicos, alergias, antecedentes_ginecobstetros, antecedentes_quirurgicos_hospitalarios,
        farmacos_uso_habitual, reaccion_anestesicos, especifique_anestesia, complicaciones_tratamientos_previos,
        habitos_bucales, frecuencia_cepillado, tipo_cepillo_cerdas, uso_hilo_dental, presion_arterial,
        pulso_cardiaco, temperatura, tejidos_blandos_observacion, tipo_mordida, estado_odontograma,
        diagnostico_presuntivo, observaciones
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    $stmt = $conexion->prepare($sql);

    if (!$stmt) {
        throw new Exception('Error al preparar la consulta MySQL: ' . $conexion->error);
    }

    $stmt->bind_param(
        "sissssssssssssissssssssssssss",
        $identidad_paciente, 
        $id_medico_tratante, 
        $fecha_apertura, 
        $remitido_por, 
        $motivo_consulta,
        $sintoma_principal, 
        $historia_enfermedad_actual, 
        $antecedentes_patologicos, 
        $antecedentes_odontologicos,
        $habitos_toxicos, 
        $alergias, 
        $antecedentes_ginecobstetros, 
        $antecedentes_quirurgicos_hospitalarios,
        $farmacos_uso_habitual, 
        $reaccion_anestesicos, 
        $especifique_anestesia, 
        $complicaciones_tratamientos_previos,
        $habitos_bucales, 
        $frecuencia_cepillado, 
        $tipo_cepillo_cerdas, 
        $uso_hilo_dental, 
        $presion_arterial,
        $pulso_cardiaco, 
        $temperatura, 
        $tejidos_blandos_observacion, 
        $tipo_mordida, 
        $estado_odontograma,
        $diagnostico_presuntivo, 
        $observaciones
    );

    if (!$stmt->execute()) {
        throw new Exception('Error al guardar en la base de datos: ' . $stmt->error);
    }

    $stmt->close();

    ob_end_clean();
    echo json_encode(['exito' => true, 'mensaje' => '¡Historia Clínica guardada con éxito!']);

} catch (Exception $e) {
    ob_end_clean();
    echo json_encode(['exito' => false, 'mensaje' => $e->getMessage()]);
}