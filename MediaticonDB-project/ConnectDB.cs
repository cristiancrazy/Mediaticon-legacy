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

        private static SqlConnection sqlConnection;
        public ConnectDB()
        {
            try
            {
                Connect(EnviromentVar.DBConnStr);
            }
            catch(MediaticonException.ConnectingDBException)
            {
                throw new MediaticonException.ConnectingDBException();
            }
        }

        
        void Connect(string conn)
        {
            try
            {
                sqlConnection = new SqlConnection(conn);
                sqlConnection.Open();
            }
            catch
            {
                throw new MediaticonException.ConnectingDBException();
            }
            //SqlCommand command = new SqlCommand("INSERT INTO FilmTable ( Wallpaper, Cover, Titolo, Trama, Durata, Anno, Generi, Attori) VALUES ( 'gino', 'gino1', 'gino2', 'gino3', 'gino4', 'gino5', 'gino6', 'gino6')", sqlConnection);
            //int a = command.ExecuteNonQuery();
            //Console.WriteLine($"Record {a}");

            //Console.ReadKey();
        }

        public Film Read(int line, string tableName)
        {
            try
            {
                SqlCommand cmd = new SqlCommand(
                    $"SELECT * FROM {tableName} WHERE id=\'{line}\'", sqlConnection);
                SqlDataReader read = cmd.ExecuteReader();

                Film output = new Film(
                    read[1].ToString(),
                    read[2].ToString(),
                    read[3].ToString(),
                    read[4].ToString(),
                    Convert.ToInt32(read[5]),
                    read[6].ToString(),
                    read[7].ToString(),
                    read[8].ToString()
                    );
                return output.RetFromSQL();
            }
            catch
            {
                throw new MediaticonException.ReadingDBException();
            }
            
        }

        public void Append(Film film, string tableName)
        {
            try
            {
                film = film.RetToSQL(); //prepare to save with sql
                SqlCommand command = new SqlCommand(
                    $"INSERT INTO {tableName} " +
                    $"(Wallpaper, Cover, Titolo, Trama, Durata, Anno, Generi, Attori) " +
                    $"VALUES " +
                    $"(\'{film.BigImage}\', " +
                    $"\'{film.Image}\', " +
                    $"\'{film.Title}\', " +
                    $"\'{film.Description}\', " +
                    $"\'{film.Duration}\', " +
                    $"\'{film.Year}\', " +
                    $"\'{film.Genres.ListToString()}\', " +
                    $"\'{film.Actors.ListToString()}\')", sqlConnection);

                command.ExecuteNonQuery();
            }
            catch
            {
                throw new MediaticonException.WritingDBException();
            }
        }

        public void Replace(int lineToReplace, Film newFilm, string tableName)
        {
            try
            {
                newFilm = newFilm.RetToSQL();
                SqlCommand cmd = new SqlCommand(
                    $"UPDATE {tableName} SET " +
                    $"Wallpaper = {newFilm.BigImage}, " +
                    $"Cover = {newFilm.Cover}, " +
                    $"Titolo = {newFilm.Title}, " +
                    $"Trama = {newFilm.Description}, " +
                    $"Durata = {newFilm.Duration}, " +
                    $"Anno = {newFilm.Year}, " +
                    $"Generi = {newFilm.Genres.ListToString()}, " +
                    $"Attori = {newFilm.Actors.ListToString()} " +
                    $" WHERE id = \'{lineToReplace}\'", sqlConnection);

                cmd.ExecuteNonQuery();
            }
            catch
            {
                throw new MediaticonException.WritingDBException();
            }
        }

        public int LastID(string tableName)
        {
            try
            {
                SqlCommand cmd = new SqlCommand($"SELECT MAX(Id) FROM {tableName}", sqlConnection);
                SqlDataReader lastId = cmd.ExecuteReader();

                return Int32.Parse(lastId.ToString());
            }
            catch
            {
                throw new MediaticonException.ReadingDBException();
            }
        }

        

        void Close()
        {
            sqlConnection.Close();
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
