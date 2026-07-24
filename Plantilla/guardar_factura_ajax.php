<?php
if (ob_get_length()) ob_clean();
header('Content-Type: application/json; charset=utf-8');
include('conexion.php');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $numero_recibo       = isset($_POST['numero_recibo']) ? trim($_POST['numero_recibo']) : '';
    $identidad_paciente  = isset($_POST['identidad_paciente']) ? trim($_POST['identidad_paciente']) : '';
    $rtn_cliente         = isset($_POST['rtn_cliente']) ? trim($_POST['rtn_cliente']) : '';
    $fecha_emision       = isset($_POST['fecha_emision']) ? $_POST['fecha_emision'] : date('Y-m-d');
    $concepto            = isset($_POST['concepto']) ? trim($_POST['concepto']) : '';
    $suma_neta           = isset($_POST['suma_neta']) ? floatval($_POST['suma_neta']) : 0;
    $total_honorarios    = isset($_POST['total_honorarios']) ? floatval($_POST['total_honorarios']) : 0;
    $total_retenido      = isset($_POST['total_retenido']) ? floatval($_POST['total_retenido']) : 0;
    $total_neto_recibido = isset($_POST['total_neto_recibido']) ? floatval($_POST['total_neto_recibido']) : 0;
    $metodo_pago         = isset($_POST['metodo_pago']) ? trim($_POST['metodo_pago']) : 'Efectivo';

    if (empty($identidad_paciente) || empty($concepto) || $total_neto_recibido <= 0) {
        echo json_encode(['exito' => false, 'mensaje' => 'La identidad, el concepto y un total neto válido son obligatorios.']);
        exit();
    }

    $stmt = $conexion->prepare("INSERT INTO facturacion_recibos (numero_recibo, identidad_paciente, rtn_cliente, fecha_emision, concepto, suma_neta, total_honorarios, total_retenido, total_neto_recibido, metodo_pago) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("sssssdddds", $numero_recibo, $identidad_paciente, $rtn_cliente, $fecha_emision, $concepto, $suma_neta, $total_honorarios, $total_retenido, $total_neto_recibido, $metodo_pago);

    if ($stmt->execute()) {
        echo json_encode(['exito' => true, 'mensaje' => 'Recibo registrado correctamente.']);
    } else {
        echo json_encode(['exito' => false, 'mensaje' => 'Error al guardar el recibo: ' . $conexion->error]);
    }
    
    $stmt->close();
} else {
    echo json_encode(['exito' => false, 'mensaje' => 'Acceso no permitido.']);
}
?>