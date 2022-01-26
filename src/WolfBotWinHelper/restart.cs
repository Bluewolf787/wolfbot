using System;
using System.Diagnostics;

namespace WolfBotWinHelper
{
    class Program
    {
        public static void Main(string[] args)
        {
            var jarFile = "wolfbot-2021.1.2-SNAPSHOT.jar";

            if (args.Length == 0)
            {
                Console.WriteLine("Restarting WolfBot on Windows platform.\nWARNING Don\'t run this application in normal console");
                Console.Read();
                Environment.Exit(0);
            }

            if (args[0].ToString() == "-restart")
            {
                try
                {
                    var arguments = String.Format(" -jar {0}", jarFile);

                    var process = new Process();
                    var processStartInfo = new ProcessStartInfo("java", arguments);

                    process.StartInfo = processStartInfo;
                    process.Start();
                }
                catch (Exception exception)
                {
                    Console.WriteLine(exception.Message);
                }
            }
        }
    }
}