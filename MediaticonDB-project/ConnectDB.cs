using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB
{
    internal class ConnectDB : IDisposable
    {
#if DEBUG
        static string cwd = @"C:\Users\Visual Laser 10 New\source\repos\MediaticonDB\";
#else
        static string cwd = System.Environment.CurrentDirectory;
#endif
        string c = @$"Data Source=(localdb)\MSSQLLocalDb;Integrated Security=true;AttachDbFileName={cwd}Database1.mdf";//C:\Users\Visual Laser 10 New\source\repos\MediaticonDB\Database1.mdf;";

        public ConnectDB()
        {
            Connect(c);
        }

        void Connect(string conn)
        {
            SqlConnection sqlConnection = new SqlConnection(conn);
            sqlConnection.Open();
            //SqlCommand command = new SqlCommand("INSERT INTO FilmTable ( Wallpaper, Cover, Titolo, Trama, Durata, Anno, Generi, Attori) VALUES ( 'gino', 'gino1', 'gino2', 'gino3', 'gino4', 'gino5', 'gino6', 'gino6')", sqlConnection);
            //int a = command.ExecuteNonQuery();
            //Console.WriteLine($"Record {a}");

            //Console.ReadKey();
        }

        public Film Read(int line, string tableName)
        {

        }

        public void Append(Film film, string tableName)
        {

        }

        public void Replace(int lineToReplace, Film newFilm, string tableName)
        {

        }

        public int LastID(string tableName)
        {

        }

        void Close()
        {

        }

        //close using
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disp)
        {
            if (disp)
            {
                Close();
            }
        }
    }
}
