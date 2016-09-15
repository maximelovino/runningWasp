#include <WaspGPRS_SIM928A.h>
#include <WaspUART.h>
#include <WaspUSB.h>

//URL to contact for run sending
#define BASE_URL "http://sampang.internet-box.ch:8080"
//In sec, time before failing the connection to the GPS network
#define GPS_TIMEOUT 200
//Number of tries (on try approx. 5 seconds) before failing connection to the WiFi network.
#define WIFI_CONNECTION_TRYOUT 12
//APN of the SIM card
#define AT_GPRS_APN "internet"
//WHATEVER THIS IS USED FOR, ZOMG WAT ???111
#define THRESHOLD 0.0001
//Name of the file containing a run on the SD card.
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
//Always useful.
int i = 0;
int k = 0;
//amount of points that are correct at when averaging
int cnt = 0;
//average points
float a[2];
//"old" value of the button to test if it has changed
int val;
//begin of the run in milliseconds
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

  //If a run file is on the SD card, waspOS tries to send it immediately
  retr = SD.isFile(DATA_FILE);
  if(retr == 1)
  {
    sendRunOverWifi();
  }

  //Waiting for the button to change value, meaning activation.
  USB.println(F("Waiting for button press"));
  do { } while((val == digitalRead(DIGITAL3)) || !alive);

  //Updating position of button after change and starting the run.
  val = digitalRead(DIGITAL3);
  startTime = millis();
}


/* Loop function
 * Send data to the server
 */
void loop()
{
  //Setup succeeded, the run is ongoing.
  if(alive)
  {
    updateGPS();
    sendGPSData();

    pc++;
    //Looking for a button movement, meaning end of run.
    if(val != digitalRead(DIGITAL3))
    {
      endrun = true;
    }
  }

  //End of run asked, end of run still able to deliver !
  if(endrun && alive)
  {
    endRun();
  }

  //If end of run created a datafile...
  if(datafileAvailable)
  {
    sendRunOverWifi();
  }
}

void startRun()
{
  if(gprs)
  {
    //Sending start signal to webserver.
    USB.println(F("Starting a run..."));
    strcpy(tmpString, BASE_URL);
    strcat(tmpString, "/run.php?uid=1&start");
    USB.print(F("Contacting "));
    USB.println(tmpString);
    GPRS_SIM928A.readURL(tmpString, 1);
  }
  //In case the GPRS network bailed :
  else
  {
    writeStartToSD();
  }
}

void endRun()
{
  //Formating the time in sec, obtaining the total duration of the run
  sprintf(time, "%i", (millis()-startTime) / 1000);
  if(gprs)
  {
    //Sending end signal to server
    strcpy(tmpString, BASE_URL);
    strcat(tmpString, "/run.php?uid=1&time=");
    strcat(tmpString, time);
    strcat(tmpString, "&end");
    GPRS_SIM928A.readURL(tmpString, 1);
  }
  else
  {
    //If no GPRS, appending end of run signal on SD card
    strcpy(tmpString, "/end;");
    strcat(tmpString, "1;");
    strcat(tmpString, time);
    SD.appendln(DATA_FILE, tmpString);

    //Declaring a data file available.
    datafileAvailable = true;
  }
  //Run is ended here, loop function will not do anything now.
  alive = false;
  USB.println(F("Run stopped !"));
}

//Very mysterious function believed to cause some shit in North Korea
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
    //Build point adding signal and sending it to webserver
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
  //Creating file if not existing and appending a start signal
  SD.create(DATA_FILE);
  SD.appendln(DATA_FILE, "/start;1");
}

void writeGPSToSD()
{
  //Simply appending a run signal that can be understood by the socket
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
  // Configures multiplexer for WiFly. Cannot be done at setup because of interferences with other modules' setup.
  Utils.setMuxAux1();
  beginSerial(115200, 1);
  serialFlush(1);
  USB.println(F("WiFly module ready"));
  USB.println(F("Trying to send run over Wifi"));
  USB.println(F("Connecting"));
  delay(10);

  //Loop for connection to the wify network
  int tryout = 0;
  while(!network)
  {
    //Entering conf mode for wifly
    printString("$$$", 1);
    delay(400);
    printNewline(1);

    //Reading UART answer
    while(serialAvailable(1))
    {
      USB.print((char)serialRead(1));
    }
    USB.println();

    //Trying to open the TCP connection stored in wifly conf
    printString("open\r\n", 1);
    //Preparing string to store UART answer
    char* response = (char*) malloc(200 * sizeof(char));
    int i = 0;
    //Waiting two seconds for answer
    delay(2000);
    while(serialAvailable(1))
    {
      char next = (char)serialRead(1);
      response[i] = next;
      i++;
      USB.print(next);
    }
    USB.println();

    //Last character of answer should be this if the TCP connection opened. (WIFly answers "*OPEN*")
    if(response[i - 1] == '*')
    {
      //Network acquired, otherwise loop will start another connection try.
      network = true;
      USB.println(F("NETWORK ACQUIRED"));
    }

    free(response);

    if(!network)
    {
      //Exiting configuration mode to let wifly connect to WiFi network
      printString("exit\r\n", 1);
      delay(100);
      //Printing WiFly answer to leaving conf mode
      while(serialAvailable(1))
      {
        USB.print((char)serialRead(1));
      }
      USB.println();

      //Adding a missed try
      tryout++;
      //If max missed tries reached, abandoning the connection
      if(tryout >= WIFI_CONNECTION_TRYOUT)
      {
        USB.println(F("Max connections tries exceeded. Proceeding with execution. File not sent."));
        return;
      }
    }
    //Waiting 5 seconds for WiFly to connect the WiFi network if not done already
    delay(5000);
  }

  //Now that connection is obtained, checking the number of lines of the file
  retr = SD.numln(DATA_FILE);
  //And reading lines one by one
  for(int i = 0 ; i < retr ; i++)
  {
    SD.catln(DATA_FILE, i, 1);

    char* string = SD.buffer;
    USB.print(F("NOW SENDING : "));
    USB.println(string);
    //And sending one line each 20ms to the socket
    printString(string, 1);
    delay(20);
  }

  //Now that file is sent, entering conf mode in WiFly
  delay(1000);
  printString("$$$", 1);
  delay(400);
  while(serialAvailable(1))
  {
    USB.print((char)serialRead(1));
  }
  USB.println();
  //Closing TCP connection
  printString("close\r\n", 1);
  delay(200);
  while(serialAvailable(1))
  {
    USB.print((char)serialRead(1));
  }
  USB.println();
  //And exiting conf mode
  printString("exit\r\n", 1);
  delay(200);
  while(serialAvailable(1))
  {
    USB.print((char)serialRead(1));
  }
  USB.println();

  //Then we delete the file on the SD card to allow a new run to be stored
  retr = SD.del(DATA_FILE);
  if(retr == 1)
  {
    USB.println(F("FILE DELETED"));
  }
  else
  {
    USB.println(F("FILE NOT DELETED FOR SOME REASON, THANKS OBAMA")); //Thanks Obama.
  }
}

//I'm a teapot.
