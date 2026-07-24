<?php
// Incluimos la conexión a la base de datos
include('conexion.php');

// Verificamos si los datos fueron enviados mediante el método POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    // Recibimos y limpiamos las variables del formulario
    $identidad            = $_POST['identidad'];
    $nombre_completo      = $_POST['nombre_completo'];
    $fecha_nacimiento     = !empty($_POST['fecha_nacimiento']) ? $_POST['fecha_nacimiento'] : NULL;
    $edad                 = !empty($_POST['edad']) ? $_POST['edad'] : NULL;
    $genero               = $_POST['genero'];
    $estado_civil         = $_POST['estado_civil'];
    $ocupacion            = $_POST['ocupacion'];
    $telefono             = $_POST['telefono'];
    $domicilio            = $_POST['domicilio'];
    $persona_responsable  = $_POST['persona_responsable'];
    $telefono_responsable = $_POST['telefono_responsable'];

    // Preparamos la consulta SQL para evitar errores e inyecciones SQL
    $stmt = $conexion->prepare("INSERT INTO Pacientes 
        (identidad, nombre_completo, fecha_nacimiento, edad, genero, estado_civil, ocupacion, domicilio, telefono, persona_responsable, telefono_responsable) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

    $stmt->bind_param("sssisssssss", 
        $identidad, 
        $nombre_completo, 
        $fecha_nacimiento, 
        $edad, 
        $genero, 
        $estado_civil, 
        $ocupacion, 
        $domicilio, 
        $telefono, 
        $persona_responsable, 
        $telefono_responsable
    );

    // Ejecutamos la consulta
    if ($stmt->execute()) {
        echo "<script>
                alert('¡Paciente registrado con éxito!');
                window.location.href = 'registrar_paciente.php';
              </script>";
    } else {
        echo "Error al registrar paciente: " . $stmt->error;
    }

    $stmt->close();
    $conexion->close();
} else {
    header("Location: registrar_paciente.php");
    exit();
}
?>