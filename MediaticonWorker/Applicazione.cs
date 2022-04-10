using System;
using System.Windows;
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

        public enum CloserType
        {
            Close,
            Hide,
            None
        }

        public static void openWindow<T, U>(CloserType type, params object[] parameters) where T : Window, new() where U : Window, new()
        {
            T a = (T)Activator.CreateInstance(typeof(T), parameters);
            U b = new U();
            a.Show();
            if (type == CloserType.Close)
            {
                b.Close();
            }
            else if (type == CloserType.Hide)
            {
                b.Hide();
            }

            GC.Collect();
        }
    }
}
