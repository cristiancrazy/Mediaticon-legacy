using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;

namespace Mediaticon
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        public static void openWindow<T,U>(bool close) where T : Window, new() where U : Window, new()
        {
            T a = new T();
            U b = new U();
            a.Show();
            if (close)
                b.Close();

            GC.Collect();
        }
    }
}
