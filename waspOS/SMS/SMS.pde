#include <WaspGPRS_SIM928A.h>

#define BASE_URL "http://sampang.internet-box.ch:8080"
#define GPS_TIMEOUT 200
#define AT_GPRS_APN "internet"

//temp varible to check errors
int retr = 0;
//sim code for the card
char simCode[5] = "3891";
//Concat string
char tmpString[80];

/* Setup function
 * Initialize all modules check for errors
 */
void setup() {
  USB.ON();
  USB.println(F("USB port started"));

  //Activates the GPRS+GPS module: (1 and -3 are success code)
  retr = GPRS_SIM928A.ON();
  if ((retr == 1) || (retr == -3))
  {
    USB.println(F("GPRS+GPS module ready"));

    //Activating sim card
    USB.println(F("Setting PIN code..."));
    if (GPRS_SIM928A.setPIN(simCode) == 1)
    {
      USB.println(F("PIN code accepted")); 
      GPRS_SIM928A.setInfoIncomingSMS();
    }
    else
    {
      USB.println(F("PIN code incorrect"));
    }
  }
  else
  {
    USB.println(F("GPRS+GPS module activation failed"));
  }
}


/* Loop function
 * Send data to the server
 */
void loop() {
  int i = GPRS_SIM928A.manageIncomingData();
  if(i == 2) {
    USB.println(GPRS_SIM928A.tlfIN);
    USB.println(GPRS_SIM928A.buffer_GPRS);
  }
  USB.println("Looooooop")
}








