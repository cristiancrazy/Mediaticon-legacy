using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using System.Runtime.Serialization;

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
                using (FileStream fs = new FileStream(EnviromentVar.UsersPath.UserMyListFile(EnviromentVar.UsersPath.UserName), FileMode.Create))
                {
                    BinaryFormatter formattatore = new BinaryFormatter();
                    try
                    {
                        formattatore.Serialize(fs, MyFilmList);
                    }
                    catch (SerializationException e)
                    {
                        return false;
                    }
                    return true;
                }
            }
            public static bool Deserialize()
            {
                using (FileStream fs = new FileStream(EnviromentVar.UsersPath.UserMyListFile(EnviromentVar.UsersPath.UserName), FileMode.Create))
                {
                    BinaryFormatter formattatore = new BinaryFormatter();
                    try
                    {
                        MyFilmList= (List<MyFilm>)formattatore.Deserialize(fs);
                    }
                    catch (SerializationException e)
                    {
                        return false;
                    }
                    return true;
                }

            }
        }
    }
}
