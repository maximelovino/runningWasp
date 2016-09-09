// Put your libraries here (#include ...)
#include <WaspUSB.h>
#include <WaspUART.h>
//Basic UART communication example
void setup()
{
  char carriageReturn = 13;
  // Configures internal multiplexer
  Utils.setMuxAux1();
  // Configures baudrate
  beginSerial(115200, 1);
  serialFlush(1);  //clear buffers

  printString("$$$", 1);
  delay(400);
  printNewline(1);
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  delay(1000);
  printString("open\r\n", 1);
  delay(2000);
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  
  delay(5000);
}

void loop()
{
  printString("/start;1", 1);
  delay(10);
  printString("/run;12;4;1;1", 1);
  delay(10);
  printString("/run;12;3;2;1", 1);
  delay(10);
  printString("/run;12;2;3;1", 1);
  delay(10);
  printString("/end;1;63", 1);
  
  
  delay(10000);
  printString("$$$", 1);
  delay(400);
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  printString("close\r\n", 1);
  delay(200);
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  printString("exit\r\n", 1);
  delay(200);
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  
  USB.println("SLEEP MODE");
  delay(5000000);
}



