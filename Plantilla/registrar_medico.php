<?php
session_start();
// Proteger acceso: solo usuarios logueados
if (!isset($_SESSION['usuario_id'])) {
    header("Location: login.php");
    exit();
}
include('conexion.php');

$mensaje = "";
$tipo_mensaje = "";

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $nombre_completo = trim($_POST['nombre_completo']);
    $identidad       = trim($_POST['identidad']);
    $rol             = $_POST['rol'];
    $especialidad    = trim($_POST['especialidad']);
    $telefono        = trim($_POST['telefono']);
    $correo          = trim($_POST['correo']);
    $contrasena      = trim($_POST['contrasena']);
    $estado          = $_POST['estado'];

    // Comprobar si el correo o la identidad ya existen
    $check_stmt = $conexion->prepare("SELECT id_medico FROM Personal_Medico WHERE correo = ? OR identidad = ?");
    $check_stmt->bind_param("ss", $correo, $identidad);
    $check_stmt->execute();
    $res_check = $check_stmt->get_result();

    if ($res_check->num_rows > 0) {
        $mensaje = "El correo electrónico o el número de identidad ya están registrados.";
        $tipo_mensaje = "error";
    } else {
        // Insertar el nuevo médico / usuario
        $stmt = $conexion->prepare("INSERT INTO Personal_Medico (nombre_completo, identidad, rol, especialidad, telefono, correo, contrasena, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("ssssssss", $nombre_completo, $identidad, $rol, $especialidad, $telefono, $correo, $contrasena, $estado);

        if ($stmt->execute()) {
            $mensaje = "¡Personal Médico registrado exitosamente!";
            $tipo_mensaje = "exito";
        } else {
            $mensaje = "Error al registrar: " . $conexion->error;
            $tipo_mensaje = "error";
        }
    }
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrar Personal Médico - SOE Odontología</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-image: url('fondo-clinica.jpg');
            background-size: cover;
            background-position: center;
            background-attachment: fixed;
            margin: 0;
            padding: 30px 20px;
        }

        .container {
            background: rgba(255, 255, 255, 0.95);
            max-width: 650px;
            margin: 0 auto;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
        }

        h2 {
            color: #007bf0;
            margin-top: 5px;
            margin-bottom: 20px;
            text-align: center;
        }

        .alert {
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 14px;
            text-align: center;
            font-weight: bold;
        }

        .alert-exito {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }

        .full-width {
            grid-column: span 2;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        label {
            font-weight: bold;
            font-size: 13px;
            margin-bottom: 5px;
            color: #333;
        }

        input, select {
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 14px;
            box-sizing: border-box;
        }

        button {
            background-color: #007bf0;
            color: white;
            border: none;
            padding: 12px;
            border-radius: 6px;
            cursor: pointer;
            width: 100%;
            font-size: 16px;
            font-weight: bold;
            margin-top: 15px;
            transition: background 0.3s;
        }

        button:hover {
            background-color: #0056b3;
        }

        .back-link {
            text-decoration: none;
            color: #007bf0;
            font-weight: bold;
            font-size: 14px;
        }
    </style>
</head>
<body>

<div class="container">
    <div style="margin-bottom: 15px;">
        <a href="menu.php" class="back-link">← Volver al Menú Principal</a>
    </div>

    <h2>Registrar Personal Médico / Usuario</h2>

    <?php if ($mensaje != ""): ?>
        <div class="alert alert-<?php echo $tipo_mensaje; ?>">
            <?php echo $mensaje; ?>
        </div>
    <?php endif; ?>

    <form action="registrar_medico.php" method="POST">
        <div class="form-grid">
            
            <div class="form-group full-width">
                <label for="nombre_completo">Nombre Completo:*</label>
                <input type="text" id="nombre_completo" name="nombre_completo" placeholder="Ej: Dr. Carlos Mendoza" required>
            </div>

            <div class="form-group">
                <label for="identidad">Identidad (DNI):*</label>
                <input type="text" id="identidad" name="identidad" placeholder="0801-1995-12345" required>
            </div>

            <div class="form-group">
                <label for="rol">Rol de Usuario:*</label>
                <select id="rol" name="rol" required>
                    <option value="Especialista">Especialista / Doctor</option>
                    <option value="Operativo">Operativo / Recepción</option>
                    <option value="Administrador">Administrador</option>
                </select>
            </div>

            <div class="form-group">
                <label for="especialidad">Especialidad:*</label>
                <input type="text" id="especialidad" name="especialidad" placeholder="Ej: Ortodoncia / General / Asistente" required>
            </div>

            <div class="form-group">
                <label for="telefono">Teléfono:*</label>
                <input type="text" id="telefono" name="telefono" placeholder="9999-9999" required>
            </div>

            <div class="form-group">
                <label for="correo">Correo Electrónico (Login):*</label>
                <input type="email" id="correo" name="correo" placeholder="doctor@soe.com" required>
            </div>

            <div class="form-group">
                <label for="contrasena">Contraseña de Acceso:*</label>
                <input type="password" id="contrasena" name="contrasena" placeholder="••••••••" required>
            </div>

            <div class="form-group full-width">
                <label for="estado">Estado del Usuario:*</label>
                <select id="estado" name="estado" required>
                    <option value="Activo">Activo</option>
                    <option value="Inactivo">Inactivo</option>
                </select>
            </div>

        </div>

        <button type="submit">Guardar Personal Médico</button>
    </form>
</div>

</body>
</html>