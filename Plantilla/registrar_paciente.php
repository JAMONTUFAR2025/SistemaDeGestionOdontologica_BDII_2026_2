<?php include('conexion.php'); ?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro de Pacientes - SOE Odontología</title>
    <style>
        body { 
    font-family: Arial, sans-serif; 
    background-image: url('fondo-clinica.jpg'); 
    background-size: cover; 
    background-position: center; 
    background-attachment: fixed; 
    margin: 20px; 
}
        .container { 
    max-width: 800px; 
    margin: 30px auto; 
    background: rgba(255, 255, 255, 0.95); 
    padding: 25px; 
    border-radius: 8px; 
    box-shadow: 0 4px 15px rgba(0,0,0,0.2); 
}
        h2 { color: #007bf0; text-align: center; margin-bottom: 20px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; color: #333; }
        input, select, textarea { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .row { display: flex; gap: 15px; }
        .col { flex: 1; }
        button { background-color: #28a745; color: white; border: none; padding: 12px; border-radius: 4px; cursor: pointer; width: 100%; font-size: 16px; margin-top: 15px; }
        button:hover { background-color: #218838; }
        .section-title { background: #e9ecef; padding: 8px; border-radius: 4px; font-weight: bold; margin-top: 20px; margin-bottom: 15px; color: #495057; }
    </style>
</head>
<body>

<div class="container">
    <div style="margin-bottom: 15px;">
    <a href="menu.php" style="text-decoration: none; color: #007bf0; font-weight: bold; font-size: 14px;">
        ← Volver al Menú Principal
    </a>
</div>
    <h2>Registro de Nuevo Paciente</h2>
    
    <form action="guardar_paciente.php" method="POST">
        
        <div class="section-title">Datos Personales</div>
        
        <div class="row">
            <div class="col form-group">
                <label for="identidad">Identidad (DNI):</label>
                <input type="text" id="identidad" name="identidad" placeholder="Ej: 0801-1990-12345" required>
            </div>
            <div class="col form-group">
                <label for="nombre_completo">Nombre Completo:</label>
                <input type="text" id="nombre_completo" name="nombre_completo" required>
            </div>
        </div>

        <div class="row">
            <div class="col form-group">
                <label for="fecha_nacimiento">Fecha de Nacimiento:</label>
                <input type="date" id="fecha_nacimiento" name="fecha_nacimiento">
            </div>
            <div class="col form-group">
                <label for="edad">Edad:</label>
                <input type="number" id="edad" name="edad" placeholder="Años">
            </div>
            <div class="col form-group">
                <label for="genero">Género:</label>
                <select id="genero" name="genero">
                    <option value="M">Masculino</option>
                    <option value="F">Femenino</option>
                </select>
            </div>
        </div>

        <div class="row">
            <div class="col form-group">
                <label for="estado_civil">Estado Civil:</label>
                <input type="text" id="estado_civil" name="estado_civil">
            </div>
            <div class="col form-group">
                <label for="ocupacion">Ocupación:</label>
                <input type="text" id="ocupacion" name="ocupacion">
            </div>
            <div class="col form-group">
                <label for="telefono">Teléfono:</label>
                <input type="text" id="telefono" name="telefono" required>
            </div>
        </div>

        <div class="form-group">
            <label for="domicilio">Domicilio / Dirección:</label>
            <textarea id="domicilio" name="domicilio" rows="2"></textarea>
        </div>

        <div class="section-title">En caso de Menor de Edad / Persona Responsable</div>
        
        <div class="row">
            <div class="col form-group">
                <label for="persona_responsable">Nombre del Responsable:</label>
                <input type="text" id="persona_responsable" name="persona_responsable">
            </div>
            <div class="col form-group">
                <label for="telefono_responsable">Teléfono del Responsable:</label>
                <input type="text" id="telefono_responsable" name="telefono_responsable">
            </div>
        </div>

        <button type="submit">Guardar Paciente</button>
    </form>
</div>

</body>
</html>