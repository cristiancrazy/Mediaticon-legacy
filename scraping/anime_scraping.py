import requests, re, bs4, time, sys
from datetime import date
from dataclasses import dataclass, field

@dataclass
class Anime:
    image : str = ''
    name : str = ''
    trama : str = ''
    episodes : str = ''
    anno : str = ''
    tags : list[str] = field(default_factory=list)
    actors_list : list[str] = field(default_factory=list)

    def __str__(self):
        return f'{self.image};{self.name};{self.trama};{self.episodes};{self.anno};{self.tags};{self.actors_list}'

def myanilist(session, _from_year, _to_year, path):
    #letters = [ '.', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
    #_from_year = 1970
    #_to_year = 0

    #CURRENT YEAR
    today_date = date.today()
    current_year = int(today_date.year)

    page = 0

    while True:
        #GET HTML
        try:
            response = session.get(f'https://myanimelist.net/anime.php?cat=0&q=&type=0&score=0&status=0&p=0&r=0&sm=0&sd=0&sy={_from_year}&em=0&ed=0&ey={_to_year}&c%5B0%5D=d&o=2&w=2&show={page}')
            response.raise_for_status() # give an error if the page returns an error code
        except:
            break

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
                    response2 = session.get(link)
                    response2.raise_for_status()

                    #PREPARE FOR PARSING
                    soup = bs4.BeautifulSoup(response2.text, 'html.parser')

                    lateral_bar = soup.find('td', {'class' : 'borderClass'})
                    
                    #IMAGE
                    if(image := lateral_bar.find('img')):
                        anime.image = image['data-src']

                    menu_info = soup.find_all('div', {'class' : 'spaceit_pad'})
                    for info in menu_info:
                        #NUMBER OF APISODES
                        if('Episodes' in info.text):
                            anime.episodes = info.text.split(':')[1].strip()
                        #YEAR
                        elif('Aired' in info.text):
                            if('to' in info.text):
                                anime.anno = info.text.split(', ')[1].split(' to')[0].strip()
                            else:
                                try:
                                    anime.anno = info.text.split(', ')[1].strip()
                                except:
                                    try:
                                        anime.anno = info.text.split(':')[1].strip()
                                    except:
                                        anime.anno = [s for s in info.text.split('to ')[1] if s.isdigit()][0]
                        #TAGS
                        elif('Genres' in info.text or 'Genre' in info.text):
                            for genre in info.find_all('a'):
                                anime.tags.append(genre.text)
                    
                    #TRAMA
                    anime.trama = soup.find('p', {'itemprop' : 'description'}).text.strip().replace(';', '§').replace('\n', ' ')

                    #ACTORS
                    if(actors_list := soup.find('div', {'class' : 'detail-characters-list'})):
                        actors_list = actors_list.find_all('h3', {'class' : 'h3_characters_voice_actors'})

                        for actor in actors_list:
                            anime.actors_list.append(actor.text.strip())

                    with open(path, 'a', encoding="utf-8") as f:
                        f.write(str(anime))
                        f.write('\n')
            
            #print(anime)
        page += 50

        time.sleep(.6)
        # if(page % 250 == 0):
        #     time.sleep(40)

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
