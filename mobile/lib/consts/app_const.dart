/// Clase utilitaria que almacena las claves (etiquetas) utilizadas para
/// almacenar y recuperar datos del usuario en el almacenamiento persistente
/// (como SharedPreferences o similar).
///
/// Estas constantes definen los nombres de las claves para identificar de forma
/// única cada dato relacionado con la sesión del usuario, evitando errores de
/// escritura y facilitando el mantenimiento.
///
/// ## Propósito
/// Centralizar las claves de almacenamiento permite:
/// - Evitar duplicación de cadenas literales en el código.
/// - Facilitar cambios en los nombres de las claves (solo se modifica aquí).
/// - Mejorar la legibilidad y el autocompletado en el IDE.
///
/// ## Claves definidas
/// - [etiquetaIdToken]: Token de autenticación del usuario.
/// - [etiquetaEmail]: Correo electrónico del usuario.
/// - [etiquetaNombre]: Nombre completo o de usuario.
/// - [etiquetaUrlFoto]: URL de la foto de perfil del usuario.
///
/// ## Ejemplo de uso
/// ```dart
/// // Guardar datos
/// final prefs = await SharedPreferences.getInstance();
/// await prefs.setString(ConstanteApp.etiquetaIdToken, 'abc123');
/// await prefs.setString(ConstanteApp.etiquetaEmail, 'usuario@ejemplo.com');
///
/// // Recuperar datos
/// final token = prefs.getString(ConstanteApp.etiquetaIdToken);
/// final email = prefs.getString(ConstanteApp.etiquetaEmail);
/// ```
///
/// ## Notas
/// - Todas las constantes son `static final String`, por lo que se acceden
///   directamente desde la clase sin instanciar.
/// - Se recomienda usar estas constantes en lugar de cadenas literales para
///   evitar errores tipográficos.
class AppConst {
  /// Clave para almacenar el token de identificación (ID token) del usuario.
  ///
  /// Este token se utiliza para autenticar solicitudes al servidor y mantener
  /// la sesión activa.
  static final String etiquetaIdToken = "idToken";

  /// Clave para almacenar el correo electrónico del usuario autenticado.
  ///
  /// Se usa para mostrar el email en la interfaz o para recuperación de cuenta.
  static final String etiquetaEmail = "email";

  /// Clave para almacenar el nombre del usuario (puede ser nombre completo
  /// o nombre de usuario).
  ///
  /// Se utiliza para personalizar saludos y mostrar información del perfil.
  static final String etiquetaNombre = "name";

  /// Clave para almacenar la URL de la foto de perfil del usuario.
  ///
  /// Esta URL apunta a la imagen de avatar del usuario, que puede cargarse
  /// con widgets como [Image.network].
  static final String etiquetaUrlFoto = "photoUrl";
}