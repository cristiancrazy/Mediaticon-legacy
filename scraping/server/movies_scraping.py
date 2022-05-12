import requests, bs4, sys, json, ftfy

############################################################

#big_image : str #BigImage
#image : str #Image
#name : str #Title
#trama : str #Description
#durata : int #Duration
#anno : int #Year
#tags : list[str] #Genres
#actors_list : list[str] #Actors

############################################################

class MainPageError(Exception):
    pass

class LinkError(Exception):
    pass

class ActorsError(Exception):
    pass

class PlotError(Exception):
    pass

############################################################

def moviesPageScraping(link: str):
    actors_list : list[str] = []
    trama : str = ''

    try:
        response = requests.get(link)
        response.raise_for_status() # give an error if the page returns an error code
    except:
        raise LinkError
    #-----------------------------------------------------------------------------------------------------#
    soup = bs4.BeautifulSoup(response.text, 'html.parser')
    #-----------------------------------------------------------------------------------------------------#
    try:
        actors = soup.find('p', {'class' : 'sottotitolo_rec mm-hide-xs mm-show-sm'})
        actors = actors.find_all('a')

        for actor in actors:
            if not actor.has_attr('class'):
                if not ' ' in actor.text:
                    break
                actors_list.append(ftfy.fix_text(actor.text))#.encode('ascii', 'ignore').decode())
    except:
        raise ActorsError
    #-----------------------------------------------------------------------------------------------------#
    try:
        trama = ftfy.fix_text(soup.find('p', {'class' : 'corpo'}).get_text(separator=" ").strip().replace('\r', '').replace('\n', ' '))
    except:
        raise PlotError
    
    return actors_list, trama

def mymovies(_from_year, _to_year, path):
    #GLOBAL VARIABLES
    page = 1

    while True:
        #RESET VARIABLES
        page_is_valid = 0

        #GET HTML
        response = requests.get(f'https://www.mymovies.it/film/{_from_year}/?orderby=release&p={page}')
        response.raise_for_status() # give an error if the page returns an error code

        #GET ALL FILMS
        films = bs4.BeautifulSoup(response.text, 'html.parser').find('div', {'class' : 'mm-col sm-7 md-6 lg-6'}).findChildren()

        #INFORMATION EXTRACTED
        image : str = ''
        big_image: str = ''
        name : str = ''
        trama : str = ''
        anno : int
        tags : list[str] = []
        durata : int = 0
        actors_list : list[str] = []

        for element in films:
            #RESET VARIABLES
            link2: str = ''

            if element.has_attr('class'):
                #IMAGE
                if 'poster-schedina-div' in element.attrs['class'] and 'mm-left' in element.attrs['class']:
                    image = element.find('amp-img')['src']

                elif 'video-player' in element.attrs['class']:
                    if _big_image := element.find('img'): # if it's found
                        big_image = _big_image['src']
                
                elif 'mm-white' in element.attrs['class'] and 'mm-padding-8' in element.attrs['class']:
                    page_is_valid = 1 # page is valid

                    #RESET OF VARIABLES
                    name = ''
                    trama = ''
                    tags = []
                    anno = 0
                    actors_list = []

                    for info in element.findChildren():
                        if info.has_attr('class'):
                            #NAME
                            if 'schedine-titolo' in info.attrs['class']: #name
                                link2 = info.find('a')['href']
                                name = ftfy.fix_text(info.text.strip('\n'))

                            if 'mm-line-height-130' in info.attrs['class'] and 'schedine-lancio' in info.attrs['class']:
                                el_tags = info.select('a')
                                #DURATA (if exist)
                                try:
                                    durata = int(info.find('strong').text.split(' ')[1])
                                except:
                                    durata = 0
                                #TAGS & YEAR
                                for tag in el_tags:
                                    if tag.text.isnumeric():
                                        anno = int(tag.text)
                                    else:
                                        tags.append(tag.text)
                    #############################################################################################################
                    #if an error occurs the film is skipped
                    try:
                        actors_list, trama = moviesPageScraping(link2)
                    except LinkError:
                        print(f"<ERROR>\npage: {page}\nName: {name}\ntypeOfError: LinkError")
                        continue
                    except ActorsError:
                        print(f"<ERROR>\npage: {page}\nName: {name}\ntypeOfError: ActorsError")
                        continue
                    except PlotError:
                        print(f"<ERROR>\npage: {page}\nName: {name}\ntypeOfError: PlotError")
                        continue
                    
                    #############################################################################################################
                    with open(path, 'a') as f:
                        _dict  = {
                            'BigImage' : big_image,
                            'Image' : image,
                            'Title' : name,
                            'Description' : trama,
                            'Duration' : durata,
                            'Year' : anno,
                            'Genres' : tags,
                            'Actors' : actors_list
                        }
                        f.write(json.dumps(_dict) + '\n')
                    image = ''
                    big_image = ''
        
        #find end
        if not page_is_valid:
            page = 1
            break

        page += 1

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

    mymovies(year, year, path)
    sys.exit(0)
