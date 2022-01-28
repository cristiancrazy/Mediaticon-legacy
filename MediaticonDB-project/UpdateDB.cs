using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Configuration;
using System.Data.SqlClient;

namespace MediaticonDB
{
    internal class UpdateDB
    {
#if DEBUG
        string cwd = @"C:\Users\Visual Laser 10 New\source\repos\MediaticonDB\";
#else
            string cwd = System.Environment.CurrentDirectory;
#endif
        string c = @$"Data Source=(localdb)\MSSQLLocalDb;Integrated Security=true;AttachDbFileName={cwd}Database1.mdf";//C:\Users\Visual Laser 10 New\source\repos\MediaticonDB\Database1.mdf;";
        

        static void connection(string conn)
        {
            using (SqlConnection sqlConnection = new SqlConnection(conn))
            {
                sqlConnection.Open();
                SqlCommand command = new SqlCommand("INSERT INTO FilmTable ( Wallpaper, Cover, Titolo, Trama, Durata, Anno, Generi, Attori) VALUES ( 'gino', 'gino1', 'gino2', 'gino3', 'gino4', 'gino5', 'gino6', 'gino6')", sqlConnection);
                int a = command.ExecuteNonQuery();
                Console.WriteLine($"Record {a}");
            }
            Console.ReadKey();
        }
    }
}
