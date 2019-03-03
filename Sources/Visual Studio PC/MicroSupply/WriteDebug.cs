using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MicroSupply
{
    class WriteDebug
    {
        //**********************************************************************************//
        // Write to Consol if DEBUG mode
        //**********************************************************************************//
        public static void Pgm(String stDebug, bool FlagDebug)
        {
            if (FlagDebug)
            {
                Console.WriteLine(stDebug);
            }
        }
        //**********************************************************************************//
        // Write a line if Debug
        //**********************************************************************************//
        public static void Log(bool FlagDebug)
        {
            if (FlagDebug)
            {
                Console.Write("=======================================");
                Console.Write("=======================================");
                Console.WriteLine("=======================================");
            }
        }
        //**********************************************************************************//
        // Do Nothing
        //**********************************************************************************//
        public static void Nothing()
        {
        }
    }
}
