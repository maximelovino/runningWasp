#include <WaspGPRS_SIM928A.h>

#define BASE_URL "http://sampang.internet-box.ch:8080"
#define GPS_TIMEOUT 200
#define AT_GPRS_APN "internet"

//temp varible to check errors
int retr = 0;
//counter for points
int pc = 1;
//sim code for the card
char simCode[5] = "3891";
//if the setp was successful this should be true
bool alive = false;
//if the run should be ended
bool endrun = false;
//Concat string
char tmpString[80];
//String representation of the x value (longitude)
//16 because angles can be SXXX.XXXXXXXXXX = 15 char + the nul
char x[16];
//String representation of the y value (latitude)
char y[16];
//String representation of the pc value (counter)
char pcs[6];


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
    }
    else
    {
      USB.println(F("PIN code incorrect"));
    }

    //Waits for connection to the network: 
    retr = GPRS_SIM928A.check(180); 
    if (retr == 1)
    {
      GPRS_SIM928A.set_APN(AT_GPRS_APN);
      USB.println(F("GPRS+GPS module connected to the network..."));
      // configures GPRS Connection for HTTP or FTP applications: 
      retr = GPRS_SIM928A.configureGPRS_HTTP_FTP(1);
      if (retr == 1)
      {
        USB.println(F("Network configuration done"));
        retr = GPRS_SIM928A.GPS_ON();
        if(retr == 1) 
        {
          USB.println(F("GPS engine started"));
          bool status = GPRS_SIM928A.waitForGPSSignal(GPS_TIMEOUT);
          if(status)
          {
            USB.println(F("GPS signal aquired"));
            USB.println(F("Starting a run..."));
            strcpy(tmpString, BASE_URL);
            strcat(tmpString, "/run.php?uid=1&start");
            USB.print(F("Contacting "));
            USB.println(tmpString);
            GPRS_SIM928A.readURL(tmpString, 1);
            alive = true;
          }
          else 
          {
            USB.println(F("I couldn't get GPS signal. I'll just die now, reboot me pls"));
          }
        }
        else  
        {
          USB.println(F("GPS engine couldn't start"));
        }
      } 
      else
      {
        USB.print(F("Network configuration failed. Error code:"));
        USB.println(retr, DEC); 
      }
    } 
    else
    {
      USB.println(F("GPRS+GPS module cannot connect to the network"));
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
  if(alive) {
    retr = GPRS_SIM928A.getGPSData(1);
    if(retr) 
    {
      Utils.float2String(GPRS_SIM928A.longitude, x, 10);
      Utils.float2String(GPRS_SIM928A.latitude, y, 10);
      sprintf(pcs, "%i", pc);
      
      USB.print(F("Longitude: "));
      USB.println(GPRS_SIM928A.longitude);
      USB.print(F("Latitude: "));
      USB.println(GPRS_SIM928A.latitude);
      //Maybe add altitude and stuff
      //build url
      strcpy(tmpString, BASE_URL);
      strcat(tmpString, "/run.php?uid=1&x=");
      strcat(tmpString, x);
      strcat(tmpString, "&y=");
      strcat(tmpString, y);
      strcat(tmpString, "&time=");
      strcat(tmpString, pcs);
      USB.print(F("Contacting "));
      USB.println(tmpString);
      GPRS_SIM928A.readURL(tmpString, 1);
    }
    //counter increments, even if the gps fails, to keep data integrity
    pc++;
    //wait 5 sec
    delay(5000);
    //purely fake condition to determine if the run should end while we dont have a button
    if(pc > 20) 
    {
      endrun = true;
    }
  }

  if(endrun && alive) 
  {
    //send a stop flag with the time elapsed, the pc*5 should be replaced with something more precise
    sprintf(pcs, "%i", pc*5);
    strcpy(tmpString, BASE_URL);
    strcat(tmpString, "/run.php?uid=1&time=");
    strcat(tmpString, pcs);
    strcat(tmpString, "&end");
    GPRS_SIM928A.readURL(tmpString, 1);
    alive = false;
    USB.println(F("Run stopped !"));
  }

}





