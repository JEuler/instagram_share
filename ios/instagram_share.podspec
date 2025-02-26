#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint instagram_share.podspec' to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'instagram_share'
  s.version          = '0.2.1'
  s.summary          = 'Flutter plugin allowing to share media to Instagram'
  s.description      = <<-DESC
Flutter plugin allowing to share media to Instagram by using native mechanisms
                       DESC
  s.homepage         = 'https://github.com/JEuler/instagram_share'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'JEuler' => 'https://github.com/JEuler' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.platform = :ios, '11.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
