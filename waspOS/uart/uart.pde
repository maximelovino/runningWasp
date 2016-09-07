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
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  printString("open sampang.internet-box.ch 8080\r\n", 1);
  delay(200);
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  
  delay(5000);
  printString("exit\r\n", 1);
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
}

void loop()
{  
  delay(100);
  
  printString("GET /run.php?uid=1&start HTTP/1.1\r\nHost: sampang.internet-box.ch\r\nConnection: keep-alive\r\n\r\n>", 1);
  delay(10000);
  USB.println("Received data");
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  
  delay(100);
  
  printString("GET /run.php?uid=1&x=7&y=9&time=1 HTTP/1.1\r\nHost: sampang.internet-box.ch\r\nConnection: keep-alive\r\n\r\n", 1);
  delay(10000);
  USB.println("Received data");
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  
  delay(100);
  
  printString("GET /run.php?uid=1&time=2&end HTTP/1.1\r\nHost: sampang.internet-box.ch\r\nConnection: keep-alive\r\n\r\n", 1);
  delay(10000);
  USB.println("Received data");
  while(serialAvailable(1))
  {
    USB.println((char)serialRead(1));
  }
  
  
  USB.println("SLEEP MODE");
  closeSerial(1);
  delay(5000000);
}



