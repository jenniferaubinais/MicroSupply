using System;
using System.Threading;
using System.Runtime.InteropServices;
using System.IO.Ports;
using System.IO;
using System.Collections.Generic;
using System.Reflection;
using System.Linq;
using System.Net;
using Microsoft.Win32;
using System.Management;

namespace MicroSupply
{
    class Program
    {
        // Use to hide the console program because the main program is the Osc_ACRD.dll
        [DllImport("kernel32.dll")]
        static extern IntPtr GetConsoleWindow();
        [DllImport("user32.dll")]
        static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
        [DllImport("User32.dll", CharSet = CharSet.Unicode)]
        public static extern int MessageBox(IntPtr h, string m, string c, int type);
        const int SW_HIDE = 0;
        const int SW_SHOW = 5;

        // Number of datas
        public const Int16 LenQueue = 4000;
        public const Int16 LenQueue1 = 999;
        public const Int16 LenQueue2 = 1999;
        public const Int16 LenQueue3 = 2999;
        public const Int16 LenQueue4 = 3999;
        // Flag to Debug mode, this is changed if the argument is DEBUG
        private static bool FlagDebug = false;
        public const bool FlagDataDebug = true;
        // The threads
        public static Thread t, tw;
        // Flag to thread receive datas or not
        private static bool FlagReceive = false;
        // Serial port
        private static SerialPort ComPort = new SerialPort();
        // Queue of datas used to 2 secondes or 4 secondes screens
        private static Queue<int> intQueue = new Queue<int>();
        //**********************************************************************************//
        // Main program (if arg is "DEBUG" than the Console mode not be hiden 
        // and debug informations will be print
        //**********************************************************************************//
        static void Main(string[] args)
        {
            // test arg = DEBUG
            if (args.Length > 0)
            {
                if (args[0].ToUpper() == "DEBUG")
                {
                    FlagDebug = true;
                }
            }
            // test Version Windows 10
            WriteDebug.Pgm("<< Test Version Windows 10 >>", FlagDebug);
            String Version = GetOsVer().ToString().Replace(".", "");
            Int64 xVersion = 0;
            if (Int64.TryParse(Version, out xVersion))
            {
                WriteDebug.Pgm("Version Windows : " + xVersion.ToString(), FlagDebug);
                if (xVersion < 10014393)
                {
                    String errorVer = "Version Windows 10 is not updated : " + Version;
                    MessageBox((IntPtr)0, errorVer, "Error Windows 10", 0);
                    Environment.Exit(0);
                }
            }
            else
            {
                if (Version != "")
                {
                    MessageBox((IntPtr)0, "Version Windows 10 not found", "Error Windows 10", 0);
                    Environment.Exit(0);
                }
            }
            // test JAVA files
            WriteDebug.Pgm("<< Test folder Java >>", FlagDebug);
            try
            {
                bool bJava = Directory.Exists("C:\\Program files\\Java");
                if (!bJava)
                {
                    bJava = Directory.Exists("C:\\Program Files (x86)\\Java");
                    if (!bJava)
                    {
                        MessageBox((IntPtr)0, "Folder Java doesn't found !", "Error Java", 0);
                        Environment.Exit(0);
                    }
                }
            }
            catch
            {
                MessageBox((IntPtr)0, "Folder Java doesn't found !", "Error Java", 0);
                Environment.Exit(0);
            }
            // test version Java
            WriteDebug.Pgm("<< Test Version Java >>", FlagDebug);
            int verJava = 0;
            try
            {
                String registryKey = @"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall";
                using (RegistryKey key = Registry.LocalMachine.OpenSubKey(registryKey))
                {
                    foreach (RegistryKey subkey in key.GetSubKeyNames().Select(keyName => key.OpenSubKey(keyName)))
                    {
                        string oneKey = subkey.GetValue("DisplayName") as string;

                        if (oneKey != null && oneKey.Contains("Java") && !oneKey.Contains("Updater"))
                        {
                            string versionMEM = subkey.GetValue("DisplayVersion") as String;
                            WriteDebug.Pgm("<< Version Java (1) >> " + versionMEM, FlagDebug);
                            string[] tempo = versionMEM.Split('.');
                            verJava = int.Parse(tempo[0]);
                        }
                    }
                }
                if (verJava == 0)
                {
                    registryKey = @"SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall";
                    using (RegistryKey key = Registry.LocalMachine.OpenSubKey(registryKey))
                    {
                        foreach (RegistryKey subkey in key.GetSubKeyNames().Select(keyName => key.OpenSubKey(keyName)))
                        {
                            string oneKey = subkey.GetValue("DisplayName") as string;

                            if (oneKey != null && oneKey.Contains("Java") && !oneKey.Contains("Updater"))
                            {
                                string versionMEM = subkey.GetValue("DisplayVersion") as String;
                                WriteDebug.Pgm("<< Version Java (2) >> " + versionMEM, FlagDebug);
                                string[] tempo = versionMEM.Split('.');
                                verJava = int.Parse(tempo[0]);
                            }
                        }
                    }
                }
                if (verJava == 0)
                {
                    registryKey = @"SOFTWARE\JavaSoft\Java Runtime Environment";
                    using (RegistryKey key = Registry.LocalMachine.OpenSubKey(registryKey))
                    {
                        string versionMEM = key.GetValue("CurrentVersion") as string;
                        WriteDebug.Pgm("<< Version Java (3) >> " + versionMEM, FlagDebug);
                        string[] tempo = versionMEM.Split('.');
                        verJava = int.Parse(tempo[1]);
                    }
                }
                if (verJava == 0)
                {
                    registryKey = @"SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment";
                    using (RegistryKey key = Registry.LocalMachine.OpenSubKey(registryKey))
                    {
                        string versionMEM = key.GetValue("CurrentVersion") as string;
                        WriteDebug.Pgm("<< Version Java (4) >> " + versionMEM, FlagDebug);
                        string[] tempo = versionMEM.Split('.');
                        verJava = int.Parse(tempo[1]);
                    }
                }

                if (verJava < 8)
                {
                    MessageBox((IntPtr)0, "Java must have version 1.8 or over !", "Error version Java", 0);
                    Environment.Exit(0);
                }
            }
            catch (Exception ex)
            {
                MessageBox((IntPtr)0, "ERROR : " + ex.Message.ToString(), "Error version Java", 0);
                Environment.Exit(0);
            }
            // test files IKVM
            WriteDebug.Pgm("<< Test IKVM DLL files >>", FlagDebug);
            if (!File.Exists("ikvm-native-win32-x64.dll"))
            {
                MessageBox((IntPtr)0, "file ikvm-native-win32-x64.dll is missing", "File missing", 0);
                Environment.Exit(0);
            }
            if (!File.Exists("IKVM.OpenJDK.Core.dll"))
            {
                MessageBox((IntPtr)0, "file IKVM.OpenJDK.Core.dll is missing", "File missing", 0);
                Environment.Exit(0);
            }
            // test files SWT
            WriteDebug.Pgm("<< Test files SWT >>", FlagDebug);
            String pathSWT = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
            pathSWT += "\\.swt\\lib\\win32\\x86_64";
            bool[] bools = new bool[6];
            bools[0] = File.Exists(pathSWT + "\\swt-gdip-win32-4233.dll");
            bools[1] = File.Exists(pathSWT + "\\swt-gdip-win32-4763.dll");
            bools[2] = File.Exists(pathSWT + "\\swt-gdip-win32-4919.dll");
            bools[3] = File.Exists(pathSWT + "\\swt-win32-4233.dll");
            bools[4] = File.Exists(pathSWT + "\\swt-win32-4763.dll");
            bools[5] = File.Exists(pathSWT + "\\swt-win32-4919.dll");
            int[] arrs = Array.ConvertAll(bools, b => b ? 0 : 1);
            int arrCount = arrs[0] + arrs[1] + arrs[2] + arrs[3] + arrs[4] + arrs[5];
            switch (arrCount)
            {
                case 0:
                    break;
                case 1:
                    String stFile = "";
                    if (bools[0]) stFile = "swt-gdip-win32-4233.dll";
                    if (bools[1]) stFile = "swt-gdip-win32-4763.dll";
                    if (bools[2]) stFile = "swt-gdip-win32-4919.dll";
                    if (bools[3]) stFile = "swt-win32-4233.dll";
                    if (bools[4]) stFile = "swt-win32-4763.dll";
                    if (bools[5]) stFile = "swt-win32-4919.dll";
                    MessageBox((IntPtr)0, "file " + stFile + " is missing in " + pathSWT, "File missing", 0);
                    Environment.Exit(0);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    MessageBox((IntPtr)0, arrCount + " files missing in " + pathSWT, "Files missing", 0);
                    Environment.Exit(0);
                    break;
                case 6:
                    MessageBox((IntPtr)0, "All files missing in " + pathSWT, "Files missing", 0);
                    Environment.Exit(0);
                    break;
                default:
                    break;
            }
            // test if no COM port
            WriteDebug.Pgm("<< Test if COM port found >>", FlagDebug);
            string[] testPorts = SerialPort.GetPortNames();
            if (testPorts.Count() == 0)
            {
                MessageBox((IntPtr)0, "No COM port found !", "Error port COM", 0);
                Environment.Exit(0);
            }
            // Application standard
            //Application.EnableVisualStyles();
            //Application.SetCompatibleTextRenderingDefault(false);
            // Hide Console if not debug mode
            if (!FlagDebug)
            {
                var handle = GetConsoleWindow();
                ShowWindow(handle, SW_HIDE);
            }
            else
            {
                WriteDebug.Pgm("<< BEGIN >>", true);
            }
            // test to acces to the MicroSupply_JA.dll
            WriteDebug.Pgm("<< Test DLL MicroSupply_JA >>", FlagDebug);
            try
            {
                //String dllVersion = MyDLL.getVersion();
                //var thisApp = Assembly.GetExecutingAssembly();
                //AssemblyName name = new AssemblyName(thisApp.FullName);
                //String pgmVersion = name.Version.ToString().Substring(0, name.Version.ToString().Length - 2);
                //if (String.Compare(dllVersion, pgmVersion) == 0)
                //{
                //    WriteDebug.Pgm("MicroSupply_JA.dll Version : " + dllVersion, FlagDebug);
                //    WriteDebug.Pgm("MicroSupply.exe Version : " + pgmVersion, FlagDebug);
                //}
                //else
                //{
                //    WriteDebug.Pgm("Versions ERROR", true);
                //    WriteDebug.Pgm("MicroSupply_JA.dll Version : " + dllVersion, true);
                //    WriteDebug.Pgm("MicroSupply Version : " + pgmVersion, true);
                //    Thread.Sleep(5000);
                //    Environment.Exit(0);
                //}
            }
            catch (Exception e)
            {
                WriteDebug.Pgm(e.Message.ToString(), FlagDebug);
                Thread.Sleep(5000);
                Environment.Exit(0);
            }
            // INIT screen
            WriteDebug.Pgm("<< Launch the screen >>", FlagDebug);
            try
            {
                MyDLL.initScreenJA();
            }
            catch (Exception e)
            {
                WriteDebug.Pgm(e.Message.ToString(), FlagDebug);
                Thread.Sleep(5000);
                Environment.Exit(0);
            }

            try
            {
                while (!MyDLL.testScreenReady()) { }
                MyDLL.setValueX("0.25", "0.5", "0.75", "1s");
                MyDLL.setValueY("10", "20", "30", "40", "µA");
                MyDLL.setVisible("COM", false);
                MyDLL.setVisible("REFRESH", false);
                MyDLL.setVisible("HELP", true);
                MyDLL.setVisible("EXIT", true);
                MyDLL.setVisible("START", false);
                // test arg = TEST
                if (args.Length > 0)
                {
                    if (args[0].ToUpper() == "TEST")
                    {
                        MyDLL.setVisible("COM", true);
                        MyDLL.setVisible("REFRESH", true);
                        MyDLL.setVisible("START", true);
                        MyDLL.setVisible("STOP", true);
                        MyDLL.setVisible("1S", true);
                        MyDLL.setVisible("2S", true);
                        MyDLL.setVisible("4S", true);
                        MyDLL.setVisible("SAVE", true);
                        MyDLL.setVisible("INSIDE", true);
                        MyDLL.setVisible("MOVE", true);
                        MyDLL.setVisible("MAX", true);
                        MyDLL.setVisible("MIN", true);
                        while (true)
                        {
                            while (!MyDLL.getStatusKey()) { }
                            String stKey = MyDLL.getKeyValue();
                            WriteDebug.Pgm("KEY : " + stKey, FlagDebug);
                            if (stKey.Contains("EXIT"))
                            {
                                Thread.Sleep(500);
                                Environment.Exit(0);
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                WriteDebug.Pgm(e.Message.ToString(), FlagDebug);
                Thread.Sleep(5000);
                Environment.Exit(0);
            }
            // Launch Thread if the screen will be closed
            t = new Thread(ENDApplication);
            t.Start();
            // Identify Serial port list
            string[] ports = null;
            String stPort = "";
            ports = SerialPort.GetPortNames();
            // Add port list
            MyDLL.setVisible("START", false);
            if (ports.Count() != 0)
            {
                String ListPort = "";
                bool flagPort = true;
                foreach (string port in ports)
                {
                    if (ListPort.Contains(port))
                    {
                        flagPort = false;
                    }
                    else
                    {
                        ListPort = ListPort + port + ",";
                    }
                    WriteDebug.Pgm("COM PORT : " + port, FlagDebug);
                }
                if (!flagPort)
                {
                    string[] lports = ListPort.Split(',');
                    foreach (string port in lports)
                    {
                        try
                        {
                            if (port != "")
                            {
                                ComPort.PortName = port;
                                try
                                {
                                    ComPort.Open();
                                }
                                finally
                                {
                                    if (ComPort.IsOpen)
                                        ComPort.Close();//<---this is the important bit!!!
                                }
                                ComPort.Close();
                            }
                        }
                        catch (UnauthorizedAccessException ex)
                        {
                            //Process[] processlist = Process.GetProcesses();
                            //foreach (Process theprocess in processlist)
                            //{
                            //    Console.WriteLine("Process: {0} ID: {1}", theprocess.ProcessName, theprocess.Id);
                            //}
                            try
                            {
                                ComPort.Close();
                            }
                            catch (UnauthorizedAccessException ex2)
                            {
                                WriteDebug.Log(FlagDebug);
                                WriteDebug.Pgm(ex2.Message.ToString(), FlagDebug);
                                WriteDebug.Log(FlagDebug);
                            }
                            WriteDebug.Log(FlagDebug);
                            WriteDebug.Pgm("ERROR : " + port + " port doesn't work", FlagDebug);
                            WriteDebug.Pgm(ex.Message.ToString(), FlagDebug);
                            WriteDebug.Log(FlagDebug);
                            MyDLL.writeMessage("ERROR", "MicroSupply ERROR", port + " port doesn't work !");
                            ListPort = ListPort.Replace(port + ",", "");
                        }
                        catch (IOException ex)
                        {
                            WriteDebug.Log(FlagDebug);
                            WriteDebug.Pgm("WARNING : " + port + " port can't be open", FlagDebug);
                            WriteDebug.Pgm(ex.Message.ToString(), FlagDebug);
                            WriteDebug.Log(FlagDebug);
                            MyDLL.writeMessage("WARNING", "MicroSupply WARNING", port + " port can't be open !");
                        }
                    }
                }
                //if (!flagPort)
                //{
                // error port COM
                //    wDebug("ERROR : A COM port doesn't work");
                //    MyDLL.writeMessage("ERROR", "MicroSupply ERROR", "A COM port doesn't work !");
                //    Thread.Sleep(500);
                //    t.Abort();
                //    Environment.Exit(0);
                //}
                MyDLL.setVisible("COM", true);
                MyDLL.setVisible("REFRESH", true);
                MyDLL.addCOMPort(ListPort);
                if (ports.Length == 1)
                {
                    initPORTCOM(ports[0]);
                    stPort = ports[0];
                    // Launch Thread to receive datas but not now (FlagReceive is false)
                    tw = new Thread(launchRECEIVE);
                    FlagReceive = false;
                    tw.Start();
                }
            }

            // Wait a key to begin the program
            WriteDebug.Pgm("Wait Key Start", FlagDebug);
            String stTouchSeconds = "";
            while (true)
            {
                while (!MyDLL.getStatusKey()) { }
                String stKey = MyDLL.getKeyValue();
                WriteDebug.Pgm("KEY : " + stKey, FlagDebug);
                // Refresh the port COM
                if (stKey.Contains("REFRESH"))
                {
                    ports = refresh.Pgm(FlagDebug);
                    if (ports.Length == 1)
                    {
                        bool bRet = initPORTCOM(ports[0]);
                        // Launch Thread to receive datas but not now
                        if (bRet)
                        {
                            tw = new Thread(launchRECEIVE);
                            FlagReceive = false;
                            tw.Start();
                        }
                        else
                        {
                            MyDLL.addCOMPort("");
                        }
                    }
                }
                // COM port
                if (stKey.Contains("COM"))
                {
                    stPort = stKey.Replace(",", "");
                    initPORTCOM(stPort);
                    tw = new Thread(launchRECEIVE);
                    FlagReceive = false;
                    tw.Start();
                    MyDLL.setVisible("START", true);
                }
                // START button
                if (stKey.Contains("START"))
                {
                    initPORTCOM(stPort);
                    bool codeReturn = start.Pgm(ComPort, FlagDebug);
                    FlagReceive = true;
                    MyDLL.setEnable("COM", false);
                }
                // STOP button
                if (stKey.Contains("STOP"))
                {
                    FlagReceive = false;
                    int retValue = 0;
                    while (retValue < 50000)
                    {
                        WriteDebug.Pgm("Send data END (E)", FlagDataDebug);
                        try
                        {
                            ComPort.Write("E");
                        }
                        catch { }
                        retValue = readCOM.Pgm(ComPort, true, FlagDebug);
                    }
                    stop.Pgm(ComPort, intQueue.Count, LenQueue1, LenQueue2, LenQueue3, LenQueue4, FlagDebug);
                    MyDLL.setEnable("COM", true);
                }
                // 1 secondes button
                if (stKey.Contains("1S"))
                {
                    stTouchSeconds = "1S";
                    seconds.Pgm("1S", intQueue, LenQueue1, LenQueue2, LenQueue3, LenQueue4);
                }
                // 2 secondes button
                if (stKey.Contains("2S"))
                {
                    stTouchSeconds = "2S";
                    seconds.Pgm("2S", intQueue, LenQueue1, LenQueue2, LenQueue3, LenQueue4);
                }
                // 4 secondes button
                if (stKey.Contains("4S"))
                {
                    stTouchSeconds = "4S";
                    seconds.Pgm("4S", intQueue, LenQueue1, LenQueue2, LenQueue3, LenQueue4);
                }
                // INSIDE button
                if (stKey.Contains("INSIDE"))
                {
                    int valDecale = inside.Pgm(stTouchSeconds, intQueue.Count, LenQueue1, LenQueue2, LenQueue3, LenQueue4, FlagDebug);
                    for (int x = valDecale; x <= (LenQueue1 + valDecale); x++)
                    {
                        MyDLL.addData(intQueue.ElementAt(x));
                    }
                }
                // MOVE button
                if (stKey.Contains("MOVE"))
                {
                    move.Pgm(stTouchSeconds, intQueue, LenQueue2, LenQueue4, FlagDebug);
                }
                // the SAVE button is choose
                if (stKey.Contains("SAVE"))
                {
                    save.Pgm(intQueue);
                }
                // the EXIT button is choose
                if (stKey.Contains("EXIT"))
                {
                    FlagReceive = false;
                    int retValue = 0;
                    while (retValue < 50000)
                    {
                        WriteDebug.Pgm("Send data END (E)", FlagDataDebug);
                        try
                        {
                            ComPort.Write("E");
                        }
                        catch { }
                        retValue = readCOM.Pgm(ComPort, true, FlagDebug);
                    }
                    try
                    {
                        if (ComPort.IsOpen)
                        {
                            Thread.Sleep(500);
                            ComPort.Close();
                        }
                    }
                    catch (Exception) { }
                    Thread.Sleep(500);
                    t.Abort();
                    Environment.Exit(0);
                }
                if (stKey.Contains("HELP"))
                {
                    WebRequest request = WebRequest.Create("http://www.acrd.fr");
                    try
                    {
                        request.GetResponse();
                    }
                    catch //If exception thrown then couldn't get response from address
                    {
                        WriteDebug.Pgm("The URL is incorrect", FlagDebug);
                    }
                }
            }
        }
        //**********************************************************************************//
        //
        //**********************************************************************************//
        private static bool initPORTCOM(String PortName)
        {
            if (PortName == "")
            {
                WriteDebug.Pgm("NO COM port", FlagDebug);
                MyDLL.setVisible("START", false);
                MyDLL.setVisible("STOP", false);
                MyDLL.setVisible("1S1", false);
                MyDLL.setVisible("1S2", false);
                MyDLL.setVisible("1S3", false);
                MyDLL.setVisible("1S4", false);
                MyDLL.setVisible("2S1", false);
                MyDLL.setVisible("2S2", false);
                MyDLL.setVisible("4S", false);
            }
            else
            {
                if (!ComPort.IsOpen)
                {
                    try
                    {
                        WriteDebug.Pgm("Init COM port", FlagDebug);
                        ComPort.PortName = PortName;
                        ComPort.BaudRate = 115200;
                        ComPort.DataBits = 8;
                        ComPort.StopBits = StopBits.One;
                        ComPort.RtsEnable = false;
                        ComPort.Handshake = Handshake.None;
                        ComPort.Parity = Parity.None;
                        //ComPort.DataReceived += new SerialDataReceivedEventHandler(port_DataReceived);
                        ComPort.Open();
                    }
                    catch (Exception ex)
                    {
                        WriteDebug.Pgm("ERROR : init COM port - " + ex.Message.ToString(), FlagDebug);
                        return false;
                    }
                }
                else
                {
                    WriteDebug.Pgm("COM port is still opened", FlagDebug);
                }
                int totalBytes = ComPort.BytesToRead;
                while (totalBytes > 0)
                {
                    char[] buffer = new char[totalBytes];
                    ComPort.Read(buffer, 0, totalBytes);
                    Thread.Sleep(100);
                    totalBytes = ComPort.BytesToRead;
                }
            }
            return true;
        }
        //**********************************************************************************//
        //
        //**********************************************************************************//
        private static void launchRECEIVE()
        {
            try
            {
                int ComptNoReceive = 0;
                while (true)
                {
                    if (FlagReceive)
                    {
                        int iRead = 0;
                        while ((byte)ComPort.ReadChar() != '\n')
                        {
                            ComptNoReceive++;
                            iRead++;
                            if (ComptNoReceive > 2000)
                            {
                                WriteDebug.Pgm("Compteur No Receive : " + ComptNoReceive.ToString(), FlagDebug);
                                ComptNoReceive = 0;
                            }
                        }
                        ComptNoReceive = 0;
                        if (FlagDebug)
                        {
                            if (iRead > 0)
                            {
                                WriteDebug.Pgm("Error decalage : " + iRead, FlagDebug);
                            }
                        }
                        while (FlagReceive)
                        {
                            int Value = readCOM.Pgm(ComPort, false, FlagDebug);
                            //wDebug("=> " + Value.ToString());
                            if (Value > 40000)
                            {
                                WriteDebug.Pgm("=> " + Value.ToString(), FlagDebug);
                                // 40184
                                WriteDebug.Nothing();
                                switch (Value)
                                {
                                    case 65530:
                                        MyDLL.writeMessage("WARNING", "MicroSupply disconneted !", "Warning datas received !");
                                        FlagReceive = false;
                                        stop.Pgm(ComPort, intQueue.Count, LenQueue1, LenQueue2, LenQueue3, LenQueue4, FlagDebug);
                                        break;
                                    // OK launch datas serial
                                    case 65520:
                                        MyDLL.writeMessage("ERROR", "MicroSupply disconneted !", "Low Power Output !");
                                        FlagReceive = false;
                                        stop.Pgm(ComPort, intQueue.Count, LenQueue1, LenQueue2, LenQueue3, LenQueue4, FlagDebug);
                                        break;
                                    case 65100:
                                        FlagReceive = false;
                                        try
                                        {
                                            if (ComPort.IsOpen)
                                            {
                                                WriteDebug.Pgm("Send data : E", FlagDebug);
                                                ComPort.Write("E");
                                            }
                                        }
                                        catch (Exception) { }
                                        try
                                        {
                                            if (ComPort.IsOpen)
                                            {
                                                Thread.Sleep(500);
                                                ComPort.Close();
                                            }
                                        }
                                        catch (Exception) { }
                                        Thread.Sleep(500);
                                        MyDLL.writeMessage("ERROR", "MicroSupply Error Serial Port", "ERROR Serial Port ! \n\n EXIT");
                                        t.Abort();
                                        Environment.Exit(0);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            else
                            {
                                intQueue.Enqueue(Value);
                                if (intQueue.Count > LenQueue)
                                {
                                    int firstToLeave = intQueue.Dequeue();
                                }
                                MyDLL.addData(Value);
                                if (MyDLL.getStatusScale())
                                {
                                    int ValScale = MyDLL.getScale();
                                    if (ValScale == 0)
                                    {
                                        MyDLL.setValueY("10", "20", "30", "40", "µA");
                                    }
                                    else
                                    {
                                        if (ValScale == 1)
                                        {
                                            MyDLL.setValueY("100", "200", "300", "400", "µA");
                                        }
                                        else
                                        {
                                            if (ValScale == 10)
                                            {
                                                MyDLL.setValueY("1", "2", "3", "4", "mA");
                                            }
                                            else
                                            {
                                                if (ValScale == 100)
                                                {
                                                    MyDLL.setValueY("10", "20", "30", "40", "mA");
                                                }
                                                else
                                                {
                                                    MyDLL.setValueY("-", "-", "-", "-", "?");
                                                }
                                            }
                                        }
                                        WriteDebug.Nothing();
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        Thread.Sleep(100);
                    }
                }
            }
            catch (Exception)
            {
                FlagReceive = false;
                MyDLL.setVisible("START", false);
                MyDLL.setVisible("STOP", false);
            }
        }
        //**********************************************************************************//
        //
        //**********************************************************************************//
        private static void ENDApplication()
        {
            while (!MyDLL.getStatusClose())
            {
                Thread.Sleep(500);
            }
            try
            {
                WriteDebug.Pgm("Send data : E", FlagDebug);
                ComPort.Write("E");
            }
            catch (Exception) { }
            try
            {
                if (ComPort.IsOpen)
                {
                    Thread.Sleep(500);
                    ComPort.Close();
                }
            }
            catch (Exception) { }
            Thread.Sleep(500);
            Environment.Exit(0);
        }

        private static ManagementObject GetMngObj(string className)
        {
            var wmi = new ManagementClass(className);

            foreach (var o in wmi.GetInstances())
            {
                var mo = (ManagementObject)o;
                if (mo != null) return mo;
            }

            return null;
        }

        public static string GetOsVer()
        {
            try
            {
                ManagementObject mo = GetMngObj("Win32_OperatingSystem");

                if (null == mo)
                    return string.Empty;

                return mo["Version"] as string;
            }
            catch (Exception)
            {
                return string.Empty;
            }
        }
    }
}
