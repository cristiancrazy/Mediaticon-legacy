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
using MediaticonDB;
using NewMessageBox;
using MediaticonWorker;

namespace Mediaticon
{
	/// <summary>
	/// 
	/// show loading progress gif
	/// create another task:
	///		load last 50 elements from db, obviously chosing appropriate table
	///		foreach file, download image picture from internet, and set it to Cover of film
	/// close the other task and set the listbox
	/// 
	/// Handle when user scroll down the listbox
	/// Handle when user click on tab menu
	/// Handle when user click on listboxItem
	/// Handle when user do a search
	/// Handle when user click on account button, or image
	/// </summary>



	public partial class MainWindow : Window
	{
		private static List<Film> basedList = new List<Film>();
		public MainWindow()
		{
			InitializeComponent();
			//loadElement();

			/*List<Film> listafilm = new List<Film>();

			for(int i =0 ;i< 10; i++)
				listafilm.Add(new Film("gino " + i, "il film dell'anno", ""));

			listaLB.ItemsSource = listafilm;

			for (int i = 0; i < 30; i++)
				filterCBL.Items.Add(new CheckBox { Content=$"ciao{i}"});
			*/
		}


		private void searchTxt_IsMouseCaptureWithinChanged(object sender, DependencyPropertyChangedEventArgs e)
		{
			searchTxt.SelectAll();
		}

		private void accountBord_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
		{
			accountCombo.IsDropDownOpen = !accountCombo.IsDropDownOpen;
		}

		
		//PRIMARY EVENT HANDLER METHODS
        private async void TabBtn_Click(object sender, RoutedEventArgs e)
        {
			string clicked = ((Button)sender).Name;
			EnviromentVar.Modality.CurrentModality = clicked switch
			{
				"filmBtn" => EnviromentVar.Modality.modType.Film,
				"serieBtn" => EnviromentVar.Modality.modType.Serie,
				"animeBtn" => EnviromentVar.Modality.modType.Anime,
				_ => EnviromentVar.Modality.modType.Film
			};
			DBHelper.GetFilms(true);
			loadElement();
        }

        private void listaLB_SelectionChanged(object sender, RoutedEventArgs e)
        {
            //open the details.xaml and pass the selected film
            openDetails(basedList[listaLB.SelectedIndex]);
        }

        private async void searchTxt_TextChanged(object sender, TextChangedEventArgs e)
        {
			//do the research
			object tokenLock = new object();
			//ResearchHelper.Search(); //i don't know if use a yield or do a while that get one element for time
        }

        private void filterCBL_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
			//do the research
        }

        private void accountCombo_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
			//when user click on accountCombo item
			switch(accountCombo.SelectedIndex)
            {
				case 0:
					//open my list
					Applicazione.openWindow<mylist, MainWindow>(Applicazione.CloserType.Close);
					break;
				case 1:
					//open website
					break;
				case 2:
					//exit from account
					Applicazione.openWindow<login, MainWindow>(Applicazione.CloserType.Close);
					break;
			}
		}

        private void HomeBtn_Click(object sender, RoutedEventArgs e)
        {

        }
    }

    public partial class MainWindow : Window
    {
		private void fillFilterCBL()
		{
			/*for (int i = 0; i < 30; i++)
				filterCBL.Items.Add(new CheckBox { Content = $"ciao{i}" });
			*/

		}

		private void loadElement()
		{
			//show the gif
			ShadowCircular load = new ShadowCircular();
			load.showLoading(ref loadingShadow);

			//wait the end of loading of 50 elements in DBHelper
			while (!DBHelper.Ready) ;

			try
			{
				//set the 50 elements on listBox
				basedList = DBHelper.loadedFilmList;
				listaLB.ItemsSource = basedList;
			}
			catch
			{
				NMSG.Show("Si è verificato un errore nel caricamento dei contenuti", NMSGtype.Ok);
				Applicazione.Close(1);
			}
			finally
			{
				//hide the gif
				load.hideLoading(ref loadingShadow);
			}
		}

		private class ShadowCircular
		{
			public void showLoading(ref Grid grid)
			{
				grid.Visibility = Visibility.Visible;
				grid.Opacity = 0;
				for (int i = 0; i < 10; i++)
				{
					grid.Opacity += 10;
					Task.Delay(50).Wait();
				}
			}

			public void hideLoading(ref Grid grid)
			{
				grid.Opacity = 100;
				for (int i = 0; i < 10; i++)
				{
					grid.Opacity -= 10;
					Task.Delay(50).Wait();
				}
				grid.Visibility = Visibility.Hidden;
			}
		}

		private void openDetails(Film toPass)
		{
			Applicazione.openWindow<details, MainWindow>(Applicazione.CloserType.Close, toPass);
		}
	}
}
