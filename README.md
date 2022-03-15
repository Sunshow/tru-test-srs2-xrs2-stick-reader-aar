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


`build.gradle (app)`

```groovy
implementation project(':uniplugin-trutest')
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