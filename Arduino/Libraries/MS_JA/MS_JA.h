/*!
*/
#include <SSD1306_text.h>
#include <SPI.h>
#include <Wire.h>
#include <LT_I2C.h>
#include <JC_Button.h>
#include <EEPROM.h>
// Leds
#define ledRed A3
#define ledGreen A2
#define ledOK A0
// port OLED
#define OLED_DC 7
#define OLED_CS 0
#define OLED_RST 10
// Buttons
#define buttonPinUP 3
#define buttonPinDOWN 4
#define buttonONOFF 2
// For JC_Button
#define PULLUP true
#define INVERT true
#define DEBOUNCE_MS 20
#define LONG_PRESS 1000
#define INCRE_INTERVAL 100
// Address LTC2631, LTC2309
#define AddrLTC2631 0x10
#define AddrLTC2309 0x08
// ADC limit calcul
#define limite 4000
//
uint16_t readPower(bool flag);
uint16_t readCurrent(int iPower, bool flag);
void PowerOut(int ValueOut, bool flag);
void WritePowerOled(int Power);
void WriteCurrentOled(char myPointer[]);
void WriteSerialOled();
void WriteErrorOled();
void InitOled();
void ClearOled();
void ResetOled();
void WriteOled(int Line, int Pos, String stText);
void WriteOledBig(int Line, int Pos, String stText);
void WriteOledFont(int Line, char myPointer[]);
uint16_t readI2C(byte address,byte x);
bool testI2C();
bool WriteOledEnd(String st1, String st2, String st3, String st4);
bool WriteOledFunc(String st1, String st2, String st3, String Function);
String ConvertFloatString(float fOut, byte valComa);
void functTest();
byte functCalibrate(bool flag);
void setCalibrate(byte Calibrate);
void WriteOledGain(String stG);
void WriteOledLed(String color);
void WriteOledReadI2C(String valLTC);
bool testCircuitI2C(byte address);
int testReadI2C(byte address,byte x,uint16_t Ucompare, uint8_t Udiff);
void writeDeb(String stDebug, boolean flag);
void writelnDeb(String stDebug, bool flag);
void setMillis(unsigned long new_millis);
