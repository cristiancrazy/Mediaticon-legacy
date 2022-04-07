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
            var res = await Task.Run(() => update());
            if(!res)
            {
                
            }
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
