import sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.firefox.options import Options

def yt_tr_link(session, name):
    #GET HTML
    try:
        response = session.get(f'https://www.youtube.com/results?search_query={name}')
        response.raise_for_status() # give an error if the page returns an error code
    except:
        sys.exit(1)

    #PREPARE FOR PARSING
    soup = bs4.BeautifulSoup(response.text, 'html.parser')

    title = soup.find('a', {'id' : 'video-title'})

    print(title)

if __name__ == "__main__":
    name : str = ''

    if(len(sys.argv) > 3 or len(sys.argv) < 3):
        ch = 'many' if len(sys.argv) > 5 else 'few'
        print(f'too {ch} arguments, example: scr.py -n anime_name')
        sys.exit(1)
    else:
        for index, arg in enumerate(sys.argv):
            try:
                if('-n' in arg):
                    name = sys.argv[index+1] + ' trailer'
            except:
                print('error in one of the arguments')
                sys.exit(1)

    s = requests.Session()
    yt_tr_link(s, name)
    sys.exit(0)