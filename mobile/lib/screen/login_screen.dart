import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:weather/screen/error_screen.dart';
import 'package:weather/screen/success_screen.dart';
import 'package:weather/services/google_service.dart';
import 'package:weather/consts/app_colors.dart';

/// Pantalla de inicio de sesión con autenticación vía Google.
///
/// Incluye animaciones de entrada (fade) y flotación continua del logo,
/// manejo robusto de estados de carga y cancelación de autenticación.
class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen>
    with TickerProviderStateMixin {
  // Logger configurado con formato legible para desarrollo.
  // PrettyPrinter incluye método, línea y tiempo de ejecución.
  static final Logger _logger = Logger(
    printer: PrettyPrinter(
      methodCount: 2,
      errorMethodCount: 8,
      lineLength: 80,
      colors: true,
      printEmojis: true,
      dateTimeFormat: DateTimeFormat.onlyTimeAndSinceStart,
    ),
  );

  static const String _nombreClase = 'PantallaLogin';
  final GoogleService _servicioGoogle = GoogleService();

  late final AnimationController _controladorFlotacion;
  late final AnimationController _controladorDesvanecimiento;
  late final Animation<Offset> _animacionFlotacion;
  late final Animation<double> _animacionDesvanecimiento;

  bool _estaCargando = false;



  @override
  void initState() {
    super.initState();
    _logger.i('[$_nombreClase] Inicializando pantalla de login');
    _inicializarAnimaciones();
  }

  /// Configura las animaciones de flotación y desvanecimiento.
  ///
  /// - Flotación: movimiento vertical continuo del logo (ida y vuelta).
  /// - Desvanecimiento: entrada gradual de todos los elementos al cargar la pantalla.
  void _inicializarAnimaciones() {
    // Controlador de flotación: 3 segundos por ciclo, se repite infinitamente.
    _controladorFlotacion = AnimationController(
      duration: const Duration(seconds: 3),
      vsync: this,
    )..repeat(reverse: true);

    _animacionFlotacion = Tween<Offset>(
      begin: Offset.zero,
      end: const Offset(0, -0.015),
    ).animate(
      CurvedAnimation(parent: _controladorFlotacion, curve: Curves.easeInOut),
    );

    // Controlador de desvanecimiento: 800ms, se ejecuta una sola vez al inicio.
    _controladorDesvanecimiento = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );

    _animacionDesvanecimiento = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _controladorDesvanecimiento, curve: Curves.easeOut),
    );

    _controladorDesvanecimiento.forward();
    _logger.d('[$_nombreClase] Animaciones inicializadas correctamente');
  }

  @override
  void dispose() {
    _logger.d('[$_nombreClase] Liberando recursos de animación');
    _controladorFlotacion.dispose();
    _controladorDesvanecimiento.dispose();
    super.dispose();
  }

  /// Maneja el flujo completo de autenticación con Google.
  ///
  /// Diferencia tres escenarios:
  /// 1. Éxito: navega a PantallaExito con la cuenta obtenida.
  /// 2. Cancelación: el usuario cerró el diálogo de Google.
  /// 3. Error: excepción durante el proceso, navega a PantallaError.
  Future<void> _manejarLoginGoogle() async {
    _logger.i('[$_nombreClase] Iniciando proceso de autenticación con Google');
    _establecerEstadoCarga(true);

    try {
      final bool ok = await _servicioGoogle.iniciarSesion();

      if (!mounted) {
        return;
      }

      if (ok) {
        _logger.i('[$_nombreClase] Autenticación exitosa');
        Navigator.of(context).pushReplacement(
          MaterialPageRoute<SuccessScreen>(
            builder: (BuildContext context) => SuccessScreen(),
          ),
        );
      } else {
        _logger.w('[$_nombreClase] Autenticación fallida o cancelada');
        _establecerEstadoCarga(false);
      }
    } catch (error, stackTrace) {
      _logger.e(
        '[$_nombreClase] Error durante autenticación con Google',
        error: error,
        stackTrace: stackTrace,
      );
      if (mounted) {
        _establecerEstadoCarga(false);
        Navigator.of(context).pushReplacement(
          MaterialPageRoute<ErrorScreen>(
            builder: (BuildContext context) => ErrorScreen(
              mensajeError: 'Error: ${error.toString()}',
            ),
          ),
        );
      }
    }
  }

  /// Actualiza el estado de carga de forma segura.
  ///
  /// Verifica que el widget esté montado antes de llamar a setState,
  /// evitando errores cuando la navegación ocurre durante una operación asíncrona.
  void _establecerEstadoCarga(bool cargando) {
    if (!mounted) {
      return;
    }
    setState(() {
      _estaCargando = cargando;
    });
    _logger.d('[$_nombreClase] Estado de carga actualizado: $cargando');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: _construirGradienteFondo(),
        child: Stack(
          children: <Widget>[
            _construirSobreposicionGradienteRadial(),
            SafeArea(
              child: Column(
                children: <Widget>[
                  Expanded(child: _construirContenidoPrincipal()),
                  _construirPie(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  /// Gradiente lineal de fondo con tonos naranjos (identidad visual UTEM).
  BoxDecoration _construirGradienteFondo() {
    return const BoxDecoration(
      gradient: LinearGradient(
        begin: Alignment(0.42, -0.91),
        end: Alignment(-0.42, 0.91),
        colors: <Color>[
          AppColors.naranjaPrimarioClaro,
          AppColors.naranjaPrimario,
          AppColors.naranjaPrimarioMedio,
          AppColors.naranjaPrimarioOscuro,
        ],
        stops: <double>[0.0, 0.25, 0.5, 1.0],
      ),
    );
  }

  /// Capa decorativa con gradiente radial para dar profundidad visual.
  Widget _construirSobreposicionGradienteRadial() {
    return Container(
      decoration: BoxDecoration(
        gradient: RadialGradient(
          center: const Alignment(0.2, 0.5),
          radius: 0.8,
          colors: <Color>[
            AppColors.blancoClaro,
            Colors.transparent,
          ],
        ),
      ),
    );
  }

  /// Contenido principal: logo, títulos y botón de login.
  Widget _construirContenidoPrincipal() {
    return SingleChildScrollView(
      physics: const NeverScrollableScrollPhysics(),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          const SizedBox(height: 24),
          _construirLogoAnimado(),
          const SizedBox(height: 32),
          _construirTitulo(),
          const SizedBox(height: 8),
          _construirSubtitulo(),
          const SizedBox(height: 48),
          _construirBotonLogin(),
          const SizedBox(height: 24),
        ],
      ),
    );
  }

  /// Logo con animación combinada: flotación continua + desvanecimiento inicial.
  Widget _construirLogoAnimado() {
    return Semantics(
      label: 'Logo de Clima UTEM',
      child: SlideTransition(
        position: _animacionFlotacion,
        child: FadeTransition(
          opacity: _animacionDesvanecimiento,
          child: _construirLogoClimatico(),
        ),
      ),
    );
  }

  /// Iconografía del logo: nube con sol superpuesto.
  Widget _construirLogoClimatico() {
    return Container(
      width: 120,
      height: 120,
      decoration: BoxDecoration(
        boxShadow: <BoxShadow>[
          BoxShadow(
            color: AppColors.negroClaro,
            blurRadius: 16,
            offset: const Offset(0, 8),
          ),
        ],
      ),
      child: Stack(
        alignment: Alignment.center,
        children: <Widget>[
          const Icon(
            Icons.cloud,
            size: 100,
            color: AppColors.blanco,
          ),
          Positioned(
            top: 8,
            right: 8,
            child: Icon(
              Icons.sunny,
              size: 48,
              color: AppColors.oroSol,
            ),
          ),
        ],
      ),
    );
  }

  Widget _construirTitulo() {
    return FadeTransition(
      opacity: _animacionDesvanecimiento,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24),
        child: Text(
          'Clima UTEM',
          textAlign: TextAlign.center,
          style: Theme.of(context).textTheme.displayLarge,
        ),
      ),
    );
  }

  Widget _construirSubtitulo() {
    return FadeTransition(
      opacity: _animacionDesvanecimiento,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24),
        child: Text(
          'Clima en tu ubicación',
          textAlign: TextAlign.center,
          style: Theme.of(context).textTheme.bodyLarge,
        ),
      ),
    );
  }

  Widget _construirBotonLogin() {
    return FadeTransition(
      opacity: _animacionDesvanecimiento,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24),
        child: _construirBotonLoginGoogle(),
      ),
    );
  }

  /// Botón de login con Google, muestra indicador de carga cuando corresponde.
  Widget _construirBotonLoginGoogle() {
    return Material(
      color: Colors.transparent,
      child: InkWell(
        onTap: _estaCargando ? null : _manejarLoginGoogle,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
          decoration: BoxDecoration(
            color: AppColors.blanco,
            borderRadius: BorderRadius.circular(12),
            boxShadow: <BoxShadow>[
              BoxShadow(
                color: AppColors.negroMedio,
                blurRadius: 24,
                offset: const Offset(0, 8),
              ),
            ],
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              if (_estaCargando)
                const SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              else
                _construirIconoGoogle(),
              const SizedBox(width: 12),
              Text(
                _estaCargando ? 'Iniciando sesión...' : 'Continuar con Google',
                style: Theme.of(context).textTheme.labelLarge,
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _construirIconoGoogle() {
    return const Icon(
      Icons.g_mobiledata,
      size: 20,
      color: AppColors.azulGoogle,
    );
  }

  Widget _construirPie() {
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: FadeTransition(
        opacity: _animacionDesvanecimiento,
        child: Text(
          'Al continuar aceptas nuestros términos de servicio',
          textAlign: TextAlign.center,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: AppColors.blancoClarisimo,
            fontSize: 12,
            fontWeight: FontWeight.w400,
          ),
        ),
      ),
    );
  }
}