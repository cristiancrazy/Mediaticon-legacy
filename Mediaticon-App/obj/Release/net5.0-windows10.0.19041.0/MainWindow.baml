   M S B A M L   `   `   `  ���� E  AMediaticon, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null'clr-namespace:Mediaticon
Mediaticon  c _PresentationFramework.Royale, Version=5.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35iLclr-namespace:Microsoft.Windows.Themes;assembly=PresentationFramework.RoyaleMicrosoft.Windows.Themes     Mediaticon.MainWindow   W SPresentationCore, Version=5.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35\ XPresentationFramework, Version=5.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35e aSystem.Windows.Controls.Ribbon, Version=5.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089R NWindowsBase, Version=5.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35^ ZWindowsFormsIntegration, Version=5.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35H 9http://schemas.microsoft.com/winfx/2006/xaml/presentation      R NSystem.Xaml, Version=5.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     @d2http://schemas.microsoft.com/expression/blend/2008     Jmc;http://schemas.openxmlformats.org/markup-compatibility/2006     V RXamlAnimatedGif, Version=2.0.0.0, Culture=neutral, PublicKeyToken=20a987d8023d9690<gif2https://github.com/XamlAnimatedGif/XamlAnimatedGif  $localclr-namespace:Mediaticon  Ishell9http://schemas.microsoft.com/netfx/2009/xaml/presentation    oMicrosoft_Windows_ThemesLclr-namespace:Microsoft.Windows.Themes;assembly=PresentationFramework.Royale    -     �Name$  
mainWindow�� � Title$ 
Mediaticon��$	��470��$	��850��$	��850��$	��470�� � 	Resources  �� 
TargetType �� Property �� Value �� 
TargetType �� CornerRadius �� Triggers	 P� Property
 P� Value��%V  '��       '�� �     '�� �     �� " ��.���  � �� �� " ��.R��� #��z��$ 10��._��� $
��Left=�$��Center=� P� 	 �@J�IsMouseOver$

 True��. ���  � �� �	#FF1F1F23�� " ��.���  � �� �	#FF191919��  � �� �	#FF191919�� " ��.���  � ��$ 0,5,0,5q���  � �� �	#FFBCBCBE.��� -    �Name$ gridBack�����#0e0e10.+��� -    ��Name$ HomeBtn��$
��Left=�$��63��$��37,4,0,0q� 
  ImgBtn#��[  $	��Top=�$��60�� J� RenderTransformOrigin$ 0.5,0.5&����	#00707070����  �� ImageSource$ /mediaticon.png���� -    ��Name$ accountCombo��$��16��$��0,30,0,0q�  NormalCombo#��[ $	��Top=�$	��144��$��Right=�.@��� .��La mia lista�� .��Informazioni�� .��Esci�� -    ��Name$ accountBord��$��1q�$��Right=�$	��148��$��65��$	��Top=�$��	0,-1,-1,0q�$ 0,0,0,10��._�� -   $ accountGrid��.+��� -    ��Name$ 
accountLbl��$��Account��$
��Left=�$��63,0,0,0q�$��Center=�$��73��$
��Bold"�$��16(��� -    ��Name$ 
accountImg��$
��Left=�$��50��$��10,4,0,0q�$	��Top=�$��50���� -	   $ typeBtnBord��$��1q�$��186,-2,240,0q�$��75��$	��Top=�$ 	0,0,10,10��$	��400��._�� -
   $ typeBtnGrid��$��-1,-1,-1,-1q�.+��� -   $ filmBtn��  MenuBtn#��[ $
��Film��$
��Left=�$��42��$��Center=�$��73��$��15,0,0,0q��� -   $ serieBtn��#��[ $��Serie Tv��$��Center=�$��42��$��Center=�$��76���� -   $ animeBtn��#��[ $��Anime��$��Right=�$��42��$��Center=�$��68��$��
319,0,15,0q��� -   $ programmiBtn��#��[ $
��Show��$
��Left=�$��42��$��Center=�$��58��$��	319,0,0,0q�$y�Hidden=��� ���#5c16c5$��0,0,5,5q�$��18,93,163,10q�$ 0,0,10,0�����#0e0e10 �� Padding$ 0,0,0,0q�._��� -    ��Name$ listaLB��$��1,3,2,2q�
������ $
��Bold"����	#00000000$��Hidden=�$��Hidden=����� ���� ���� .R���  �� Orientation$ 
Horizontal=�$��90��$	��600��.+��� $ ListPic��$
��Fill=�$��50��$��70��$��	10,0,10,0q����� *	RetImgSrc+�� $ Vertical=�$	��458��.+��� $ 	ListTitle��$
��Bold"�$��20(����	#FF939393���� *Title+��  ��Name$ 	ListDescr��$��53�����	#FFBCBCBE �� Padding$ 5,0,5,0q����� *Description+�� -   $ 
filterBord��$��1q�$��
0,113,0,38q�$��Right=�$	��147��$ 	10,0,0,10��._�� -   $ 
filterGrid��.+��� ���#5c16c5$��3,0,0,3q�$
��Left=�$��39��$��	10,10,0,0q�$	��Top=�$	��136��$ 0,0,0,10�����	#FF464649$ 0,0,0,0q�._��� -    ��Name$ 	searchTxt�� �� TextWrapping$
 Wrap=�$��Search��$��7,1,-1,8q�$��0,0,0,0q�$��Bottom=�
������ 
�������$
��Bold"�$��0,0,0,0q�$��14(��� -   $ 	filterCBL��$��
10,62,0,10q�$��Hidden=�$��Hidden=����	#FF1F1F23���	#00ABADB3���	#FF7272B2$
��Bold"�.@��� -    ��Name$ myItemsControl������ .R��� ���� *	Content+ d� Checked �� *	IsChecked+� -   $ loadingShadow��$��0q� J� Opacity$ 0Q�$y�Hidden=�.+��� -   $ 
loadingGif��$��250,80,250,80q��� $��0q����#0e0e10$ .6Q�