using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MediaticonDB;

namespace MediaticonWorker
{
	public class ResearchHelper
	{
		private static Object tokenLock = new Object();

		[ObsoleteAttribute("sobstituted with yield", false)]
		public static List<Film> foundList = new List<Film>();


		public static async Task<bool> Search()
		{
			 
			while()
				//return an element by element found with a yield

		}
	}
}
