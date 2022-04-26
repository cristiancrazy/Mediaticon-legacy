using System;
using System.Collections.Generic;
using System.Drawing;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB
{
    public class Replica
    {
        public DateTime data, orainizio, orafine;
        public Replica(DateTime data,DateTime orainizio,DateTime orafine)
        {
            this.data = data;
            this.orainizio = orainizio;
            this.orafine = orafine;
        }

        public Replica(string data,string orainizio,string orafine)
        {
            this.data = DateTime.ParseExact(data, EnviromentVar.DateFormat, new CultureInfo("it-IT"));
            this.orainizio = DateTime.ParseExact(orainizio, EnviromentVar.TimeFormat, new CultureInfo("it-IT"));
            this.orafine = DateTime.ParseExact(orafine, EnviromentVar.TimeFormat, new CultureInfo("it-IT"));
        }
    }

    public class Channel
    {
        public string path;
        public Bitmap image;
        public List<Replica> programmi;

        public Channel(string path, List<Replica> programmi)
        {
            this.path = path;

            Connection.openImage(path, out this.image);

            this.programmi = programmi;
        }
    }
}
