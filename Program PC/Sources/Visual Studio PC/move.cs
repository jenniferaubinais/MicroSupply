using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MicroSupply
{
    class move
    {
        public static void Pgm(String stSeconds, Queue<int> iQueue, Int16 lenQueue2, Int16 lenQueue4, bool flagDebug)
        {
            MyDLL.setWhite(0);
            MyDLL.cleanScreen();
            MyDLL.setAutoMaxMin(true, true);
            int ScaleValue = MyDLL.getScaleMove();
            WriteDebug.Pgm("MOVE = " + ScaleValue.ToString(), flagDebug);
            if (stSeconds == "4S")
            {
                for (int x = 0; x <= lenQueue4; x++)
                {
                    if (x % 4 == ScaleValue)
                    {
                        MyDLL.addData(iQueue.ElementAt(x));
                    }
                }
            }
            if (stSeconds == "2S")
            {
                for (int x = 0; x <= lenQueue2; x++)
                {
                    if (x % 2 == ScaleValue)
                    {
                        MyDLL.addData(iQueue.ElementAt(x));
                    }
                }
            }
        }
    }
}

