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
using System.Threading;

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
			loadElement();

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
			DBHelper.GetFilms(true); //this will run async, not wait
			loadElement();
		}

		private void listaLB_SelectionChanged(object sender, RoutedEventArgs e)
		{
			//open the details.xaml and pass the selected film
			openDetails(basedList[listaLB.SelectedIndex]);
		}

		private void searchEvent(object sender, TextChangedEventArgs e)
		{
			//do the research
			Search();
			//while this return a yield get add element to baselist, load it to screen 
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
					System.Diagnostics.Process.Start(@"https://github.com/VisualLaser10New");
					break;
				case 2:
					//exit from account
					Applicazione.openWindow<login, MainWindow>(Applicazione.CloserType.Close);
					break;
			}
		}
	}

	public partial class MainWindow : Window
	{
		private void fillFilterCBL()
		{
			//fill the filter of 
			var items = new ObservableCollection<FilterItem>();

			for (int i = 0; i < 30; i++) //TODO: populate list
				items.Add(new FilterItem { Content = $"ciao{i}" });

			filterCBL.Items.Clear();
			filterCBL.ItemsSource = null;
			filterCBL.ItemsSource = items;
		}

		static CancellationTokenSource  cancelTask = new CancellationTokenSource();
		static CancellationToken token = cancelTask.Token;
		private async void Search()
        {
			//cancel the previous search
			cancelTask.Cancel();
			//wait a second
			Thread.Sleep(1000);

			//do the new search
			basedList.Clear();
			Task searchTask = new Task(doSearch, token);

			ResearchHelper.setSearchParams(searchTxt.Text, filterCBL.Items);
			searchTask.Start();
			showElement(); //in case there is no result -> show empty list, and messagebox will showed by research helper
		}

		private async void doSearch()
        {
			//this function run in another task
			await Dispatcher.BeginInvoke(new Action(() => { //i'm not sure that this await is correct
				foreach (var film in ResearchHelper.Search())
				{
					basedList.Add(film.Result);
					showElement();
				}
			}));
		}


		private void loadElement()
		{
			//show the gif
			//ShadowCircular load = new ShadowCircular();
			ShadowCircular.showLoading(ref loadingShadow);

			//wait the end of loading of 50 elements in DBHelper
			while (!DBHelper.Ready);

			try
			{
				//set the 50 elements on listBox
				basedList = DBHelper.loadedFilmList;
				showElement();
			}
			catch
			{
				NMSG.Show("Si è verificato un errore nel caricamento dei contenuti", NMSGtype.Ok);
				Applicazione.Close(1);
			}
			finally
			{
				//hide the gif
				ShadowCircular.hideLoading(ref loadingShadow);
			}
		}

		private void loadNewElements()
        {
			ShadowCircular.showLoading(ref loadingShadow);
			DBHelper.GetFilms(false);

			while (!DBHelper.Ready) ;

            try
            {
				basedList = DBHelper.loadedFilmList;
				showElement();
            }
			catch
			{
				NMSG.Show("Si è verificato un errore nel caricamento dei contenuti", NMSGtype.Ok);
				Applicazione.Close(1);
			}
			finally
			{
				//hide the gif
				ShadowCircular.hideLoading(ref loadingShadow);
			}
		}

		private void showElement()
		{
			listaLB.Items.Clear();
			listaLB.ItemsSource = null;
			try
			{
				listaLB.ItemsSource = basedList;
			}
			catch
            {
				listaLB.Items.Clear();
            }
		}

		
		private void openDetails(Film toPass)
		{
			Applicazione.openWindow<details, MainWindow>(Applicazione.CloserType.Close, toPass);
		}

		private class ShadowCircular
		{
			public static void showLoading(ref Grid grid)
			{
				grid.Visibility = Visibility.Visible;
				grid.Opacity = 0;
				for (int i = 0; i < 10; i++)
				{
					grid.Opacity += 10;
					Task.Delay(50).Wait();
				}
			}

			public static void hideLoading(ref Grid grid)
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
	}
}
