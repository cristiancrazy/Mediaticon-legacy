using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using MediaticonDB;

namespace MediaticonWorker
{
	public class ResearchHelper
	{
		private static Object tokenLock = new Object();

		[ObsoleteAttribute("sobstituted with yield", false)]
		public static List<Film> foundList = new List<Film>();

		public static void setSearchParams(string title, ItemCollection genreFilter)
        {

        }

		public static async IEnumerable<Task<Film>> Search()
		{
			while()
				//return an element by element found with a yield

		}
	}
}
