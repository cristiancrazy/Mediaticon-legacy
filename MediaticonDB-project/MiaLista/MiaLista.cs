using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MediaticonDB.MiaLista
{
    class MiaLista
    {
        public static List<MyFilm> MyFilmList;

        public static bool Append(MyFilm Input) 
        {
            try
            {
                MyFilmList.Append(Input);
                return true;
            }
            catch 
            {
                return false;
            }
        }
        public static bool Remove(int index) 
        {
            try
            {
                MyFilmList.RemoveAt(index);
                return true;
            }
            catch
            {
                return false;
            }
        }
        public static bool Return(int index,out MyFilm Output) 
        {
            Output = null;
            try
            {
                Output = MyFilmList[index];
                return true;
            }
            catch
            {
                return false;
            }
            
        }

        class Serialization
        {
            public static bool Serialize()
            {

            }
            public static bool Deserialize()
            {

            }
        }
    }
}
