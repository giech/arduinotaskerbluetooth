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

// Maximum delay between bluetooth AP resets
#define MAX_CONTINUOUS 60*60*1000

// Bluetooth device name and pairing code
#define BT_NAME "YOUR-NAME"
#define BT_PASS "YOUR_PASS"

// Global variables
SoftwareSerial bluetooth(RX_PIN, TX_PIN);
char input[AR_LEN + 1];
int index = 0;
bool isOn = true;
long last_reset = 0;

// Function that sets AT commands
bool send_command(const char* cmd, const char* param = NULL) {
  bluetooth.print("AT+");
  bluetooth.print(cmd);
  if (param) {
    bluetooth.print("=\"");
    bluetooth.print(param);
    bluetooth.print('"');
  }
  bluetooth.print("\r\n");
  delay(DELAY);

  return read_incoming() && !are_equal_ic(input, "OK");
}

// Function that compares the two strings, ignoring case
bool are_equal_ic(char const *s1, char const *s2) {
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
void write_pins(bool val) {
  digitalWrite(OUT_PIN1, val);
  digitalWrite(OUT_PIN2, val);
}

// Function that resets bluetooth AP info
void reset_bluetooth() {
  delay(DELAY);
  
  // Factory defaults + new name and pass
  send_command("ORGL");
  send_command("NAME", BT_NAME);
  send_command("PSWD", BT_PASS);

  // Commands to make it pairable
  send_command("INIT");
  send_command("INQ");
  send_command("INQC");
  
  delay(2*DELAY);
  
  last_reset = millis();
}

void setup() {
  pinMode(OUT_PIN1, OUTPUT);
  pinMode(OUT_PIN2, OUTPUT);
  bluetooth.begin(38400);
  
  // Start with low output
  write_pins(LOW);
  
  reset_bluetooth();

  // Switch to high output
  write_pins(HIGH);
}

inline bool is_crlf(char c) {
  return c == '\r' || c == '\n';
}

// Function to read incoming message from Bluetooth
bool read_incoming() {
  while (bluetooth.available()) {
    char c = bluetooth.read();
    
    // Do not overflow
    if (index == AR_LEN) {
      index = 0;
    }

    // Only process commands on CR or LF
    if (is_crlf(c)) {
      // Consume remaining CR/LF characters
      while (is_crlf(bluetooth.peek())) {
        bluetooth.read();
      }
      
      input[index] = '\0';
      index = 0;
      return true;
    } else {
      input[index] = c;
      ++index;
    }
  }
  return false;
}

void loop() {

  // Reset if running for too long
  if ((millis() - last_reset) > MAX_CONTINUOUS) {
    reset_bluetooth();
  }

  if (!read_incoming()) {
    return;
  }

  if (are_equal_ic(input, "off")) {
    isOn = false;
  } else if (are_equal_ic(input, "on")) {
    isOn = true;
  } else if (are_equal_ic(input, "toggle")) {
    isOn = !isOn;
  }

  // Update output
  write_pins(isOn);
}
