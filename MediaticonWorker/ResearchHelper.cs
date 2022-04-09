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
		public static List<Film> foundList = new List<Film>();


		public static async Task<bool> Search()
		{
			 
			while()
			lock(tokenLock)
            {
				foundList =
            }
		}
	}
}
