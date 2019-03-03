using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.IO.Ports;

namespace MicroSupply
{
    class start
    {
        public static bool Pgm(SerialPort comPort, bool flagDebug)
        {
            try
            {
                int totalBytes = comPort.BytesToRead;
                while (totalBytes > 0)
                {
                    char[] buffer = new char[totalBytes];
                    comPort.Read(buffer, 0, totalBytes);
                    Thread.Sleep(100);
                    totalBytes = comPort.BytesToRead;
                }
                int retValue = 0;
                while (retValue != 65534)
                {
                    WriteDebug.Pgm("Send data BEGIN (B)",flagDebug);
                    comPort.Write("B");
                    retValue = readCOM.Pgm(comPort, true, flagDebug);
                }
            }
            catch (Exception ex)
            {
                WriteDebug.Pgm("ERROR : COM port - " + ex.Message.ToString(),flagDebug);
                MyDLL.writeMessage("ERROR", "START button", "Error Com port !");
                return false;
            }
            MyDLL.setVisible("START", false);
            MyDLL.setVisible("REFRESH", false);
            MyDLL.setVisible("SAVE", false);
            MyDLL.setVisible("INSIDE", false);
            MyDLL.setVisible("MOVE", false);
            MyDLL.setVisible("1S", false);
            MyDLL.setVisible("2S", false);
            MyDLL.setVisible("4S", false);
            MyDLL.setVisible("STOP", true);
            MyDLL.setVisible("HELP", false);
            MyDLL.setAutoMaxMin(true, true);
            MyDLL.cleanScreen();
            MyDLL.setWhite(50);
            MyDLL.setVisible("MAX", true);
            return true;
        }
    }
}
