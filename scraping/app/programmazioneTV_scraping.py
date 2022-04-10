import requests, bs4, sys, re, json
from concurrent.futures import ThreadPoolExecutor
from datetime import date, datetime
from itertools import repeat
from dataclasses import dataclass

@dataclass
class Channel:
    name : str = ''
    link : str = ''

@dataclass
class Date:
    day : int = 0
    month : int = 0
    year : int = 0

#SCRAPES TROUGH THE TV PROGRAMMATION
def channel_src(channel, session, film_name):
    with open(f'./json/{channel.name}.json', 'w') as f:
        for i in range(7):
            response = session.get(f'{channel.link}{Date.day + i:02d}-{Date.month:02d}-{Date.year:04d}')
            soup = bs4.BeautifulSoup(response.text, 'html.parser')

            all_today_prg = soup.find_all('a', {'class' : 'program'})

            _dict = {
                'date' : '',
                'start_time' : '',
                'end_time' : ''
            }

            for index, today_prg in enumerate(all_today_prg[:-1]):
                if film_name in today_prg.find('div', {'class' : 'program-title'}).text.lower():
                    #Date
                    _dict['date'] = f'{str(Date.year)[-2:]}-{Date.month}-{Date.day + i}'
                    #f.write(f'{str(Date.year)[-2:]}-{Date.month}-{Date.day + i}')
                    
                    #Start Hour
                    if (_hour := today_prg.find('div', {'class' : 'hour'}).text.replace(':', '-')) == 'IN ONDA':
                        _dict['start_time'] = f'{datetime.now().strftime("%H-%M")}'
                        #f.write(f';{datetime.now().strftime("%H-%M")}')
                    else:
                        _dict['start_time'] = f'{_hour}'
                        #f.write(f';{_hour}')
                    
                    #end Hour
                    if (_hour := all_today_prg[index + 1].find('div', {'class' : 'hour'}).text.replace(':', '-')) == 'IN ONDA':
                        _dict['end_time'] = f'{datetime.now().strftime("%H-%M")}'
                        #f.write(f';{datetime.now().strftime("%H-%M")}')
                    else:
                        _dict['end_time'] = f'{_hour}'
                        #f.write(f';{_hour}')
                    
                    f.write(json.dumps(_dict) + '\n')

#SEARCH ALL THE CHANNELS
def prgTV(session, film_name):
    channels : list(str) = []

    try:
        response = session.get('https://guidatv.quotidiano.net/')
        response.raise_for_status() # give an error if the page returns an error code
    except:
        sys.exit(1)

    #PREPARE FOR PARSING
    soup = bs4.BeautifulSoup(response.text, 'html.parser')

    #CREATES CHANNEL LINKS
    raw_channels = soup.find_all('section', {'class' : 'channel channel-thumbnail'})

    for channel in raw_channels:
        ch_path = channel.find('a').attrs['href']

        link : str = f'https://guidatv.quotidiano.net{ch_path}'
        name : str = re.sub('[/_]', '', ch_path)

        channels.append(Channel(name, link))
    
    with ThreadPoolExecutor() as executor:
        for result in executor.map(channel_src, channels, repeat(session), repeat(film_name)):
            if result != None:
                print(result)
                sys.exit(1)

if __name__ == "__main__":
    name : str = ''

    if(len(sys.argv) > 3 or len(sys.argv) < 3):
        ch = 'many' if len(sys.argv) > 3 else 'few'
        print(f'too {ch} arguments, example: scr.py -n film_name')
        sys.exit(1)
    else:
        for index, arg in enumerate(sys.argv):
            try:
                if('-n' in arg):
                    name = sys.argv[index+1].lower()
            except:
                print('error in one of the arguments')
                sys.exit(1)

    Date.day = int(date.today().day)
    Date.month = int(date.today().month)
    Date.year = int(date.today().year)

    s = requests.Session()
    prgTV(s, name)
    sys.exit(0)
