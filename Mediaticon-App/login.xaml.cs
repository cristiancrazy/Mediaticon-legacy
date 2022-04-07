using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using MediaticonWorker;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Mediaticon
{
	/// <summary>
	/// load account info, or make new account
	/// start download films list --> async, another tasks
	/// start load films list ------^
	/// </summary>
	public partial class login : Window
	{
		public login()
		{
			InitializeComponent();
			DBHelper.UpdateDB();
		}

		//GOT FOCUS ON TEXTBOX
		private void nameTxt_GotFocus(object sender, RoutedEventArgs e)
		{
			nameTxt.SelectAll();
		}

		private void nameTxt_IsMouseCaptureWithinChanged(object sender, DependencyPropertyChangedEventArgs e)
		{
			nameTxt.SelectAll();
		}

		//CLICK ON LOGIN BTN
		private void enterBtn_Click(object sender, RoutedEventArgs e)
		{
			if(!String.IsNullOrEmpty(nameTxt.Text))
			{
				if (LoginHelper.LoadUser(nameTxt.Text))
				{
					App.openWindow<MainWindow, login>(true);
				}
			}
		}
	}

  
}
