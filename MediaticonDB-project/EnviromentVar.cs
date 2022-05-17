using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Drawing;

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
    public class EnviromentVar
    {
        //path url

        //url
        public static string SiteUrl = @"https://mediaticon.000webhostapp.com/";

        //csv path & url
        public static string CsvfromUrl = SiteUrl + "csv/";
        public static string CsvPath = @".\csv\";
        public static string CsvfileExt = ".csv";

        //guidatv csv path
        public static string GuidaTvCsvPath = Path.Combine(CsvPath, ContentType.GuidaTvCsv) + "\\";

        public static string JsonPath = @".\json\";
        public class JsonVar
        {
            public static string JsonfromUrl = SiteUrl + "json/";
            public static string JsonfileExt = ".json";
            public static string JsonPath = @".\json\";

            public static string GuidaTvJsonPath = Path.Combine(JsonPath, ContentType.GuidaTvCsv) + "\\";
        }

        //scraper path
        public class ScraperVar
        {
            public static string ScraperPath = @".\scraper\";
            public static string PythonExt = ".py";
            public static string ExeExt = ".exe";
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

            public static string defaultAvatarImage = Path.Combine(defaultImgPath + "Avatar"+ImgfileExt);
            public static string defaultCoverImage = Path.Combine(defaultImgPath + "Cover"+ImgfileExt);

            public static string loadingGifImage = Path.Combine(System.Environment.CurrentDirectory + defaultImgPath + "load_progress.gif");


        }

        public class UsersPath
        {
            //name
            public static string UserName = ""; //when the user enter the username will be saved here

            //avatar
            public static Bitmap Avatar;

            //path
            public static string UsersMainPath = @".\Users\";
            public static Func<string, string> UserPath = (name) => UsersMainPath + name + "\\";

            //file
            public static Func<string, string> UserAvatarFile = (name) =>
            UserPath(name) + ImagesVar.defaultImages[0] + ImagesVar.ImgfileExt;

            public static Func<string, string> UserMyListFile = (name) => UserPath(name) + "MyList.obj";
        }

        //DB path
#if DEBUG
        static string cwd = @"C:\Users\Visual Laser 10 New\source\repos\cristiancrazy\Mediaticon-sviluppatori\MediaticonDB-project\";
        //static string cwd = @"C:\Users\Cristian\Desktop\Workspaces\Mediaticon\MediaticonDB-project\";
        //static string cwd = @"C:\Users\12036\Downloads\Mediaticon-sviluppatori-main\Mediaticon-sviluppatori-main\MediaticonDB-project\";
        //static string cwd = @"C:\Users\12036\source\repos\cristiancrazy\Mediaticon-sviluppatori\MediaticonDB-project\";
#else
        static string cwd = System.Environment.CurrentDirectory + "\\";
#endif
        public static string DBConnStr = @$"Data Source=(localdb)\MSSQLLocalDb;Integrated Security=true;AttachDbFileName={cwd}Database1.mdf";

        //Type
        public class ContentType
        {
            public static string[] Tables = { "Film" };
            //public static string[] Tables = { "Film", "Serie", "Anime" };//, "Show" };
            public static string GuidaTvCsv = "GuidaTV";
            public static string[]FilmStatus= { "Visto", "Stò Guardando", "Da Guardare"};
        }

        public class Modality
        {
            //this is the opened modality in main window
            public enum modType
            {
                Film, Serie, Anime, //Show
            }
            public static modType CurrentModality = modType.Film;
        }

        //datetime format

        public static string NTPServer = "time.nist.gov";

        public static string DateFormat = "yy-MM-dd";

        public static string TimeFormat = "HH-mm";

        public static DateTime MinDate = new DateTime(2019, 1, 1);
        

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
