using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB
{
    public class MakeDirs
    {
        public static bool MakeAllsFolders()
        {
            if (!DefaultFolders())
                return false;

            if (!JsonFolders())
                return false;

            if (!DataFolders())
                return false;

            if (!UserFolders())
                return false;

            return true;
        }

        public static bool UserFolders()
        {
            try
            {
                Directory.CreateDirectory(EnviromentVar.UsersPath.UsersMainPath);
            }
            catch
            {
                return false;
            }
            return true;
        }

        public static bool SpecificUserFolders(string username)
        {
            try
            {
                Directory.CreateDirectory(EnviromentVar.UsersPath.UserPath(username));
            }
            catch
            {
                return false;
            }
            return true;
        }

        private static bool DefaultFolders()
        {
            try
            {
                Directory.CreateDirectory(EnviromentVar.ImagesVar.defaultImgPath);
                Directory.CreateDirectory(EnviromentVar.ImagesVar.ChannelLogoPath);
            }
            catch
            {
                return false;
            }
            return true;
        }

        private static bool JsonFolders()
        {
            try
            {
                foreach(var dir in EnviromentVar.ContentType.Tables)
                {
                    Directory.CreateDirectory(Path.Combine(EnviromentVar.JsonVar.JsonPath, dir));
                }

                Directory.CreateDirectory(EnviromentVar.JsonVar.GuidaTvJsonPath);
            }
            catch
            {
                return false;
            }
            return true;
        }

        private static bool DataFolders()
        {
            return true;
        }

        private static bool ScraperFolders()
        {
            return true;
        }
    }
}
