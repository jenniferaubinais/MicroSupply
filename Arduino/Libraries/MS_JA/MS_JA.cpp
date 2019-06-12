
/*!

*/

#include <Arduino.h>
#include <MS_JA.h>

// 0.96" SPI OLED (D11=Data and D13=Clk)
SSD1306_text oled(OLED_DC, OLED_RST, OLED_CS);

//***************************************************************************//
//***************************************************************************//
unsigned char font[] = {
    0b01111100,0b10001010,0b10010010,0b10100010,0b01111100,0b00000000, // 0 - 0
    0b00000000,0b01000010,0b11111110,0b00000010,0b00000000,0b00000000, // 1 - 1
    0b01000010,0b10000110,0b10001010,0b10010010,0b01100010,0b00000000, // 2 - 2
    0b10000100,0b10000010,0b10100010,0b11010010,0b10001100,0b00000000, // 3
    0b00011000,0b00101000,0b01001000,0b11111110,0b00001000,0b00000000, // 4
    0b11100100,0b10100010,0b10100010,0b10100010,0b10011100,0b00000000, // 5
    0b00111100,0b01010010,0b10010010,0b10010010,0b00001100,0b00000000, // 6
    0b10000000,0b10001110,0b10010000,0b10100000,0b11000000,0b00000000, // 7
    0b01101100,0b10010010,0b10010010,0b10010010,0b01101100,0b00000000, // 8
    0b01100000,0b10010010,0b10010010,0b10010100,0b01111000,0b00000000, // 9
    0b00000000,0b00000110,0b00000110,0b00000000,0b00000000,0b00000000, // . - 10
    0b01111110,0b10001000,0b10001000,0b10001000,0b01111110,0b00000000, // A - 11
    0b00111110,0b00100000,0b00011100,0b00100000,0b00111110,0b00000000, // m - 12
    0b01101100,0b00010000,0b00010000,0b01100000,0b00000000,0b00000000, // µ - 13
    0b11111000,0b00000100,0b00000010,0b00000100,0b11111000,0b00000000, // V - 14
    0b00000000,0b00000000,0b00000000,0b00000000,0b00000000,0b00000000, // space - 15
    0b00000000,0b00001100,0b00001100,0b00001100,0b00001100,0b00000000, // - - 16
    0b11111110,0b10010010,0b10010010,0b10010010,0b10000010,0b00000000, // E - 17
    0b11111110,0b10010000,0b10011000,0b10010100,0b01100010,0b00000000, // R - 18
    0b01111100,0b10000010,0b10000010,0b10000010,0b01111100,0b00000000, // O - 19
    0b01000000,0b10000000,0b11111110,0b10000000,0b10000000,0b00000000, // T - 20
    0b10101010,0b10101010,0b10101010,0b10101010,0b10101010,0b10101010, // barre - 21
    0b01100010,0b10010010,0b10010010,0b10010010,0b10001100,0b00000000, // S - 22
    0b00000000,0b10000010,0b11111110,0b10000010,0b00000000,0b00000000, // I - 23
    0b11111110,0b00000010,0b00000010,0b00000010,0b00000010,0b00000000, // L - 24
};
//***************************************************************************//
//***************************************************************************//
volatile uint8_t *csport, *dcport, cspinmask, dcpinmask;
extern volatile unsigned long timer0_millis;
volatile byte MemCalibrate = 5;
volatile uint16_t memVoltage = 0;
//***************************************************************************//
// read current
//***************************************************************************//
uint16_t readCurrent(int iPower, bool flag)
{
  uint16_t adc_code_10 = 0;
  uint16_t adc_code_100 = 0;
  uint16_t adc_code_1 = 0;
  uint16_t adc_code_OUT = 0;
  uint8_t acknowledge = 0;
  byte x_100 = 0b01111000; // port CH6-CH7
  byte x_10 = 0b01101000; // port CH4-CH5
  byte x_1 = 0b11011000; // port CH3
  byte x_OUT = 0b11001000; // CH1 output power
  acknowledge = i2c_read_word_data(AddrLTC2309, x_100, &adc_code_100);
  uint16_t uI = 65535;
  if (acknowledge == 0)
  {
    acknowledge = i2c_read_word_data(AddrLTC2309, x_10, &adc_code_100);
    acknowledge = i2c_read_word_data(AddrLTC2309, x_1, &adc_code_10);
    acknowledge = i2c_read_word_data(AddrLTC2309, x_OUT, &adc_code_1);
    acknowledge = i2c_read_word_data(AddrLTC2309, x_OUT, &adc_code_OUT);
    adc_code_10 = adc_code_10 >> 4;
    adc_code_10 = adc_code_10 & 0xFFF;
    adc_code_100 = adc_code_100 >> 4;
    adc_code_100 = adc_code_100 & 0xFFF;
    adc_code_1 = adc_code_1 >> 4;
    adc_code_1 = adc_code_1 & 0xFFF;
    adc_code_OUT = adc_code_OUT >> 4;
    adc_code_OUT = adc_code_OUT & 0xFFF;
    if (flag)
    {
      writeDeb((String)adc_code_100,true);
      writeDeb("-",true);
      writeDeb((String)adc_code_10,true);
      writeDeb("-",true);
      writeDeb((String)adc_code_1,true);
      writeDeb(" - ",true);
      writelnDeb((String)adc_code_OUT,true);
    }
    memVoltage = adc_code_OUT;
    // Fuse to 100mV Low
      writeDeb("Low Power : ",flag);
    writeDeb((String)(adc_code_OUT*2),flag);
    writeDeb(" / ",flag);
    iPower = (iPower * 10) - 100;
    writelnDeb((String)iPower,flag);
    if (((adc_code_OUT*2) < iPower) && (iPower != 0))
    {
      if (flag)
      {
        WriteOledEnd("Log Power", (String)iPower, (String)adc_code_OUT, "");
      }
      else
      {
        WriteOledEnd("Error Power", "", "output", "is low");
      }
      return 65520;
    }
    if ((adc_code_1 < 10) && (adc_code_10 < 10) && (adc_code_100 < 10))
    {
      if (flag)
      {
        WriteOledEnd("Log 65530", (String)adc_code_1, (String)adc_code_10, (String)adc_code_100);
      }
      else
      {
        WriteOledEnd("Error 65530", (String)adc_code_1, (String)adc_code_10, (String)adc_code_100);
      }
      return 65530;
    }
    if (adc_code_1 < 4060)
    {
      // * 100
      if (adc_code_100 < limite)
      {
          if (MemCalibrate < 10)
          {
              uI = (adc_code_100 + MemCalibrate)/10;
          }
          else
          {
              uI = (adc_code_100 - (MemCalibrate-10))/10;
          }
      }
      else
      // * 10
      {
        if (adc_code_10 < limite)
        {
          uI = adc_code_10;
        }
        else
        // * 1
        {
          uI = adc_code_1*10;
        }
      }
    }
  }
  else
  {
    if (flag)
    {
      WriteOledEnd("Log 65530", (String)adc_code_1, (String)adc_code_10, (String)adc_code_100);
    }
    else
    {
      WriteOledEnd("Error 65530", (String)adc_code_1, (String)adc_code_10, (String)adc_code_100);
    }
    uI = 65530;
  }
  if (flag)
  {
    writeDeb("<< ",true);
    writeDeb((String)uI,true);
    writelnDeb(" uA >>",true);
  }
  return uI;
}
//***************************************************************************//
// read power out
//***************************************************************************//
uint16_t readPower(bool flag)
{
  memVoltage = memVoltage + 4;
  writeDeb("<< ",flag);
  writeDeb(String(memVoltage), flag);
  writelnDeb(" V >>",flag);
  return memVoltage;
}
//***************************************************************************//
// Power Out
//***************************************************************************//
void PowerOut(int ValueOut, bool flag)
{
    uint32_t uTempo = (uint32_t)ValueOut;
    writelnDeb(String(uTempo), flag);
    uTempo = (uTempo * 4095) / 500;
    writelnDeb(String(uTempo), flag);
    uTempo = uTempo << 4;
    uTempo = uTempo & 0xFFF0;
    int8_t retour = i2c_write_word_data(AddrLTC2631,0b00110000,(uint16_t)uTempo);
}
//***************************************************************************//
// Write Power on OLED (new font)
//***************************************************************************//
void WritePowerOled(int Power)
{
    char Text[11] = {15,15,15,15,15,15,15,14,15,15,15};
    if (Power != 999)
    {
        byte tempo0 = Power/100;
        Text[2] = tempo0;
        Text[3] = 10;
        byte tempo1 = Power - (tempo0 * 100);
        tempo0 = tempo1/10;
        Text[4] = tempo0;
        tempo1 = tempo1 - (tempo0 * 10);
        Text[5] = tempo1;
    }
    oled.setCursor(5,0);
    WriteOledFont(0,Text);
    WriteOledFont(1,Text);
    WriteOledFont(2,Text);
}
//***************************************************************************//
// Write Current on OLED (new font)
//***************************************************************************//
void WriteCurrentOled(char myPointer[])
{
    oled.setCursor(1,0);
    WriteOledFont(0,myPointer);
    WriteOledFont(1,myPointer);
    WriteOledFont(2,myPointer);
}
//***************************************************************************//
// Write "SERIAL" on OLED (new font)
//***************************************************************************//
void WriteSerialOled()
{
    char Text[11] = {15,15,22,17,18,23,11,24,15,15,15};
    oled.clear();
    oled.setCursor(3,0);
    WriteOledFont(0,Text);
    WriteOledFont(1,Text);
    WriteOledFont(2,Text);
}
//***************************************************************************//
// Write "ERROR" on OLED (new font)
//***************************************************************************//
void WriteErrorOled()
{
    char Text[11] = {15,15,15,17,18,18,19,18,15,15,15};
    oled.clear();
    oled.setCursor(3,0);
    WriteOledFont(0,Text);
    WriteOledFont(1,Text);
    WriteOledFont(2,Text);
}
//***************************************************************************//
// Init OLED
//***************************************************************************//
void InitOled()
{
    oled.init();
    oled.clear();
    SPI.begin();
    //
    csport      = portOutputRegister(digitalPinToPort(OLED_CS));
    cspinmask   = digitalPinToBitMask(OLED_CS);
    dcport      = portOutputRegister(digitalPinToPort(OLED_DC));
    dcpinmask   = digitalPinToBitMask(OLED_DC);
}
//***************************************************************************//
// Clear OLED and write "MicroSupply"
//***************************************************************************//
void ClearOled()
{
    oled.clear();
}
//***************************************************************************//
// Clear OLED and write "MicroSupply"
//***************************************************************************//
void ResetOled()
{
    oled.clear();
    oled.setCursor(2,5);
    oled.setTextSize(2,1);
    oled.print("MicroSupply");
}
//***************************************************************************//
// Write at position one text
//***************************************************************************//
void WriteOled(int Line, int Pos, String stText)
{
    oled.setCursor(Line,Pos);
    oled.setTextSize(2,1);
    oled.print(stText);
}
//***************************************************************************//
// Write BIG at position one text
//***************************************************************************//
void WriteOledBig(int Line, int Pos, String stText)
{
    oled.setCursor(Line,Pos);
    oled.setTextSize(3,8);
    oled.print(stText);
}
//***************************************************************************//
// Write OLED High caracters
//***************************************************************************//
void WriteOledFont(int Line, char myPointer[])
{
    bool first = true;
    // test
    int chiffre = 0;
    int compt = 0;
    char poschar = 0;
    for (uint8_t i = 0; i < 128; i++) {
        uint8_t LineChar0 = 0;
        uint8_t pos0 = uint8_t(poschar/2)+(myPointer[chiffre]*6);
        unsigned char ReadChar = font[pos0];
        switch (Line)
        {
            case 0:
                if ((ReadChar&0x80) == 0x80) LineChar0 |= 0b00000111;
                if ((ReadChar&0x40) == 0x40) LineChar0 |= 0b00111000;
                if ((ReadChar&0x20) == 0x20) LineChar0 |= 0b11000000;
                break;
            case 1:
                if ((ReadChar&0x20) == 0x20) LineChar0 |= 0b00000001;
                if ((ReadChar&0x10) == 0x10) LineChar0 |= 0b00001110;
                if ((ReadChar&0x08) == 0x08) LineChar0 |= 0b01110000;
                if ((ReadChar&0x04) == 0x04) LineChar0 |= 0b10000000;
                break;
            case 2:
                if ((ReadChar&0x04) == 0x04) LineChar0 |= 0b00000011;
                if ((ReadChar&0x02) == 0x02) LineChar0 |= 0b00011100;
                if ((ReadChar&0x01) == 0x01) LineChar0 |= 0b11100000;
                break;
        }
        poschar++;
        *csport |= cspinmask;
        *dcport |= dcpinmask;
        *csport &= ~cspinmask;
        SPI.transfer(LineChar0);
        *csport |= cspinmask;
        if ((compt+1)%6 == 0)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                first = true;
                chiffre++;
                poschar = 0;
            }
        }
        compt++;
    }
}
//***************************************************************************//
// I2C test if a device is present
//***************************************************************************//
bool testI2C()
{
    oled.setCursor(5,0);
    byte address;
    for(address = 1; address < 127; address++ )
    {
        *csport |= cspinmask;
        *dcport |= dcpinmask;
        *csport &= ~cspinmask;
        SPI.transfer(15);
        *csport |= cspinmask;
        int8_t ack=0;
        ack |= i2c_start();
        ack |= i2c_write((address<<1) | I2C_WRITE_BIT);
        if (ack == 0)
        {
            i2c_stop();
            return true;
        }
    }
    return false;
}
//***************************************************************************//
// Write 4 lines on OLED and LOOP (need to reset)
//***************************************************************************//
bool WriteOledEnd(String st1, String st2, String st3, String st4)
{
    oled.clear();
    oled.setTextSize(2,1);
    oled.setCursor(0,0);
    oled.print(st1);
    oled.setCursor(2,0);
    oled.print(st2);
    oled.setCursor(4,0);
    oled.print(st3);
    oled.setCursor(6,0);
    oled.print(st4);
    digitalWrite(ledRed, 1);
    digitalWrite(ledGreen, 0);
    while(true)
    {
    }
}
//***************************************************************************//
// Write 3 lines on OLED and wait press the button up to continue
//***************************************************************************//
bool WriteOledFunc(String st1, String st2, String st3, String Function)
{
    bool FlExit = true;
    oled.clear();
    oled.setTextSize(2,1);
    oled.setCursor(0,0);
    oled.print(st1);
    oled.setCursor(2,0);
    oled.print(st2);
    oled.setCursor(4,0);
    oled.print(st3);
    oled.setCursor(6,0);
    // NEXT => wait press key
    if (Function == "NEXT")
    {
        oled.print("       Next");
    }
    // PRESS => wait press key
    if (Function == "PRESS")
    {
        oled.print("      Press");
    }
    if ((Function == "NEXT") || (Function == "PRESS"))
    {
      delay(50);
      while (!digitalRead(buttonPinUP)) {}
      while (!digitalRead(buttonPinDOWN)) {}
      bool Floop = true;
      while(Floop)
      {
        bool valueYes = digitalRead(buttonPinUP);
        if (valueYes == LOW)
        {
            delay(10); // anti-rebond
            valueYes = digitalRead(buttonPinUP);
            if (valueYes == LOW) // Check still
            {
                Floop = false;
            }
        }
      }
    }
    // END => loop
    if (Function == "END")
    {
        oled.print(" !!");
        while(true) {}
    }
    // YESNO => return true if YES
    if (Function == "YESNO")
    {
      oled.setCursor(6,0);
      oled.print("No      Yes");
      delay(50);
      while (!digitalRead(buttonPinUP)) {}
      while (!digitalRead(buttonPinDOWN)) {}
      bool Floop = true;
      while(Floop)
      {
        bool valueYes = digitalRead(buttonPinUP);
        if (valueYes == LOW)
        {
            delay(10); // anti-rebond
            valueYes = digitalRead(buttonPinUP);
            if (valueYes == LOW) // Check still
            {
                Floop = false;
                FlExit = true;
            }
        }
        bool valueNo = digitalRead(buttonPinDOWN);
        if (valueNo == LOW)
        {
            delay(10); // anti-rebond
            valueNo = digitalRead(buttonPinDOWN);
            if (valueNo == LOW) // Check still
            {
                Floop = false;
                FlExit = false;
            }
        }
      }
      if (FlExit)
      {
        Serial.println("YES");
      }
      else
      {
        Serial.println("NO");
      }
    }
    return FlExit;
}
//***************************************************************************//
// Convert Float to String
//***************************************************************************//
String ConvertFloatString(float fOut, byte valComa)
{
    char chOut[sizeof(float)];
    dtostrf(fOut, 4, valComa, chOut);
    String stOut = "";
    for(int k=0; k<sizeof(chOut); k++)
    {
        if (chOut[k] != ' ')
        {
            stOut += chOut[k];
        }
    }
    return stOut;
}
//***************************************************************************//
// Read I2C port
//***************************************************************************//
uint16_t readI2C(byte address,byte x)
{
    uint16_t adc_code = 0;
    uint8_t acknowledge = i2c_read_word_data(address, x, &adc_code);
    acknowledge = i2c_read_word_data(address, x, &adc_code);
    if (acknowledge == 0)
    {
        adc_code = adc_code >> 4;
        adc_code = (adc_code & 0xFFF);
        return adc_code;
    }
    return 0xFFFF;
}
//***************************************************************************//
// Function RESET
//***************************************************************************//
void(* resetFunc) (void) = 0; //declare reset function @ address 0
//***************************************************************************//
// All test if button is presse on startup
//***************************************************************************//
byte functCalibrate(bool flag)
{
    delay(500);
    ResetOled();
    WriteOled(5,0," Calibrate ");
    delay(1500);
    WriteOledFunc("Connect","1M ohm","on output","NEXT");
    byte retour = i2c_write_word_data(AddrLTC2631,0b00110000,0xFFFF);
    retour = i2c_write_word_data(AddrLTC2631,0b00110000,0xFFFF);
    byte value = readI2C(AddrLTC2309,0b01111000);
    byte Min = 100;
    byte Max = 0;
    for (int x = 0; x < 100; x++)
    {
        delay(10);
        value = readI2C(AddrLTC2309,0b01111000);
        if (Max >= 60)
        {
            WriteOledFunc("ERROR max","Calibrate","1M connect","END");
        }
        if (value < 30)
        {
            WriteOledFunc("ERROR min","Calibrate","1M connect","END");
        }
        if (value > Max) Max = value;
        if (value < Min) Min = value;
        writelnDeb(String(value),true);
    }
    retour = i2c_write_word_data(AddrLTC2631,0b00110000,0);
    retour = i2c_write_word_data(AddrLTC2631,0b00110000,0);
    if (flag)
    {
        writeDeb("--------------",true);
        writeDeb("Min : ",true);
        writeDeb((String)Min,true);
        writeDeb(" - Max : ",true);
        writelnDeb((String)Max,true);
    }
    if ((Max-Min) > 9)
    {
        WriteOledFunc("ERROR diff","Calibrate","1M connect","END");
    }
    WriteOledFunc("MicroSupply","is Ready","","NEXT");
    return 60-Min;
}
//***************************************************************************//
// Set value to variable
//***************************************************************************//
void setCalibrate(byte Calibrate)
{
    MemCalibrate = Calibrate;
}
//***************************************************************************//
// All test if button is presse on startup
//***************************************************************************//
void functTest()
{
    //goto test;
    delay(500);
    ResetOled();
    WriteOled(5,10,"** TEST **");
    delay(1500);
    WriteOledFunc("Connect","100k ohm","on output","NEXT");
    //goto toto;
    // led green right
    digitalWrite(ledOK, HIGH);
    if (!WriteOledFunc("orange led","is on ?","","YESNO"))
    {
        WriteOledLed("orange");
    }
    digitalWrite(ledOK, LOW);
    // led red bottom
    digitalWrite(ledRed, HIGH);
    digitalWrite(ledGreen, LOW);
    if (!WriteOledFunc("red led","is on ?","","YESNO"))
    {
        WriteOledLed("red");
    }
    // led green bottom
    digitalWrite(ledRed, LOW);
    digitalWrite(ledGreen, HIGH);
    if (!WriteOledFunc("green led","is on ?","","YESNO"))
    {
        WriteOledLed("green");
    }
    digitalWrite(ledRed, LOW);
    digitalWrite(ledGreen, LOW);
    // led blue
    if (!WriteOledFunc("blue led","is on ?","","YESNO"))
    {
        if (!WriteOledFunc("Check the","test point","5V ?","YESNO"))
        {
            WriteOledFunc("Check power","and LT1762"," !!","END");
        }
        WriteOledLed("blue");
    }
    // test I2C
    WriteOledFunc("Check I2C","port","","NEXT");
    if (!testI2C())
    {
        WriteOledFunc("ERROR I2C","component","no found !!","END");
    }
    // test LTC2631
    WriteOledFunc("Check","IC: LTC2631","","NEXT");
    if (!testCircuitI2C(AddrLTC2631))
    {
        WriteOledReadI2C("LTC2631");
    }
    // test LTC2309
    WriteOledFunc("Check","IC LTC2309","","NEXT");
    if (!testCircuitI2C(AddrLTC2309))
    {
        WriteOledReadI2C("LTC2309");
    }
    // Check 2.5V
    if (!WriteOledFunc("Check","2.5V with","voltmeter ?","YESNO"))
    {
        WriteOledFunc("ERROR ref","IC: LT1790"," 2.5V !!","END");
    }
    // Check 1.25V
    if (!WriteOledFunc("Check","1.25V with","voltmeter ?","YESNO"))
    {
        WriteOledFunc("ERROR ref","IC: LT1790"," 1.25V !!","END");
    }
toto:
    // Check DAC = 775 mV
    uint8_t acknowledge = i2c_write_word_data(AddrLTC2631,0b01110000,0b0100110011000000);
    acknowledge = i2c_write_word_data(AddrLTC2631,0b00110000,0b0100110011000000);
    if (!WriteOledFunc("Check DAC","775mV with","voltmeter ?","YESNO"))
    {
        WriteOledFunc("ERROR ref","IC: LTC2631"," !!","END");
    }
    //goto toto2;
    // test read LTC2309 1.25V
    WriteOledFunc("Check read","LTC2309","1,25V input","NEXT");
    if (!testReadI2C(AddrLTC2309,0b10101000,1250,6))
    {
        WriteOledReadI2C("LTC2309");
    }
    // test read LTC2309 all gain
    WriteOledGain("10");
    if (testReadI2C(AddrLTC2309,0b01101000,16,3) == 99)
    {
        WriteOledReadI2C("LTC2309");
    }
    WriteOledGain("100");
    if (testReadI2C(AddrLTC2309,0b01111000,160,30) == 99)
    {
        WriteOledReadI2C("LTC2309");
    }
toto2:
    WriteOledFunc("Connect","1K ohm","on output","NEXT");
    WriteOledGain("1");
    if (testReadI2C(AddrLTC2309,0b10011000,160,30) == 99)
    {
        WriteOledReadI2C("LTC2309");
    }
    WriteOledFunc("MicroSupply","is Ready","","NEXT");
    resetFunc();  //call reset
    while(true){}
}
//***************************************************************************//
// Write test gain on OLED
//***************************************************************************//
void WriteOledGain(String stG)
{
    WriteOledFunc("Check read","LTC2309","GAIN " + stG,"NEXT");
}
//***************************************************************************//
// Write error led on OLED
//***************************************************************************//
void WriteOledLed(String color)
{
    WriteOledFunc("Check the","direction","of led " + color,"END");
}
//***************************************************************************//
// Write error I2C on OLED
//***************************************************************************//
void WriteOledReadI2C(String valLTC)
{
    WriteOledFunc("ERROR Read","IC: " + valLTC," !!","END");
}
//***************************************************************************//
// I2C test if connect to device is possible
//***************************************************************************//
bool testCircuitI2C(byte address)
{
    Wire.beginTransmission(address);
    if (Wire.endTransmission() == 0)
    {
        return true;
    }
    return false;
}
//***************************************************************************//
// I2C test if char can be read
//***************************************************************************//
int testReadI2C(byte address,byte x,uint16_t Ucompare, uint8_t Udiff)
{
    uint16_t adc_code = 0;
    uint8_t acknowledge = i2c_read_word_data(address, x, &adc_code);
    acknowledge = i2c_read_word_data(address, x, &adc_code);
    if (acknowledge == 0)
    {
        adc_code = adc_code >> 4;
        adc_code = (adc_code & 0xFFF);
        Serial.print("<< ");
        Serial.print(adc_code,DEC);
        Serial.println(" >>");
        if (adc_code < 2)
        {
            return 0;
        }
        if ((adc_code > (Ucompare-Udiff)) && (adc_code < (Ucompare+Udiff)))
        {
            return 88;
        }
    }
    return 99;
}
//***************************************************************************//
// Debug
//***************************************************************************//
void writeDeb(String stDebug, boolean flag)
{
    if (flag) Serial.print(stDebug);
}
void writelnDeb(String stDebug, bool flag)
{
    if (flag) Serial.println(stDebug);
}
//***************************************************************************//
// Set milliseconds to official counter
//***************************************************************************//
void setMillis(unsigned long new_millis)
{
    uint8_t oldSREG = SREG;
    cli();
    timer0_millis = new_millis;
    SREG = oldSREG;
}


