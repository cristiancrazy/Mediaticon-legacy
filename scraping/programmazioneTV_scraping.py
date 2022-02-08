import requests, re, bs4, time, sys, threading
from concurrent.futures import ThreadPoolExecutor
from datetime import date
from itertools import repeat
from dataclasses import dataclass, field

@dataclass
class Record:
    start_hour : str = ''
    end_hour : str = ''
    date : str = ''

@dataclass
class Channel:
    link : str = ''
    name : str = ''
    image : str = ''

    def __str__(self):
        return f'{self.link}\n{self.name}\n{self.image}\n\n'

# class Threads(threading.Thread):
#     def __init__(self, channel : Channel):
#         super(Threads, self).__init__()
#         self.channel = channel
#     def run(self):
#         record_scr(self.channel)

#SCRAPES TROUGH THE TV PROGRAMMATION
def record_scr(channel, session, film_name):
    today_date = str(date.today())

    with open(f'./csv/{channel.name}.csv', 'a') as f:
        f.write(f'{channel.name};{channel.image}')
        f.write('\n')

        response = session.get(f'{channel.link}{today_date}')
        soup = bs4.BeautifulSoup(response.text, 'html.parser')

        all_today_prg = soup.find_all('div', {'id' : 'table-content'})
        print(all_today_prg)

        for today_prg in all_today_prg:
            f.write(today_date.find('span', {'class' : 'program_title'}).text.lower())
            if(today_date.find('span', {'class' : 'program_title'}).text.lower() == film_name):
                f.write('\nciao')


#SEARCH ALL THE CHANNELS
def prgTV(session, film_name):
    channels : list(str) = []

    try:
        response = session.get('https://simpleguidatv.suppaman.it/')
        response.raise_for_status() # give an error if the page returns an error code
    except:
        sys.exit(1)

    #PREPARE FOR PARSING
    soup = bs4.BeautifulSoup(response.text, 'html.parser')

    #CREATES CHANNEL LINKS
    raw_channels = soup.find_all('div', {'class' : 'canale-img'})

    for channel in raw_channels:
        image : str = ''
        ch_name = channel.attrs['data-channelname']

        link : str = f'https://simpleguidatv.suppaman.it/?canale={ch_name}&data='

        if(_image := channel.find('img')['src']):
            image = 'https://simpleguidatv.suppaman.it/' + _image

        channels.append(Channel(link, channel.attrs['data-channelnamefancy'], image))
    
    with ThreadPoolExecutor() as executor:
        for result in executor.map(record_scr, channels, repeat(session), repeat(film_name)):
            print(result)

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

    s = requests.Session()
    prgTV(s, name)
    sys.exit(0)