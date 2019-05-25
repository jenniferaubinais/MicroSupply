using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO.Ports;
using System.Threading;

namespace MicroSupply
{
    class readCOM
    {
        public static bool FlagWait = true;
        public static int Pgm(SerialPort comPort, bool flagWait,bool flagDebug)
        {
            byte[] buffer = new byte[10];
            int retValue = 65100;
            Thread tWait;
            try
            {
                if (!flagWait)
                {
                    while (comPort.BytesToRead < 4) { }
                }
                else
                {
                    tWait = new Thread(pgmWait);
                    tWait.Start();
                    while ((comPort.BytesToRead < 4) && FlagWait) { }
                    tWait.Abort();
                }
                int dimension = comPort.BytesToRead;
                buffer[0] = (byte)comPort.ReadChar();
                buffer[1] = (byte)comPort.ReadChar();
                buffer[2] = (byte)comPort.ReadChar();
                buffer[3] = (byte)comPort.ReadChar();
                if (buffer[3] != 10)
                {
                    if (buffer[0] == 10)
                    {
                        WriteDebug.Pgm("ERROR read 1 -char 10", flagDebug);
                        buffer[0] = buffer[1];
                        buffer[1] = buffer[2];
                        buffer[2] = buffer[3];
                        buffer[3] = (byte)comPort.ReadChar();
                    }
                    else
                    {
                        if (buffer[1] == 10)
                        {
                            WriteDebug.Pgm("ERROR read 2 -char 10", flagDebug);
                            buffer[0] = buffer[2];
                            buffer[1] = buffer[3];
                            buffer[2] = (byte)comPort.ReadChar();
                            buffer[3] = (byte)comPort.ReadChar();
                        }
                        else
                        {
                            if (buffer[2] == 10)
                            {
                                WriteDebug.Pgm("ERROR read 3 -char 10", flagDebug);
                                buffer[0] = buffer[3];
                                buffer[1] = (byte)comPort.ReadChar();
                                buffer[2] = (byte)comPort.ReadChar();
                                buffer[3] = (byte)comPort.ReadChar();
                            }
                            else
                            {
                                WriteDebug.Pgm("ERROR Other - char 10", flagDebug);
                            }
                        }
                    }
                }
                retValue = ((Convert.ToInt32(buffer[0]) - 0x30) & 0x0000003F) +
                    (((Convert.ToInt32(buffer[1]) - 0x30) << 6) & 0x00000FC0) +
                    (((Convert.ToInt32(buffer[2]) - 0x30) << 12) & 0x0000F000);
                //wDebug("=> " + retValue.ToString(),flagDebug);
            }
            catch (Exception)
            {}
            if (retValue == 65100)
            {
                doNothing();
            }
            return retValue;
        }
        //**********************************************************************************//
        // Wait 1s
        //**********************************************************************************//
        private static void pgmWait()
        {
            Thread.Sleep(1000);
            FlagWait = false;
        }
        //**********************************************************************************//
        // Do Nothing
        //**********************************************************************************//
        private static void doNothing()
        {
        }
    }
}
