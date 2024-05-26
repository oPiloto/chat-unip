@echo off

javac -d .\bin -cp .\src .\src\client\Main.java
cd .\bin
java --enable-preview -cp . client.Main %1 %2

pause    