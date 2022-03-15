# tru-test-srs2-xrs2-stick-reader-aar

Android SDK for Tru-Test SRS2/XRS2 Stick Reader

## Usage in UniAPP (Android)

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

globalEvent.addEventListener('TruTest_BlueToothDeviceDetected', function(ev) {
    // {"device":{"name":"I_TL","address":"C2:EA:AA:0F:D0:DA"}}
    console.log('TruTest_BlueToothDeviceDetected: ' + JSON.stringify(ev));
});

trutest.startDetectNearbyDevices()
```