using System;
using System.Collections.Generic;
using System.IO.Ports;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace MicroSupply
{
    class stop
    {
        public static void Pgm(SerialPort comPort, int longueur, Int16 lenQueue1, Int16 lenQueue2, Int16 lenQueue3, Int16 lenQueue4, bool flagDebug)
        {
            try
            {
                int totalBytes = comPort.BytesToRead;
                while (totalBytes > 0)
                {
                    WriteDebug.Pgm("Send data END (E)",flagDebug);
                    comPort.Write("E");
                    char[] buffer = new char[totalBytes];
                    comPort.Read(buffer, 0, totalBytes);
                    Thread.Sleep(100);
                    totalBytes = comPort.BytesToRead;
                    if (buffer[1] == 'j' && buffer[2] == 'o' && buffer[3] == '?')
                        totalBytes = 0;
                }
                totalBytes = comPort.BytesToRead;
                char[] buffer2 = new char[totalBytes];
                comPort.Read(buffer2, 0, totalBytes);
            }
            catch {}
            MyDLL.setVisible("START", true);
            MyDLL.setVisible("REFRESH", true);
            MyDLL.setVisible("STOP", false);
            MyDLL.setVisible("HELP", true);
            MyDLL.setAutoMaxMin(false,false);
            if (longueur > lenQueue4)
            {
                MyDLL.setVisible("1S", true);
                MyDLL.setVisible("2S", true);
                MyDLL.setVisible("4S", true);
            }
            else
            {
                if (longueur > lenQueue3)
                {
                    MyDLL.setVisible("1S", true);
                    MyDLL.setVisible("2S", true);
                }
                else
                {
                    if (longueur > lenQueue1)
                    {
                        MyDLL.setVisible("1S", true);
                    }
                }
            }

        }
    }
}
