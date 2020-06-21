package pl.pleszka.multithreading;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class Otodom {

    public static boolean finalEndOfSearching = false;
    private static int numberOfAds = 1000;
    private static int counterOfAds = 0;
    static Set<String> setOfLinks = new TreeSet<>();


    public Queue<House> pageReader() throws Exception {

        Queue<House> queueOfHouses = new LinkedList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        long start = System.currentTimeMillis();

        URL oracle = new URL("https://warszawa.nieruchomosci-online.pl/mieszkania,sprzedaz/");

        int counterOfCycle = 0;

        for (int index = 2; index < numberOfAds/41; index++) {

            counterOfCycle++;
            System.out.println(counterOfCycle);

            System.out.println(numberOfAds / 41 + " czy równe " + index);
            if (numberOfAds / 41 == index + 1) {
                finalEndOfSearching = true;
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));

            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
                stringBuilder.append(System.lineSeparator());
            }
            in.close();

            String content = stringBuilder.toString();

            int indexOfLast = content.indexOf("https://warszawa.nieruchomosci-online.pl/mieszkania,sprzedaz/?p=2");
            int indexOfNumber = content.indexOf("id=\"boxOfCounter\">");
            String substringForCondition = content.substring(indexOfNumber + 18);
            numberOfAds = parseInt(substringForCondition.split("ogłosze")[0].replace(" ", ""));


            for (int i = 800; i < indexOfLast - 5000; i++) {

                i = content.indexOf("https://warszawa.nieruchomosci-online.pl/", i);
                if (i < 0)
                    break;
                String substring = content.substring(i);
                String link = substring.split(".html")[0] + ".html";
                setOfLinks.add(link);
            }
            oracle = new URL("https://warszawa.nieruchomosci-online.pl/mieszkania,sprzedaz/?p=" + index);
        }

            for (int i = 0; i < setOfLinks.size(); i++) {
                int finalI = i;

                executorService.submit(() -> {
                    try {
                        String contentOfSubpage = readWebsite(setOfLinks.toArray()[finalI].toString(), queueOfHouses);
                        House house = createHouse(contentOfSubpage, setOfLinks.toArray()[finalI].toString());
                        synchronized (queueOfHouses) {
                            queueOfHouses.add(house);
                            queueOfHouses.notify();
                            counterOfAds++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        executorService.shutdown();

        long end = System.currentTimeMillis();
        System.out.println(end - start);
        return queueOfHouses;
    }
    public static int getSetSize() {
        return setOfLinks.size();
    }

    public static synchronized void printList(Queue<House> queueOfHouses) {
        System.out.println("wielkość listy nieruchomości: " + queueOfHouses.size());
        for (House h : queueOfHouses) {
            System.out.println(h.toString());
        }
    }

    public String readWebsite(String link, Queue<House> queueOfHouses) throws IOException {

        URL oracle = new URL(link);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        StringBuilder stringBuilder = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            stringBuilder.append(inputLine);
            stringBuilder.append(System.lineSeparator());
        }

        String contentOfSubpage = stringBuilder.toString();
        in.close();

        return contentOfSubpage;
    }
    private House createHouse(String contentOfSubpage, String link) {
        String name = "";
        String address = "";
        double price = 0;
        double area = 0;
        double priceOfOneMeter = 0;
        String floors = "";
        int numberOfRooms = 0;
        int yearOfBuilt = 0;
        String dateOfPublishing = "";
        String condition = "";

        int indexOfName = 0;
        int indexOfAddress = 0;
        int indexOfPrice = 0;
        int indexOfArea = 0;
        int indexOfFloors = 0;
        int indexOfNumberOfRooms = 0;
        int indexOfYearOfBuilt = 0;
        int indexOfDateOfPublishing = 0;
        int indexOfCondition = 0;
        for (int i = 400; i < contentOfSubpage.length(); i++) {

            if (i < 0) {
                System.out.println("mniej niż zero");
                break;
            }
            indexOfName = contentOfSubpage.indexOf("<h1 class=\"header-b mod-c desktop\" id=\"h1Title\">", i) + 48;
            indexOfAddress = contentOfSubpage.indexOf("<p class=\"title-b\">", i) + 19;
            indexOfPrice = contentOfSubpage.indexOf("<li class=\"info-primary-price\">", i) + 31;
            indexOfArea = contentOfSubpage.indexOf("\"info-area\">", i) + 12;
            indexOfFloors = contentOfSubpage.indexOf("floor absolute\"></em><span class=\"fsize-a\">", i) + 43;
            indexOfNumberOfRooms = contentOfSubpage.indexOf("rooms absolute\"></em><span class=\"fsize-a\">", i) +43;
            indexOfYearOfBuilt = contentOfSubpage.indexOf("year-built absolute\"></em><span class=\"fsize", i) + 48;
            indexOfDateOfPublishing = contentOfSubpage.indexOf("class=\"current-as absolute mobile\"><span class=\"current-as-text\">zaktualizowane:<br/></span><strong>", i) + 100;
            indexOfCondition = contentOfSubpage.indexOf("Stan mieszkania:</span><br/><span class=\"fsize-c\">", i) + 50;
            if (indexOfName < 48)
                break;
        }

        String substringForName = contentOfSubpage.substring(indexOfName);
        name = substringForName.split("</h1>")[0];

        String substringForAddress = contentOfSubpage.substring(indexOfAddress);
        address = substringForAddress.split("</p>")[0];

        String substringForPrice = contentOfSubpage.substring(indexOfPrice);
        String priceString = substringForPrice.split("&nbsp;zł</li>")[0];
        priceString = priceString.replace("&nbsp;","");
        price = parseInt(priceString);

        String substringForArea = contentOfSubpage.substring(indexOfArea);
        String areaString = substringForArea.split("&nbsp;m&sup2;</li>")[0];
        areaString = areaString.replace(",",".");
        area = parseDouble(areaString);

        priceOfOneMeter = price/area;

        String substringForFloors = contentOfSubpage.substring(indexOfFloors);
        floors = substringForFloors.split("</span>")[0];
        if (floors.length() > 3) {
            floors = "0";
        }

        String substringForNumberOfRooms = contentOfSubpage.substring(indexOfNumberOfRooms);
        numberOfRooms = parseInt(substringForNumberOfRooms.split("</span>")[0]);

        String substringForBuilt = contentOfSubpage.substring(indexOfYearOfBuilt);
        String yearOfBuiltString = substringForBuilt.split("</span>")[0];
        if (yearOfBuiltString.length() > 4) {
            yearOfBuiltString = "0";
        }
        yearOfBuilt = parseInt(yearOfBuiltString);

        String substringForPublishing = contentOfSubpage.substring(indexOfDateOfPublishing);
        dateOfPublishing = substringForPublishing.split("</strong>")[0];
        if (dateOfPublishing.length() > 18) {
            dateOfPublishing = "0";
        }

        String substringForCondition = contentOfSubpage.substring(indexOfCondition);
        condition = substringForCondition.split("</span>")[0];
        if (condition.length() > 25) {
            condition = "0";
        }

        House house = new House(name, address, price, area, priceOfOneMeter, floors, numberOfRooms, yearOfBuilt, dateOfPublishing, condition, link);
        System.out.println(house.toString());
        return house;
    }
}
