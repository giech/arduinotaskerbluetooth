EESchema Schematic File Version 2
LIBS:power
LIBS:device
LIBS:transistors
LIBS:conn
LIBS:linear
LIBS:regul
LIBS:74xx
LIBS:cmos4000
LIBS:adc-dac
LIBS:memory
LIBS:xilinx
LIBS:special
LIBS:microcontrollers
LIBS:dsp
LIBS:microchip
LIBS:analog_switches
LIBS:motorola
LIBS:texas
LIBS:intel
LIBS:audio
LIBS:interface
LIBS:digital-audio
LIBS:philips
LIBS:display
LIBS:cypress
LIBS:siliconi
LIBS:opto
LIBS:atmel
LIBS:contrib
LIBS:valves
LIBS:userlib
EELAYER 27 0
EELAYER END
$Descr A4 11693 8268
encoding utf-8
Sheet 1 1
Title "Arduino Bluetooth switch"
Date "26 dec 2015"
Rev ""
Comp "Ilias Giechaskiel"
Comment1 "https://ilias.giechaskiel.com"
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
$Comp
L ATTINY85-P IC1
U 1 1 56799C92
P 7650 1600
F 0 "IC1" H 6500 2000 40  0000 C CNN
F 1 "ATTINY85-P" H 8650 1200 40  0000 C CNN
F 2 "DIP8" H 8650 1600 35  0000 C CIN
F 3 "" H 7650 1600 60  0000 C CNN
	1    7650 1600
	1    0    0    -1  
$EndComp
$Comp
L NPN Q1
U 1 1 56799CB7
P 5100 2900
F 0 "Q1" H 5100 2750 50  0000 R CNN
F 1 "S8050" H 5100 3050 50  0000 R CNN
F 2 "~" H 5100 2900 60  0000 C CNN
F 3 "~" H 5100 2900 60  0000 C CNN
	1    5100 2900
	0    1    1    0   
$EndComp
$Comp
L NPN Q2
U 1 1 56799CC6
P 5100 3450
F 0 "Q2" H 5100 3300 50  0000 R CNN
F 1 "S8050" H 5100 3600 50  0000 R CNN
F 2 "~" H 5100 3450 60  0000 C CNN
F 3 "~" H 5100 3450 60  0000 C CNN
	1    5100 3450
	0    1    1    0   
$EndComp
$Comp
L NPN Q3
U 1 1 56799CD5
P 5100 3950
F 0 "Q3" H 5100 3800 50  0000 R CNN
F 1 "S8050" H 5100 4100 50  0000 R CNN
F 2 "~" H 5100 3950 60  0000 C CNN
F 3 "~" H 5100 3950 60  0000 C CNN
	1    5100 3950
	0    1    1    0   
$EndComp
$Comp
L JUMPER JP1
U 1 1 56799CE4
P 5100 4550
F 0 "JP1" H 5100 4700 60  0000 C CNN
F 1 "JUMPER" H 5100 4470 40  0000 C CNN
F 2 "~" H 5100 4550 60  0000 C CNN
F 3 "~" H 5100 4550 60  0000 C CNN
	1    5100 4550
	1    0    0    -1  
$EndComp
$Comp
L HC-05 M1
U 1 1 5679A074
P 1950 1300
F 0 "M1" H 450 1400 60  0000 C CNN
F 1 "HC-05" H 400 2150 60  0000 C CNN
F 2 "" H 400 2150 60  0000 C CNN
F 3 "" H 400 2150 60  0000 C CNN
	1    1950 1300
	-1   0    0    1   
$EndComp
NoConn ~ 4450 1550
NoConn ~ 6300 1550
NoConn ~ 6300 1850
$Comp
L VCC_IN +5V
U 1 1 5679E47A
P 3150 4800
F 0 "+5V" H 3150 4800 60  0001 C CNN
F 1 "VCC_IN" H 1900 4950 60  0001 C CNN
F 2 "~" H 3150 4800 60  0000 C CNN
F 3 "~" H 3150 4800 60  0000 C CNN
	1    3150 4800
	1    0    0    -1  
$EndComp
$Comp
L VCC_OUT U?
U 1 1 5679E77D
P 9300 5550
F 0 "U?" H 9300 5550 60  0001 C CNN
F 1 "VCC_OUT" H 9300 5550 60  0001 C CNN
F 2 "~" H 9300 5550 60  0000 C CNN
F 3 "~" H 9300 5550 60  0000 C CNN
	1    9300 5550
	1    0    0    -1  
$EndComp
Connection ~ 5300 3550
Connection ~ 5300 3000
Connection ~ 4900 3000
Connection ~ 4900 3550
Connection ~ 5300 4050
Connection ~ 4900 4050
Wire Wire Line
	5300 3000 5300 4050
Wire Wire Line
	5300 4050 5400 4050
Wire Wire Line
	5400 4050 5400 4550
Connection ~ 5400 4550
Wire Wire Line
	4800 4550 4800 4050
Wire Wire Line
	4800 4050 4900 4050
Wire Wire Line
	4900 4050 4900 3000
Wire Wire Line
	4750 1750 6300 1750
Wire Wire Line
	6300 1650 6150 1650
Connection ~ 6150 1750
Wire Wire Line
	4500 1450 6300 1450
Wire Wire Line
	4600 1350 6300 1350
Wire Wire Line
	4450 1950 4450 4650
Connection ~ 4800 4550
Wire Wire Line
	2350 4550 4800 4550
Wire Wire Line
	4450 4450 2350 4450
Connection ~ 4450 2050
Wire Wire Line
	4450 1850 4600 1850
Wire Wire Line
	4600 1850 4600 4550
Connection ~ 4600 4550
Wire Wire Line
	2350 4450 2350 1050
Connection ~ 4450 4450
Wire Wire Line
	5400 4550 8150 4550
Wire Wire Line
	4450 4650 8150 4650
Wire Wire Line
	2350 4550 2350 4800
Wire Wire Line
	9000 1350 9000 1050
Wire Wire Line
	9000 1050 2350 1050
Text Label 2100 4450 0    60   ~ 0
+5V
Text Label 8150 4650 0    60   ~ 0
+5V
Wire Wire Line
	9000 1850 9000 4800
Wire Wire Line
	9000 4800 2350 4800
Connection ~ 2350 4450
Connection ~ 2350 4550
Wire Wire Line
	4450 1650 4500 1650
Wire Wire Line
	4500 1650 4500 1450
Wire Wire Line
	4450 1750 4600 1750
Wire Wire Line
	4600 1750 4600 1350
Wire Wire Line
	6150 1650 6150 1750
Wire Wire Line
	4750 1750 4750 3750
Wire Wire Line
	4750 2700 5100 2700
Wire Wire Line
	4750 3250 5100 3250
Connection ~ 4750 2700
Wire Wire Line
	4750 3750 5100 3750
Connection ~ 4750 3250
$EndSCHEMATC
