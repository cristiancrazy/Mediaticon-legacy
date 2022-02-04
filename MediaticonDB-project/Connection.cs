using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Sockets;
using System.Net.NetworkInformation;
using System.Net;
using System.IO;
using System.Drawing;

namespace MediaticonDB
{
	/// <summary>
	/// class level 0
	/// </summary>
	
	
    internal class Connection
    {
		public static bool IsOnline()
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

		public static bool downloadFile(string type, int year, string fromUrl, string fileExt, string toPath)
		{
			string contents = null;
			string file = type + "_" + year + fileExt;
			string urlDownload = fromUrl + type + "/" + file;

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

		public static bool DeleteAll(string path)
        {
			try
			{
				GC.Collect();
			}
			catch
			{
				return false;
			}

            try
            {
				foreach(var file in Directory.GetFiles(path))
                {
					File.Delete(file);
				}
            }
			catch
            {
				return false;
            }
			return true;
		}

		

		public static bool DownloadImage(string url, out Bitmap bitmap)
        {
			bitmap = null;
			using(var wc = new WebClient())
            {
				try
				{
					byte[] imgdata = wc.DownloadData(url);

					ImageConverter conv = new ImageConverter();
					Image image = (Image)conv.ConvertFrom(imgdata);
					bitmap = new Bitmap(image);
					return true;
				}
                catch
                {
					return false;
                }
            }
			return true;
        }
	}
}
