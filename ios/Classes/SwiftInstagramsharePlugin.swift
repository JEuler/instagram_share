import Flutter
import UIKit
import Photos

public class SwiftInstagramsharePlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "instagramshare", binaryMessenger: registrar.messenger())
    let instance = SwiftInstagramsharePlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if call.method == "share" {
      guard let args = call.arguments as? [String: Any],
            let path = args["path"] as? String,
            let type = args["type"] as? String else {
        result(FlutterError(code: "INVALID_ARGUMENTS", message: "Invalid arguments", details: nil))
        return
      }
      
      shareToInstagram(path: path, type: type, result: result)
    } else {
      result(FlutterMethodNotImplemented)
    }
  }
  
  private func shareToInstagram(path: String, type: String, result: @escaping FlutterResult) {
    let fileURL = URL(fileURLWithPath: path)
    
    // Check if Instagram is installed
    let instagramURL = URL(string: "instagram://app")!
    if UIApplication.shared.canOpenURL(instagramURL) {
      // Determine the UTI type based on the file extension
      let fileExtension = fileURL.pathExtension.lowercased()
      var typeIdentifier: String
      
      if type == "image" {
        if fileExtension == "jpg" || fileExtension == "jpeg" {
          typeIdentifier = "com.instagram.photo"
        } else {
          typeIdentifier = "public.image"
        }
      } else if type == "video" {
        typeIdentifier = "com.instagram.video"
      } else {
        result(FlutterError(code: "INVALID_TYPE", message: "Invalid media type", details: nil))
        return
      }
      
      // Share to Instagram
      let interactionController = UIDocumentInteractionController(url: fileURL)
      interactionController.uti = typeIdentifier
      
      // Get the root view controller to present from
      guard let rootViewController = UIApplication.shared.windows.first?.rootViewController else {
        result(FlutterError(code: "NO_VIEW_CONTROLLER", message: "Could not find a view controller to present from", details: nil))
        return
      }
      
      if interactionController.presentOpenInMenu(from: CGRect.zero, in: rootViewController.view, animated: true) {
        result(nil)
      } else {
        result(FlutterError(code: "PRESENTATION_ERROR", message: "Failed to present sharing options", details: nil))
      }
    } else {
      // Instagram is not installed, open App Store
      if let appStoreURL = URL(string: "itms-apps://itunes.apple.com/app/instagram/id389801252") {
        UIApplication.shared.open(appStoreURL, options: [:], completionHandler: nil)
      }
      result(FlutterError(code: "APP_NOT_INSTALLED", message: "Instagram is not installed", details: nil))
    }
  }
}
