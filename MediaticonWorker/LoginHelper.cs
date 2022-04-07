using System;
using MediaticonDB;
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
					readUser(username);
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

		private static void readUser(string username)
		{
			EnviromentVar.UsersPath.UserName = username;
			EnviromentVar.UsersPath.Avatar = new Bitmap(Bitmap.FromFile(EnviromentVar.UsersPath.UserAvatarFile(username)));
		}

		private static bool ExistUser(string username)
		{
			return Directory.Exists(EnviromentVar.UsersPath.UserPath(username));
		}

		private static bool makeUser(string username)
		{
			if(NewMessageBox.Show("Creare un nuovo utente?"))
            {
				MakeDirs.SpecificUserFolders(username);
				File.Copy(EnviromentVar.ImagesVar.defaultImgPath, EnviromentVar.UsersPath.UserPath(username), true);
				return true;
			}
			return false;
		}
	}
}
