import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:logger/logger.dart';
import 'package:weather/consts/app_const.dart';

/// Servicio de autenticación mediante Google Sign-In.
///
/// Gestiona el ciclo completo de autenticación: inicialización del SDK,
/// inicio/cierre de sesión, almacenamiento seguro del idToken y
/// verificación del estado de autenticación del usuario.
///
/// **Consideraciones de seguridad:**
/// - El idToken se almacena en [FlutterSecureStorage] (Keychain en iOS,
///   Keystore en Android).
/// - [estaAutenticado] valida expiración del JWT, no solo su existencia.
///
/// **Glosario:**
/// - `idToken`: JWT emitido por Google que identifica al usuario.
/// - `scope`: Permiso OAuth2 solicitado al proveedor de identidad.
/// - `Keychain/Keystore`: Almacén seguro a nivel sistema operativo.
class GoogleService {
  final FlutterSecureStorage _almacenamiento;
  final Logger _logger;
  final GoogleSignIn _google;

  bool _inicializado = false;
  Future<void>? _inicializacionFutura;

  /// Crea una instancia del servicio.
  ///
  /// Los parámetros son opcionales para facilitar la inyección de
  /// dependencias en tests unitarios.
  GoogleService({
    FlutterSecureStorage? almacenamiento,
    Logger? logger,
    GoogleSignIn? google,
  }) : _almacenamiento = almacenamiento ?? const FlutterSecureStorage(),
       _logger = logger ?? Logger(),
       _google = google ?? GoogleSignIn.instance;

  Future<void> _asegurarInicializacion() async {
    if (_inicializado) return;

    _inicializacionFutura ??= _inicializar();
    await _inicializacionFutura;
  }

  Future<void> _inicializar() async {
    try {
      await _google.initialize();
      _inicializado = true;
      _logger.i('Google Sign-In initialized successfully');
    } catch (error, stackTrace) {
      _inicializacionFutura = null; // Permitir reintentos
      _logger.e(
        'Failed to initialize Google Sign-In',
        error: error,
        stackTrace: stackTrace,
      );
      rethrow;
    }
  }

  /// Inicia el flujo de autenticación con Google.
  ///
  /// Retorna `true` si la autenticación fue exitosa y el idToken
  /// fue almacenado correctamente. Retorna `false` en caso de
  /// cancelación por el usuario, token vacío o error.
  Future<bool> iniciarSesion() async {
    try {
      await _asegurarInicializacion();

      final GoogleSignInAccount cuenta = await _google.authenticate(
        scopeHint: <String>['email', 'profile'],
      );

      final GoogleSignInAuthentication autenticacion = cuenta.authentication;
      final String? tokenId = autenticacion.idToken;

      if (tokenId == null || tokenId.isEmpty) {
        _logger.w('No ID token received from Google');
        return false;
      }

      await _almacenamiento.write(
        key: AppConst.etiquetaIdToken,
        value: tokenId,
      );
      await _almacenamiento.write(
        key: AppConst.etiquetaEmail,
        value: cuenta.email,
      );

      _logger.i('User authenticated: ${cuenta.email}');
      return true;
    } catch (error, stackTrace) {
      _logger.e('Authentication failed', error: error, stackTrace: stackTrace);
      return false;
    }
  }

  /// Obtiene el idToken almacenado.
  Future<String?> obtenerToken() async {
    try {
      return await _almacenamiento.read(key: AppConst.etiquetaIdToken);
    } catch (error) {
      _logger.e('Failed to retrieve token from secure storage', error: error);
      return null;
    }
  }

  /// Obtiene el email del usuario autenticado.
  Future<String?> obtenerEmail() async {
    try {
      return await _almacenamiento.read(key: AppConst.etiquetaEmail);
    } catch (error) {
      _logger.e('Failed to retrieve email from secure storage', error: error);
      return null;
    }
  }

  /// Verifica si el usuario tiene una sesión activa.
  ///
  /// Valida la existencia del token y su fecha de expiración
  /// decodificando el payload del JWT.
  Future<bool> estaAutenticado() async {
    final String? token = await obtenerToken();
    if (token == null || token.isEmpty) {
      return false;
    }
    return !_estaTokenExpirado(token);
  }

  /// Decodifica el payload del JWT y verifica el campo `exp`.
  bool _estaTokenExpirado(String token) {
    try {
      final List<String> partes = token.split('.');
      if (partes.length != 3) return true;

      final String payloadBase64 = partes[1];
      final String payloadJson = utf8.decode(
        base64Url.decode(base64Url.normalize(payloadBase64)),
      );
      final Map<String, dynamic> payload =
          json.decode(payloadJson) as Map<String, dynamic>;

      final int? exp = payload['exp'] as int?;
      if (exp == null) return true;

      final DateTime expiracion = DateTime.fromMillisecondsSinceEpoch(
        exp * 1000,
      );
      return DateTime.now().isAfter(expiracion);
    } catch (error) {
      _logger.w('Failed to decode JWT', error: error);
      return true; // Ante la duda, considerar expirado
    }
  }

  /// Cierra la sesión del usuario.
  ///
  /// Ejecuta `signOut()` en Google y elimina las claves
  /// específicas del almacenamiento seguro.
  ///
  /// Retorna `true` si la operación fue exitosa.
  Future<bool> cerrarSesion() async {
    try {
      await _google.signOut();
      await _almacenamiento.delete(key: AppConst.etiquetaIdToken);
      await _almacenamiento.delete(key: AppConst.etiquetaEmail);
      _logger.i('User signed out successfully');
      return true;
    } catch (error, stackTrace) {
      _logger.e('Failed to sign out', error: error, stackTrace: stackTrace);
      return false;
    }
  }
}
