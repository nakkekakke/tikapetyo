# Internet Database of Movies

## Pikaohje

Sovelluksen pääasiallinen toiminta perustuu elokuvien (tai sarjojen yms.) lisäämiseen tietokantaan ja hakemiseen tietokannasta. Sovellukseen voi myös tallettaa elokuviin liittyviä henkilöitä ja genrejä. Kun henkilö tai genre on tallennettu, voi sen liittää tietokannassa olevaan elokuvaan elokuvan omalta sivulta. Bonuksena tietokantaan voi tallettaa elokuvan suoraan IMDB.com -sivustosta elokuvaan viittaavalla linkillä.

## Toiminnot tarkemmin


#### [Home](http://tikapetyo.herokuapp.com/)
Sovelluksen kotisivu. Sisältää sattumanvaraisesti tietokannasta valitun "Featured Titlen", sekä linkin **elokuvan omalle sivulle**. Kotisivulla on myös linkit **hakusivulle** sekä **lisäyssivulle**.

#### [Hakusivu](http://tikapetyo.herokuapp.com/search)
Oletusarvoisesti hakee elokuvia nimen perusteella. Parametreja voi muuttaa dropdown-listojen avulla.
**Search type** -listasta voi valita, hakeeko elokuvia, henkilöitä vai genrejä.
- Elokuvia pystyy hakemaan nimellä, julkaisuvuodella, elokuvan kuvauksella tai elokuvan genrellä. Hakutulosta painettaessa vie **elokuvan sivulle**
- Henkilöitä pystyy hakemaan pelkällä nimellä. Hakutulosta painettaessa vie **henkilön sivulle**.
- Genrejä ei varsinaisesti pysty hakemaan, vaan ne listataan linkeiksi. Kun painat genreä, näet listan kaikista genreen kuuluvista elokuvista.

#### [Lisäyssivu](http://tikapetyo.herokuapp.com/add)
Tietokantaan voi tältä sivulta lisätä elokuvia, henkilöitä ja genrejä. IMDB-linkkiä käyttämällä tietokantaan tallentuu automaattisesti elokuvan lisäksi myös elokuvassa esiintyvät henkilöt (jotka eivät ennestään ole tietokannassa). Henkilöitä ja genrejä lisättäessä riittää pelkkä nimi.</br>
**Huom!** Elokuvaa lisätessä kentät *Name*, *Release year* ja *Length in minutes* ovat pakollisia!</br>
**_Huom!_** Elokuvan perustietoja (esim. genreä ja pituutta) voi päivittää vain lisäämällä uuden samannimisen elokuvan.

#### Elokuvasivu
Yhden elokuvan oma sivu. Listaa paljon tietoja elokuvasta, kuten elokuvan näyttelijät ja käsikirjoittajat, sekä linkit näiden **henkilösivuille**. Sisältää mahdollisuuden liittää elokuvaan jokin tietokannassa jo oleva henkilö. Henkilön rooliksi voi valita joko näyttelijän tai käsikirjoittajan. Samassa kohdassa on myös toiminto henkilön ja elokuvan yhteyden poistamisen (tämä ei siis poista henkilöä tietokannasta). Myös koko elokuvan voi poistaa tietokannasta delete-linkillä.

#### Henkilö- ja genresivut
Yhden henkilön tai genren oma sivu. Sekä henkilön että genren sivuilla on lista henkilöön tai genreen liittyvistä elokuvista, sekä mahdollisuus poistaa henkilö tai genre delete-linkillä. Lisäksi henkilön sivulla näytetään henkilön mahdollinen biografia.

</br>
</br>

Lisäile ja poistele kaikkea sovelluksessa olevaa niin paljon kuin vain haluat! Toteutimme sovelluksen niin, että aika ajoin kaikki tehdyt muutokset poistetaan joka tapauksessa. Kuitenkin jos satut tuhoamaan tietokannan totaalisesti, käytä piilotettua sivua **/resetDatabase** peruuttaaksesi kaikki tehdyt muutokset. :)


#### COPYRIGHT
https://www.imdb.com/licensing/subservicetc/?pf_rd_m=A2FGELUUNOQJNL&pf_rd_p=24a4f514-e7c0-4ffb-9985-f9aaa3587145&pf_rd_r=1NCJVSQPCEDJ6T8HPDVH&pf_rd_s=center-1&pf_rd_t=60601&pf_rd_i=licensing&ref_=fea_cln_lk2
IMDb Content Subscription Service Terms and Conditions
IMDb Content 2.B

"IMDb Title ID and IMDb Name IDs. Notwithstanding the foregoing license set forth in Section 2.a above, we grant you a nonexclusive and sublicenseable right to use, reproduce, publish, distribute, and display any IMDb Title ID or an IMDb Name ID (which are alternatively known as “IMDb Tconst” for IMDb Title IDs and “IMDb Nconst” for IMDb Name IDs) throughout the world in any media."
