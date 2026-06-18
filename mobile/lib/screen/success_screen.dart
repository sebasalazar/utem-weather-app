import 'package:flutter/material.dart';
import 'package:weather/consts/app_colors.dart';
import 'package:weather/screen/login_screen.dart';
import 'package:weather/services/google_service.dart';

/// Pantalla que se muestra después de una autenticación exitosa con Google.
///
/// Esta pantalla celebra el inicio de sesión correcto, mostrando un mensaje de
/// bienvenida y un icono de verificación. Proporciona al usuario la opción de
/// cerrar sesión, lo que ejecuta el método [GoogleService.cerrarSesion] y
/// redirige de vuelta a la [LoginScreen].
///
/// ## Estructura visual
/// - **Fondo**: Degradado lineal de naranja con un efecto radial blanco para
///   dar sensación de profundidad y luminosidad.
/// - **Icono**: `Icons.check_circle` de gran tamaño (120 px) con sombra,
///   representando el éxito de la operación.
/// - **Mensajes**: Título "¡Bienvenido!" y texto "Autenticación exitosa".
/// - **Área de información**: Un contenedor semitransparente que muestra un
///   mensaje "Éxito" (placeholder para futuros datos del usuario).
/// - **Botón**: Botón "Cerrar sesión" que invoca el cierre de sesión y navega
///   a la pantalla de inicio.
///
/// ## Comportamiento
/// - Al presionar "Cerrar sesión", se llama a `_manejarCierreSesion`, que:
///   1. Ejecuta `_servicioGoogle.cerrarSesion()` (cierra la sesión en Google).
///   2. Verifica que el contexto esté montado (evita errores en asincronía).
///   3. Reemplaza la pila de navegación con [LoginScreen] usando
///      `pushReplacement`, impidiendo que el usuario regrese a la pantalla de
///      éxito con el botón "atrás".
///
/// ## Dependencias
/// - [AppColors]: Paleta de colores centralizada.
/// - [GoogleService]: Servicio que maneja la autenticación con Google.
/// - [LoginScreen]: Pantalla de inicio de sesión a la que se redirige.
///
/// ## Ejemplo de uso
/// ```dart
/// // Navegar a la pantalla de éxito después de un login exitoso
/// Navigator.pushReplacement(
///   context,
///   MaterialPageRoute(builder: (context) => const SuccessScreen()),
/// );
/// ```
///
/// ## Consideraciones
/// - La clase crea una instancia de [GoogleService] directamente (`_servicioGoogle`).
///   En aplicaciones más grandes, se recomienda inyectar el servicio mediante
///   un patrón de inyección de dependencias para facilitar pruebas y
///   mantenibilidad.
/// - El contenedor con el texto "Exito" es un placeholder; podría reemplazarse
///   por información del usuario (nombre, email, foto) obtenida del servicio
///   o de SharedPreferences.
/// - El manejo de `context.mounted` asegura que no se intente navegar si el
///   widget ya no está en el árbol, evitando errores comunes en callbacks
///   asíncronos.
///
/// ## Mejoras potenciales
/// - Mostrar datos reales del usuario (nombre, email, foto de perfil) en lugar
///   del placeholder "Exito".
/// - Agregar un indicador de carga mientras se cierra sesión.
/// - Extraer el botón y el contenedor de información en widgets separados para
///   mejorar la legibilidad.
class SuccessScreen extends StatelessWidget {
  /// Instancia del servicio de Google para manejar el cierre de sesión.
  ///
  /// Se crea directamente en la clase; en un entorno con inyección de
  /// dependencias, podría recibirse como parámetro.
  final GoogleService _servicioGoogle = GoogleService();

  /// Constructor de la pantalla de éxito.
  ///
  /// No recibe parámetros, ya que la información de usuario se obtiene
  /// típicamente de otras fuentes (servicio, almacenamiento local).
  SuccessScreen({super.key});

  /// Maneja el cierre de sesión del usuario.
  ///
  /// Este método asíncrono:
  /// 1. Llama a `_servicioGoogle.cerrarSesion()` para finalizar la sesión.
  /// 2. Comprueba que el contexto siga montado (evita navegación en widgets
  ///    destruidos).
  /// 3. Navega a la [LoginScreen] reemplazando la pila actual.
  ///
  /// [context]: El contexto de construcción del widget, necesario para
  ///            la navegación y para verificar `mounted`.
  Future<void> _manejarCierreSesion(BuildContext context) async {
    await _servicioGoogle.cerrarSesion();
    // Verificar que el widget aún esté en el árbol antes de navegar
    if (context.mounted) {
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (context) => const LoginScreen()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment(0.42, -0.91),
            end: Alignment(-0.42, 0.91),
            colors: [
              AppColors.naranjaPrimarioClaro,
              AppColors.naranjaPrimario,
              AppColors.naranjaPrimarioMedio,
              AppColors.naranjaPrimarioOscuro,
            ],
            stops: [0.0, 0.25, 0.5, 1.0],
          ),
        ),
        child: Stack(
          children: [
            // Efecto de luz radial para dar profundidad
            Container(
              decoration: BoxDecoration(
                gradient: RadialGradient(
                  center: const Alignment(0.2, 0.5),
                  radius: 0.8,
                  colors: [AppColors.blancoClaro, Colors.transparent],
                ),
              ),
            ),
            SafeArea(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Expanded(
                    child: SingleChildScrollView(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const SizedBox(height: 24),
                          // Icono de éxito con sombra
                          Icon(
                            Icons.check_circle,
                            size: 120,
                            color: AppColors.blanco,
                            shadows: [
                              BoxShadow(
                                color: AppColors.negroClaro,
                                blurRadius: 16,
                                offset: const Offset(0, 8),
                              ),
                            ],
                          ),
                          const SizedBox(height: 32),
                          // Título principal
                          Text(
                            '¡Bienvenido!',
                            style: Theme.of(context).textTheme.displayLarge,
                          ),
                          const SizedBox(height: 16),
                          // Subtítulo descriptivo
                          Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 24),
                            child: Text(
                              'Autenticación exitosa',
                              textAlign: TextAlign.center,
                              style: Theme.of(context).textTheme.bodyLarge
                                  ?.copyWith(color: AppColors.blancoClarisimo),
                            ),
                          ),
                          const SizedBox(height: 48),
                          // Área de información y botón de cierre de sesión
                          Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 24),
                            child: Column(
                              children: [
                                // Contenedor placeholder para datos del usuario
                                Container(
                                  padding: const EdgeInsets.all(16),
                                  decoration: BoxDecoration(
                                    color: AppColors.blanco.withValues(
                                      alpha: 0.1,
                                    ),
                                    borderRadius: BorderRadius.circular(12),
                                  ),
                                  child: Text('Exito'), // Placeholder
                                ),
                                const SizedBox(height: 32),
                                // Botón de cierre de sesión
                                SizedBox(
                                  width: double.infinity,
                                  child: ElevatedButton(
                                    style: ElevatedButton.styleFrom(
                                      backgroundColor: AppColors.blanco,
                                      padding: const EdgeInsets.symmetric(
                                        vertical: 16,
                                      ),
                                      shape: RoundedRectangleBorder(
                                        borderRadius: BorderRadius.circular(12),
                                      ),
                                    ),
                                    onPressed: () =>
                                        _manejarCierreSesion(context),
                                    child: Text(
                                      'Cerrar sesión',
                                      style: Theme.of(context)
                                          .textTheme
                                          .labelLarge
                                          ?.copyWith(
                                            color: AppColors.naranjaPrimario,
                                          ),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(height: 24),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
