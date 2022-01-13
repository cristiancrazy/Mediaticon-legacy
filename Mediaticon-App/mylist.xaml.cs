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
    /// Interaction logic for mylist.xaml
    /// </summary>
    public partial class mylist : Window
    {
        public mylist()
        {
            InitializeComponent();
        }

        private void accountBord_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            accountCombo.IsDropDownOpen = !accountCombo.IsDropDownOpen;
        }
    }
}
