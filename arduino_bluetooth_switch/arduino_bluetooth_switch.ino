/*
 * Author: Ilias Giechaskiel
 * Website: https://ilias.giechaskiel.com
 * Description: Code that sets up bluetooth and waits for commands over serial
 * to switch power output
 *
 */

#include <SoftwareSerial.h>

// Pin where Arduino will receive commands
#define RX_PIN PB0

// Pin where Arduino will send commands
#define TX_PIN PB1

// Pins that are connected to transistor bases
#define OUT_PIN1 PB4
#define OUT_PIN2 PB3

// Delay between bluetooth commands
#define DELAY 600

// Max length for incoming commands
#define AR_LEN 150

// Bluetooth device name and pairing code
#define BT_NAME "YOUR-NAME"
#define BT_PASS "YOUR_PASS"

// Global variables
SoftwareSerial bluetooth(RX_PIN, TX_PIN);
char input[AR_LEN + 1];
int index = 0;
bool isOn = true;

// Function that sets AT commands
void sendCommand(const char* cmd, const char* param = NULL) {
  bluetooth.print("AT+");
  bluetooth.print(cmd);
  if (param) {
    bluetooth.print("=\"");
    bluetooth.print(param);
    bluetooth.print('"');
  }
  bluetooth.print("\r\n");
  delay(DELAY);
}

// Function that compares the two strings, ignoring case
bool areEqualCI(char const *s1, char const *s2) {
    while (true) {
        char c1 = tolower(*s1);
        char c2 = tolower(*s2);

        if (c1 != c2) {
          return false;
        } else if (c1 == '\0') {
          return true;
        }

        ++s1;
        ++s2;
    }
}

// Function that writes to the transistor pins
void writePins(bool val) {
  digitalWrite(OUT_PIN1, val);
  digitalWrite(OUT_PIN2, val);
}

void setup() {
  pinMode(OUT_PIN1, OUTPUT);
  pinMode(OUT_PIN2, OUTPUT);
  bluetooth.begin(38400);
  
  // Start with low output
  writePins(LOW);
  delay(DELAY);
  
  // Factory defaults + new name and pass
  sendCommand("ORGL");
  sendCommand("NAME", BT_NAME);
  sendCommand("PSWD", BT_PASS);

  // Commands to make it pairable
  sendCommand("INIT");
  sendCommand("INQ");
  sendCommand("INQC");

  // Wait to enable output
  delay(2*DELAY);
  writePins(HIGH);
}

void loop() {
  while (bluetooth.available()) {
    char c = bluetooth.read();
    
    // Do not overflow
    if (index == AR_LEN) {
      index = 0;
    }

    // Only process commands on CR or LF
    if (c == '\r' || c == '\n') {
      input[index] = '\0';
      index = 0;
      if (areEqualCI(input, "off")) {
        isOn = false;
      } else if (areEqualCI(input, "on")) {
        isOn = true;
      }
    } else {
      input[index] = c;
      ++index;
    }
  }

  // Update output
  writePins(isOn);
}
