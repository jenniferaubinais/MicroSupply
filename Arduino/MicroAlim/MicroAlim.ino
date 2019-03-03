#include <stdint.h>
#include <inttypes.h>
#include <MS_JA.h>
#include <WString.h>
//
#define pPowerIn A1
#define pTest 6
#define testPower 5
// Debug
#define flagDebug false
//***************************************************************************//
unsigned long new_value = 0;
//***************************************************************************//
Button myBtnUP(buttonPinUP, PULLUP, INVERT, DEBOUNCE_MS);
Button myBtnDOWN(buttonPinDOWN, PULLUP, INVERT, DEBOUNCE_MS);
Button myBtnONOFF(buttonONOFF, PULLUP, INVERT, DEBOUNCE_MS);
//***************************************************************************//
// Variables
//***************************************************************************//
// Button UP
enum {ONOFF, TO_BLINK, BLINK, TO_ONOFF};
uint8_t STATE_UP;
// Button DOWN
uint8_t STATE_DOWN;
unsigned long ms_Button;
unsigned long msLast_Button;
// 
bool flagONOFF = false;                                                                                                                                      ;
bool flagSecurity = false;
struct RESULTAT {
  uint32_t current;
  uint16_t voltage;
  bool flagMESURE;
};
bool flagSerial = false;
RESULTAT Resultat = {0,0,true};
bool valBlink = false;
int Power = 0;
int countOrangeLed = 0;
//***************************************************************************//
// MAIN program
//***************************************************************************//  
void setup()   
{
  //------
  // LEDs
  //------
  pinMode(ledRed, OUTPUT);
  pinMode(ledGreen, OUTPUT);
  pinMode(ledOK, OUTPUT);
  digitalWrite(ledRed, LOW);
  digitalWrite(ledGreen, LOW); 
  digitalWrite(ledOK, LOW); 
  //------------ 
  // Set button
  //------------
  pinMode(buttonPinUP, INPUT_PULLUP);
  pinMode(buttonPinDOWN, INPUT_PULLUP);
  pinMode(buttonONOFF, INPUT_PULLUP);
  //------------
  // all in/out
  //------------
  pinMode(testPower, INPUT);
  pinMode(pTest,OUTPUT);
  digitalWrite(pTest,LOW);
  //----------------
  // init port OLED
  //----------------
  InitOled();
  ResetOled();
  WriteOled(4,5,"(c) Elektor");
  WriteOled(6,15,"2018 - JA");
  //-------------
  // Init Serial
  //-------------
  Serial.begin(115200);
  Serial.println("Hello, Micro Supply");
  Serial.println("(c) Elektor - 170464");
  Serial.println("(c) Jennifer AUBINAIS 2018 version 3.0");
  delay(2000);
  //---------------------
  // Message if Debug Mode
  //---------------------
  if (flagDebug)
  {
    ClearOled();
    WriteOled(0,0,"!!!!!!!!!!!!");
    WriteOled(3,5,"Debug Mode");
    WriteOled(6,20,"No PC !!");
    delay(2000);
  }
  //---------------------
  // wait external Power
  //---------------------
  int VPower = (analogRead(pPowerIn))/40;
  writeDeb("External power : ", flagDebug);
  writelnDeb((String)VPower, flagDebug);
  if (VPower < 10)
  {
    ClearOled();
    WriteOled(1,5,"Connect");
    WriteOled(3,5,"External");
    WriteOled(5,5,"10V Power");
  }
  // add limit+++
  float fTempo = (float)analogRead(pPowerIn) * 1.1 / 40;
  while(VPower < 10)
  {
    fTempo = (float)analogRead(pPowerIn) * 1.1 / 40;
    VPower = (int)fTempo;
    writeDeb("External power : ", flagDebug);
    writelnDeb((String)VPower, flagDebug);
    delay(100);
  }
  //---------------------
  // Display current limit from external Power
  //---------------------
  fTempo = (float)analogRead(pPowerIn) / 2;
  fTempo = fTempo * 5;
  VPower = (int)fTempo;
  int Vhigh = VPower / 100;
  int Vlow = VPower - (Vhigh * 100);
  String Vstring = (String)Vhigh + "." + (String)Vlow;
  ClearOled();
  WriteOled(1,5,"Limit");
  WriteOled(3,5,"");
  WriteOled(5,5,(String)Vstring);
  delay(2000);
  //----------
  // Init I2C
  //----------
  quikeval_I2C_init();
  TWSR = (HARDWARE_I2C_PRESCALER_1 & 0x03);  
  TWBR = 2;
  //-----------------
  // Init Power at 0
  //-----------------
  int8_t retour = i2c_write_word_data(AddrLTC2631,0b01110000,0);
  retour = i2c_write_word_data(AddrLTC2631,0b00110000,0);
  //----------------------------
  // Read calibrate
  //----------------------------
  byte Calibrate  = EEPROM.read(2);
  writeDeb("Calibrate Mem : ", flagDebug);
  writelnDeb((String)Calibrate, flagDebug);
  if (Calibrate > 10) Calibrate = 5;
  if (digitalRead(buttonPinUP) == 0 && digitalRead(buttonPinDOWN) == 0) 
  {
    Calibrate = functCalibrate(flagDebug);
    EEPROM.write(2, Calibrate);
    writeDeb("Calibrate OK : ", flagDebug);
    writelnDeb((String)Calibrate, flagDebug);
  }
  setCalibrate(Calibrate);
  while(digitalRead(buttonPinUP) == 0);
  while(digitalRead(buttonPinDOWN) == 0);
  delay(100);
  while(digitalRead(buttonPinUP) == 0);
  while(digitalRead(buttonPinDOWN) == 0);
  //----------------------------
  // Read for Test if necessary
  //----------------------------
  uint16_t adc_code = 0;
  //uint8_t acknowledge = i2c_read_word_data(AddrLTC2309, 0b10001000, &adc_code);
  //do {
  //  acknowledge = i2c_read_word_data(AddrLTC2309, 0b10011000, &adc_code);
  //  adc_code = adc_code >> 4;
  //  adc_code = adc_code & 0xFFF;
  //  writelnDeb((String)adc_code, true);
  //} while (1 == 1);
  //functTest();
  //do {
  //  uint16_t retour = readI2C(AddrLTC2309,0b10101000);
  //  Serial.println(retour,DEC);
  //} while(1);
  if (digitalRead(buttonONOFF) == 0) functTest();
  if (digitalRead(buttonPinDOWN) == 0) functTest();
  if (digitalRead(buttonPinUP) == 0) functTest();
  //-------------------------------------------------
  // Read Power value from EEPROM and Output LTC2631
  //-------------------------------------------------
  Power  = (EEPROM.read(0) * 256) + EEPROM.read(1);
  Power = Power / 5;
  Power = Power * 5;
  if ((Power > 501) || (Power < 150))
  {
    Power = 150;
    EEPROM.write(0, Power >> 8);
    EEPROM.write(1, Power & 0x00FF);
  }
  writeDeb("Power in memory : ", flagDebug);
  writelnDeb((String)Power, flagDebug);
  //-----------------
  // OLED with power
  //-----------------
  ResetOled();
  WritePowerOled(Power);
  //----------------
  // interrupt 1kHz
  //----------------
  bitClear(TCCR0A,WGM00);
  //bitSet(TCCR0A,COM0A0);
  OCR0A = 249;
  bitSet(TIMSK0,OCIE0A);
}
//***************************************************************************//
// LOOP
//***************************************************************************//
void loop()
{
  //------------
  // test Power
  //------------
  if (digitalRead(testPower) == 0)
  {
    WriteOledEnd("ERROR : ","Low Power",""," Reboot");
  }
  if ((analogRead(pPowerIn)/4) < 10)
  {
    WriteOledEnd("ERROR : ","Low Power",""," Reboot");
  }
  //---------------
  // button ON/OFF
  //---------------
  if (digitalRead(buttonONOFF) == 0)
  {
    changeONOFF();
    while(digitalRead(buttonONOFF) == 0) {}
    delay(100);
    while(digitalRead(buttonONOFF) == 0) {}
  }
  //-------------------
  // test Serial input
  //-------------------
  if(Serial.available())
  {
    byte value =  Serial.read();
    if (value == 'B')
    {
      startSerial();
    }
    if ((value != 'B') & (value != 10))
    {
      if (value == 'E')
      {
        stopSerial();
      }
    }
  }
  //--------------------------
  // Read buttons Up and Down
  //--------------------------
  if (flagONOFF == false)
  {
    //-----------
    // button UP
    //-----------
    setMillis(new_value);
    ms_Button = millis();
    myBtnUP.read();
    setMillis(new_value);
    switch (STATE_UP)
    {
      case ONOFF:
        if (myBtnUP.wasReleased())
          switchUP();
        else if (myBtnUP.pressedFor(LONG_PRESS))
        {
          STATE_UP = TO_BLINK;
        }
        break;
      case TO_BLINK:
        if (myBtnUP.wasReleased())
        {
          STATE_UP = ONOFF;
          switchUP();
        }
        else
          fastUP();
        break;
      case BLINK:
        if (myBtnUP.pressedFor(LONG_PRESS))
        {
          STATE_UP = TO_ONOFF;
        }
        else
          fastUP();
        break;
      case TO_ONOFF:
        if (myBtnUP.wasReleased())
        {
          STATE_UP = ONOFF;
        }
        break;
    }
    //-------------
    // button DOWN
    //-------------
    setMillis(new_value);
    ms_Button = millis();
    myBtnDOWN.read();
    setMillis(new_value);
    switch (STATE_DOWN)
    {
      case ONOFF:
        if (myBtnDOWN.wasReleased())
          switchDOWN();
        else if (myBtnDOWN.pressedFor(LONG_PRESS))
        {
          STATE_DOWN = TO_BLINK;
        }
        break;
      case TO_BLINK:
        if (myBtnDOWN.wasReleased())
        {
          STATE_DOWN = ONOFF;
          switchDOWN();
        }
        else
          fastDOWN();
        break;
      case BLINK:
        if (myBtnDOWN.pressedFor(LONG_PRESS))
        {
          STATE_DOWN = TO_ONOFF;
        }
        else
          fastDOWN();
        break;
      case TO_ONOFF:
        if (myBtnDOWN.wasReleased())
        {
          STATE_DOWN = ONOFF;
        }
        break;
    }
  }
  if ((flagONOFF == true) && (flagSerial == false))
  {
    //-----------
    // Blink led
    //-----------
    valBlink ^= 1;
    digitalWrite(ledOK,valBlink);
    //Serial.print("flagONOFF :");
    //Serial.print(flagONOFF);
    //Serial.print(" - flagSerial :");
    //Serial.println(flagSerial );
    //----------
    // Read ADC
    //----------
    Resultat =  MesureADC(true);
    writelnDeb(String(Resultat.current),flagDebug);
    if (Resultat.flagMESURE)
    {
      if ((Resultat.current > 20100) && (flagSecurity == true))
      {
        //-------------
        // Test Secure
        //-------------
        digitalWrite(ledRed, 1);
        digitalWrite(ledGreen, 0);
        flagONOFF = false;
        writelnDeb("false", flagDebug);
        delay(5000);
      }
    }
  }
  if (flagSerial == true)
  {
    countOrangeLed++;
    if (countOrangeLed > 50)
    {
      //-----------
      // Blink led
      //-----------
      valBlink ^= 1;
      digitalWrite(ledOK,valBlink);
      countOrangeLed = 0;
    }
  }
}
//***************************************************************************//
// Interrupt is called once 2 millisecond
//***************************************************************************//  
SIGNAL(TIMER0_COMPA_vect)
{
  //--------------------------------
  // milliseconds counter increment
  //--------------------------------
  new_value++;
  if (flagSerial)
  {
    digitalWrite(pTest,HIGH);
    if (digitalRead(testPower) == 0)
    {
      WriteOledEnd("ERROR : ","Low Power",""," Reboot");
    }
    //--------------
    // Read current
    //--------------
    uint16_t uI = readCurrent(Power,flagDebug);
    //Serial.print(" => ");
    //Serial.println(uI);
    //------------------
    // Send Datas to PC
    //------------------
    char val[4];
    val[0] = char('\n');
    val[1] = (unsigned long)(uI & 0x3F) + 0x30;
    val[2] = (unsigned long)((uI>>6) & 0x3F) + 0x30;
    val[3] = (unsigned long)((uI>>12) & 0x0F) + 0x30;
    //unsigned long valueSerial = ((unsigned long)(val[1]-0x30) & 0x0000003F)
    //+ ((unsigned long)((val[2]-0x30)<<6) & 0x00000FC0)
    //+ ((unsigned long)((val[3]-0x30)<<12) & 0x0000F000);
    //Serial.println(valueSerial);
    String sVal = String(val[0])+String(val[1])+String(val[2])+String(val[3]);
    Serial.print(sVal);
    digitalWrite(pTest,LOW);
  }
}
//***************************************************************************//
// Read B (Begin) from PC
//***************************************************************************//
void startSerial()
{
  if (flagDebug)
  {
    WriteOledEnd("ERROR : ","Debug Mode",""," New Build");
  }
  //--------------
  // Write SERIAL
  //--------------
  WriteSerialOled();
  //-------------------
  // Turn on green led
  //-------------------
  digitalWrite(ledRed, 0);
  digitalWrite(ledGreen, 1);
  //---------------
  // Set Power Out
  //---------------
  Power  = (EEPROM.read(0) * 256) + EEPROM.read(1);
  PowerOut(Power,flagDebug);
  //------------------
  // send begin to PC
  //------------------
  Serial.print('\n');
  Serial.print("no?");
  //------
  // flag
  //------
  flagONOFF = true;
  flagSerial = true;
}
//***************************************************************************//
// Read E (End) from PC
//***************************************************************************//
void stopSerial()
{
  //------
  // flag
  //------
  flagONOFF = true;
  flagSerial = false;
  //----------------
  // send END to PC
  //----------------
  Serial.print('\n');
  Serial.print("jo?");
  //------------
  // clear Oled
  //------------
  ClearOled();
}
//***************************************************************************//
// If the button ONOFF is pushed
//***************************************************************************//
void changeONOFF()
{
  if (flagONOFF)
  {
    flagONOFF = false;
    flagSerial = false;
    writelnDeb("Switch OFF", flagDebug);
    //----------------
    // Send END to PC
    //----------------
    Serial.print('\n');
    Serial.print("jo?");
    //-----------------
    // Init Power at 0
    //-----------------
    int8_t retour = i2c_write_word_data(AddrLTC2631,0b00110000,0x0000);
    ResetOled();
    Power  = (EEPROM.read(0) * 256) + EEPROM.read(1);
    WritePowerOled(Power);
    //--------------
    // Turn off led
    //--------------
    digitalWrite(ledRed, 0);
    digitalWrite(ledGreen, 0);
    digitalWrite(ledOK,0);
    //----------------------
    // send again END to PC
    //----------------------
    Serial.print('\n');
    Serial.print("jo?");
  }
  else
  {
    flagONOFF = true;
    flagSerial = false;
    writelnDeb("Switch ON", flagDebug);
    //-------------
    // Turn on led
    //-------------
    digitalWrite(ledRed, 0);
    digitalWrite(ledGreen, 1);
    //---------------
    // Set Power Out
    //---------------
    Power  = (EEPROM.read(0) * 256) + EEPROM.read(1);
    ClearOled();
    PowerOut(Power,flagDebug);
    Resultat =  MesureADC(false);
  }
}
//***************************************************************************//
// Buttons
//***************************************************************************//
void switchUP()
{
  msLast_Button = ms_Button;
  Power  = (EEPROM.read(0) * 256) + EEPROM.read(1);
  if (Power < 500)
  {
    Power = Power + 5;
    WritePowerOled(Power);
    writeDeb("Power UP : ", flagDebug);
    writelnDeb(String(Power), flagDebug);
    EEPROM.write(0, Power >> 8);
    EEPROM.write(1, Power & 0x00FF);
  }
}
//***************************************************************************//
void fastUP()
{
  if (ms_Button - msLast_Button >= INCRE_INTERVAL)
    switchUP();
}
//***************************************************************************//
void switchDOWN()
{
  msLast_Button = ms_Button;
  Power  = (EEPROM.read(0) * 256) + EEPROM.read(1);
  if (Power > 150)
  {
    Power = Power - 5;
    WritePowerOled(Power);
    writeDeb("Power DOWN : ", flagDebug);
    writelnDeb(String(Power), flagDebug);
    EEPROM.write(0, Power >> 8);
    EEPROM.write(1, Power & 0x00FF);
  }
}
//***************************************************************************//
void fastDOWN()
{
  if (ms_Button - msLast_Button >= INCRE_INTERVAL)
    switchDOWN();
}
//***************************************************************************//
// Mesure ADC (normal loop)
//***************************************************************************//
RESULTAT MesureADC(bool flagNON)
{
  //Serial.println("Mesure");
  RESULTAT Result = {0,0,true};
  uint16_t uI = readCurrent(0,flagDebug);
  if (uI != 65530)
  {
    int pOut = readPower(flagDebug);
    WritePowerOled(pOut);
    String str;
    if (!flagNON)
    {
      return Result;
    }
    if (uI != 65535)
    {    
      String str;
      char Text[11] = {15,15,15,15,15,15,15,13,11,15,15};
      //----------
      // 0 to 9uA
      //----------
      if (uI < 10)
      {
        char a[2];
        str = String(uI);
        str.toCharArray(a, 2);
        Text[5] = a[0]-0x30;
        WriteCurrentOled(Text);
      }
      else
      {
        //------------
        // 10 to 99uA
        //------------
        if (uI < 100)
        {
          char b[3];
          str = String(uI);
          str.toCharArray(b, 3);
          Text[4] = b[0]-0x30;
          Text[5] = b[1]-0x30;
          WriteCurrentOled(Text);
        }
        else
        {
          //--------------
          // 100 to 999uA
          //--------------
          if (uI < 1000)
          {
            char c[4];
            str = String(uI);
            str.toCharArray(c, 4);
            Text[3] = c[0]-0x30;
            Text[4] = c[1]-0x30;
            Text[5] = c[2]-0x30;
            WriteCurrentOled(Text);
          }
          else
          {
            Text[7] = 12; // m
            //--------------
            // 1mA to 9.99mA
            //--------------
            if (uI < 10000)
            {
              char d[5];
              str = String(uI);
              str.toCharArray(d, 5);
              Text[2] = d[0]-0x30;
              Text[3] = 10; // .
              Text[4] = d[1]-0x30;
              Text[5] = d[2]-0x30;
              WriteCurrentOled(Text);
            }
            else
            {
              //--------------
              // 10mA to 40mA
              //--------------
              char e[6];
              str = String(uI);
              str.toCharArray(e, 6);
              Text[2] = e[0]-0x30;
              Text[3] = e[1]-0x30;
              Text[4] = 10; // .
              Text[5] = e[2]-0x30;
              WriteCurrentOled(Text); 
            }
          }
        }
      }
    }
    else
    {
      //-------------------------
      // Current High , turn off
      //-------------------------
      byte retour = i2c_write_word_data(AddrLTC2631,0b01110000,0);
      retour = i2c_write_word_data(AddrLTC2631,0b00110000,0);
      digitalWrite(ledRed, 1);
      digitalWrite(ledGreen, 0);
      WriteOledFunc("ERROR : ","  HIGH","  current","PRESS");
      while(digitalRead(buttonPinUP) == 1){} 
      changeONOFF();    
      delay(1000);
      while(digitalRead(buttonPinUP) == 0){}  
      Result.flagMESURE = false;
      digitalWrite(ledRed, 0);
      digitalWrite(ledGreen, 0);
    }
  }
  else
  {
    writelnDeb("ERROR",flagDebug);
    delay(200);
    WriteOledEnd("ERROR : ","Power off",""," Reboot");
  }
  return Result;
}
