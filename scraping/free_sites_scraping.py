import sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

def free_sites(name):
    try:
        s = Service('./drivers/geckodriver.exe')

        options = Options()
        options.headless = True
        driver = webdriver.Firefox(options=options, service=s)

        wait = WebDriverWait(driver, 3)
        visible = EC.visibility_of_element_located

        #RAIPLAY
        driver.get(f'https://www.raiplay.it/ricerca.html?q={name}')

        wait.until(visible((By.XPATH, '/html/body/main/rai-search/section[2]/div/div[2]/div[2]/div/div/div[1]/a')))
        link = driver.find_element(By.XPATH, '/html/body/main/rai-search/section[2]/div/div[2]/div[2]/div/div/div[1]/a')
        
        print(link.get_attribute('href'))

        #MEDIASETPLAY
        driver.get('https://www.mediasetplay.mediaset.it/?search')

        textBox = driver.find_element(By.XPATH, '//*[@id="search"]')
        textBox.send_keys(name)

        wait.until(visible((By.XPATH, '/html/body/div/div/div[1]/div[1]/div/div[3]/section[1]/div[1]/ul/li[1]/div/a')))
        link = driver.find_element(By.XPATH, '/html/body/div/div/div[1]/div[1]/div/div[3]/section[1]/div[1]/ul/li[1]/div/a')
        print(link.get_attribute('href'))

        driver.quit()
    except Exception as e:
        print(e)
        sys.exit(1)

if __name__ == "__main__":
    name : str = ''

    if(len(sys.argv) > 3 or len(sys.argv) < 3):
        ch = 'many' if len(sys.argv) > 5 else 'few'
        print(f'too {ch} arguments, example: scr.py -y 2021 -p scrapers/data.csv')
        sys.exit(1)
    else:
        for index, arg in enumerate(sys.argv):
            try:
                if('-n' in arg):
                    name = sys.argv[index+1]
            except:
                print('error in one of the arguments')
                sys.exit(1)

    free_sites(name)
    sys.exit(0)