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
	/// <summary>
	/// download all film from last in DB to latest movie released
	/// </summary>
	public class Download
	{
		//check connection
		//check date online
		//check date of the last film in db
		//download all csv from that year to today, put them in ./csv/film/film_2020.csv

		private static string fromUrl = EnviromentVar.CsvfromUrl;
		//private static string fromUrl = EnviromentVar.JsonVar.JsonfromUrl;
		private static string toPath = EnviromentVar.CsvPath;
		private static string fileExt = EnviromentVar.CsvfileExt;

		public static bool DownloadAll()
		{

			UpdateDB.DeleteAll();

			if (!Connection.IsOnline())
				return false;

			string[] tables = EnviromentVar.ContentType.Tables;

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
					for (int i = lastContent.Year; i <= today.Year; i++)
					{
						//for each year download file
						if (!Connection.downloadFile(table, i, fromUrl, fileExt, toPath, true))
							return false;
					}
				}
			}
			return true;
		}

		private class Dater
		{
			public static bool getDate(out DateTime date)
			{
				string resp;
				try
				{
					var tcp = new TcpClient(EnviromentVar.NTPServer, 13);
					using (var rdr = new StreamReader(tcp.GetStream()))
					{
						resp = rdr.ReadToEnd();
					}
				}
				catch
				{
					try
					{
						date = DateTime.Today;
						return true;
					}
					catch
                    {
						date = EnviromentVar.MinDate;
						return false;
                    }
				}
				string utc = null;
				try
				{
					utc = resp.Substring(7, 8);
					//sometimes happen that the date is cought in wrong way, so retry
				}
				catch
                {
					return getDate(out date);					
                }

				CultureInfo info = CultureInfo.InvariantCulture;
				date = DateTime.ParseExact(utc, EnviromentVar.DateFormat, info);
				return true;
			}

			public static bool lastContent(string table, out DateTime date)
			{
				date = EnviromentVar.MinDate;
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
								date = EnviromentVar.MinDate;
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
	}
}
