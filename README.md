# CFP01 - HelpDesk IT

## Entrega
Entrega 1 - Semana 3: Generación de archivos de entrada.

## Objetivo
Implementar `GenerateInfoFiles` para generar datos pseudoaleatorios y coherentes que serán procesados en las siguientes entregas del proyecto HelpDesk IT.

## Archivos generados
- `data/tecnicos.csv`
- `data/tickets.txt`

## Formatos
### tecnicos.csv
`idTecnico;nombre;apellido;especialidad`

### tickets.txt
`idTicket;idTecnico;fechaApertura;fechaCierre;prioridad`

## Ejecución
Desde NetBeans:
1. Abrir el proyecto como proyecto Maven.
2. Ejecutar `GenerateInfoFiles`.
3. Verificar la carpeta `data`.

Desde terminal:
```text
mvn clean compile
mvn exec:java -Dexec.mainClass="com.mycompany.cfp01_project.GenerateInfoFiles"
```

> La guía académica exige Java 8. El proyecto está configurado para compilar con source/target 8.

## Nota
El modelo de datos de HelpDesk es una adaptación de la idea 13 del banco de proyectos. La estructura detallada de campos es una decisión de diseño para que los archivos generados puedan utilizarse posteriormente para calcular tiempos de resolución y KPIs.
