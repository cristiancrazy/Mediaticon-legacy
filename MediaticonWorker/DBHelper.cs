using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using NewMessageBox;
using MediaticonDB;

namespace MediaticonWorker
{
    /// <summary>
    /// 
    /// </summary>
    public class DBHelper
    {
        public static List<Film> loadedFilmList = new List<Film>();
        public static async void UpdateDB()
        {
            var res = await Task.Run(() => update());
            if(!res)
            {
                NMSG.Show("Si è verificato un errore nel caricamento dei contenuti", NMSGtype.Ok);
                Applicazione.Close(1);
            }

            //when load the last 50 film on loadedFilmList, stop the task, so the calling func get the list<film> to set in listbox
        }

        private static bool update()
        {
            if (!MakeDirs.MakeAllsFolders())
                return false;
            if (!Download.DownloadAll())
                return false;
            if (!MediaticonDB.UpdateDB.UpdateAll())
                return false;
            return true;
        }

    }
}
