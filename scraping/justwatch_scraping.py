import re, sys
from selenium import webdriver

def justwatch(name):
    partial_link : str = 'https://www.justwatch.com/'

    driver = webdriver.Firefox()
    driver.get(f'https://www.justwatch.com/it/cerca?q={name}')

    link = driver.find_element_by_class(class_='title-list-row__row-header')
    print(link.text)


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
    
    justwatch(name)
    sys.exit(0)