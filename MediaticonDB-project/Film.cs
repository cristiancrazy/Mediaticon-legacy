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


        public Bitmap Cover; //nullable


        public Film(string BigImage, string Image, string Title, string Description,
                    int Duration, DateTime Year, List<string> Genres, List<string> Actors)
        {
            this.BigImage = BigImage;
            this.Image = Image;
            this.Title = Title;
            this.Description = Description;
            this.Duration = Duration;
            this.Year = new DateTime(Year.Year, 1, 1);
            this.Genres = Genres;
            this.Actors = Actors;
        }

        //overload where Year is a string
        public Film(string BigImage, string Image, string Title, string Description,
                    int Duration, string Year, List<string> Genres, List<string> Actors)
        {
            this.BigImage = BigImage;
            this.Image = Image;
            this.Title = Title;
            this.Description = Description;
            this.Duration = Duration;
            this.Year = new DateTime(Int32.Parse(Year), 1, 1);
            this.Genres = Genres;
            this.Actors = Actors;
        }

        //overload where Year && genres && actors are a string
        public Film(string BigImage, string Image, string Title, string Description,
                    int Duration, string Year, string Genres, string Actors)
        {
            this.BigImage = BigImage;
            this.Image = Image;
            this.Title = Title;
            this.Description = Description.Replace("§", ";");
            this.Duration = Duration;
            this.Year = new DateTime(Int32.Parse(Year), 1, 1);
            this.Genres = Genres.Replace("\"", "").Replace("[", "").Replace("]", "").Replace("\'", "").Split(", ").ToList<string>();
            this.Actors = Actors.Replace("\"", "").Replace("[", "").Replace("]", "").Replace("\'", "").Split(", ").ToList<string>();
        }

        
    }

    public static class StringExt
    {
        public static string ListToString(this List<string> input)
        {
            string output = "";
            foreach (var item in input)
            {
                output += item + ", ";
            }

            return output;
        }
    }
}
