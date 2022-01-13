using HandlerMediaticon;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Mediaticon
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>

    


    public partial class MainWindow : Window
    {
        void openform<T>(bool close) where T : Window, new()
        {
            T a = new T();
            a.Show();
            if(close)
                this.Close();
        }

        public MainWindow()
        {
            InitializeComponent();

            List<Film> listafilm = new List<Film>();

            for(int i =0 ;i< 10; i++)
                listafilm.Add(new Film("gino " + i, "il film dell'anno", ""));

            listaLB.ItemsSource = listafilm;

            for (int i = 0; i < 30; i++)
                filterCBL.Items.Add(new CheckBox { Content=$"ciao{i}"});

            openform<login>(false);
            openform<details>(false);
            openform<mylist>(false);
        }

        private void searchTxt_IsMouseCaptureWithinChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            searchTxt.SelectAll();
        }

        private void accountBord_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            accountCombo.IsDropDownOpen = !accountCombo.IsDropDownOpen;
        }
    }
}
