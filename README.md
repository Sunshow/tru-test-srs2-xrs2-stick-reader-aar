# tru-test-srs2-xrs2-stick-reader-aar
Android SDK for Tru-Test SRS2/XRS2 Stick Reader

## Usage in Android APP

### Import as GIT module

```bash
git submodule add git@github.com:Sunshow/tru-test-srs2-xrs2-stick-reader-aar.git Tru-Test
git submodule update --init --recursive
```

`settings.gradle (project)`

```groovy
include ':Tru-Test'
project(":Tru-Test").projectDir = File(settingsDir, "./Tru-Test/module")
```


`build.gradle (app)`

```groovy
implementation project(':Tru-Test')
```