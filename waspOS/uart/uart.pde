// Put your libraries here (#include ...)
#include <WaspUSB.h>
#include <WaspUART.h>
//Basic UART communication example

bool network = false;

void setup()
{
  // Configures internal multiplexer
  Utils.setMuxAux1();
  // Configures baudrate
  beginSerial(115200, 1);
  serialFlush(1);  //clear buffers
  
  while(!network)
  {
    delay(5000);
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
      network = true;
      
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
    
    USB.println(network);
  }
}

void loop()
{
  printString("/start;1", 1);
  delay(10);
  printString("/run;12;4;1;32;1", 1);
  delay(10);
  printString("/run;12;3;2;37;1", 1);
  delay(10);
  printString("/run;12;2;3;42;1", 1);
  delay(10);
  printString("/end;1;63", 1);
  
  delay(10000);
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
  
  USB.println("SLEEP MODE");
  delay(5000000);
}



