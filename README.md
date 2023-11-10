# Despliegue
Esta guía le ayudará a configurar y desplegar los servicios de BBQ

- [1. Clonar repositorio](#1-clonar-repositorio)
- [2. Compilar codigo fuente](#2-compilar-codigo-fuente)
- [3. Despliegue local](#3-despliegue-local)
- [4. Orquestacion con Docker Compose](#4-orquestacion-con-docker-compose)
- [5. Orquestacion con Kubernetes](#5-orquestacion-con-kubernetes)
- [6. Conexion a las bases de datos](#6-conexion-a-base-de-datos)

# 1. Clonar repositorio
El repositorio del proyecto tiene las siguientes ramas:
- `feature/<feature-name>`: Contiene el código fuente en su versión de desarrollo
- `main`: Contiene la última versión estable del código fuente
- `config-server`: Contiene los archivos de configuración de los servicios

```shell script
git checkout -b feature/<feature-name>
cd bbq-monorepo 
git clone -b main <URL> bbq-monorepo
```
URL: <https://github.com/miguel-armas-abt/demo-microservices-bbq.git>

# 2. Compilar codigo fuente
El código fuente de los servicios web está en el directorio `/services`:

```javascript
    bbq-monorepo
    └───services
        ├───bbq-parent-v1
        ├───bbq-support-v1
        ├───business-services
        │   ├───business-menu-option-v1
        │   ├───business-dining-room-order-v1
        │   └─── ...
        └───infrastructure-services
            ├───api-gateway-v1
            ├───config-server-v1
            ├───registry-discovery-server-v1
            └─── ...
```

- `bbq-parent-v1`: Proyecto `parent module` para los servicios web que fueron implementados con Spring Boot
- `bbq-support-v1`: Proyecto no ejecutable que centraliza las utilidades requeridas por los servicios web implementados con Spring Boot
- `business-services`: Directorio que contiene los servicios web de negocio
- `infrastructure-services`: Directorio que contiene los servicios web de infraestructura

> **NOTA**: Para que los servicios web de negocio e infraestructura compilen es necesario compilar previamente los proyectos `bbq-parent-v1` y `bbq-support-v1`.

# 3. Despliegue local
Para desplegar localmente los servicios web es necesario ejecutar previamente los servicios de infraestructura 
`registry-discovery-server-v1`, `config-server-v1` y `api-gateway-v1`. 

Los puertos definidos para cada servicio web son los siguientes:

| Web service                  | Port   |
|------------------------------|--------|
| registry-discovery-server-v1 | `8761` |
| config-server-v1             | `8888` |
| api-gateway-v1               | `8010` |
| auth-adapter-v1              | `8011` |
| menu-v1                      | `8012` |
| table-placement-v1           | `8013` |
| invoice-v1                   | `8014` |
| payment-v1                   | `8015` |
| menu-v2                      | `8016` |
| menu-v3                      | `8017` |

# 4. Orquestacion con Docker Compose
## 4.1. Construir imágenes
Servicios de infraestructura:
```shell script
docker build -t miguelarmasabt/registry-discovery-server-v1:0.0.1-SNAPSHOT ./services/infrastructure-services/registry-discovery-server-v1
docker build -t miguelarmasabt/config-server-v1:0.0.1-SNAPSHOT ./services/infrastructure-services/config-server-v1
docker build -t miguelarmasabt/auth-adapter-v1:0.0.1-SNAPSHOT ./services/infrastructure-services/auth-adapter-v1
docker build -t miguelarmasabt/api-gateway-v1:0.0.1-SNAPSHOT ./services/infrastructure-services/api-gateway-v1
```

Servicios de negocio:
```shell script
docker build -t miguelarmasabt/business-menu-option-v1:0.0.1-SNAPSHOT ./services/business-services/business-menu-option-v1
docker build -t miguelarmasabt/business-dining-room-order-v1:0.0.1-SNAPSHOT ./services/business-services/business-dining-room-order-v1
docker build -f ./services/business-services/business-menu-option-v2/src/main/docker/Dockerfile.jvm -t miguelarmasabt/business-menu-option-v2:0.0.1-SNAPSHOT ./services/business-services/business-menu-option-v2
```

## 4.2. Iniciar orquestación
Para forzar la recreación de los servicios utilice el flag `--force-recreate`
```shell script
docker-compose -f ./devops/docker-compose/docker-compose.yml up -d
```

## 4.3. Detener orquestación
```shell script
docker-compose -f ./devops/docker-compose/docker-compose.yml stop
```

# 5. Orquestacion con Kubernetes
Si usted requiere asignar más memoria RAM a Docker Desktop, cree el archivo `.wslconfig` en la ruta de usuario
`C:\Users\<username>\ `, agregue el siguiente contenido y reinicie Docker Desktop.
```javascript
[wsl2]
memory=3072MB
processors=5
```

Encienda el clúster de Kubernetes de Minikube
```shell script
minikube start --memory=2816 --cpus=4
```

> **NOTA**: Desactive el filtro de autenticación en api-gateway-v1. Revise el README correspondiente para más información.

## 5.1. Construir imágenes
Las imágenes de nuestros servicios deben estar disponibles en el clúster de Kubernetes de Minikube, para ello 
establecemos el entorno de Docker de Minikube en nuestra shell y seguidamente construimos las imágenes en la misma
sesión de la shell.

Servicios de infraestructura:
```shell script
docker build -t miguelarmasabt/registry-discovery-server-v1:0.0.1-SNAPSHOT ./services/infrastructure-services/registry-discovery-server-v1
docker build -t miguelarmasabt/config-server-v1:0.0.1-SNAPSHOT ./services/infrastructure-services/config-server-v1
docker build -t miguelarmasabt/api-gateway-v1:0.0.1-SNAPSHOT ./services/infrastructure-services/api-gateway-v1
Invoke-Expression ((minikube docker-env) -join "`n")
```

Servicios de negocio:
```shell script
docker build -t miguelarmasabt/business-menu-option-v1:0.0.1-SNAPSHOT ./services/business-services/business-menu-option-v1
Invoke-Expression ((minikube docker-env) -join "`n")
```

A continuación, abrimos una shell de Minikube y revisamos que las imágenes hayan sido creadas.
```shell script
docker images
minikube ssh
```

## 5.2. Iniciar orquestación:
```shell script
kubectl apply -f ./devops/k8s/mysql_db/
kubectl apply -f ./devops/k8s/registry-discovery-server-v1/
kubectl apply -f ./devops/k8s/config-server-v1/
kubectl apply -f ./devops/k8s/api-gateway-v1/
kubectl apply -f ./devops/k8s/business-menu-option-v1/
```

Usted puede obtener la URL del servicio `api-gateway-v1` con el siguiente comando: `minikube service --url api-gateway-v1`

## 5.3. Eliminar orquestación:
```shell script
kubectl delete -f ./devops/k8s/mysql_db/
kubectl delete -f ./devops/k8s/registry-discovery-server-v1/
kubectl delete -f ./devops/k8s/config-server-v1/
kubectl delete -f ./devops/k8s/api-gateway-v1/
kubectl delete -f ./devops/k8s/business-menu-option-v1/
```

# 6. Conexion a base de datos
Utilice DBeaver para conectarse a las bases de datos relacionales.
## 6.1. MYSQL
| Parámetro         | Valor (Docker Compose)                         | Valor (Kubernetes)                                     |   
|-------------------|------------------------------------------------|--------------------------------------------------------|
| Server Host       | `localhost`                                    | `localhost`                                            |
| Port              | `3306`                                         | Puerto de Minikube: `minikube service --url bbq-mysql` |
| Database          | `db_menu_options?allowPublicKeyRetrieval=true` | `db_menu_options?allowPublicKeyRetrieval=true`         |
| Nombre de usuario | `root` o  `bbq_user`                           | `root` o  `bbq_user`                                   |
| Contraseña        | `qwerty`                                       | `qwerty`                                               |

## 6.2. PostgreSQL
- Ubique la pestaña `PostgreSQL` y active la opción `Show all database`.

| Parámetro         | Valor (Docker Compose)         | Valor (Kubernetes)                                        |   
|-------------------|--------------------------------|-----------------------------------------------------------|
| Connect by        | `HOST`                         | `HOST`                                                    |
| Host              | `localhost`                    | `localhost`                                               |
| Port              | `5432`                         | Puerto de Minikube: `minikube service --url bbq-postgres` |
| Database          | `db_dining_room_orders`        | `db_dining_room_orders`                                   |
| Nombre de usuario | `postgres` o  `bbq_user`       | `postgres` o  `bbq_user`                                  |
| Contraseña        | `qwerty`                       | `qwerty`                                                  |

