import sys
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

def raiplay(driver, name):
    try:
        wait = WebDriverWait(driver, 3)
        visible = EC.visibility_of_element_located

        #RAIPLAY
        driver.get(f'https://www.raiplay.it/ricerca.html?q={name}')

        wait.until(visible((By.XPATH, '/html/body/main/rai-search/section[2]/div/div[2]/div[2]/div/div/div[1]/a')))
        link = driver.find_element(By.XPATH, '/html/body/main/rai-search/section[2]/div/div[2]/div[2]/div/div/div[1]/a')

        driver.get(link.get_attribute('href'))

        wait.until(visible((By.XPATH, '/html/body/main/section[1]/div[1]/div/h1')))
        title = driver.find_element(By.XPATH, '/html/body/main/section[1]/div[1]/div/h1')

        if name.lower() in title.text.lower():
            print(link.get_attribute('href'))
        else:
            return 1
    except Exception as e:
        print(e)
        driver.quit()
        return 1
    
    return 0

def mediasetplay(driver, name, name_type):
    try:
        wait = WebDriverWait(driver, 3)
        visible = EC.visibility_of_element_located

        #MEDIASETPLAY
        driver.get('https://www.mediasetplay.mediaset.it/?search')

        wait.until(visible((By.XPATH, '//*[@id="search"]')))
        textBox = driver.find_element(By.XPATH, '//*[@id="search"]')
        textBox.send_keys(name)

        wait.until(visible((By.XPATH, '/html/body/div[1]/div/div[1]/div[1]/div/div[3]/section[1]/header/div/h2/div')))
        _type = driver.find_element(By.XPATH, '/html/body/div[1]/div/div[1]/div[1]/div/div[3]/section[1]/header/div/h2/div')

        if _type.text == 'Programmi' and name_type == 'Film' or (_type.text == 'Film' and (name_type == 'Show' or name_type == 'Serie')):
            wait.until(visible((By.XPATH, '/html/body/div[1]/div/div[1]/div[1]/div/div[3]/section[2]/div[1]/ul/li/div/a/div/h3')))
            title = driver.find_element(By.XPATH, '/html/body/div[1]/div/div[1]/div[1]/div/div[3]/section[2]/div[1]/ul/li/div/a/div/h3')

            if title.text.lower() == name.lower():
                wait.until(visible((By.XPATH, '/html/body/div[1]/div/div[1]/div[1]/div/div[3]/section[2]/div[1]/ul/li[1]/div/a')))
                link = driver.find_element(By.XPATH, '/html/body/div[1]/div/div[1]/div[1]/div/div[3]/section[2]/div[1]/ul/li[1]/div/a')
            else:
                return 1
        else:
            wait.until(visible((By.XPATH, '/html/body/div[1]/div/div[1]/div[1]/div/div[3]/section[1]/div[1]/ul/li[1]/div/a/div/h3')))
            title = driver.find_element(By.XPATH, '/html/body/div[1]/div/div[1]/div[1]/div/div[3]/section[1]/div[1]/ul/li[1]/div/a/div/h3')

            if name.lower() in title.text.lower():
                wait.until(visible((By.XPATH, '/html/body/div/div/div[1]/div[1]/div/div[3]/section[1]/div[1]/ul/li[1]/div/a')))
                link = driver.find_element(By.XPATH, '/html/body/div/div/div[1]/div[1]/div/div[3]/section[1]/div[1]/ul/li[1]/div/a')
            else:
                return 1
        
        print(link.get_attribute('href'))
    except Exception as e:
        print(e)
        driver.quit()
        return 1
    
    return 0

def justwatch(driver, name):
    try:
        wait = WebDriverWait(driver, 3)
        visible = EC.visibility_of_element_located

        driver.get(f'https://www.justwatch.com/it/cerca?q={name}')

        wait.until(visible((By.XPATH, '/html/body/div[1]/div[4]/div[3]/div/div[2]/ion-grid/div/ion-row[1]/ion-col[2]/a/span[1]')))
        title = driver.find_element(By.XPATH, '/html/body/div[1]/div[4]/div[3]/div/div[2]/ion-grid/div/ion-row[1]/ion-col[2]/a/span[1]')

        if not (name.lower() in title.text.lower()):
            return 1

        wait.until(visible((By.XPATH, '/html/body/div[1]/div[4]/div[3]/div/div[2]/ion-grid/div/ion-row[1]/ion-col[2]/a')))
        link = driver.find_element(By.XPATH, '/html/body/div[1]/div[4]/div[3]/div/div[2]/ion-grid/div/ion-row[1]/ion-col[2]/a')
        print(link.get_attribute('href'))

        driver.quit()
    except Exception as e:
        print(e)
        driver.quit()
        return 1
    
    return 0

if __name__ == "__main__":
    name : str = ''

    if(len(sys.argv) > 3 or len(sys.argv) < 3):
        ch = 'many' if len(sys.argv) > 3 else 'few'
        print(f'too {ch} arguments, example: -n film')
        sys.exit(1)
    else:
        for index, arg in enumerate(sys.argv):
            try:
                if('-n' in arg):
                    name = sys.argv[index+1]
            except:
                print('error in one of the arguments')
                sys.exit(1)


    s = Service('./drivers/geckodriver.exe')

    options = Options()
    options.headless = True
    driver = webdriver.Firefox(options=options, service=s)

    if(raiplay(driver, name) != 1):
        driver.quit()
        sys.exit(0)
    elif(mediasetplay(driver, name, 'Film') != 1):
        driver.quit()
        sys.exit(0)
    elif(justwatch(driver, name) != 1):
        driver.quit()
        sys.exit(0)

    driver.quit()
    sys.exit(1)
