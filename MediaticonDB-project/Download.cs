using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.NetworkInformation;
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

        private string fromUrl = @"https://mediaticon.000webhostapp.com//";
        private string toPath = @".\csv\";

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
            string host = @"https://www.google.com";
            bool result = false;
            Ping p = new Ping();
            try
            {
                PingReply reply = p.Send(host, 3000);
                if (reply.Status == IPStatus.Success)
                    return true;
            }
            catch { }
            return result;
        }

        private class Dater
        {
            public static bool getDate(out DateTime date)
            {

            }

            public static bool lastContent(string table, out DateTime date)
            {

            }
        }

        private static bool downloadFile(string type, int year)
        {

        }

    }
}
