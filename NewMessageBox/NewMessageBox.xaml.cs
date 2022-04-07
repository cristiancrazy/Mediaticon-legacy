using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace NewMessageBox
{
    /// <summary>
    /// Interaction logic for NewMessageBox.xaml
    /// </summary>
    public enum NMSGtype
    {
        YesNo,
        Ok
    }

    public partial class NMSG : Window
    {
        private static bool YesNo = false;
        public NMSG()
        {
            InitializeComponent();
        }

        public static bool Show(string text, NMSGtype type)
        {
            NMSG a = new NMSG();
            a.WindowStartupLocation = WindowStartupLocation.CenterScreen;
            a.ShowDialog();
            ManualResetEvent waitHandle = new ManualResetEvent(false);

            if (type == NMSGtype.YesNo)
            {
                a.YesBTN.Visibility = Visibility.Visible;
                a.NoBTN.Visibility = Visibility.Visible;
                
                a.YesBTN.Click += (Object sender, RoutedEventArgs e) => waitHandle.Set();
                a.NoBTN.Click += (Object sender, RoutedEventArgs e) => waitHandle.Set();
            }
            else if(type == NMSGtype.Ok)
            {
                a.OkBTN.Visibility = Visibility.Visible;
                a.OkBTN.Click += (Object sender, RoutedEventArgs e) => waitHandle.Set();
            }
            waitHandle.WaitOne();
            return YesNo;
        }

        private void YesBTN_Click(object sender, RoutedEventArgs e)
        {
            YesNo = true;
        }

        private void NoBTN_Click(object sender, RoutedEventArgs e)
        {
            YesNo = false;
        }

        private void OkBTN_Click(object sender, RoutedEventArgs e)
        {
            YesNo = true;
        }
    }
}
