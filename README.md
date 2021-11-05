This is the multi-module of JSkat.

![alt screenshot](http://jskat.org/img/jskat_0.7_bidding.png)

License:

* Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)

Use at least JDK 11 to build and run JSkat. The project is organized with the Java Platform Module System (a.k.a. Jigsaw).

Build executable fat JAR: `./gradlew clean fatJar`

Build installation with multiple JARs and start scripts: `./gradlew clean installDist`

Build installation with fat JAR and start scripts: `./gradlew clean assembleDist`

Continous integration: https://travis-ci.org/b0n541/jskat-multimodule

We develop according the GitFlow workflow: http://nvie.com/posts/a-successful-git-branching-model/

So if you want to contribute to JSkat please create a feature branch from the `develop` branch and create pull requests
towards the `develop` branch.

[![Build Status](https://travis-ci.org/b0n541/jskat-multimodule.png?branch=develop)](https://travis-ci.org/b0n541/jskat-multimodule)

[![CodeScene general](https://codescene.io/images/analyzed-by-codescene-badge.svg)](https://codescene.io/projects/1209)

[![CodeScene Code Health](https://codescene.io/projects/1209/status-badges/code-health)](https://codescene.io/projects/1209)
[![CodeScene System Mastery](https://codescene.io/projects/1209/status-badges/system-mastery)](https://codescene.io/projects/1209)
