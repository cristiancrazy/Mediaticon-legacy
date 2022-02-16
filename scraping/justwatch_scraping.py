import sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.firefox.options import Options

def justwatch(name):
    partial_link : str = 'https://www.justwatch.com/'

    try:
        s = Service('./drivers/geckodriver.exe')

        options = Options()
        options.headless = True
        driver = webdriver.Firefox(options=options, service=s)

        driver.get(f'https://www.justwatch.com/it/cerca?q={name}')

        link = driver.find_element(By.XPATH, '/html/body/div[1]/div[4]/div[3]/div/div[2]/ion-grid/div/ion-row[1]/ion-col[2]/a')
        print(link.get_attribute('href'))
    except Exception as e:
        print(e)
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
    
    justwatch(name)
    sys.exit(0)