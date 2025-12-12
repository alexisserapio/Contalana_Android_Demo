# Contalana Android *DEMO* 
¡Tu negocio al día!, con **Contalana** lleva la gestión de tu negocio a otro nivel.

App móvil multiplataforma orientada a asistir en la administración, gestión e inventariado de pequeñas y medianas empresas (Pymes) desarrollada en los lenguajes nativos **Kotlin** y **iOS**.

>Conoce el estado de tu negocio al día, lleva el **control de tus productos**, **gestiona los ingresos y gastos** que llevas y **genera pruebas de compra** para tus movimientos. 
*Descubre que más puedes lograr* para darle a tu negocio ese impulso que necesita.

## 📱 Descripción  

Con la app **Contalana**, ¡Tu negocio al momento!

Una app pensada para proporcionar herramientas financieras, de gestión y de analisis de una manera gratuita y accesible para las personas con proyectos emergentes.

>Este proyecto constituye una version preeliminar (**alpha**) a modo de probar y demostrar las técinicas de desarrollo utilizadas en este prototipo funcional de la aplicación móvil **Contalana**, desarrollado de forma nativa para **Android** usando **Kotlin**.  

### Se planea el lanzamiento de la app para el primer trimestre de 2026.

El objetivo del demo es construir una base sólida para la interfaz de usuario, la navegación y la lógica de negocio inicial de la aplicación, sirviendo para demostración de la app proxima y del desarrollo de la misma.

>Si deseas consultar la versión para **iOS** da click en el siguiente enlace: https://github.com/alexisserapio/Contalana_Demo_Swift

## 🚀 Características
El objetivo del demo es construir una base sólida para la interfaz de usuario, la navegación y la lógica de negocio inicial de la aplicación, sirviendo para demostración de la app proxima y del desarrollo de la misma.

Con el demo de la app el usuario puede:

- Efectuar el **manejo del inventario**, manteniendo un registro detallado y ordenado de los productos dentro del negocio.
- Implementar **gestión financiera** para su negocio y **registrar movimientos operativos** de una manera sencilla e intuitiva.
- Realizar la **planeación y el control diario del negocio**, consultando de manera visual el desempeño de su negocio, **configurar recordatorios** de pagos a proveedores, **agendar entregas** próximas o pedidos, etc.

*Caracteristicas planeadas para la versión final:*
- **Crear un perfil de usuario y de negocio** para poder **compartir pruebas de compra de manera electronica**, haciendo uso de redes sociales como Whatsapp o Facebook Messenger.


## 💻 Tech Stack 
- Se desarrolló utilizando **Android Studio** y el lenguaje nativo **Kotlin**.
- Arquitectura siguiendo los patrones de diseño **MVVM** y **Singleton**.
- Se hace uso de Repositorios para acceder correctamente a las instancias Singleton de edición de la base de datos..
- **DataStore Preferences** para almacenamiento de valores *bool* como bandera para identificar eventos ya ejecutados.
- Aplicación de la base de datos utilizando **SQLite** mediante **Room**.
- Uso de **Google Analytics** mediante **Firebase** de Google.
- **Coroutines** para programación asíncrona y evitar "bloqueos" de la UI entre la respuesta de la base de datos y los servicios de firebase.
- Interfaz basada en **Material Design**.
- Navegación entre pantallas haciendo uso de `Fragments` y `Activities`.
- Proyecto configurado con **Gradle Kotlin DSL**  
- Compatibilidad con **API nivel 24 (Android 7.0)** o superior.
- En el proceso de desarrollo se implemento control de versionamiento utilizando **Git** y manejo de repositorios en **Github**.
 ### Adciones del Módulo 9
 - Se añade Inicio de Sesión con Firebase Auth con el método personal de email, constraseña y con Cuenta Google
 - Se añade lifeCycleScope haciendo uso de diferentes hilos como Main y IO.

## ⚙️ Requisitos  
- **Android Studio** (versión Arctic Fox o superior)  
- **JDK 11** o compatible  
- Dispositivo o emulador Android con **API 24+**

## 📲 In App 
- Observamos el icono de la app.
- Al iniciar la aplicación, se nos da la bienvenida y se nos invita a continuar si aceptamos los términos y condiciones de la app así como el aviso de privacidad que puedes consultar aquí: https://alexisserapio.github.io/.

- Posteriormente, se nos da un recorrido por las caracteristicas principales con las que cuenta la app.

- Finalmente decidimos el nombre a utilizar para nuestro negocio y una vez con él, procedemos a realizar un formulario donde podremos conocer un poco más con lo que trabajamos para poder obtener un mejor desempeño de análisis.

- Una vez con esta información conocemos el menú principal de la aplicación, en donde encontramos un TabBar para poder navegar a través de las distintas actividades de la app. 

