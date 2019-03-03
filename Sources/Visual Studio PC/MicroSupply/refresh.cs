using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO.Ports;

namespace MicroSupply
{
    class refresh
    {
        public static String[] Pgm(bool flagDebug)
        {
            MyDLL.addCOMPort("");
            String[] ports = SerialPort.GetPortNames();
            // Add port list
            if (ports.Count() != 0)
            {
                String ListPort = "";
                foreach (string port in ports)
                {
                    if (!ListPort.Contains(port))
                        ListPort = ListPort + port + ",";
                    WriteDebug.Equals("SERIALPORT = " + port, flagDebug);
                }
                if (ListPort != "")
                {
                    MyDLL.addCOMPort(ListPort);
                }
                else
                {
                    MyDLL.addCOMPort("");
                }
            }
            return ports;
        }
    }
}
