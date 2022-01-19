import requests, re, bs4
from dataclasses import dataclass, field

'''
scraping\Scripts\activate
big_image => X
image
name
trama
durata => x
anno
tags
tipo
actors_list
'''

@dataclass
class Anime:
    image : str = ''
    name : str = ''
    trama : str = ''
    episodes : str = ''
    anno : str = ''
    tags : list[str] = field(default_factory=list)
    tipo : str = 'Anime'
    actors_list : list[str] = field(default_factory=list)

    def __str__(self):
        return f'{self.tipo}\n{self.name} | {self.anno} -> {self.episodes}\n* {self.image}\n\n{self.trama}\n\n{self.tags}\n{self.actors_list}'

def myanilist():
    letters = [ '.', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
    _from_index = 0
    _to_index = 1

    page = 0
    tipo = 'Anime'

    for letter in letters[_from_index:_to_index]:
        #GET HTML
        response = requests.get(f'https://myanimelist.net/anime.php?letter={letter}&show={page}')
        response.raise_for_status() # give an error if the page returns an error code

        #PREPARE FOR PARSING
        soup = bs4.BeautifulSoup(response.text, 'html.parser')

        #GET NEEDED HTML
        animes = soup.find_all('tr')

        for anime_list in animes:
            anime : Anime = Anime()
            # if(image := anime_list.find('div', {'class' : 'picSurround'})):
            #     if(image := image.find('img')):
            #         anime.image = image['data-src']
            
            if(header := anime_list.find('div', {'class' : 'title'})):
                if(title := header.find('strong')):
                    #TITLE
                    anime.name = title.text
                
                #enter anime page
                if(link := header.find('a')['href']):
                    #GET HTML 2° LINK
                    response2 = requests.get(link)
                    response2.raise_for_status()

                    #PREPARE FOR PARSING
                    soup = bs4.BeautifulSoup(response2.text, 'html.parser')

                    lateral_bar = soup.find('td', {'class' : 'borderClass'})
                    
                    #IMAGE
                    anime.image = lateral_bar.find('img')['data-src']

                    menu_info = soup.find_all('div', {'class' : 'spaceit_pad'})
                    for info in menu_info:
                        #NUMBER OF APISODES
                        if 'Episodes' in info.text:
                            anime.episodes = info.text.split(':')[1].strip()
                        #YEAR
                        elif 'Aired' in info.text:
                            if 'to' in info.text:
                                anime.anno = info.text.split(', ')[1].split(' to')[0].strip()
                            else:
                                try:
                                    anime.anno = info.text.split(', ')[1].strip()
                                except:
                                    anime.anno = info.text.split(':')[1].strip()
                        #TAGS
                        elif 'Genres' in info.text or 'Genre' in info.text:
                            for genre in info.find_all('a'):
                                anime.tags.append(genre.text)
                    
                    #TRAMA
                    anime.trama = soup.find('p', {'itemprop' : 'description'}).text.strip()

                    #ACTORS
                    

                    print(anime)
            
            #print(anime)

if __name__ == "__main__":
    myanilist()
