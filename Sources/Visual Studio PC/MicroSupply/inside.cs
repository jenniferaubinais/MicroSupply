using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MicroSupply
{
    class inside
    {
        public static int Pgm(String stSeconds, int longeur, Int16 lenQueue1, Int16 lenQueue2, Int16 lenQueue3, Int16 lenQueue4, bool flagDebug)
        {
            MyDLL.setWhite(0);
            MyDLL.cleanScreen();
            MyDLL.setAutoMaxMin(true,true);
            int ScaleValue = MyDLL.getScaleInside();
            WriteDebug.Pgm("INSIDE = " + ScaleValue.ToString(), flagDebug);
            int valDecale = 0;
            if (stSeconds == "1S")
            {
                valDecale = ScaleValue * 250;
                if ((lenQueue1 + valDecale) < longeur)
                {
                    switch (ScaleValue)
                    {
                        case 0:
                            MyDLL.setZero("0");
                            MyDLL.setValueX("0.25", "0.5", "0.75", "1s");
                            break;
                        case 1:
                            MyDLL.setZero("0.25");
                            MyDLL.setValueX("0.5", "0.75", "1", "1.25s");
                            break;
                        case 2:
                            MyDLL.setZero("0.5");
                            MyDLL.setValueX("0.75", "1", "1.25", "1.5s");
                            break;
                        case 3:
                            MyDLL.setZero("0.75");
                            MyDLL.setValueX("1", "1.25", "1.5", "1.75s");
                            break;
                        case 4:
                            MyDLL.setZero("1");
                            MyDLL.setValueX("1.25", "1.5", "1.75", "2s");
                            break;
                        case 5:
                            MyDLL.setZero("1.25");
                            MyDLL.setValueX("1.5", "1.75", "2", "2.25s");
                            break;
                        case 6:
                            MyDLL.setZero("1.5");
                            MyDLL.setValueX("1.75", "2", "2.25", "2.5s");
                            break;
                        case 7:
                            MyDLL.setZero("1.75");
                            MyDLL.setValueX("2", "2.25", "2.5", "2.75s");
                            break;
                        case 8:
                            MyDLL.setZero("2");
                            MyDLL.setValueX("2.25", "2.5", "2.75", "3s");
                            break;
                        case 9:
                            MyDLL.setZero("2.25");
                            MyDLL.setValueX("2.5", "2.75", "3", "3.25s");
                            break;
                        case 10:
                            MyDLL.setZero("2.5");
                            MyDLL.setValueX("2.75", "3", "3.25", "3.5s");
                            break;
                        case 11:
                            MyDLL.setZero("2.75");
                            MyDLL.setValueX("3", "3.25", "3.5", "3.75s");
                            break;
                        case 12:
                            MyDLL.setZero("3");
                            MyDLL.setValueX("3.25", "3.5", "3.75", "4s");
                            break;
                        default:
                            break;
                    }
                }
            }
            if (stSeconds == "2S")
            {
                valDecale = ScaleValue * 500;
                if ((lenQueue1 + valDecale) < longeur)
                {
                    switch (ScaleValue)
                    {
                        case 0:
                            MyDLL.setZero("0");
                            MyDLL.setValueX("0.5", "1", "1.5", "2s");
                            break;
                        case 1:
                            MyDLL.setZero("0.5");
                            MyDLL.setValueX("1", "1.5", "2", "2.5s");
                            break;
                        case 2:
                            MyDLL.setZero("1");
                            MyDLL.setValueX("1.5", "2", "2.5", "3s");
                            break;
                        case 3:
                            MyDLL.setZero("1.5");
                            MyDLL.setValueX("2", "2.5", "3", "3.5s");
                            break;
                        case 4:
                            MyDLL.setZero("2");
                            MyDLL.setValueX("2.5", "3", "3.5", "4s");
                            break;
                        default:
                            break;
                    }
                }
            }
            return valDecale;
        }
    }
}
