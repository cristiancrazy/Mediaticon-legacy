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



        public static bool setSearchParams(string title, ItemCollection genreFilter)
        {
            ///<summary>
            ///this func set the search words in properly lists
            ///</summary>
            titlesToSearch = title.Split(new char[] { ' ', ',', ';', ':', '.', '-', '\'', '&', '/', '_' }, StringSplitOptions.RemoveEmptyEntries).ToList<string>();

            genreToSearch.Clear();
            //add filter items in adeguate list
            /*foreach(FilterItem item in genreFilter)
			{
				if (item.IsChecked == true)
					genreToSearch.Add(item.Content);
			}*/

            return titlesToSearch.Count != 0 || genreToSearch.Count != 0;
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
                        query += $" AND Generi LIKE \'%{word}%\'";
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
                        query += $" AND Titolo LIKE \'%{word}%\'";
                    }
                    foreach (string word in genreToSearch)
                    {
                        query += $" AND Generi LIKE \'%{word}%\'";
                    }
                    query += " ORDER BY Id DESC";
                }

                using (SqlDataReader read = db.initQuery(query).ExecuteReader(System.Data.CommandBehavior.SingleResult))
                {
                    try
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
                    finally
                    { }
                    
                }
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
