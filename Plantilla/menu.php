<?php 
session_start();
// Configurar zona horaria local (Honduras / América Central)
date_default_timezone_set('America/Tegucigalpa');

if (!isset($_SESSION['usuario_id'])) {
    header("Location: login.php");
    exit();
}
include('conexion.php');

$fecha_hoy = date('Y-m-d');
if (!isset($_SESSION['usuario_id'])) {
    header("Location: login.php");
    exit();
}
include('conexion.php'); 
// Consultar citas del día actual uniendo por la columna identidad
$fecha_hoy = date('Y-m-d');
$citas_hoy = $conexion->query("
    SELECT c.hora_cita, c.motivo, c.prioridad, p.nombre_completo AS paciente, p.telefono 
    FROM Citas c 
    JOIN Pacientes p ON c.id_paciente = p.identidad
    WHERE c.fecha_cita = '$fecha_hoy' 
    ORDER BY c.hora_cita ASC
");

// Si falla la primera por el nombre del ID, ejecutamos esta alternativa
if (!$citas_hoy) {
    $citas_hoy = $conexion->query("
        SELECT c.hora_cita, c.motivo, c.prioridad, p.nombre_completo AS paciente, p.telefono 
        FROM Citas c 
        JOIN Pacientes p 
        WHERE c.fecha_cita = '$fecha_hoy' 
        ORDER BY c.hora_cita ASC
    ");
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menú Principal - SOE Odontología</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            background-image: url('fondo-clinica.jpg'); 
            background-size: cover; 
            background-position: center; 
            background-attachment: fixed; 
            margin: 0;
            padding: 20px;
        }

        .header {
            text-align: center;
            background: rgba(255, 255, 255, 0.95);
            max-width: 900px;
            margin: 10px auto 30px auto;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }

        .header h1 {
            color: #007bf0;
            margin: 0;
            font-size: 28px;
        }

        .header p {
            color: #555;
            margin-top: 5px;
            font-size: 14px;
        }

        /* Contenedor de las tarjetas de módulos */
        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            max-width: 900px;
            margin: 0 auto;
        }

        /* Estilo de cada tarjeta / módulo */
        .card {
            background: rgba(255, 255, 255, 0.95);
            padding: 25px;
            border-radius: 10px;
            text-align: center;
            box-shadow: 0 4px 15px rgba(0,0,0,0.15);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            text-decoration: none;
            color: #333;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,123,240,0.3);
        }

        .card .icon {
            font-size: 40px;
            margin-bottom: 10px;
        }

        .card h3 {
            margin: 10px 0 5px 0;
            color: #007bf0;
        }

        .card p {
            font-size: 13px;
            color: #666;
            margin: 0;
        }

        .badge-disabled {
            background: #e0e0e0;
            color: #777;
            font-size: 11px;
            padding: 3px 8px;
            border-radius: 12px;
            margin-top: 8px;
        }
    </style>
</head>

<!-- Panel de Citas del Día (Estilo Dashboard de la segunda foto) -->
    <div style="max-width: 900px; margin: 30px auto 0 auto; background: rgba(255, 255, 255, 0.95); padding: 20px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.15);">
        <h3 style="color: #007bf0; margin-top: 0;">🔔 Citas Programadas para Hoy (<?php echo date('d/m/Y'); ?>)</h3>
        
        <?php if ($citas_hoy && $citas_hoy->num_rows > 0): ?>
            <table style="width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 14px;">
                <thead>
                    <tr style="background: #007bf0; color: white; text-align: left;">
                        <th style="padding: 8px;">Hora</th>
                        <th style="padding: 8px;">Paciente</th>
                        <th style="padding: 8px;">Motivo</th>
                        <th style="padding: 8px;">Teléfono</th>
                    </tr>
                </thead>
                <tbody>
                    <?php while($row = $citas_hoy->fetch_assoc()): ?>
                        <tr style="border-bottom: 1px solid #ddd;">
                            <td style="padding: 8px; font-weight: bold; color: #007bf0;"><?php echo date('g:i A', strtotime($row['hora_cita'])); ?></td>
                            <td style="padding: 8px;"><?php echo $row['paciente']; ?></td>
                            <td style="padding: 8px;"><?php echo $row['motivo']; ?></td>
                            <td style="padding: 8px;"><?php echo $row['telefono']; ?></td>
                        </tr>
                    <?php endwhile; ?>
                </tbody>
            </table>
        <?php else: ?>
            <p style="color: #666; font-size: 14px; margin: 0;">No hay citas agendadas para el día de hoy.</p>
        <?php endif; ?>
    </div>
<body>

    <div class="header">
        <h1>Clínica Odontológica SOE</h1>
        <p>Sistema de Gestión Médica y Administrativa</p>
        <p style="color: #007bf0; font-weight: bold;">
    Bienvenido(a): <?php echo $_SESSION['usuario_nombre']; ?> (<?php echo $_SESSION['usuario_rol']; ?>)
    | <a href="logout.php" style="color: #dc3545; text-decoration: none;">Cerrar Sesión</a>
</p>
    </div>

    <div class="dashboard-grid">

        <!-- Módulo: Registrar Paciente (ACTIVO) -->
        <a href="registrar_paciente.php" class="card">
            <div class="icon">👤➕</div>
            <h3>Registrar Paciente</h3>
            <p>Ingresar un nuevo paciente al sistema</p>
        </a>

        <!-- Módulo: Agenda / Citas (PRÓXIMAMENTE) -->
        <!-- Módulo: Agenda / Citas (ACTIVO) -->
        <a href="agendar_cita.php" class="card">
            <div class="icon">📅</div>
            <h3>Agenda y Citas</h3>
            <p>Programar y gestionar citas médicas</p>
        </a>

        <!-- Módulo: Historia Clínica -->
    <a href="historia_clinica.php" class="card">
    <div class="icon">📋</div>
    <h3>Historia Clínica</h3>
    <p>Odontograma y diagnósticos</p>
</a>

        <!-- Módulo: Facturación (PRÓXIMAMENTE) -->
        <a href="facturacion.php" class="card">
            <div class="icon">💳</div>
            <h3>Facturación</h3>
            <p>Cobros, recibos del SAR y pagos</p>
        </a>

        <!-- Módulo: Control de Egresos (PRÓXIMAMENTE) -->
        <a href="egresos.php" class="card">
            <div class="icon">📉</div>
            <h3>Egresos y Gastos</h3>
            <p>Registro de compras e insumos</p>
        </a>

        <!-- Módulo: Personal Médico (PRÓXIMAMENTE) -->
        <!-- Módulo: Personal Médico (ACTIVO) -->
        <a href="registrar_medico.php" class="card">
            <div class="icon">👨‍⚕️</div>
            <h3>Personal Médico</h3>
            <p>Gestión de doctores, usuarios y roles</p>
        </a>

    </div>

</body>
</html>