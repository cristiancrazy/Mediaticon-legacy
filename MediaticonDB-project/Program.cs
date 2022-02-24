using System;
using System.Configuration;
using System.Data.SqlClient;

namespace MediaticonDB
{
    internal class Program
    {
        static void Main(string[] args)
        {
            //link_imgGrande; link_img; titolo; §trama§; durata; anno; ['generi', ]; tipo; ['attori', ]
            //string c = @"Data Source=(LocalDB)\v11.0;AttachDbFilename=|DataDirectory|\Database1.mdf;Integrated Security=True";
            //string c = @"Server=(localdb)\\MSSQLLocalDb;Integrated Security=true;database=Database1.mdf";
            //giusta    string c = @"Data Source=(localdb)\MSSQLLocalDb;Integrated Security=true;AttachDbFileName=C:\Users\Visual Laser 10 New\source\repos\MediaticonDB\Database1.mdf;";
            MakeDirs.MakeAllsFolders();
            //Download.DownloadAll();
            UpdateDB.UpdateAll();
            Console.ReadKey();
        }
    }
}
