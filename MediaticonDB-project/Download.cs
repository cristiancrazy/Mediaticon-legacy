using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB
{
	internal class Download
	{
		//check connection
		//check date online
		//check date of the last film in db
		//download all csv from that year to today, put them in ./csv/film/film_2020.csv

		private static string fromUrl = @"https://mediaticon.000webhostapp.com/";
		private static string toPath = @".\csv\";
		private static string fileExt = ".csv";

		public static bool downloadAll()
		{
			if (!IsOnline())
				return false;

			string[] tables = { "Film", "Serie", "Anime", "Show" };

			foreach (string table in tables)
			{
				//foreach type
				DateTime today;
				DateTime lastContent;

				if (!Dater.getDate(out today))
					return false;
				if (!Dater.lastContent(table, out lastContent))
					return false;

				if (today.Year >= lastContent.Year)
				{
					//download
					for (int i = today.Year; i <= lastContent.Year; i++)
					{
						//for each year download file
						if (!downloadFile(table, i))
							return false;
					}
				}
			}
			return true;
		}

		private static bool IsOnline()
		{
			string host = @"8.8.8.8";
			Ping p = new Ping();
			try
			{
				PingReply reply = p.Send(host, 3000);
				if (reply.Status == IPStatus.Success)
					return true;
			}
			catch 
			{
				return false;
			}
			return false;
		}

		private class Dater
		{
			public static bool getDate(out DateTime date)
			{
				string resp;
				try
				{
					var tcp = new TcpClient("time.nist.gov", 13);
					using (var rdr = new StreamReader(tcp.GetStream()))
					{
						resp = rdr.ReadToEnd();
					}
				}
				catch
				{
					date = new DateTime(0,0,0);
					return false;
				}

				string utc = resp.Substring(7, 8);
				CultureInfo info = CultureInfo.InvariantCulture;
				date = DateTime.ParseExact(utc, "yy-MM-dd", info);
				return true;
			}

			public static bool lastContent(string table, out DateTime date)
			{
				date = new DateTime(0, 0, 0);
				try
				{
					using (ConnectDB dB = new ConnectDB())
					{
						Film last = null;
						try
						{
							last = dB.Read(dB.LastID(table), table);
						}
						catch
						{
							//database table is empty
							if (last == null)
							{
								//if there wasn't a film in a list
								date = new DateTime(0, 0, 0);
								return true;
							}
						}
						date = last.Year;
						return true;
					}
				}
				catch
                {
					//cannot contact database
					return false;
                }
			}
		}

		private static bool downloadFile(string type, int year)
		{
			string contents = null;
			string file = type + "_" + year + fileExt;
			string urlDownload = fromUrl + "csv/" + type + "/" + file;

            try
			{
				using (var wc = new WebClient())
				{
					contents = wc.DownloadString(urlDownload);
				}
			}
			catch
            {
				return false;
            }

			try
			{
				File.WriteAllText(Path.Combine(toPath + "\\" + type + "\\", file), contents);
			}
			catch
            {
				return false;
            }

			return true;
		}

	}
}
