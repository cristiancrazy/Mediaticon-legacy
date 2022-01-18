import requests, re, bs4
from datetime import date

def to_list(*args):
    return list(args)

def myTVseries():
    #YEARS RANGE
    _from_year = 1970
    _to_year = 1972

    #GLOBAL VARIABLES
    page = 1
    tipo = 'TV series'

    #CURRENT YEAR
    today_date = date.today()
    current_year = int(today_date.year)

    while _from_year != _to_year:
        while True:
            #RESET VARIABLES
            page_is_valid = 0

            #GET HTML
            response = requests.get(f'https://www.mymovies.it/serietv/{_from_year}/?p={page}')
            response.raise_for_status() # give an error if the page returns an error code

            #PREPARE FOR PARSING
            soup = bs4.BeautifulSoup(response.text, 'html.parser')

            #GET NEEDED HTML
            films = soup.find('div', {'class' : 'mm-col sm-7 md-6 lg-6'})
            films = films.findChildren()

            #INFORMATION EXSTRACTED
            image : str = ''
            big_image: str = ''
            name : str = ''
            trama : str = ''
            anno : int
            tags : list[str] = []
            durata : str = ''
            actors_list:list[str] = []

            #CICLE IN PAGE
            for element in films:
                #RESET VARIABLES
                link2: str = ''

                if element.has_attr('class'):
                    #IMAGE
                    if 'poster-schedina-div' in element.attrs['class'] and 'mm-left' in element.attrs['class']:
                        image = element.find('amp-img')['src']

                    elif 'video-player' in element.attrs['class']:
                        _big_image = element.find('img')

                        if _big_image:
                            big_image = _big_image['src']
                    
                    elif 'mm-white' in element.attrs['class'] and 'mm-padding-8' in element.attrs['class']:
                        page_is_valid = 1
                        #RESET OF VARIABLES
                        name = ''
                        trama = ''
                        tags = []
                        anno = 0
                        durata = ''

                        for info in element.findChildren():
                            if info.has_attr('class'):
                                #NAME
                                if 'schedine-titolo' in info.attrs['class']: #name
                                    link2 = info.find('a')['href']
                                    name = info.text.strip('\n')

                                if 'mm-line-height-130' in info.attrs['class'] and 'schedine-lancio' in info.attrs['class']:
                                    el_tags = info.select('a')
                                    #DURATA (if exist)
                                    try:
                                        durata = info.find('strong').text
                                    except:
                                        durata = ''
                                    #TAGS & YEAR
                                    for tag in el_tags:
                                        if tag.text.isnumeric():
                                            anno = int(tag.text)
                                        else:
                                            tags.append(tag.text)
                        #############################################################################################################

                        try:
                            response2 = requests.get(link2)
                            response2.raise_for_status() # give an error if the page returns an error code
                        except:
                            continue

                        soup2 = bs4.BeautifulSoup(response2.text, 'html.parser')

                        actors = soup2.find('p', {'class' : 'sottotitolo_rec mm-hide-xs mm-show-sm'})
                        actors = actors.find_all('a')

                        for actor in actors:
                            if not actor.has_attr('class'):
                                if not ' ' in actor.text:
                                    break
                                
                                actors_list.append(actor.text.encode('ascii', 'ignore').decode())
                        
                        trama = soup2.find('p', {'class' : 'corpo'}).text.encode('ascii', 'ignore').decode().strip().replace('\n', ' ').replace(';', '§')
                        #############################################################################################################
                        with open('data2.csv', 'a') as f:
                            f.write(';'.join(str(i) for i in to_list(big_image, image, name, trama, durata, anno, tags, tipo, actors_list)))
                            f.write('\n')
                        image = ''
                        big_image = ''
            
            #find end
            if not page_is_valid:
                page = 1
                break

            page += 1
        _from_year += 1

if __name__ == "__main__":
    myTVseries()