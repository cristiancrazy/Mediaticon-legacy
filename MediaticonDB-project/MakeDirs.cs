﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB
{
    internal class MakeDirs
    {
        public static bool MakeAllsFolders()
        {
            if (!DefaultFolders())
                return false;

            if (!CsvFolders())
                return false;

            if (!DataFolders())
                return false;

            return true;
        }

        public static bool UserFolders()
        {
            
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

        private static bool CsvFolders()
        {
            try
            {
                foreach(var dir in EnviromentVar.ContentType.Tables)
                {
                    Directory.CreateDirectory(Path.Combine(EnviromentVar.CsvPath, dir));
                }

                Directory.CreateDirectory(EnviromentVar.GuidaTvCsvPath);
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
