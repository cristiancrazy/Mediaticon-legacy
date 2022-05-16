using System;
using MediaticonDB;
using NewMessageBox;
using System.IO;
using System.Drawing;

namespace MediaticonWorker
{
	/// <summary>
	/// help the login process
	/// </summary>
	public class LoginHelper
	{
		public static bool LoadUser(string username)
		{
			try
			{
				if (ExistUser(username))
				{
					if(readUser(username))
                    {
						return true;
                    }
                    else
                    {
						return false;
                    }
				}
				else
				{
					return makeUser(username);
				}
				return true;
			}
			catch
			{
				return false;
			}
		}

		private static bool readUser(string username)
		{
			try
			{
				EnviromentVar.UsersPath.UserName = username;
				EnviromentVar.UsersPath.Avatar = new Bitmap(Bitmap.FromFile(EnviromentVar.UsersPath.UserAvatarFile(username)));
			}
			catch
            {
				if(NMSG.Show("Errore nella lettura dei file, eliminare l'utente?", NMSGtype.YesNo))
                {
                    try
                    {
						if (Directory.Exists(EnviromentVar.UsersPath.UserPath(username)))
							Directory.Delete(EnviromentVar.UsersPath.UserPath(username), true);
						return false;
					}
                    catch
                    {
						return false;
                    }
                }
				return false;
            }
			return true;
		}

		private static bool ExistUser(string username)
		{
			return Directory.Exists(EnviromentVar.UsersPath.UserPath(username));
		}

		private static bool makeUser(string username)
		{
			if (NMSG.Show("Creare un nuovo utente?", NMSGtype.YesNo))
			{
				try
				{
					MakeDirs.SpecificUserFolders(username);
					File.Copy(EnviromentVar.ImagesVar.defaultAvatarImage, EnviromentVar.UsersPath.UserAvatarFile(username), true);
				}
				catch
				{
					try
					{
						if (Directory.Exists(EnviromentVar.UsersPath.UserPath(username)))
							Directory.Delete(EnviromentVar.UsersPath.UserPath(username), true);
					}
					catch { }
					NMSG.Show("Impossibile creare un nuovo utente, riprovare", NMSGtype.Ok);
					return false;
				}
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
