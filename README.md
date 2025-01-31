# Servidor Web en Java

## Descripción

Este proyecto consiste en la implementación de un servidor web en Java que soporta múltiples solicitudes secuenciales (no concurrentes). El servidor puede leer archivos del disco local y servir páginas HTML, archivos JavaScript, hojas de estilo CSS e imágenes.

Para probar el servidor, se ha desarrollado una aplicación web que incluye comunicación asíncrona con servicios REST en el backend. No se han utilizado frameworks web como Spark o Spring, solo Java y sus librerías de manejo de red.

---

## Instalación

### Prerrequisitos
- Java 17
- Git
- Navegador web
- Maven

### Clonación del repositorio
```sh
 git clone https://github.com/johanestrada01/AREP_LAB01.git
 cd AREP_LAB01
 mvn clean install
 mvn exec:java
```

### Acceso
- Ingresar a 127.0.0.1:35000

### Arquitectura

