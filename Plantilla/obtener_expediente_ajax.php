<?php
// Limpiar cualquier salida previa para evitar corromper el JSON
if (ob_get_length()) ob_clean();
header('Content-Type: application/json; charset=utf-8');
include('conexion.php');

$identidad = isset($_GET['identidad']) ? trim($_GET['identidad']) : '';

if (empty($identidad)) {
    echo json_encode(['exito' => false, 'mensaje' => 'Por favor ingrese un número de identidad.']);
    exit();
}

// 1. Obtener datos del paciente
$stmt_p = $conexion->prepare("SELECT * FROM pacientes WHERE identidad = ? LIMIT 1");
$stmt_p->bind_param("s", $identidad);
$stmt_p->execute();
$paciente = $stmt_p->get_result()->fetch_assoc();
$stmt_p->close();

// 2. Obtener historia clínica base
$stmt_h = $conexion->prepare("SELECT * FROM historia_clinica WHERE identidad_paciente = ? LIMIT 1");
$stmt_h->bind_param("s", $identidad);
$stmt_h->execute();
$historia = $stmt_h->get_result()->fetch_assoc();
$stmt_h->close();

// 3. Obtener evoluciones y sus procedimientos
$consultas = [];
$stmt_c = $conexion->prepare("SELECT * FROM evolucion_clinica WHERE identidad_paciente = ? ORDER BY fecha_consulta DESC");
if ($stmt_c) {
    $stmt_c->bind_param("s", $identidad);
    $stmt_c->execute();
    $resultado_consultas = $stmt_c->get_result();
    
    while ($row = $resultado_consultas->fetch_assoc()) {
        $id_ev = $row['id_evolucion'];
        $row['procedimientos'] = [];
        
        $stmt_proc = $conexion->prepare("SELECT * FROM procedimientos_ejecutados WHERE id_evolucion = ?");
        if ($stmt_proc) {
            $stmt_proc->bind_param("i", $id_ev);
            $stmt_proc->execute();
            $res_proc = $stmt_proc->get_result();
            while ($proc = $res_proc->fetch_assoc()) {
                $row['procedimientos'][] = $proc;
            }
            $stmt_proc->close();
        }
        
        $consultas[] = $row;
    }
    $stmt_c->close();
}

if ($paciente || $historia) {
    echo json_encode([
        'exito' => true,
        'paciente' => $paciente ? $paciente : ['nombre' => 'Paciente', 'apellido' => 'Registrado', 'identidad' => $identidad],
        'historia' => $historia,
        'consultas' => $consultas
    ]);
} else {
    echo json_encode(['exito' => false, 'mensaje' => 'No se encontraron registros para esta identidad.']);
}
?>