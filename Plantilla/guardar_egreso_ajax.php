<?php
if (ob_get_length()) ob_clean();
header('Content-Type: application/json; charset=utf-8');
include('conexion.php');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $descripcion    = isset($_POST['descripcion']) ? trim($_POST['descripcion']) : '';
    $monto          = isset($_POST['monto']) ? floatval($_POST['monto']) : 0;
    $fecha          = isset($_POST['fecha']) ? $_POST['fecha'] : date('Y-m-d');
    $comprobante    = isset($_POST['numero_comprobante_factura']) ? trim($_POST['numero_comprobante_factura']) : '';

    if (empty($descripcion) || $monto <= 0) {
        echo json_encode(['exito' => false, 'mensaje' => 'La descripción y un monto válido son obligatorios.']);
        exit();
    }

    $stmt = $conexion->prepare("INSERT INTO egresos_gastos (fecha, descripcion, monto, numero_comprobante_factura) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("ssds", $fecha, $descripcion, $monto, $comprobante);

    if ($stmt->execute()) {
        echo json_encode(['exito' => true, 'mensaje' => 'Egreso registrado correctamente.']);
    } else {
        echo json_encode(['exito' => false, 'mensaje' => 'Error al registrar el egreso: ' . $conexion->error]);
    }
    
    $stmt->close();
} else {
    echo json_encode(['exito' => false, 'mensaje' => 'Acceso no permitido.']);
}
?>