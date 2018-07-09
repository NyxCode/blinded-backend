#!/bin/sh

echo "deleting existing artifacts..."
rm -rf build/libs/
rm blinded-backend.jar

echo "updating repository..."
git pull

echo "building..."
./gradlew shadowJar

echo "copying artifacts..."
mv build/libs/blinded-backend.jar .
