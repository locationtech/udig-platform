#!/bin/bash

LIB_DIR="lib"
LIB_SRC_DIR="lib-src"
SRC="-sources"
REGEX="^${LIB_DIR}/(.*)(.jar)$"

for JAR in ${LIB_DIR}/*.jar
do
    if [[ -f "${JAR}" ]]; then
        [[ "${JAR}" =~ $REGEX ]]
    
        JAR_NAME="${BASH_REMATCH[1]}"
        EXT="${BASH_REMATCH[2]}"
        SRC_JAR="${LIB_SRC_DIR}/${JAR_NAME}${SRC}${EXT}"

        if [[ -f "${SRC_JAR}" ]]; then
            sed -E -i 's#(path="'"${JAR}"'")/>$#\1 sourcepath="'"${SRC_JAR}"'"/>#' .classpath
        fi
    fi
done

echo "done"
