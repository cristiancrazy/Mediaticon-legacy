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
using MediaticonDB;
using MediaticonWorker;
using NewMessageBox;
using System.Windows.Shapes;
using System.Windows.Interop;

namespace Mediaticon
{
    /// <summary>
    /// Interaction logic for details.xaml
    /// </summary>
    /// 

    public partial class details : Window
    {
        static Film? opened = null;

        public details()
        {
            InitializeComponent();
        }

        public details(Film toOpen)
        {
            InitializeComponent();
            setFilm(toOpen);
            setGUI();
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

    public partial class details : Window
    {
        void setFilm(Film input)
        {
            try
            {
                opened = input;
            }
            catch
            {
                NMSG.Show("Impossibile caricare il contenuto", NMSGtype.Ok);
                Applicazione.openWindow<MainWindow, details>(Applicazione.CloserType.Close);
            }
        }

        void setGUI()
        {
            titleLbl.Content = opened.Title;
            descBox.Text = opened.Description;
            attoriBox.Text = "Attori: " + opened.Actors.ListToString();
            durataBox.Text = opened.Duration + " minuti";
            annoBox.Text = opened.Year.ToString();
            genereBox.Text = opened.Genres.ListToString();
            tipoBox.Text = EnviromentVar.Modality.CurrentModality.ToString();

            setCover(opened.Cover);
            setBackground(opened.BigImage);

            makeList();
        }

        void setCover(Bitmap bitmap)
        {
            try
            {
                var bitmapSource = Imaging.CreateBitmapSourceFromHBitmap(bitmap.GetHbitmap(),
                                                                               IntPtr.Zero,
                                                                               Int32Rect.Empty,
                                                                               BitmapSizeOptions.FromEmptyOptions()
                );
                bitmap.Dispose();
                coverImg.Source = new ImageBrush(bitmapSource).ImageSource;
            }
            catch
            {

                Connection.openImage(EnviromentVar.ImagesVar.defaultCoverPath, out bitmap);
                var bitmapSource = Imaging.CreateBitmapSourceFromHBitmap(bitmap.GetHbitmap(),
                                                                           IntPtr.Zero,
                                                                           Int32Rect.Empty,
                                                                           BitmapSizeOptions.FromEmptyOptions()
                );
                bitmap.Dispose();
                coverImg.Source = new ImageBrush(bitmapSource).ImageSource;
            }
        }

        void setBackground(string url)
        {
            Bitmap bitmap;
            try
            {
                Connection.DownloadImage(url, out bitmap);
                var bitmapSource = Imaging.CreateBitmapSourceFromHBitmap(bitmap.GetHbitmap(),
                                                                                IntPtr.Zero,
                                                                                Int32Rect.Empty,
                                                                                BitmapSizeOptions.FromEmptyOptions()
                );
                bitmap.Dispose();
                bkgImg.ImageSource = new ImageBrush(bitmapSource).ImageSource;
            }
            catch
            {
                return;
            }
        }


        void makeList()
        {
            //provide to get the guidatv
            List<Channel> progTV = new List<Channel>();

            try
            {
                listaTV.Items.Clear();
                    listaTV.ItemsSource = null;
            }
            catch
            {
                return;
            }

            try
            {
                if (GuidaTv.ReadAll(out progTV, opened.Title))
                {
                    foreach (var pr in progTV)
                    {
                        foreach (var re in pr.programmi)
                        {
                            listaTV.Items.Add(new ProgramTOShow(pr, re));
                        }
                    }
                }
                else
                {
                    return;
                }
            }
            catch
            {
                return;
            }
        }
    }
}
