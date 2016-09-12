
void setup() {
  pinMode(DIGITAL3, INPUT);
  PWR.setSensorPower(SENS_3V3, SENS_ON);
}


void loop() {
  int val = digitalRead(DIGITAL3);
  USB.println(val);
}



