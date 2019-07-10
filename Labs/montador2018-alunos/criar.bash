echo "cria virtual machine..."
cd  vm
javac VMRun.java 

echo "cria montador..."
cd ..
java -jar cocor/Coco.jar -frames cocor/ Montador.atg 
javac *.java

echo "compila um exemplo em assembly"
java Compile asm/exemplo1.asm 

echo "executa o codigo objeto gerado"
java -cp vm/ VMRun asm/exemplo1.obj 

