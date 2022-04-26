using System;
using System.Collections.Generic;
using System.Linq;
using Newtonsoft.Json;
using System.Text.Json;
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
                param[3].Replace("§", ";"),
                Convert.ToInt32(param[4]),
                new DateTime(Convert.ToInt32(param[5]), 1, 1),
                genres,
                actors);

            return tmp;
        }

        public static Replica ReadLineGuidatv(string line)
        {
            //data;orainizio;orafine
            List<string> info = line.Split(";").ToList();
            Replica dati = new Replica(info[1], info[2], info[3]);

            return dati;
        }
    }
    
    internal class JsonReader
    {
        public static Film ReadFilm(string line)
        {
            Film tmp = JsonConvert.DeserializeObject<Film>(line);
            return tmp;
        }

        public static Replica ReadLineGuidaTv(string line)
        {
            dynamic data = JsonConvert.DeserializeObject(line);

            return new Replica(data.date, data.start_time, data.end_time);
        }
    }
}
