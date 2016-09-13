#include <WaspGPRS_SIM928A.h>
#include <WaspUART.h>
#include <WaspUSB.h>

#define BASE_URL "http://sampang.internet-box.ch:8080"
#define GPS_TIMEOUT 200
#define AT_GPRS_APN "internet"
#define THRESHOLD 0.0001
#define DATA_FILE "DATA.TXT"

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
//Connected to GPRS network
bool gprs = false;
//Connected to an IP network through WiFly
bool network = false;
//if a run ended with a file created on the SD card
bool datafileAvailable = false;
//Concat string
char tmpString[80];
//String representation of the x value (longitude)
//16 because angles can be SXXX.XXXXXXXXXX = 15 char + the null
char x[16];
//String representation of the y value (latitude)
char y[16];
//String representation of the pc value (counter)
char pcs[6];
//String representation of the seconds elapsed
char time[6];
//Array of GPS points
float P[5][2];

int i = 0;

float a[2];

int cnt = 0;

int k = 0;

int val;

int startTime = 0;

/* Setup function
 * Initialize all modules check for errors
 */
void setup() {
  pinMode(DIGITAL3, INPUT);
  PWR.setSensorPower(SENS_3V3, SENS_ON);
  val = digitalRead(DIGITAL3);

  SD.ON();
  USB.ON();
  USB.println(F("Setup started"));
  
  // Configures multiplexer for WiFly
  Utils.setMuxAux1();
  // Configures baudrate for WiFly
  beginSerial(115200, 1);
  //clear buffers, ready to use.
  serialFlush(1); 
  USB.println(F("WiFly module ready"));

  //Activates the GPRS+GPS module: (1 and -3 are success code)
  retr = GPRS_SIM928A.ON();
  if (retr == 1 || retr == -3)
  {
    USB.println(F("GPRS+GPS module ready"));

    //Activating sim card
    USB.println(F("Setting PIN code..."));
    if (GPRS_SIM928A.setPIN(simCode) == 1)
    {
      USB.println(F("PIN code accepted"));
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
          gprs = true;
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
      USB.println(F("PIN code incorrect"));
    }

    retr = GPRS_SIM928A.GPS_ON();
    if(retr == 1)
    {
      USB.println(F("GPS engine started"));
      bool status = GPRS_SIM928A.waitForGPSSignal(GPS_TIMEOUT);
      if(status)
      {
        USB.println(F("GPS signal aquired"));
        Utils.setLED(LED1, LED_ON);
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
    USB.println(F("GPRS+GPS module activation failed"));
  }
  USB.println(F("Waiting for button press"));
  do 
  {
    retr = SD.isFile(DATA_FILE);
    if(retr == 1)
    {
      sendRunOverWifi();
    }
  } while((val == digitalRead(DIGITAL3)) || !alive);
  
  val = digitalRead(DIGITAL3);
  startTime = millis();
}


/* Loop function
 * Send data to the server
 */
void loop() 
{
  if(alive) 
  {
    updateGPS();
    sendGPSData();

    pc++;
    if(val != digitalRead(DIGITAL3)) 
    {
      endrun = true;
    }
  }

  if(endrun && alive)
  {
    endRun();
  }
  
  if(datafileAvailable)
  {
    sendRunOverWifi();
  }
}

void startRun() 
{
  if(gprs) 
  {
    USB.println(F("Starting a run..."));
    strcpy(tmpString, BASE_URL);
    strcat(tmpString, "/run.php?uid=1&start");
    USB.print(F("Contacting "));
    USB.println(tmpString);
    GPRS_SIM928A.readURL(tmpString, 1);
  } 
  else 
  {
    writeStartToSD();
  }
}

void endRun() 
{
  sprintf(time, "%i", (millis()-startTime) / 1000);
  if(gprs) 
  {
    strcpy(tmpString, BASE_URL);
    strcat(tmpString, "/run.php?uid=1&time=");
    strcat(tmpString, time);
    strcat(tmpString, "&end");
    GPRS_SIM928A.readURL(tmpString, 1);
  } 
  else 
  {
    strcpy(tmpString, "/end;");
    strcat(tmpString, "1;");
    strcat(tmpString, time);
    SD.appendln(DATA_FILE, tmpString);
    
    datafileAvailable = true;
  }
  alive = false;
  USB.println(F("Run stopped !"));
}

void updateGPS() 
{
  i = 0;
  k = 0;
  cnt = 0;
  a[0] = 0;
  a[1] = 0;
  while(i < 5) 
  {
    retr = GPRS_SIM928A.getGPSData(1);
    if(retr)
    {
      P[i][0] = GPRS_SIM928A.longitude;
      P[i][1] = GPRS_SIM928A.latitude;
      a[0] += GPRS_SIM928A.longitude;
      a[1] += GPRS_SIM928A.latitude;
      i++;
    }
  }
  a[0] /= i;
  a[1] /= i;
  USB.print(F("Average1: "));
  USB.print(a[0]);
  USB.print(F(", "));
  USB.println(a[1]);

  for(k = 0; k < i;k++) 
  {
    if(sqrt((P[k][0] - a[0])*(P[k][0] - a[0])+(P[k][1] - a[1])*(P[k][1] - a[1])) > THRESHOLD) 
    {
      P[k][0] = -200;
    }
    else 
    {
      cnt++;
    }
  }

  a[0] = 0;
  a[1] = 0;

  for(k = 0; k < i; k++) 
  {
    if(P[k][0] != -200) 
    {
      a[0] += P[k][0];
      a[1] += P[k][1];
    }
  }

  a[0] /= cnt;
  a[1] /= cnt;

  USB.print(F("Average2: "));
  USB.print(a[0]);
  USB.print(F(", "));
  USB.println(a[1]);

  if(cnt > 0) 
  {
    Utils.float2String(a[0], x, 10);
    Utils.float2String(a[1], y, 10);
    sprintf(pcs, "%i", pc);
    sprintf(time, "%i", millis() / 1000);
  }
}

void sendGPSData() 
{
  if(gprs) 
  {
    //build url
    Utils.setLED(LED0, LED_ON);
    strcpy(tmpString, BASE_URL);
    strcat(tmpString, "/run.php?uid=1&x=");
    strcat(tmpString, x);
    strcat(tmpString, "&y=");
    strcat(tmpString, y);
    strcat(tmpString, "&time=");
    strcat(tmpString, time);
    strcat(tmpString, "&cnt=");
    strcat(tmpString, pcs);
    USB.print(F("Contacting "));
    USB.println(tmpString);
    GPRS_SIM928A.readURL(tmpString, 1);
    Utils.setLED(LED0, LED_OFF);
  }
  else 
  {
    writeGPSToSD();
  }
}

void writeStartToSD() 
{
  SD.create(DATA_FILE);
  SD.appendln(DATA_FILE, "/start;1");
}

void writeGPSToSD() 
{
  strcpy(tmpString, "/run;");
  strcat(tmpString, x);
  strcat(tmpString, ";");
  strcat(tmpString, y);
  strcat(tmpString, ";");
  strcat(tmpString, pcs);
  strcat(tmpString, ";");
  strcat(tmpString, time);
  strcat(tmpString, ";1");
  SD.appendln(DATA_FILE, tmpString);
}

void sendRunOverWifi()
{
  USB.println(F("Trying to send run over Wifi"));
  USB.println(F("Connecting"));
  //serialFlush(1);
  delay(10);
  
  while(!network)
  {
    printString("$$$", 1);
    delay(400);
    printNewline(1);
    
    while(serialAvailable(1))
    {
      USB.print((char)serialRead(1));
    }
    USB.println();
    
    printString("open\r\n", 1);
    char* response = (char*) malloc(200 * sizeof(char));
    int i = 0;
    delay(2000);
    while(serialAvailable(1))
    {
      char next = (char)serialRead(1);
      response[i] = next;
      i++;
      USB.print(next);
    }
    USB.println();
    
    if(response[i - 1] == '*')
    {
      network = true;
      USB.println(F("NETWORK ACQUIRED"));
    }
      
    free(response);
    
    if(!network)
    {
      printString("exit\r\n", 1);
      delay(100);
      while(serialAvailable(1))
      {
        USB.print((char)serialRead(1));
      }
      USB.println();
    }
    delay(5000);
  }
  
  retr = SD.numln(DATA_FILE);
  for(int i = 0 ; i < retr ; i++)
  {
    SD.catln(DATA_FILE, i, 1);
    
    printString(SD.buffer, 1);
    delay(10);
  }
  
  delay(1000);
  printString("$$$", 1);
  delay(400);
  while(serialAvailable(1))
  {
    USB.print((char)serialRead(1));
  }
  USB.println();
  printString("close\r\n", 1);
  delay(200);
  while(serialAvailable(1))
  {
    USB.print((char)serialRead(1));
  }
  USB.println();
  printString("exit\r\n", 1);
  delay(200);
  while(serialAvailable(1))
  {
    USB.print((char)serialRead(1));
  }
  USB.println();
  USB.println(F("SLEEP MODE"));
  delay(50000000);  
  retr = SD.del(DATA_FILE);
  if(retr == 1)
  {
    USB.println(F("FILE DELETED"));
  }
  else
  {
    USB.println(F("FILE NOT DELETED FOR SOME REASON, THANKS OBAMA"));
  }
}


