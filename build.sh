#!/bin/sh

rm -rf build/libs/
./gradlew shadowJar
mv build/libs/blinded-backend.jar .
