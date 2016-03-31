# Spectrum
[![Bintray](https://img.shields.io/bintray/v/nwalters512/maven/spectrum.svg?style=flat-square)](https://bintray.com/nwalters512/maven/spectrum/view) [![Maven Central](https://img.shields.io/maven-central/v/com.thebluealliance/spectrum.svg?style=flat-square)](http://search.maven.org/#artifactdetails|com.thebluealliance|spectrum|0.1.1|aar)

Spectrum is an Android library that makes it easy to let your user pick from a list of colors.

<img src="https://raw.githubusercontent.com/the-blue-alliance/spectrum/master/art/dialog.png" width="300">

## Download

Download with Gradle:
```groovy
compile 'com.thebluealliance:spectrum:0.2.0'
```

The library is hosted on both Bintray (JCenter) and Maven Central; use whichever you prefer.
```groovy
repositories {
  jcenter()
}
```

## Features
- `SpectrumDialog` with Builder pattern
- `SpectrumPreference` for easily adding color selection to your app's preferences
  - Can be added either in XML or programatically
- `SpectrumPreferenceCompat` for use with the preference support library
- `SpectrumPalette` for integrating a color selection view into any part of your app

## Deploying to Bintray
First, make sure the project's information is configured correctly in the `ext` block in `spectrum/build.gradle`. Next, put your Bintray information in your `local.properties` file; you will need to define `bintray.user`, `bintray.apikey`, and `bintray.gpg.password`.

To deploy the artifacts, run the following commands. The first is a dry run to make sure the artifacts can be built correctly. The second actually deploys to Bintray.

```
./gradlew install
./gradlew bintray
```

For a more thorough introduction on how to distribute libraries through jCenter and Maven Central, please see http://inthecheesefactory.com/blog/how-to-upload-library-to-jcenter-maven-central-as-dependency/en.

## Developers
- Nathan Walters ([@nwalters512](https://github.com/nwalters512))
