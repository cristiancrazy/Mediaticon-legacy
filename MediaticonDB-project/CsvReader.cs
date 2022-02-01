using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB
{
    internal class CsvReader
    {
        private static string FormatList(string Line)
        {
            Line = Line.Replace("[", "").Replace("]", "");
            Line = Line.Replace("\'", "");
            return Line;
        }

        public static Film ReadLine(string Line)
        {
            //convert a csv line to a film obj
            List<string> param = Line.Split(";").ToList();

            List<string> genres = FormatList(param[6]).Split(", ").ToList();
            List<string> actors = FormatList(param[7]).Split(", ").ToList();

            Film tmp = new Film(
                param[0],
                param[1],
                param[2],
                param[3].Replace("§",";"),
                Convert.ToInt32(param[4]),
                new DateTime(Convert.ToInt32(param[5]), 1 , 1),
                genres,
                actors);

            return tmp;
        }
    }
}
