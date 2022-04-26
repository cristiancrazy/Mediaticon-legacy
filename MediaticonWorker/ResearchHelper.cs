using System;
using System.Collections.Generic;
using System.ComponentModel;
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
			titlesToSearch = title.Split(new char[] {' ', ',', ';', ':', '.', '-'}, StringSplitOptions.RemoveEmptyEntries).ToList<string>();

			genreToSearch.Clear();
			//add filter items in adeguate list
			foreach(FilterItem item in genreFilter)
			{
				if (item.IsChecked == true)
					genreToSearch.Add(item.Content);
			}
		}

		public static async IEnumerable<Task<Film>> Search()
		{
			while ()
				//return an element by element found with a yield

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
