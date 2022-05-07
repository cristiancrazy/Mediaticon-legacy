using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using Newtonsoft.Json;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media;
using System.Windows.Interop;
using System.Windows;
using System.Windows.Media.Imaging;

namespace MediaticonDB
{
    /// <summary>
    /// class level 0
    /// </summary>
    public class Film
    {
        public string BigImage;
        public string Image;
        public string Title;
        public string Description;
        public int Duration; //minutes
        public DateTime Year;
        public List<string> Genres;
        public List<string> Actors;


        public Bitmap Cover; //nullable
        public ImageSource CoverSource;


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
            this.Cover = LoadCover();
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
            this.Cover = LoadCover();

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
            this.Cover = LoadCover();
        }

        [JsonConstructor]
        public Film(string BigImage, string Image, string Title, string Description,
                    int Duration, int Year, List<string> Genres, List<string> Actors)
        {
            this.BigImage = BigImage;
            this.Image = Image;
            this.Title = Title;
            this.Description = Description;
            this.Duration = Duration;
            this.Year = new DateTime(Year, 1, 1);
            this.Genres = Genres;
            this.Actors = Actors;
            this.Cover = LoadCover();
        }

        public Bitmap LoadCover()
        {
            //automatic load cover when Film() construction is called
            Bitmap cover;
            if (Connection.DownloadImage(this.Image, out cover)) { }
            else
            {
                try
                {
                    Connection.openImage(EnviromentVar.ImagesVar.defaultCoverPath, out cover);
                    
                }
                catch
                {
                    cover = Connection.generateBitmap(420, 600, System.Drawing.Color.Transparent);
                }
            }
            var bitmapSource = Imaging.CreateBitmapSourceFromHBitmap(cover.GetHbitmap(),
                                                                                IntPtr.Zero,
                                                                                Int32Rect.Empty,
                                                                                BitmapSizeOptions.FromEmptyOptions()
                );
            cover.Dispose();
            this.CoverSource = new ImageBrush(bitmapSource).ImageSource;
            return cover;
        }

        public Film RetToSQL()
        {
            Film output = new Film(this.BigImage, this.Image, this.Title, this.Description,
                                    this.Duration, this.Year, this.Genres, this.Actors);

            //in the sql replace ' with Ø
            output.BigImage = this.BigImage.Replace('\'', 'Ø');
            output.Image = this.Image.Replace('\'', 'Ø');
            output.Title = this.Title.Replace('\'', 'Ø');
            output.Description = this.Description.Replace('\'', 'Ø');
            output.Duration = this.Duration;
            output.Year = this.Year;
            output.Genres = this.Genres.Select(a => a.Replace('\'','Ø')).ToList();
            output.Actors = this.Actors.Select(a => a.Replace('\'','Ø')).ToList();

            return output;
        }

        public Film RetFromSQL()
        {
            Film output = new Film(this.BigImage, this.Image, this.Title, this.Description,
                                    this.Duration, this.Year, this.Genres, this.Actors);

            //in the sql replace Ø with '
            output.BigImage = this.BigImage.Replace('Ø', '\'');
            output.Image = this.Image.Replace('Ø', '\'');
            output.Title = this.Title.Replace('Ø', '\'');
            output.Description = this.Description.Replace('Ø', '\'');
            output.Duration = this.Duration;
            output.Year = this.Year;
            output.Genres = this.Genres.Select(a => a.Replace('Ø', '\'')).ToList();
            output.Actors = this.Actors.Select(a => a.Replace('Ø', '\'')).ToList();

            return output;
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
