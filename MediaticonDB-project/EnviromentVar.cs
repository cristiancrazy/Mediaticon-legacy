using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;

/// <summary>
/// this file contains the global variables
/// 
/// the url of main website
/// 
/// where take & where save the csv files
/// where is the Database & the connection string
/// the list of tables in the db
/// 
/// the url of ntp server
/// the format of the date
/// </summary>

namespace MediaticonDB
{
    internal class EnviromentVar
    {
        //path url

        //url
        public static string SiteUrl = @"https://mediaticon.000webhostapp.com/";

        //csv path & url
        public static string CsvfromUrl = SiteUrl + "public_html/csv/";
        public static string CsvPath = @".\csv\";
        public static string CsvfileExt = ".csv";

        //guidatv csv path
        public static string GuidaTvCsvPath = Path.Combine(CsvPath, GuidaTvCsv) + "\\";


        //scraper path
        public class ScraperVar
        {
            public static string ScraperPath = @".\scraper\";
            public static string PythonExt = ".py";
        }        

        public class ImagesVar
        {
            //default path
            public static string defaultPath = @".\Default\";
            public static string defaultImgPath = Path.Combine(defaultPath + "Images\\");
            public static string ChannelLogoPath = Path.Combine(defaultImgPath + "Channels\\");

            //default imgs
            public static string ImgfileExt = ".png";
            public static string[] defaultImages = { "Avatar", "Cover", "Background" };
        }

        //DB path
#if DEBUG
        static string cwd = @"C:\Users\Visual Laser 10 New\source\repos\MediaticonDB\";
#else
        static string cwd = System.Environment.CurrentDirectory + "\\";
#endif
        public static string DBConnStr = @$"Data Source=(localdb)\MSSQLLocalDb;Integrated Security=true;AttachDbFileName={cwd}Database1.mdf";

        //Type
        public static string[] Tables = { "Film", "Serie", "Anime", "Show" };
        public static string GuidaTvCsv = "GuidaTV";

        //datetime format

        public static string NTPServer = "time.nist.gov";

        public static string DateFormat = "yy-MM-dd";

        public static string TimeFormat = "HH-mm";

        

        public static Func<string,string,string> CsvPathCombine = (type, year) =>
        CsvPath + type + "_" + year + CsvfileExt;

    }

    internal class MediaticonException
    {
        public class WritingDBException : System.Exception { }

        public class ReadingDBException : System.Exception { }

        public class ConnectingDBException : System.Exception { }
    }
}
