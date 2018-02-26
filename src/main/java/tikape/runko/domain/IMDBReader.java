package tikape.runko.domain;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import tikape.runko.database.Database;
import tikape.runko.database.GenreDao;
import tikape.runko.database.PersonDao;
import tikape.runko.database.TitleDao;

public class IMDBReader {

    private Database database;
    private PersonDao personDao;
    private TitleDao titleDao;
    private GenreDao genreDao;

    public IMDBReader(Database d) {
        database = d;
        personDao = new PersonDao(database);
        titleDao = new TitleDao(database);
        genreDao = new GenreDao(database);
    }

    public void addTitleFromIMDB(String address) throws MalformedURLException, IOException, SQLException {

        int year = 0;
        String name = "";
        String description = "";
        int length = 0;
        String genre = "";
        String director = "";
        ArrayList<String> writers = new ArrayList<>();
        ArrayList<String> actors = new ArrayList<>();

        URL url = null;

        try {
            url = new URL(address);
        } catch (Exception e) {
            System.out.println("Error in getting IMDB movie. Returning.");
            return;
        }

        Scanner reader = new Scanner(url.openStream());

        boolean genreFound = false;
        boolean minutesFound = false;
        boolean foundActors = false;

        //Parsing HTML file
        while (reader.hasNextLine()) {

            String line = reader.nextLine();

            //Movie title original (if translated)
            if (line.contains("originalTitle")) {

                String[] split = line.split("originalTitle\">");
                split = split[1].split("<");
                name = split[0];

            }

            //Movie title 
            if (line.contains("<div class=\"title_wrapper\">")) {

                line = reader.nextLine();

                String[] split = line.split("<h1 itemprop=\"name\" class=\"\">");
                split = split[1].split("&nbsp");
                name = split[0];

            }

            //Movie year
            if (line.contains("titleYear")) {

                String[] split = line.split("/year/");

                split = split[1].split("/");
                year = Integer.parseInt(split[0]);

            }

            //Movie description
            if (line.contains("og:description")) {

                String[] split = line.split("content=\"");
                split = split[1].split("\" />");
                StringBuilder newDesc = new StringBuilder(split[0]);

                if (newDesc.length() > 500) {
                    newDesc.setLength(500);
                }

                description = newDesc.toString();

                continue;
            }

            //Movie genre
            if (line.contains("class=\"itemprop\" itemprop=\"genre\">")) {

                if (genreFound) {
                    continue;
                }

                String[] split = line.split("itemprop=\"genre\">");
                split = split[1].split("<");
                genre = split[0];

                genreFound = true;

                continue;
            }

            //Movie director
            if (line.contains("class=\"inline\">Director")) {

                reader.nextLine();
                reader.nextLine();
                String nextLine = reader.nextLine();

                String[] split = nextLine.split("itemprop=\"name\">");

                split = split[1].split("<");
                director = split[0];

                continue;
            }

            //Movie writers
            if (line.contains("<h4 class=\"inline\">Writers:</h4>")) {

                while (reader.hasNextLine()) {

                    if (reader.nextLine().contains("credit_summary_item")) {
                        break;
                    };
                    if (reader.nextLine().contains("credit_summary_item")) {
                        break;
                    };
                    String nextLine = reader.nextLine();

                    if (nextLine.contains("credit_summary_item")) {
                        break;
                    }

                    if (!nextLine.contains("itemprop=\"name\">")) {
                        break;
                    }

                    String[] split = nextLine.split("itemprop=\"name\">");
                    split = split[1].split("<");
                    String writer = split[0].replaceAll("'", "");
                    writer = writer.replaceAll(";", "");
                    writer = writer.replaceAll("´", "");
                    writers.add(writer);

                }

                continue;
            }

            //Movie writer (if only one)
            if (line.contains("<h4 class=\"inline\">Writer:</h4>")) {

                while (reader.hasNextLine()) {

                    if (reader.nextLine().contains("credit_summary_item")) {
                        break;
                    };
                    if (reader.nextLine().contains("credit_summary_item")) {
                        break;
                    };
                    String nextLine = reader.nextLine();

                    if (nextLine.contains("credit_summary_item")) {
                        break;
                    }

                    if (!nextLine.contains("itemprop=\"name\">")) {
                        break;
                    }

                    String[] split = nextLine.split("itemprop=\"name\">");
                    split = split[1].split("<");

                    String writer = split[0].replaceAll("'", "");
                    writer = writer.replaceAll(";", "");
                    writer = writer.replaceAll("´", "");
                    writers.add(writer);
                }

                continue;
            }

            //Movie stars
            if (line.contains("<b>Stars:</b> ")) {

                if (foundActors) {
                    continue;
                }

                while (reader.hasNextLine()) {

                    line = reader.nextLine();

                    if (line.contains("</span></div>")) {

                        String[] split = line.split("</span></div>");

                        String actor = split[0].replaceAll("'", "");
                        actor = actor.replaceAll(";", "");
                        actor = actor.replaceAll("´", "");
                        writers.add(split[0]);

                        actors.add(actor);
                        break;

                    } else {

                        String[] split = line.split(",");
                        actors.add(split[0]);

                    }

                }

                foundActors = true;

            }

            //Movie length in minutes
            if (line.contains("itemprop=\"duration\"")) {

                if (minutesFound) {
                    continue;
                }

                String nextLine = reader.nextLine();

                String[] stringLength = new String[2];
                stringLength[0] = "";
                stringLength[1] = "";

                int i = 0;

                boolean hours = false;

                for (Character c : nextLine.toCharArray()) {

                    if (c >= '0' && c <= '9') {
                        stringLength[i] = stringLength[i] + c;
                    }

                    if (c == 'h') {
                        i++;
                        hours = true;
                    }

                }

                int minutes = 0;

                if (hours) {
                    if (stringLength[0].length() > 0) {
                        minutes += 60 * Integer.parseInt(stringLength[0]);
                    }

                    if (stringLength[1].length() > 0) {
                        minutes += Integer.parseInt(stringLength[1]);
                    }
                } else {
                    if (stringLength[0].length() > 0) {
                        minutes += Integer.parseInt(stringLength[0]);
                    }
                }

                length = minutes;

                minutesFound = true;
                continue;
            }

            //Movie cast
            if (line.contains("<div class=\"article\" id=\"titleCast\">")) {

                while (reader.hasNextLine()) {

                    line = reader.nextLine();

                    if (line.contains("<span class=\"itemprop\" itemprop=\"name\">")) {

                        String[] split = line.split("<span class=\"itemprop\" itemprop=\"name\">");
                        split = split[1].split("<");
                        
                        String actor = split[0].replaceAll("'", "");
                        actor = actor.replaceAll(";", "");
                        actor = actor.replaceAll("´", "");
                        actors.add(split[0]);

                        actors.add(actor);

                    }

                    if (line.contains("<div class=\"see-more\">")) {
                        break;
                    }

                }

            }

        }
        //Parsing finished

        System.out.println("Movie infromation:\n");
        System.out.println("Movie name: " + name);
        System.out.println("Movie description: " + description);
        System.out.println("Movie year: " + year);
        System.out.println("Movie length: " + length);
        System.out.println("Movie genre: " + genre);
        System.out.println("Movie director: " + director);
        System.out.print("Writers (" + writers.size() + ") : ");

        for (String s : writers) {
            System.out.print(s + ", ");
        }
        System.out.println("");
        System.out.print("Actors (" + actors.size() + ") : ");

        for (String s : actors) {
            System.out.print(s + ", ");
        }

        System.out.println("DONE\n");

        if (name.equals("")) {
            System.out.println("Error getting movie or bad link. Returning.");
            return;
        }

        Title title = new Title(0, name, year, length, description);

        addTitleToDatabase(title, director, genre);
        addActorsToDatabase(actors, title);
        addWritersToDatabase(writers, title);

        System.out.println("Successfully added '" + name + "' to database.");
        System.out.println("");
    }

    public void addTemplateMovies(int amount) throws IOException, MalformedURLException, SQLException {

        if (amount > 250) {
            amount = 250;
        }

        String address = "http://www.imdb.com/chart/top?ref_=nv_mv_250_6";

        System.out.println("Starting to get template movies from: " + address);

        URL url = null;

        try {
            url = new URL(address);
        } catch (Exception e) {
            System.out.println("Error in getting IMDB movie. Returning.");
            return;
        }

        Scanner reader = new Scanner(url.openStream());
        int added = 0;

        ArrayList<String> movieLinks = new ArrayList<>();

        while (reader.hasNextLine()) {

            if (added >= amount) {
                break;
            }

            String line = reader.nextLine();

            if (line.contains("<td class=\"titleColumn\">")) {
                reader.nextLine();
                line = reader.nextLine();

                String link = line.split("<a href=\"")[1].split("\" title")[0].split("/?pf")[0];
                movieLinks.add("https://www.imdb.com" + link);
                added++;
            }

        }

        System.out.println("Starting to add " + amount + " title to database.");
        System.out.println("");

        java.util.Random rng = new java.util.Random();

        while (movieLinks.size() > 0) {
            int i = rng.nextInt(movieLinks.size());
            String link = movieLinks.get(i);
            movieLinks.remove(i);

            System.out.println("Remaining movies to add: " + added);
            System.out.println("Adding movie from: " + link);
            
            try {
                addTitleFromIMDB(link);
            } catch (Exception e) {
                System.out.println("Failed to add movie: " + link);
            }

            added--;
            System.out.println("");
        }

        /* IN ORDER
        
        for (String link : movieLinks) {
            System.out.println("Remaining movies to add: " + added);
            System.out.println("Adding movie from: " + link);
            addTitleFromIMDB(link);
            added--;
            System.out.println("");
        }
         */
    }

    private void addTitleToDatabase(Title title, String director, String genre) throws SQLException {

        int director_id = personDao.getAndAddPersonId(director);
        int genre_id = genreDao.getAndAddGenreId(genre);

        title.setDirector(new Person(director_id, director));
        title.setGenre(new Genre(genre_id, genre));

        titleDao.saveOrUpdate(title);

    }

    private void addActorsToDatabase(ArrayList<String> actors, Title title) throws SQLException {

        int title_id = titleDao.findOneWithName(title.getName()).getId();

        for (String s : actors) {

            int actor_id = personDao.getAndAddPersonId(s);
            titleDao.addActor(title_id, actor_id);

        }

    }

    private void addWritersToDatabase(ArrayList<String> writers, Title title) throws SQLException {

        int title_id = titleDao.findOneWithName(title.getName()).getId();

        for (String s : writers) {

            int writer_id = personDao.getAndAddPersonId(s);
            titleDao.addWriter(title_id, writer_id);

        }

    }

}
