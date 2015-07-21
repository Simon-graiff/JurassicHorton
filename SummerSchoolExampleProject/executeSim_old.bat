@echo off
cd ..
cd "I4Simulation"
start java -Dlog4j.configuration=file:classes/log4j.properties -jar DataAggregator.jar -d 1000 -o "%cd%/../SummerSchoolExampleProject/logs" -q