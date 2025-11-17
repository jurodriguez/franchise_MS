# Franchise Microservice

## Descripción
Microservicio para la gestión de franquicias implementado con arquitectura hexagonal (ports and adapters) y reactive programming.

## Arquitectura

Este proyecto está construido bajo un esquema de arquitectura hexagonal (ports and adapters) con las siguientes capas:

### Domain

Esta capa contiene todos los modelos de dominio con la lógica de negocio principal. Aquí se definen:
- Modelos
- Objetos de valor
- Reglas de negocio
- Puertos (interfaces)

### UseCase

Esta capa contiene la lógica de aplicación y los casos de uso del sistema. Implementa las reglas de negocio específicas de la aplicación basadas en el patrón de diseño [Unit of Work y Repository].

Estas clases no pueden existir solas y debe heredarse su comportamiento en los **Driven Adapters**.

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son:
- Conexiones a servicios REST
- Servicios SOAP
- Bases de datos
- Lectura de archivos planos
- Cualquier origen y fuente de datos con la que debamos interactuar

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

### Application

Este módulo es el más externo de la arquitectura, siendo responsable de:
- Ensamblar los distintos módulos
- Resolver las dependencias
- Crear los beans de los casos de uso (UseCases) de forma automática
- Inyectar instancias concretas de las dependencias declaradas
- Iniciar la aplicación (contiene la función "public static void main(String[] args)")

**Los beans de los casos de uso se disponibilizan automáticamente gracias a un '@ComponentScan' ubicado en esta capa.**

## Stack Tecnológico

- Java 11+
- Spring Boot
- Spring WebFlux (Programación Reactiva)
- AWS DynamoDB
- Project Reactor
- JUnit 5 & Mockito para testing

## Prerrequisitos

- Java JDK 11 o superior
- Maven
- AWS CLI configurado (para DynamoDB)
- IDE compatible con Spring Boot (IntelliJ IDEA, Eclipse, VS Code)

## Configuración

### Variables de Entorno Requeridas
```properties
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_REGION=your_region