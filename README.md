# tru-test-srs2-xrs2-stick-reader-aar
Android SDK for Tru-Test SRS2/XRS2 Stick Reader

## Usage in Android APP

### Import as GIT module

```bash
touch .gitmodules
```

```
[submodule "Tru-Test"]
	path = Tru-Test
	url = git@github.com:Sunshow/tru-test-srs2-xrs2-stick-reader-aar
```

```bash
git submodule update --init --recursive
```

`settings.gradle (project)`

```groovy
include ':Tru-Test'
```


`build.gradle (app)`

```groovy
implementation project(':Tru-Test')
```