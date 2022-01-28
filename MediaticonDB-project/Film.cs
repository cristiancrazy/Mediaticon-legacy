using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB
{
    internal class Film
    {
        public string BigImage;
        public string Image;
        public string Title;
        public string Description;
        public int Duration;
        public DateTime Year;
        public List<string> Genres;
        public List<string> Actors;

        public Bitmap cover;

        public Film()
        {

        }
    }
}
