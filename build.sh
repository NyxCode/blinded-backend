#!/bin/sh

rm -rf build/libs/
rm blinded-backend.jar
git pull
./gradlew shadowJar
mv build/libs/blinded-backend.jar .
