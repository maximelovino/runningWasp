#include <WaspGPRS_SIM928A.h>

#define GPS_TIMEOUT 200 

void setup()
{
    USB.ON();
    USB.println(F("STARTING SETUP")); 
    
    GPRS_SIM928A._baudRate = 57600;
    //GPRS_SIM928A._socket = 0;
    
    GPRS_SIM928A.ON(); 
    USB.println(F("GPRS/GPS MODULE ON"));
    
    GPRS_SIM928A.setMode(GPRS_PRO_FULL);
    USB.println(F("POWERMODE SET"));
    
    GPRS_SIM928A.GPS_ON();
    USB.println(F("FUCKING SETUP IS DONE."));
    //GPRS_SIM928A.setTime();
}

void loop()
{
   USB.println(F("CATCHING SIGNAL - TIMEOUT 200"));
   // wait to connect for a maximum time of 200 seconds
   bool status = GPRS_SIM928A.waitForGPSSignal(GPS_TIMEOUT);
   if(status)
   {
      int stat;
      stat = GPRS_SIM928A.checkGPS();
      USB.print(F("GPS status: "));    
      USB.println(stat);
      
      GPRS_SIM928A.getGPSData(0);
      GPRS_SIM928A.getGPSData(1);
      USB.println(GPRS_SIM928A.buffer_GPRS);
      
      USB.print(F("COORD : "));    
      USB.println(GPRS_SIM928A.latitude);
      USB.println(GPRS_SIM928A.longitude);
      USB.println(GPRS_SIM928A.altitude);
   }
   else 
   {
     USB.println(F("TIMEOUT !"));
   }
}


