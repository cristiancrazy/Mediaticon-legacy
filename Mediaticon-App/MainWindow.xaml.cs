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
			fillFilterCBL();
			loadElement();

		   
			DataContext = this;
			/*
						for (int i = 0; i < 10; i++)
						{
							basedList.Add(new Film("", "",
								$"Gino{i}",
								"Il film di canavacciuolo",
								12, 2022,
								new List<string>() { "can", "gepp" },
								new List<string>() { "cann", "caruso" },
								true));
						}
						listaLB.ItemsSource = basedList;
						DataContext = this;

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
			if (this.IsLoaded) //beause otherwise it runs before loading component has finished
			{
				Search();
			}
			//while this return a yield get add element to baselist, load it to screen 
		}

		private void searchEvent(object sender, SelectionChangedEventArgs e)
		{
			//overload for filter list
			Search();
		}

		private void listBox_ScrollChanged(object sender, ScrollChangedEventArgs e)
		{

		}

		private void accountCombo_SelectionChanged(object sender, SelectionChangedEventArgs e)
		{
			//when user click on accountCombo item
			switch (accountCombo.SelectedIndex)
			{
				case 0:
					//open my list
					Applicazione.openWindow<mylist>(Applicazione.CloserType.Hide, toClose: this);
					break;
				case 1:
					//open website
					System.Diagnostics.Process.Start(@"https://github.com/VisualLaser10New");
					break;
				case 2:
					//exit from account
					Applicazione.openWindow<login>(Applicazione.CloserType.Hide, toClose: this);
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

		static CancellationTokenSource cancelTask = new CancellationTokenSource();
		static CancellationToken token = cancelTask.Token;
		private void Search()
		{
			//cancel the previous search
			cancelTask.Cancel();

			//regenerate the token
			cancelTask = new CancellationTokenSource();
			token = cancelTask.Token;
			//Task searchTask = new(doSearch, token);
			this.Dispatcher.BeginInvoke(new Action(() =>
			{
				Task.Run(() => doSearch(searchTxt.Text), token);
			}));
			//searchTask.Start();
			showElement(); //in case there is no result -> show empty list, and messagebox will showed by research helper
		}

		object locktoken = new object();
		private void doSearch(string text)
		{
			//this function run in another task
			this.Dispatcher.BeginInvoke(new Action(() =>
			{
				//wait a second
				Thread.Sleep(2000);

				//do the new search
				basedList.Clear();

				ResearchHelper.setSearchParams(text, filterCBL.Items);
				
				//i'm not sure that this await is correct
				foreach (var film in ResearchHelper.Search())
				{
					lock (this.locktoken)
					{
						basedList.Add(film);
					}
					showElement();
				}
			}));
		}


		[STAThread] //to call NMSG it is necessary
		private async void loadElement()
		{
			//show the gif
			//ShadowCircular load = new ShadowCircular();
			ShadowCircular.showLoading(ref loadingShadow);

			await Task.Run(() =>
			{
				//wait the end of loading of 50 elements in DBHelper
				while (!DBHelper.Ready) ;

				Dispatcher.BeginInvoke(new Action(() =>
				{ //not await, at the moment, works
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
				}));
			});
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
			lock (this.locktoken)
			{
				//listaLB.Items.Clear();
				listaLB.ItemsSource = null;
				try
				{
					listaLB.ItemsSource = basedList;
				}
				catch
				{
					listaLB.Items.Clear();
				}
				finally
				{
					DataContext = this;//update binding xaml
				}
			}
		}


		private void openDetails(Film toPass)
		{
			Applicazione.openWindow<details>(Applicazione.CloserType.Hide, toClose: this, toPass);
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
