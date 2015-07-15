@echo off
cd C:\Users\Christian\Documents\Kanada\I4Simulation
start java -Dlog4j.configuration=file:/c:/Users/Christian/Documents/Kanada/I4Simulation/classes/log4j.properties -jar DataAggregator.jar -d 20000 -o "C:\\"
pause