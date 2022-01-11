using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Mediaticon
{
    /// <summary>
    /// Interaction logic for login.xaml
    /// </summary>
    public partial class login : Window
    {
        public login()
        {
            InitializeComponent();
        }

        private void nameTxt_GotFocus(object sender, RoutedEventArgs e)
        {
            nameTxt.SelectAll();
        }

        private void nameTxt_IsMouseCaptureWithinChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            nameTxt.SelectAll();
        }
    }
}
