import Flutter

class CustomLogger {
  
  init(channel: FlutterMethodChannel) {
    self.channel = channel
  }
  
  private let channel: FlutterMethodChannel
  
  func trace(_ log: String){
    channel.invokeMethod("log", arguments: log)
  }
}

var logger: CustomLogger?

func hpdebug(_ message: String) {
  print(message);
  logger?.trace(message)
}

public class SwiftBiometricStoragePlugin: NSObject, FlutterPlugin {
  private let impl = BiometricStorageImpl(storageError: { (code, message, details) -> Any in
    FlutterError(code: code, message: message, details: details)
  }, storageMethodNotImplemented: FlutterMethodNotImplemented)

  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "biometric_storage", binaryMessenger: registrar.messenger())
    
    logger = CustomLogger(channel: channel)
    
    let instance = SwiftBiometricStoragePlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }
  
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    impl.handle(StorageMethodCall(method: call.method, arguments: call.arguments), result: result)
  }
}
