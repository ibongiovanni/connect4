#!/bin/bash

export CLASPATH=""
for file in `ls target/dependency`; do export CLASSPATH=$CLASSPATH:target/dependency/$file; done
export CLASSPATH=$CLASSPATH:target/classes

echo "*******************  EXECUTING PROGRAM******************************************"
java -cp $CLASSPATH -Dactivejdbc.log com.unrc.app.App 

