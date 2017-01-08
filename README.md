This is the multi-module of JSkat and JSkat on Android.

License: 
* JSkat base: Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0) 
* JSkat Swing GUI: GNU General Public License (GPL), Version 3.0 (http://www.gnu.org/licenses/gpl.html)
* JSkat JavaFX GUI: GNU General Public License (GPL), Version 3.0 (http://www.gnu.org/licenses/gpl.html)

Build executable fat JAR: `./gradlew clean shadowJar`

Build installation with multiple JARs and start scripts: `./gradlew clean installDist`

Build installation with fat JAR and start scripts: `./gradlew clean installShadowJar`

Continous integration: https://travis-ci.org/b0n541/jskat-multimodule

We develop according the GitFlow workflow: http://nvie.com/posts/a-successful-git-branching-model/

So if you want to contribute to JSkat please create a feature branch from the `develop` branch and create pull requests
towards the `develop` branch.

[![Build Status](https://travis-ci.org/b0n541/jskat-multimodule.png?branch=master)](https://travis-ci.org/b0n541/jskat-multimodule)