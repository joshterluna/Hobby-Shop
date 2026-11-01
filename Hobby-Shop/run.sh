#!/bin/bash
# Compile and run Aetherion from the Hobby-Shop folder (needs a JDK, Java 17+).
cd "$(dirname "$0")"
mkdir -p build
javac -d build src/hobbyshop/*.java || exit 1
java -cp build hobbyshop.HobbyShop
