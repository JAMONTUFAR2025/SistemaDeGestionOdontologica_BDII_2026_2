<?php
session_start();
include('conexion.php');

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $correo = $_POST['correo'];
    $contrasena = $_POST['contrasena'];

    // Buscar el usuario en la tabla Personal_Medico
    $stmt = $conexion->prepare("SELECT id_medico, nombre_completo, contrasena, rol FROM Personal_Medico WHERE correo = ?");
    $stmt->bind_param("s", $correo);
    $stmt->execute();
    $resultado = $stmt->get_result();

    if ($usuario = $resultado->fetch_assoc()) {
        // Verificar si la contraseña coincide
        if ($contrasena === $usuario['contrasena'] || password_verify($contrasena, $usuario['contrasena'])) {
            // Guardar datos en la sesión
            $_SESSION['usuario_id'] = $usuario['id_medico'];
            $_SESSION['usuario_nombre'] = $usuario['nombre_completo'];
            $_SESSION['usuario_rol'] = $usuario['rol'];

            header("Location: menu.php");
            exit();
        }
    }

    // Si los datos son incorrectos
    header("Location: login.php?error=1");
    exit();
} else {
    header("Location: login.php");
    exit();
}
?>