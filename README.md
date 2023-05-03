# AkuminaAuthAndroidLib

AkuminaLib is available through gradle dependency. To install
it, simply add the following line to your build gradle:


## Author

  Akumina

## Add Dependency 
  To Add AkuminaAuthAndroidLib as a dependency to your Android project add below maven repos to build.gradle file 
  
        maven {
            url 'https://pkgs.dev.azure.com/MicrosoftDeviceSDK/DuoSDK-Public/_packaging/Duo-SDK-Feed/maven/v1'
        }
        
        maven { url 'https://jitpack.io' }
        
  #### Download com.microsoft.intune.mam.build.jar file com.microsoft.intune.mam.build.jar (https://github.com/akumina/auth-android-lib/blob/main/MAMSDK/com.microsoft.intune.mam.build.jar)  and add into dependencies 
    
    classpath files("app/MAMSDK/com.microsoft.intune.mam.build.jar")
    
## License

AkuminaAuthAndroidLib is available under the MIT license. See the LICENSE file for more info.

[![](https://jitpack.io/v/akumina/auth-android-lib.svg)](https://jitpack.io/#akumina/auth-android-lib)
