using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;

namespace MediaticonDB
{
    internal class GuidaTv
    {
        /// <summary>
        /// check if is online
        /// 
        /// run scraper with scraperhandler
        /// 
        /// foreach file csv
        /// read all line of csv and fill the list channel
        /// 
        /// delete all csv files 
        /// </summary>
        ///
        /// <returns>
        /// a bool that specify if it has worked
        /// a list of channels
        /// </returns>

        static string scraper = EnviromentVar.ScraperPath;
        static string scraperToRead = EnviromentVar.ScraperPath + EnviromentVar.GuidaTvCsv + "\\";

        public static bool ReadAll(out List<Channel> canali, string ShowName)
        {
            canali = new List<Channel>();

            if (!Connection.IsOnline()) //if is offline ret false
                return false;

            //if is online invoke scraper
            ScraperHandler tvScraper = new ScraperHandler(scraper);
            tvScraper.RunScraper(ShowName); //run scraper

            if (!tvScraper.GetReturn(out _)) //if there's an error return false
                return false;

            //if the scraper has run, read all csv files
            //foreach csv read all line
            if (!readCSVs(out canali))
            {
                DeleteAll();
                return false;
            }

            //delete all
            DeleteAll();

            //if read is completed
            return true;
        }

        private static bool readCSVs(out List<Channel> output)
        {
            output = new List<Channel>(); //the output
            try
            {
                foreach(var file in Directory.GetFiles(scraperToRead))
                {//foreach file

                    List<Replica> repliche = new List<Replica>();
                    string imageLink = "";
                    try
                    {
                        using (StringReader sr = new StringReader(file))
                        {
                            string buffer = "";

                            if ((buffer = sr.ReadLine()) != null)
                            {
                                //the first line contains the path of channel logo
                                imageLink = buffer;
                            }

                            while ((buffer = sr.ReadLine()) != null)
                            {//foreach line add the replica to repliche list
                                repliche.Add(CsvReader.ReadLineGuidatv(buffer));
                            }
                        }
                    }
                    catch
                    {
                        return false;
                    }

                    output.Add(new Channel(imageLink, repliche));
                }
            }
            catch
            {
                return false;
            }
            return true;
        }

        private static bool DeleteAll()
        {
            //delete all csv files
            try
            {
                Connection.DeleteAll(scraperToRead);
            }
            catch
            {
                return false;
            }
            return true;
        }
    }
}
