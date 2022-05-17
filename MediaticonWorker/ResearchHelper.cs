using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using MediaticonDB;

namespace MediaticonWorker
{
	public class ResearchHelper
	{
		private static List<string> titlesToSearch = new List<string>();
		private static List<string> genreToSearch = new List<string>();

		

		public static void setSearchParams(string title, ItemCollection genreFilter)
		{
			///<summary>
			///this func set the search words in properly lists
			///</summary>
			titlesToSearch = title.Split(new char[] {' ', ',', ';', ':', '.', '-', '\'', '&', '/', '_'}, StringSplitOptions.RemoveEmptyEntries).ToList<string>();

			genreToSearch.Clear();
			//add filter items in adeguate list
			/*foreach(FilterItem item in genreFilter)
			{
				if (item.IsChecked == true)
					genreToSearch.Add(item.Content);
			}*/
		}

		
		

		public static IEnumerable<Film> Search() //To debug
		{			
				//return an element by element found with a yield
				using (ConnectDB db = new ConnectDB())
				{
					string query = "";

					if (titlesToSearch.Any() != true) //if it's empty or not allocated
					{
						query = $"SELECT * FROM {EnviromentVar.ContentType.Tables[(int)EnviromentVar.Modality.CurrentModality]} WHERE Generi LIKE \'%{genreToSearch[0]}%\'";
						foreach (string word in genreToSearch.Skip(1))
                        {
							query += $" AND \'Generi\' LIKE \'%{word}%\'";
                        }
						query += " ORDER BY Id DESC";

				}
				else if (genreToSearch.Any() != true) //if it's empty or not allocated
				{
						query = $"SELECT * FROM {EnviromentVar.ContentType.Tables[(int)EnviromentVar.Modality.CurrentModality]} WHERE Titolo LIKE \'%{titlesToSearch[0]}%\'";
						foreach (string word in titlesToSearch.Skip(1))
						{
							query += $" AND Titolo LIKE \'%{word}%\'";
						}
						query += " ORDER BY Id DESC";
					}
					else
                    {
						query = $"SELECT * FROM {EnviromentVar.ContentType.Tables[(int)EnviromentVar.Modality.CurrentModality]} WHERE Titolo LIKE \'%{titlesToSearch[0]}%\'";
						foreach (string word in titlesToSearch.Skip(1))
                        {
							query += $" AND \'Titoli\' LIKE \'%{word}%\'";
                        }
						foreach (string word in genreToSearch)
                        {
							query += $" AND \'Generi\' LIKE \'%{word}%\'";
                        }
						query += " ORDER BY Id DESC";
				}

				using (SqlDataReader read = db.initQuery(query).ExecuteReader(System.Data.CommandBehavior.SingleResult))
                    {
						while (read.Read())
                        {
							Film film = new Film(
							read[1].ToString(),
							read[2].ToString(),
							read[3].ToString(),
							read[4].ToString(),
							Convert.ToInt32(read[5]),
							read[6].ToString(),
							read[7].ToString(),
							read[8].ToString(),
							loadCover: true);

							yield return film.RetFromSQL();
                        }
                    }

					/*
					foreach (string word in titlesToSearch)
					{
						//"SELECt * FROM 'anime' WHERE 'title' LIKE '%ciao%' AND 'title' LIKE '%spagna%'";
						//"SELECT TOP 1 * FROM {table} ORDER BY ID DESC WHERE 'title' LIKE '%ciao%' AND 'title' LIKE '%spagna%'"

						SqlCommand command2 = db.initQuery($"SELECT * FROM \'{EnviromentVar.ContentType.Tables[(int)EnviromentVar.Modality.CurrentModality]}\' WHERE \'genres\' LIKE {}");
						SqlCommand command = db.initQuery($"SELECT * FROM \'{EnviromentVar.ContentType.Tables[(int)EnviromentVar.Modality.CurrentModality]}\' WHERE \'title\' LIKE \'%{word}%\'");
						using(SqlDataReader read = command.ExecuteReader())
						{
							while (read.Read())
							{
								foreach(string genre in genreToSearch)
								{
									if (read.GetValue(5).ToString().IndexOf(genre) != -1)
									{
										string film = "";
										for (int i = 0; i < read.FieldCount; i++)
										{
											film += read.GetValue(i).ToString();
										}
										yield return film;
									}
								}
							}
						}
					}
					*/
				}
			

		}

		/*
		TODO
			<ItemsControl x:Name="myItemsControl">
				<ItemsControl.ItemTemplate>
					<DataTemplate>
						<CheckBox Content="{Binding Caption}" Checked="{Binding IsChecked}" />
					</DataTemplate>
				</ItemsControl.ItemTemplate>
			</ItemsControl>
		 */
	}

	public class FilterItem //: INotifyPropertyChanged
	{
		public string Content { get; set; }
		public bool IsChecked { get; set; } = false;
	}
}
