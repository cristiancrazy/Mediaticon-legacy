﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Configuration;
using System.Data.SqlClient;
using System.IO;

namespace MediaticonDB
{
    internal class UpdateDB
    {
        public static bool UpdateAll()
        {
            foreach (var table in EnviromentVar.Tables) //foreach table
            {
                //seek last title in database
                string lastFilm = "";
                if (!lastTitle(table, out lastFilm))
                    return false;

                //seek same title in csv
                if (!copyCSVtoTable(table, lastFilm))
                    return false;
            }
            DeleteAll();
        }

        private static bool copyCSVtoTable(string Table, string SeekFilm)
        {
            //per ogni file nella cartella, lo apre e cerca il seekfilm, con il Csvreader;
            //se il titolo è il desiderato incomincia a scrivere
            bool copy = false;

            try
            {
                using (ConnectDB db = new ConnectDB())
                {
                    try
                    {
                        foreach (var File in Directory.GetFiles(EnviromentVar.CsvPath + "\\" + Table + "\\"))
                        {//foreach file per table
                            string buffer = "";
                            using (StringReader strRead = new StringReader(File))
                            {
                                while ((buffer = strRead.ReadLine()) != null)
                                {//foreach line
                                    Film tmp = CsvReader.ReadLine(buffer);
                                    if (copy == false && tmp.Title == SeekFilm)
                                        copy = true;

                                    if (copy == true)
                                    {
                                        //start to copy the film from csv to database, when seekfilm will found
                                        db.Append(tmp, Table);
                                    }
                                }
                            }
                        }
                    }
                    catch
                    {
                        return false;
                    }
                }
            }
            catch
            {
                return false;
            }

            return true;
        }

        private static bool lastTitle(string Table, out string Title)
        {
            Title = "";
            try
            {
                using (ConnectDB dB = new ConnectDB())
                {
                    Film last = null;
                    try
                    {
                        last = dB.Read(dB.LastID(Table), Table);
                    }
                    catch
                    {
                        //database table is empty
                        if (last == null)
                        {
                            //if there wasn't a film in a list
                            Title = "";
                            return true;
                        }
                    }
                    Title = last.Title;
                    return true;
                }
            }
            catch
            {
                //cannot contact database
                return false;
            }
        }

        private static void DeleteAll()
        {
            //run gc collector
            //delete all csv files
        }
    }
}
