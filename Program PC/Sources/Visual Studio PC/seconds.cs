using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MicroSupply
{
    class seconds
    {
        public static void Pgm(String stSeconds, Queue<int> iQueue, Int16 lenQueue1, Int16 lenQueue2, Int16 lenQueue3, Int16 lenQueue4)
        {
            switch (stSeconds)
            {
                case "1S":
                    MyDLL.setEnable("1S", false);
                    MyDLL.setEnable("2S", true);
                    MyDLL.setEnable("4S", true);
                    MyDLL.setVisible("SAVE", true);
                    MyDLL.setVisible("INSIDE", true);
                    MyDLL.setInside(12);
                    MyDLL.setVisible("MOVE", false);
                    MyDLL.setWhite(0);
                    MyDLL.cleanScreen();
                    MyDLL.setAutoMaxMin(true,true);
                    MyDLL.setZero("0");
                    MyDLL.setValueX("0.25", "0.5", "0.75", "1s");
                    for (int x = 0; x <= lenQueue1; x++)
                    {
                        MyDLL.addData(iQueue.ElementAt(x));
                    }
                    break;
                case "2S":
                    MyDLL.setEnable("1S", true);
                    MyDLL.setEnable("2S", false);
                    MyDLL.setEnable("4S", true);
                    MyDLL.setVisible("SAVE", true);
                    MyDLL.setVisible("INSIDE", true);
                    MyDLL.setVisible("MOVE", true);
                    MyDLL.setInside(4);
                    MyDLL.setMove(1);
                    MyDLL.setWhite(0);
                    MyDLL.cleanScreen();
                    MyDLL.setAutoMaxMin(true, true);
                    MyDLL.setZero("0");
                    MyDLL.setValueX("0.5", "1", "1.5", "2s");
                    for (int x = 0; x <= lenQueue2; x++)
                    {
                        if (x % 1 == 0)
                        {
                            MyDLL.addData(iQueue.ElementAt(x));
                        }
                    }
                    break;
                case "4S":
                    MyDLL.setEnable("1S", true);
                    MyDLL.setEnable("2S", true);
                    MyDLL.setEnable("4S", false);
                    MyDLL.setVisible("SAVE", true);
                    MyDLL.setVisible("INSIDE", false);
                    MyDLL.setVisible("MOVE", true);
                    MyDLL.setMove(3);
                    MyDLL.setWhite(0);
                    MyDLL.cleanScreen();
                    MyDLL.setAutoMaxMin(true, true);
                    MyDLL.setZero("0");
                    MyDLL.setValueX("1", "2", "3", "4s");
                    for (int x = 0; x <= lenQueue4; x++)
                    {
                        if (x % 4 == 0)
                        {
                            MyDLL.addData(iQueue.ElementAt(x));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
