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

    public class Film
    {
        public Film(string title, string desc)
        {
            Title = title;
            Description = desc;
        }
        public string Title {set; get;}
        public string Description { set; get;}
    }

    public partial class MainWindow : Window
    {
        
        public MainWindow()
        {
            InitializeComponent();

            List<Film> listafilm = new List<Film>();

            for(int i =0 ;i< 10; i++)
                listafilm.Add(new Film("gino " + i, "il film dell'anno"));

            listaLB.ItemsSource = listafilm;

            for (int i = 0; i < 30; i++)
                filterCBL.Items.Add(new CheckBox { Content=$"ciao{i}"});

            details details = new details();
            this.Close();
            details.Show();
        }
    }
}
