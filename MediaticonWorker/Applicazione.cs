using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonWorker
{
    public class Applicazione
    {
        public static void Close(int exitcode)
        {
            System.Windows.Application.Current.Shutdown(exitcode);
        }
    }
}
