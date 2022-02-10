using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB
{
    internal class ScraperHandler
    {
        private string path;
        private Process proc;

        public ScraperHandler(string path)
        {
            this.path = path;
        }

        public void RunScraper(params string[] parameters)
        {
            ProcessStartInfo StartInfo = Infoproc(parameters);            

            proc = Process.Start(StartInfo);                                  
        }

        public bool GetReturn(out string values)
        {
            values = null;

            if(proc == null)
                return false;

            proc.WaitForExit();
            if (proc.ExitCode != 0)
            {
                values = proc.StandardError.ReadToEnd();
                return false;
            }
            else
            {
                values = proc.StandardOutput.ReadToEnd();
                return true;
            }            
        }

        private ProcessStartInfo Infoproc (string[] args)
        {
            ProcessStartInfo StartInfo = new ProcessStartInfo(path, args.ToString());
            StartInfo.RedirectStandardOutput = true;
            StartInfo.RedirectStandardError = true;
            StartInfo.UseShellExecute = false;
            StartInfo.CreateNoWindow = true;

            return StartInfo;
        }

    }
}
