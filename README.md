# Internet Database of Movies

### Pikaohje

Sovelluksen pääasiallinen toiminta perustuu elokuvien (tai sarjojen yms.) lisäämiseen tietokantaan ja hakemiseen tietokannasta. Sovellukseen voi myös tallettaa elokuviin liittyviä henkilöitä ja genrejä. Kun henkilö tai genre on tallennettu, voi sen liittää tietokannassa olevaan elokuvaan elokuvan omalta sivulta. Bonuksena tietokantaan voi tallettaa elokuvan suoraan IMDB.com -sivustosta elokuvaan viittaavalla linkillä.

### Toiminnot tarkemmin

##### [Home](http://tikapetyo.herokuapp.com/)
Sovelluksen kotisivu. Sisältää sattumanvaraisesti tietokannasta valitun "Featured Title":n, sekä linkin **elokuvan omalle sivulle**. Kotisivulla on myös linkit **hakusivulle** sekä **lisäyssivulle**.

##### [Hakusivu](http://tikapetyo.herokuapp.com/search)
Oletusarvoisesti hakee elokuvia nimen perusteella. Parametreja voi muuttaa dropdown-listojen avulla.
**Search type** -listasta voi valita, hakeeko elokuvia, henkilöitä vai genrejä.
- Elokuvia pystyy hakemaan nimellä, julkaisuvuodella, elokuvan kuvauksella tai elokuvan genrellä. Parametri valitaan toisesta listasta. Tulosta painettaessa vie **elokuvan sivulle**
- Henkilöitä pystyy hakemaan pelkällä nimellä. Tulosta painettaessa vie **henkilön sivulle**.
- Genrejä ei varsinaisesti pysty hakemaan, vaan ne listataan linkeiksi. Kun painat genreä, näet listan kaikista genreen kuuluvista elokuvista.

##### [Lisäyssivu](http://tikapetyo.herokuapp.com/add)
Tietokantaan voi tältä sivulta lisätä elokuvia, henkilöitä ja genrejä.

##### Elokuvasivu
Yhden elokuvan oma sivu. Listaa paljon tietoja elokuvasta, kuten elokuvan näyttelijät ja käsikirjoittajat, sekä linkit näiden **henkilösivuille**. Sisältää mahdollisuuden liittää elokuvaan jokin tietokannassa jo oleva henkilö. Henkilön rooliksi voi valita joko näyttelijän tai käsikirjoittajan. Samassa kohdassa on myös toiminto henkilön ja elokuvan yhteyden poistamisen (tämä ei siis poista henkilöä tietokannasta). Myös koko elokuvan voi poistaa tietokannasta delete-linkillä.

##### Henkilö- ja genresivut
Yhden henkilön tai genren oma sivu. Sekä henkilön että genren sivuilla on lista henkilöön tai genreen liittyvistä elokuvista, sekä mahdollisuus poistaa henkilö tai genre. Lisäksi henkilön sivulla näytetään henkilön mahdollinen biografia.
