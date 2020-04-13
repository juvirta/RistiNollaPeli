Tämä on yhden pelaajan ristinollapeli.
Ohjelman käynnistyessä käyttäjä valitsee valikkoa käyttäen kentän koon (3-6 X 3-6) sekä voittoon tarvittavan määrän merkkejä (3-6).
Pelaaja aloittaa pelin ja pelaa ristimerkeillä. Pelaajan valinnan jälkeen vuoro siirtyy tietokoneelle.
Tietokonepelaaja käyttää syvyysrajoitettua minimax-algoritmia siirtonsa valitsemiseen. Syvyysrajoitus on neljä siirtoa.
Minimax käyttää heuristiikkaa, jossa jokaisesta voiton mahdollistavasta kenttäalueesta saa pisteitä kaavalla (tietokoneen merkkejä alueella)*(tietokoneen merkkejä alueella).
Vastaavasti pelaajan merkeistä lasketaan miinuspisteitä.
Peli loppuu kun jompikumpi pelaajista saa voittoon tarvittavan yhdistelmän, tai kaikki ruudut on käytetty.