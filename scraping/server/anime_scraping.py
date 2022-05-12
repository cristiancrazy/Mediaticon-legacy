import requests, bs4, time, sys, json, re
from datetime import date
from dataclasses import dataclass, field

############################################################

#image : str #Image
#name : str #Title
#trama : str #Description
#episodes : str #Episodes
#anno : int #Year
#tags : list[str] #Genres
#actors_list : list[str] #Actors
#-----------------------------------------------------#
#big_image : str #BigImage
#image : str #Image
#name : str #Title
#trama : str #Description
#durata : int #Duration
#anno : int #Year
#tags : list[str] #Genres
#actors_list : list[str] #Actors
#-----------------------------------------------------#
#'BigImage' : big_image,
#'Image' : image,
#'Title' : name,
#'Description' : trama,
#'Duration' : durata,
#'Year' : anno,
#'Genres' : tags,
#'Actors' : actors_list

############################################################

@dataclass
class Anime:
    BigImage : str = ''
    Image : str = ''
    Title : str = ''
    Description : str = ''
    Duration : int = ''
    Year : str = ''
    Genres : list[str] = field(default_factory=list)
    Actors : list[str] = field(default_factory=list)

    def __str__(self):
        return f'{self.image};{self.name};{self.trama};{self.episodes};{self.anno};{self.tags};{self.actors_list}'

def myanilist(session, _from_year, _to_year, path):
    page = 0

    while True:
        #GET HTML
        try:
            response = session.get(f'https://myanimelist.net/anime.php?cat=anime&q=&type=0&score=0&status=0&p=0&r=0&sm=0&sd=0&sy={_from_year}&em=0&ed=0&ey={_to_year+1}&c[0]=d&show={page}')
            response.raise_for_status() # give an error if the page returns an error code
        except:
            print("<ERROR>\nmain link dosen't work")
            break

        #GET NEEDED HTML
        animes = bs4.BeautifulSoup(response.text, 'html.parser').find_all('tr')

        for anime_list in animes:
            anime : Anime = Anime()
            anime.Year = 2020
            
            if(header := anime_list.find('div', {'class' : 'title'})):
                if(title := header.find('strong')):
                    #TITLE
                    anime.Title = title.text
                
                #enter anime page
                if(link := header.find('a')['href']):
                    #GET HTML 2° LINK
                    response2 = session.get(link)
                    response2.raise_for_status()

                    #PREPARE FOR PARSING
                    soup = bs4.BeautifulSoup(response2.text, 'html.parser')

                    lateral_bar = soup.find('td', {'class' : 'borderClass'})
                    
                    #IMAGE
                    if(image := lateral_bar.find('img')):
                        anime.Image = image['data-src']

                    menu_info = soup.find_all('div', {'class' : 'spaceit_pad'})
                    for info in menu_info:
                        #NUMBER OF APISODES
                        if('Episodes' in info.text):
                            try:
                                anime.Duration = int(info.text.split(':')[1].strip())
                            except:
                                anime.Duration = -1
                        #TAGS
                        elif('Genres' in info.text or 'Genre' in info.text):
                            for genre in info.find_all('a'):
                                anime.Genres.append(genre.text)
                    
                    #TRAMA
                    anime.Description = soup.find('p', {'itemprop' : 'description'}).text.strip().replace(';', '§').replace('\n', ' ')

                    #ACTORS
                    if(actors_list := soup.find('div', {'class' : 'detail-characters-list'})):
                        actors_list = actors_list.find_all('h3', {'class' : 'h3_characters_voice_actors'})

                        for actor in actors_list:
                            anime.Actors.append(actor.text.strip())

                    with open(path, 'a', encoding="utf-8") as f:
                        f.write(json.dumps(anime.__dict__))
                        f.write('\n')
            
            #print(anime)
        page += 50

        # time.sleep(.6)
        if(page % 50 == 0):
            time.sleep(20)

if __name__ == "__main__":
    year : int= 0
    path : str = ''

    if(len(sys.argv) > 5 or len(sys.argv) < 5):
        ch = 'many' if len(sys.argv) > 5 else 'few'
        print(f'too {ch} arguments, example: scr.py -y 2021 -p scrapers/data.csv')
        sys.exit(1)
    else:
        for index, arg in enumerate(sys.argv):
            try:
                if('-y' in arg):
                    year = int(sys.argv[index+1])
                elif('-p' in arg):
                    path = sys.argv[index+1]
            except:
                print('error in one of the arguments')
                sys.exit(1)

    s = requests.Session()
    myanilist(s, year, year, path)
    sys.exit(0)
