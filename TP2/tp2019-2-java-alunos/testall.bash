#!/bin/bash
FILES=`ls fontes/*.uc`
for filename in $FILES; do
    java Compile "$filename" > "$filename".dot
    dot -Tpng "$filename".dot > "$filename".png
done
