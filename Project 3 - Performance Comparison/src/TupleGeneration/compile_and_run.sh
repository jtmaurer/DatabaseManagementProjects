#!/bin/bash

# Remove existing .class files in the bin folder
rm -f bin/*.class

# Compile all Java files in the src folder
javac -d bin --enable-preview --release 22 *.java

java -cp bin --enable-preview TestTupleGenerator