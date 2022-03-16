# tru-test-srs2-xrs2-stick-reader-aar

Android SDK for Tru-Test SRS2/XRS2 Stick Reader

## Usage in UniAPP (Android)

See reference of [UniAPP NativePlugin](https://nativesupport.dcloud.net.cn/NativePlugin/course/android). 

### Import as GIT module

```bash
git submodule add git@github.com:Sunshow/tru-test-srs2-xrs2-stick-reader-aar.git Tru-Test
git submodule update --init --recursive
```

`settings.gradle (project)`

```groovy
include ':uniplugin-trutest'
project(':uniplugin-trutest').projectDir = new File('./Tru-Test/module')
```

### Add Dependence

`build.gradle (app)`

```groovy
implementation project(':uniplugin-trutest')
implementation 'com.github.prasad-psp:Android-Bluetooth-Library:1.0.2'
```

`AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

`dcloud_uniplugins.json`

```json
{
  "nativePlugins": [
    {
      "hooksClass": "io.dcloud.uniplugin.TruTestUniAppProxy",
      "plugins": [
        {
          "type": "module",
          "name": "TruTest",
          "class": "io.dcloud.uniplugin.TruTestUniModule"
        }
      ]
    }
  ]
}
```

### Call in UniAPP (JavaScript)

#### Apply Android Permission

`android.permission.ACCESS_COARSE_LOCATION` is required for detect nearby devices.

```javascript
const Build = plus.android.importClass("android.os.Build");
const Manifest = plus.android.importClass("android.Manifest");
const MainActivity = plus.android.runtimeMainActivity();

const permissions = [
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.ACCESS_COARSE_LOCATION,
];

function checkPermission(permission) {
    if (Build.VERSION.SDK_INT >= 23) {
        if (MainActivity.checkSelfPermission(permission) === -1) {
            return false;
        }
    }
    return true;
}

function checkPermissions(permissions) {
    for (let i = 0; i < permissions.length; i ++) {
      const permission = permissions[i];
      if (!checkPermission(permission)) {
        return false;
      }
    }
    return true;
}

function doYourThing() {
    // do sth.
}

function requestPermissions(permissions) {
    if (Build.VERSION.SDK_INT >= 23) {
        plus.android.requestPermissions(permissions, function(e) {
            if (e.deniedAlways.length > 0) {
                console.log('deniedAlways: ' + e.deniedAlways.toString())
            } else if (e.deniedPresent.length > 0) {
                console.log('deniedPresent: ' + e.deniedPresent.toString())
            } else if(e.granted.length === permissions.length) {
                console.log('all granted: ' + e.granted.toString())
                doYourThing()
            }
        }, function(e) {
            console.log('Request permissions error: ' + JSON.stringify(e))
        });
    }
}

if (!checkPermissions(permissions)) {
    requestPermissions(permissions)
} else {
    doYourThing()
}
```

#### Samples
```javascript
const trutest = uni.requireNativePlugin('TruTest')

const globalEvent = uni.requireNativePlugin('globalEvent');

trutest.turnOffBluetooth()

trutest.turnOnBluetooth()

globalEvent.addEventListener('TruTest_BlueToothDeviceDetected', function(ev) {
    // {"device":{"name":"I_TL","address":"C2:EA:AA:0F:D0:DA"}}
    console.log('TruTest_BlueToothDeviceDetected: ' + JSON.stringify(ev));
});
trutest.startDetectNearbyDevices()

globalEvent.addEventListener('TruTest_BlueToothDevicePairCompleted', function(ev) {
    // {"paired":true}
    console.log('TruTest_BlueToothDevicePairCompleted: '+JSON.stringify(ev));
});
trutest.requestPairDevice('C2:EA:AA:0F:D0:DA', result => {
    // {"code":0} 0=successful, others=failed
    console.log('request pair device: ' + JSON.stringify(result))
})

trutest.unpairDevice('C2:EA:AA:0F:D0:DA', result => {
    // {"code":0} 0=successful, others=failed
    console.log('request pair device: ' + JSON.stringify(result))
})

trutest.listPairedDevices(result => {
    // {"devices":[{"name":"vivo TWS 2","address":"CC:81:2A:DD:7C:BC"},{"name":"SRS2 0959","address":"2C:11:65:70:29:79"}]}
    console.log('list paired devices: ' + JSON.stringify(result))
})


globalEvent.addEventListener('TruTest_CommandExecutionCompleted', function(ev) {
    // {"command":"GetSessionRecord","error":0,"data":["991005002562568","991005002562577","991005002562569","900081001156906","991005002562572"]}
    console.log('TruTest_CommandExecutionCompleted: '+JSON.stringify(ev));
});
globalEvent.addEventListener('TruTest_DeviceConnected', function(ev) {
    console.log('TruTest_DeviceConnected: '+JSON.stringify(ev));

    trutest.requestClearAllSessionFiles(result => {
        console.log('request clear all session files: ' + JSON.stringify(result))
    })

    trutest.requestResetCurrentSessionData(result => {
        console.log('request reset current session data: ' + JSON.stringify(result))
    })
    
    trutest.requestDownloadCurrentSessionData(result => {
        console.log('request download current session data: ' + JSON.stringify(result))
    })
})

trutest.startConnection({'address': '2C:11:65:70:29:79'}, result => {
    console.log('start connection: ' + JSON.stringify(result))
})

```