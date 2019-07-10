FILE=$1

java -jar cocor/Coco.jar -frames cocor ProgramDependenceGraph.atg 
javac *.java
java Compile $1 > $1.dot
dot -Tpng -o $1.png $1.dot
