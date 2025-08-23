```
Programa de Ingeniería de Sistemas y Computación
Universidad del Quindío

Título: Proyecto Final del espacio académico
Duración estimada: 60
Docente: Rafael Perez Grisales
```

## 🎯 **Objetivo**

Desarrollar una aplicación web para la gestión de alojamientos (casas, apartamentos, fincas), reservas y comentarios, con roles diferenciados para usuarios y anfitriones, utilizando Spring Boot (JPA) y Angular.

---

## ✅ **Funcionalidades esenciales (obligatorias)**

### **Roles y Acciones**  
- **Usuario**:  
  - Registrarse e iniciar sesión.  
  - Buscar alojamientos disponibles (por ciudad, fechas, precio).  
  - Realizar reservas (seleccionar fechas, confirmar detalles).  
  - Cancelar reservas (sujeto a políticas).  
  - Dejar comentarios y calificaciones (solo después de una estadía completada).  
  - Ver su historial de reservas (activas, pasadas, canceladas).  
  - Ver detalles completos de cada alojamiento (galería, calendario de disponibilidad, comentarios, etc).

- **Anfitrión (Dueño de alojamiento)**:  
  - Registrarse e iniciar sesión (con rol "anfitrión").  
  - Gestionar sus alojamientos (CRUD). 
    - Ver la lista de sus propios alojamientos.
    - Crear nuevo alojamiento o editar uno existente.
    - Eliminar: Solo si no tiene reservas futuras. Además, la eliminación se debe hacer internamente en la base de datos por medio de un estado "eliminado" (soft delete).
  - Ver métricas básicas por alojamiento: Número de reservas, promedio de calificaciones (aplicar filtro por rango de fechas). 
  - Ver reservas de sus alojamientos (filtros por fechas, estado).  
  - Aprobar/rechazar solicitudes de reserva (opcional, si se implementa flujo de confirmación manual).  
  - Responder a comentarios de los huéspedes.  

### **Gestión de usuarios**:

- **Registro**:  
  - Campos obligatorios: nombre, email (único), contraseña (encriptada), teléfono, rol, fecha de nacimiento.  
  - Validación: formato de email, contraseña segura (mínimo 8 caracteres, mayúsculas/números).  
- **Autenticación**: JWT para sesiones persistentes.  
- **Edición de perfil**:  
  - Usuario: Actualizar nombre, teléfono, foto de perfil.  
  - Anfitrión: Añadir datos adicionales (ejemplo: descripción personal, documentos legales si aplica).
- **Cambiar contraseña**:
  - Si el usuario olvida su contraseña, deberá poder restablecerla. Para ello, se le enviará un código de recuperación a su correo electrónico. Este código deberá ingresarse en la página de recuperación de contraseña junto con la nueva contraseña deseada. Este código tendrá una validez de 15 minutos, si se vence deberá solicitar uno nuevo.
  - Si el usuario desea cambiar su contraseña por decisión propia, podrá hacerlo ingresando su contraseña actual y la nueva contraseña que desea establecer.


### **Gestión de alojamientos**:

#### **Elementos del Alojamiento**  
- **Atributos básicos**:  
  - Título, descripción detallada.  
  - Ubicación: ciudad, dirección y ubicación exacta (latitud y longitud).  
  - Precio por noche, capacidad máxima, servicios (wifi, cocina, piscina, etc).  
  - Imágenes (mínimo 1, máximo 10, con imagen principal destacada).  
- **Relaciones**:  
  - Un anfitrión puede tener múltiples alojamientos.  
  - Cada alojamiento tiene una lista de reservas y comentarios.

### **Reservas**:

#### **Creación de Reserva**  
- **Usuario**:  
   - Selecciona fechas (check-in/check-out) en el calendario interactivo, e indica el número de huéspedes.  
   - El sistema valida:  
     - Disponibilidad (no solapamiento con otras reservas).  
     - Mínimo 1 noche.  
     - No se pueden reservar fechas pasadas.
     - Capacidad máxima: No se puede superar el número de huéspedes permitido.
   - Confirma reserva: Recibe correo con detalles. 
   - Puede ver el estado de su reserva (Pendiente, Confirmada, Cancelada, Completada).
  
- **Anfitrión**:  
   - Recibe notificación (email o en-app) de nueva reserva.  
   - Puede ver todas las reservas de su alojamiento en una vista de calendario o lista.  

#### **Cancelación de Reserva**    
- **Usuario**:  
   - Puede cancelar hasta 48 horas antes del check-in.

- **Anfitrión**:  
   - Recibe notificación (email o en-app) de cancelación de reserva.   
   - Las reservas canceladas aparecen en el historial con estado "Cancelada".  

#### **Listado de reservas**

- **Usuario y Anfitrión**: 
   - Listado de sus reservas de la más reciente a la más antigua.  
   - Se debe permitir aplicar diferentes filtros a listado de reservas.

### **Comentarios y calificaciones**:

- **Usuario**:  
  - Solo puede comentar si tuvo una reserva completada (fecha de check-out pasada).  
  - Máximo 1 comentario por reserva.  
  - Campos:  
    - Calificación (1-5 estrellas, obligatorio).  
    - Comentario (texto, máximo 500 caracteres).  
  - Puede ver el listado de comentarios de un alojamiento ordenados por fecha (más recientes primero).   
  - Debe poder ver el promedio de calificaciones del alojamiento
  
- **Anfitrión**:  
  - Puede responder a comentarios (ejemplo: "Gracias por tu feedback").  
  - Puede ver el listado de comentarios de un alojamiento ordenados por fecha (más recientes primero).   
  - Recibe notificación (email o en-app) de nuevos comentarios. 
  - Debe poder ver el promedio de calificaciones del alojamiento.

### **Búsqueda de alojamientos**:

#### **Filtros Disponibles**  
- **Por ciudad**: Búsqueda predictiva (ejemplo: "Bogotá" sugiere "Bogotá D.C.").  
- **Por fechas**: Muestra solo alojamientos disponibles en ese rango.  
- **Por precio**: Deslizador para rango mínimo-máximo (ejemplo: $50 - $200/noche).  
- **Por servicios**: Filtros adicionales (ejemplo: WiFi, piscina, mascotas permitidas).  

### **Vista de Resultados**  
- Tarjetas con imagen principal, precio, ubicación y calificación promedio. 
- Mapa con la ubicación exacta de cada alojamiento.
- Para todas las listas se debe usar paginación (10 resultados por página). Recuerde ignorar aquellos alojamientos cuyo estado sea "eliminado".
- Al hacer clic en un alojamiento, se accede a su detalle completo (galería, descripción, mapa con ubicación exacta, calendario de disponibilidad, comentarios).

---

## 🟡 **Funcionalidades opcionales**

Las siguientes funcionalidades no son obligatorias, pero pueden ser consideradas como retos adicionales. 

### **1. Descuentos en Fechas Especiales**
Requiere lógica adicional y estructura de promociones.

- **Anfitrión**: Puede crear promociones (ejemplo: "10% off en diciembre").  
- **Usuario**: Ve el precio con descuento en los resultados de búsqueda.  

### **2. Sistema de Recomendaciones**  
Alta complejidad si se quiere hacer bien (requiere análisis de patrones, similaridad, etc.).

- **Usuario**: Recibe sugerencias basadas en:  
  - Historial de búsquedas.  
  - Alojamientos similares a sus reservas pasadas.  

### **3. Chat en Tiempo Real**  
Requiere WebSockets o tecnología similar.

- **Usuario y Anfitrión**: Pueden comunicarse después de una reserva confirmada.  
- Notificaciones en tiempo real (ejemplo: "Nuevo mensaje de [Usuario]").  

### **4. Pagos en Línea**  
Requiere afiliación a pasarelas de pagos.
- **Usuario**:  
  - Simula pago con tarjeta (entorno de desarrollo).  
  - Recibe confirmación automática de reserva.  
- **Anfitrión**: 
  - Ve el estado "Pagado" en las reservas.   

### **5. Gestión de listas de favoritos**
- **Usuario**:
  - Puede marcar alojamientos como “Favoritos” para revisarlos más tarde.
  - Puede acceder a una sección con todos sus alojamientos favoritos.
- **Anfitrión**:
  - Puede ver cuántos usuarios han marcado su alojamiento como favorito (métrica útil).

### **6. Recordatorios automáticos**
- **Sistema**:
  - Envía un email o notificación push al usuario con recordatorios previos al check-in.
  - Envía notificaciones al anfitrión antes de la llegada del huésped.

### **7. Cupones de descuento personalizados**
- **Anfitrión**:
  - Crea códigos promocionales para descuentos en reservas.
- **Usuario**:
  - Ingresa el cupón al momento de reservar y ve el descuento aplicado.

---

## ⚠️ Para tener en cuenta

- El proyecto se debe implementar usando Spring Boot en el backend y Angular en el frontend. Así como MariaDB para la gestión de los datos. 
- Para el manejo de imágenes se debe hacer uso de un servicio externo, puede ser Cloudinary, Firebase, AWS S3, Google Cloud Storage, etc.
- El código fuente del proyecto debe estar en un repositorio de Github. Todos los integrantes del grupo deben contribuir en el desarrollo del proyecto. 
- Para el manejo de los mapas se recomienda usar Mapbox (https://www.mapbox.com/).
- **Debe elegir de las funcionalidades opcionales una para implementar en el proyecto**.

---
Universidad del Quindío 💚 2025
