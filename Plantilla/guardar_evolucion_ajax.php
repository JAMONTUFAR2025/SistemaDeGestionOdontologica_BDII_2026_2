<?php
session_start();
include('conexion.php');

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $identidad_paciente        = isset($_POST['identidad_paciente']) ? trim($_POST['identidad_paciente']) : '';
    $id_medico                 = isset($_POST['id_medico']) ? intval($_POST['id_medico']) : 1; 
    $fecha_consulta            = isset($_POST['fecha_consulta']) ? $_POST['fecha_consulta'] : date('Y-m-d H:i:s');
    $presion_arterial          = isset($_POST['presion_arterial']) ? trim($_POST['presion_arterial']) : '';
    $pulso_cardiaco            = isset($_POST['pulso_cardiaco']) ? trim($_POST['pulso_cardiaco']) : '';
    $temperatura               = isset($_POST['temperatura']) ? trim($_POST['temperatura']) : '';
    $motivo_consulta           = isset($_POST['motivo_consulta']) ? trim($_POST['motivo_consulta']) : '';
    $sintoma_principal         = isset($_POST['sintoma_principal']) ? trim($_POST['sintoma_principal']) : '';
    $diagnostico               = isset($_POST['diagnostico']) ? trim($_POST['diagnostico']) : '';
    $tratamiento               = isset($_POST['tratamiento_realizado']) ? trim($_POST['tratamiento_realizado']) : '';
    
    // Nuevos campos para procedimientos
    $pieza_dental              = isset($_POST['pieza_dental']) ? trim($_POST['pieza_dental']) : '';
    $descripcion_procedimiento = isset($_POST['descripcion_procedimiento']) ? trim($_POST['descripcion_procedimiento']) : '';

    if (empty($identidad_paciente) || empty($motivo_consulta)) {
        echo json_encode(['exito' => false, 'mensaje' => 'Por favor complete los campos obligatorios (*).']);
        exit();
    }

    // 1. Guardar la Evolución Clínica
    $stmt = $conexion->prepare("INSERT INTO evolucion_clinica 
        (identidad_paciente, id_medico, fecha_consulta, presion_arterial, pulso_cardiaco, temperatura, motivo_consulta, sintoma_principal, diagnostico, tratamiento_realizado) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

    $stmt->bind_param("sissssssss", 
        $identidad_paciente, $id_medico, $fecha_consulta, 
        $presion_arterial, $pulso_cardiaco, $temperatura, 
        $motivo_consulta, $sintoma_principal, $diagnostico, $tratamiento
    );

    if ($stmt->execute()) {
        // Obtener el ID de la evolución recién creada
        $id_evolucion = $stmt->insert_id;
        $stmt->close();

        // 2. Si se ingresó un procedimiento, guardarlo en su tabla correspondiente
        if (!empty(trim($descripcion_procedimiento))) {
            $stmt_proc = $conexion->prepare("INSERT INTO procedimientos_ejecutados (id_evolucion, identidad_paciente, pieza_dental, descripcion_procedimiento) VALUES (?, ?, ?, ?)");
            $stmt_proc->bind_param("isss", $id_evolucion, $identidad_paciente, $pieza_dental, $descripcion_procedimiento);
            $stmt_proc->execute();
            $stmt_proc->close();
        }

        echo json_encode(['exito' => true, 'mensaje' => 'Evolución y procedimiento registrados correctamente.']);
    } else {
        echo json_encode(['exito' => false, 'mensaje' => 'Error al guardar: ' . $conexion->error]);
        $stmt->close();
    }
} else {
    echo json_encode(['exito' => false, 'mensaje' => 'Acceso no permitido.']);
}
?>