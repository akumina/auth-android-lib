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
        
  #### Download com.microsoft.intune.mam.build.jar file from link (https://github.com/akumina/auth-android-lib/blob/main/MAMSDK/com.microsoft.intune.mam.build.jar)  and add into dependencies 
    
    classpath files("com.microsoft.intune.mam.build.jar")
 
 #### Download  Microsoft.Intune.MAM.SDK.aar file from link (https://github.com/akumina/auth-android-lib/blob/main/MAMSDK/Microsoft.Intune.MAM.SDK.aar)  and add into dependencies 
 
     implementation files('Microsoft.Intune.MAM.SDK.aar')
  
 #### Add AkuminaAuthAndroidLib into depencies 
 
     implementation 'com.github.akumina:auth-android-lib:1.0.0'
     
## usage

    AkuminaLib akuminaLib = AkuminaLib.getInstance();
    akuminaLib.addSharePointAuthCallback(new SharePointAuthCallback());
    akuminaLib.addAkuminaTokenCallback(new AkuminaTokenCallback());
    akuminaLib.addLoggingHandler(new LoggingHandler());
    
    AuthFile authFile = new AuthFile(R.raw.auth_config); // If auth_config.json file used in the project 
    AuthFile authFile = new AuthFile(new File("auth_sample_config.json"); // If custom auth config file used 
    
    ClientDetails clientDetails= new ClientDetails(AUTHORITY,CLIENT_ID,REDIRECT_URL,
                SHAREPOINT_SCOPE, APP_MANAGER_URL, TENANT_ID,MSAL_SCOPES);
                
    
   #### If sign-in using MSAL 
        akuminaLib.authenticateWithMSAL(activity, authFile, clientDetails, new AuthCallback(), new AppListener());
   #### If sign-in using MSAL And MAM
        akuminaLib.authenticateWithMSALAndMAM(activity, authFile, clientDetails, new AuthCallback(), new AppListener());
   #### To Get Token 
         String graphToken = akuminaLib.getToken(TokenType.GRAPH);
         
         TokenType is Enum. Values are 
         
          GRAPH - To get Graph Token 
          SHAREPOINT - To get Sharepoint Token 
          AKUMINA -  To get Akumina Token 
          
     #### To Call Akumina API 
     
         akuminaLib.callAkuminaApi(method,url,queryParams,headers, payload,responseListener,errorListener)
         
## License

AkuminaAuthAndroidLib is available under the MIT license. See the LICENSE file for more info.

[![](https://jitpack.io/v/akumina/auth-android-lib.svg)](https://jitpack.io/#akumina/auth-android-lib)
