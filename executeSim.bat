@echo off
cd "I4Simulation"
start java -Dlog4j.configuration=file:classes/log4j.properties -jar DataAggregator.jar -d 1000 -o "C:\\" -q