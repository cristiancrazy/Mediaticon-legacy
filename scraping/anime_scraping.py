import requests, re, bs4

def myanilist():
    letters = [ '.', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
    _from_index = 0
    _to_index = 1

    page = 0
    tipo = 'Anime'

    for letter in letters[_from_index:_to_index]:
        #GET HTML
        response = requests.get(f'https://myanimelist.net/anime.php?letter={letter}&show={page}')
        response.raise_for_status() # give an error if the page returns an error code

        #PREPARE FOR PARSING
        soup = bs4.BeautifulSoup(response.text, 'html.parser')

        #GET NEEDED HTML
        animes = soup.find_all('tr')

        for anime in animes:
            pass

if __name__ == "__main__":
    myanilist()
