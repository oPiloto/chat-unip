@echo off

javac -d .\bin -cp .\src .\src\server\Main.java
cd .\bin
java --enable-preview -cp . server.Main

pause    