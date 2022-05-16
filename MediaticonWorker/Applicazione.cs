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


        public static void openWindow<T>(CloserType type, Window toClose, params object[] parameters) where T : Window, new()
        {
            //T is window to open
            //this pass params to window to open
            if (type == CloserType.Close)
            {
                toClose.Close();
            }
            else if (type == CloserType.Hide)
            {
                toClose.Hide();
            }

            T a = (T)Activator.CreateInstance(typeof(T), parameters); //the window to open
            a.Show();
            GC.Collect();
        }

        /*public static void openWindow<T, U>(CloserType type, params object[] parameters) where T : Window, new() where U : Window, new()
        {
            //this pass params to window to open

            U b = new U();//the window to close
            if (type == CloserType.Close)
            {
                b.Close();
            }
            else if (type == CloserType.Hide)
            {
                b.Hide();
            }

            T a = (T)Activator.CreateInstance(typeof(T), parameters); //the window to open
            a.Show();
            GC.Collect();
        }*/

        /*public static void openWindow2<T, U>(CloserType type, params object[] parameters) where T : Window, new() where U : Window, new()
        {
            //this pass params to window to close
            U b = (U)Activator.CreateInstance(typeof(U), parameters);//the window to close
            if (type == CloserType.Close)
            {
                b.Close();
            }
            else if (type == CloserType.Hide)
            {
                b.Hide();
            }

            T a = new T(); //the window to open
            a.Show();
            GC.Collect();
        }*/
    }
}
