import sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

def yt_tr_link(name):
    try:
        s = Service('./drivers/geckodriver.exe')

        options = Options()
        options.headless = True
        driver = webdriver.Firefox(options=options, service=s)

        wait = WebDriverWait(driver, 3)
        visible = EC.visibility_of_element_located

        driver.get(f'https://www.youtube.com/results?search_query={name}')
        driver.execute_script("window.scrollTo(0, 1000);")

        wait.until(visible((By.XPATH, '//*[@id="video-title"]')))
        link = driver.find_element(By.XPATH, '//*[@id="video-title"]')

        print(link.get_attribute('href'))

        driver.quit()
    except Exception as e:
        print(e)
        sys.exit(1)

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

    if(not name):
        print('plase insert a name')
        sys.exit(1)

    yt_tr_link(name)
    sys.exit(0)
