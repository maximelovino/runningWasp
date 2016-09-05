// Put your libraries here (#include ...)
#include <WaspUSB.h>
#include <WaspUART.h>
//Basic UART communication example
void setup()
{
  // Configures internal multiplexer
  Utils.setMuxAux1();
  // Configures baudrate
  beginSerial(115200,1);
  serialFlush(1);  //clear buffers

  delay(500);
}

void loop()
{

  int a = 0x41;    
  char b = 'B';    

  // Sends a string through UART1  
  printString("dada",1);

  // Sends int a five times.
  for(int i=0; i<5;i++){
    printHex(a,1);
    delay(50);
  }
  delay(1000);

  // Sends char b five times.
  for(int i=0; i<5;i++){
    printByte(b,1);
    delay(50);
  }

  // Check if data is available on RX buffer of UART1 and prints it.
  // (sended from PC through the gateway, to Waspmote)

  USB.println("Received data");
  while(serialAvailable(1))
  {
    USB.println(serialRead(1));
  }
  delay(5000);
}



