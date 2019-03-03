using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MicroSupply
{
    class save
    {
        public static void Pgm(Queue<int> iQueue)
        {
            String myDate = DateTime.Now.ToString("ddMMyyyy-HHmmss");
            String NameFile = "MicroSupply_" + myDate + ".csv";
            System.IO.StreamWriter file = new System.IO.StreamWriter(NameFile);
            foreach (int iQ in iQueue)
            {
                file.WriteLine(iQ.ToString());
            }
            file.Close();
            MyDLL.writeMessageYesNo("Save", "File " + NameFile + " created");
        }
    }
}
