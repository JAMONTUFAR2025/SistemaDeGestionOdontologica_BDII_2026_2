<?php
session_start();
include('conexion.php');

header('Content-Type: application/json');

if (!$conexion) {
    echo json_encode(['exito' => false, 'mensaje' => 'Error de conexión a la Base de Datos.']);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $identidad = isset($_POST['identidad']) ? trim($_POST['identidad']) : '';
    $nombre_completo = isset($_POST['nombre_completo']) ? trim($_POST['nombre_completo']) : '';
    $telefono = isset($_POST['telefono']) ? trim($_POST['telefono']) : '';

    if (empty($identidad) || empty($nombre_completo) || empty($telefono)) {
        echo json_encode(['exito' => false, 'mensaje' => 'Completa los campos obligatorios (*).']);
        exit();
    }

    // Escape de cadenas de texto
    $identidad_esc = $conexion->real_escape_string($identidad);
    $nombre_esc = $conexion->real_escape_string($nombre_completo);
    $telefono_esc = $conexion->real_escape_string($telefono);
    
    $fecha_nac = !empty($_POST['fecha_nacimiento']) ? "'" . $conexion->real_escape_string($_POST['fecha_nacimiento']) . "'" : "NULL";
    $genero = !empty($_POST['genero']) ? "'" . $conexion->real_escape_string($_POST['genero']) . "'" : "'Masculino'";

    // Inserción utilizando únicamente las columnas estándar confirmadas
    $sql = "INSERT INTO Pacientes (identidad, nombre_completo, fecha_nacimiento, genero, telefono) 
            VALUES ('$identidad_esc', '$nombre_esc', $fecha_nac, $genero, '$telefono_esc')";

    if ($conexion->query($sql)) {
        echo json_encode([
            'exito' => true,
            'identidad' => $identidad,
            'nombre' => $nombre_completo,
            'telefono' => $telefono
        ]);
    } else {
        if ($conexion->errno == 1062) {
            echo json_encode(['exito' => false, 'mensaje' => 'La identidad ' . $identidad . ' ya se encuentra registrada.']);
        } else {
            echo json_encode(['exito' => false, 'mensaje' => 'Error BD: ' . $conexion->error]);
        }
    }
}