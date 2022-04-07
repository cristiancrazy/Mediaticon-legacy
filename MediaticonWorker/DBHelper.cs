using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MediaticonDB;

namespace MediaticonWorker
{
    /// <summary>
    /// 
    /// </summary>
    public class DBHelper
    {
        public static async void UpdateDB()
        {
            await Task.Run(() => update());
        }

        private static void update()
        {
            MakeDirs.MakeAllsFolders();
            Download.DownloadAll();
            MediaticonDB.UpdateDB.UpdateAll();
        }

    }
}
