using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB.MiaLista
{/// <summary>
/// This class is used to my list film
/// </summary>
/// <example>
/// parameters
/// the film, the index to point to array of film status
/// </example>
    public class MyFilm : Film
    {
        //dichiarazioni variabili pubbliche
        public Film film;
        public byte box;

        //costruttore
        public MyFilm(Film film,byte box) : 
            base(film.BigImage,film.Image,
            film.Title,film.Description,
            film.Duration,film.Year,
            film.Genres,film.Actors)
        {
            this.film = film;
            this.box = box;
        }
        
    }
}
