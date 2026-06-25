import 'package:flutter/material.dart';

/// Clase utilitaria que centraliza todos los colores utilizados en la aplicación.
///
/// Esta clase define una paleta de colores consistente para temas oscuros y claros,
/// facilitando el mantenimiento y la coherencia visual en toda la interfaz.
/// Al ser una clase con constructor privado, no se puede instanciar; sus miembros
/// se acceden de forma estática.
///
/// ## Organización de colores
/// - **Primarios**: Gama de naranja para acentos y acciones principales.
/// - **Superficie y fondo**: Colores para fondos de pantalla y tarjetas.
/// - **Semánticos**: Colores con significado específico (como oro para logros o azul para Google).
/// - **Neutrales**: Blanco, negro y sus variantes con opacidad.
/// - **Tema claro**: Color base para el tema claro.
///
/// ## Ejemplo de uso
/// ```dart
/// Container(
///   color: ColoresApp.superficieOscura,
///   child: Text(
///     'Hola mundo',
///     style: TextStyle(color: ColoresApp.blanco),
///   ),
/// )
/// ```
///
/// ## Nota sobre temas
/// Para integrar estos colores con [ThemeData], se recomienda asignar
/// [AppColors.naranjaPrimario] como `primaryColor` y usar el resto de colores
/// manualmente en los widgets según sea necesario.
class AppColors {
  /// Constructor privado que impide la creación de instancias de esta clase.
  ///
  /// Todos los colores se acceden mediante miembros estáticos.
  AppColors._();

  // ===== Colores Primarios - Tema Oscuro =====
  /// Naranja principal, utilizado para botones primarios, iconos destacados y encabezados.
  static const Color naranjaPrimario = Color(0xFFFF7E36);

  /// Variante más clara del naranja principal, útil para fondos de elementos interactivos.
  static const Color naranjaPrimarioClaro = Color(0xFFFF9A56);

  /// Variante media del naranja principal, para estados intermedios o degradados.
  static const Color naranjaPrimarioMedio = Color(0xFFF67C3B);

  /// Variante más oscura del naranja principal, para énfasis en textos o bordes.
  static const Color naranjaPrimarioOscuro = Color(0xFFE8651E);

  // ===== Superficie y Fondo =====
  /// Color de fondo para el modo oscuro.
  static const Color superficieOscura = Color(0xFF1E1E1E);

  /// Color de fondo para el modo claro (blanco puro).
  static const Color fondoClaro = Colors.white;

  // ===== Colores Semánticos =====
  /// Color dorado (amarillo), usado para representar logros, premios o elementos premium.
  static const Color oroSol = Color(0xFFFFD700);

  /// Azul característico de Google, usado para integraciones o botones de inicio de sesión.
  static const Color azulGoogle = Color(0xFF4285F4);

  // ===== Colores Neutrales =====
  /// Blanco puro.
  static const Color blanco = Colors.white;

  /// Negro puro.
  static const Color negro = Colors.black;

  // ===== Variantes de Opacidad =====
  /// Blanco con 10% de opacidad, ideal para superposiciones tenues.
  static final Color blancoClaro = Colors.white.withValues(alpha: 0.1);

  /// Blanco con 70% de opacidad, para textos secundarios sobre fondos oscuros.
  static final Color blancoClarisimo = Colors.white.withValues(alpha: 0.7);

  /// Negro con 15% de opacidad, utilizado para sombras o separadores sutiles.
  static final Color negroClaro = Colors.black.withValues(alpha: 0.15);

  /// Negro con 20% de opacidad, para efectos de desenfoque o capas superpuestas.
  static final Color negroMedio = Colors.black.withValues(alpha: 0.2);

  // ===== Colores de Tema para Tema Claro =====
  /// Color semilla para el tema claro (basado en [Colors.deepOrange]).
  static const Color semillaTemaClaro = Colors.deepOrange;
}
