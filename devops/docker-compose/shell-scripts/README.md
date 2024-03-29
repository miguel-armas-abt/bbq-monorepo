> 📌 Utilice una shell compatible con Unix (PowerShell o Git bash) para ejecutar scripts `.sh`

# CREAR DOCKER-COMPOSE

> ⚙️ **Actualizar variables de entorno**
> <br>Las variables de entorno y scripts de inicialización de BD para cada uno de los servicios están definidas en el siguiente directorio.
> ```shell script 
> cd ./../../environment
> ```

> ⚙️ **Actualizar parámetros de Docker Compose**
> <br>Los parámetros de configuración Docker Compose para cada uno de los servicios están definidos en el siguiente archivo `csv`.
> <br><br>Utilice `nano` para Unix
> ```shell script 
> notepad ./../../environment/docker/containers-to-run.csv #Windows
> ```
>
> 💡 **Notas**:
> - Puede utilizar `#` para comentar las líneas que desea ignorar.
> - El archivo `.csv` cuenta con las siguientes columnas.
>   - `APP_NAME`: Nombre del servicio.
>   - `DOCKER_IMAGE`: Imagen de Docker.
>   - `DEPENDENCIES`: Servicios de los que depende la ejecución del servicio. (separados por punto y coma `;`). Coloque `null` si es que no aplica.
>   - `HOST_PORT`: Puerto de escucha local.
>   - `CONTAINER_PORT`: Puerto del contenedor.
>   - `VOLUMES`: Volúmenes (separados por punto y coma `;`). Coloque `null` si es que no aplica.

> ▶️ **Crear / Actualizar Docker Compose**
> ```shell script 
> ./docker-compose-builder.sh
> ```