<?php
date_default_timezone_set('America/Tegucigalpa');
$host     = "localhost";
$usuario  = "root";
$password = "";
$base_datos = "soe_odontologia";

// Crear la conexión
$conexion = new mysqli($host, $usuario, $password, $base_datos);

// Verificar la conexión
if ($conexion->connect_error) {
    die("Error de conexión: " . $conexion->connect_error);
}

// Configurar caracteres en español (tildes, ñ)
$conexion->set_charset("utf8");

// Mensaje de prueba
//echo "¡Conexión exitosa a la base de datos de SOE Odontología!";
?>