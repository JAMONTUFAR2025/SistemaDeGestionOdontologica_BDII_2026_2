<?php
session_start();
// Si ya inició sesión, redirigir directamente al menú
if (isset($_SESSION['usuario_id'])) {
    header("Location: menu.php");
    exit();
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - SOE Odontología</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-image: url('fondo-clinica.jpg');
            background-size: cover;
            background-position: center;
            background-attachment: fixed;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .login-card {
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 30px;
            border-radius: 12px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
            width: 100%;
            max-width: 380px;
            text-align: center;
        }

        .login-card h2 {
            color: #007bf0;
            margin-bottom: 5px;
            font-size: 24px;
        }

        .login-card p {
            color: #666;
            font-size: 13px;
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 18px;
            text-align: left;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: bold;
            color: #333;
            font-size: 14px;
        }

        input {
            width: 100%;
            padding: 11px;
            border: 1px solid #ccc;
            border-radius: 6px;
            box-sizing: border-box;
            font-size: 14px;
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
            margin-top: 10px;
            transition: background 0.3s;
        }

        button:hover {
            background-color: #0056b3;
        }

        .error-msg {
            background: #f8d7da;
            color: #721c24;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            font-size: 13px;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>

<div class="login-card">
    <h2>SOE Odontología</h2>
    <p>Acceso al Sistema Clínico</p>

    <?php if (isset($_GET['error'])): ?>
        <div class="error-msg">Correo o contraseña incorrectos.</div>
    <?php endif; ?>

    <form action="validar_login.php" method="POST">
        <div class="form-group">
            <label for="correo">Correo Electrónico:</label>
            <input type="email" id="correo" name="correo" placeholder="ejemplo@soe.com" required>
        </div>

        <div class="form-group">
            <label for="contrasena">Contraseña:</label>
            <input type="password" id="contrasena" name="contrasena" placeholder="••••••••" required>
        </div>

        <button type="submit">Ingresar</button>
    </form>
</div>

</body>
</html>