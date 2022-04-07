using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
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
    /// Interaction logic for details.xaml
    /// </summary>
    /// 

    public class ProgTV
    {
        public ProgTV(string icon, DateTime orainizio, DateTime orafine)
        {
            giorno = orainizio.ToString("d");
            oraInizio = orainizio.ToString("HH:mm");
            oraFine = orafine.ToString("HH:mm");
        }
        public string giorno { set; get; }
        public string oraInizio { set; get; }
        public string oraFine { set; get; }
    }

    public partial class details : Window
    {
        void setCover()
        {
            BitmapImage img = new BitmapImage();
            img.BeginInit();
            img.UriSource = new Uri(@"https://pad.mymovies.it/filmclub/2021/12/001/locandina.jpg");
            img.EndInit();
            coverImg.Source = img;
        }

        void setBackground()
        {
            BitmapImage img = new BitmapImage();
            img.BeginInit();
            img.UriSource = new Uri(@"https://pad.mymovies.it/filmclub/2021/12/001/covermd_home.jpg");
            img.EndInit();
            bkgImg.ImageSource = img;
        }

        void makeList()
        {
            List<ProgTV> tVs = new List<ProgTV>();
            for (int i = 0; i < 10; i++)
            {
                tVs.Add(new ProgTV("",
                  new DateTime(year: 1, month: 1, day: 1, hour: 12, minute: 23+i, second: 0),
                  new DateTime(year: 1, month: 1, day: 1, hour: 13, minute: 40+i, second: 0))
                  );
            }

            listaTV.ItemsSource = tVs;
        }

        public details()
        {
            InitializeComponent();
            setCover();
            setBackground();
            makeList();
        }

        private void mainWindow_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            
        }

        private void accountBord_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            accountCombo.IsDropDownOpen = !accountCombo.IsDropDownOpen;
        }

        private void comboClick(object sender, MouseButtonEventArgs e)
        {
            seeBtn.IsDropDownOpen = true;
        }
        
    }
}
