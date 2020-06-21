package pl.pleszka.multithreading;

public class House {

    private String name;
    private String address;
    private double price;
    private double area;
    private double priceOfOneMeter;
    private String floors;
    private int numberOfRooms;
    private int yearOfBuilt;
    private String dateOfPublishing;
    private String condition;
    private String link;

    public House(String name, String address, double price, double area, double priceOfOneMeter, String floors, int numberOfRooms, int yearOfBuilt, String dateOfPublishing, String condition, String link) {
        this.name = name;
        this.address = address;
        this.price = price;
        this.area = area;
        this.priceOfOneMeter = priceOfOneMeter;
        this.floors = floors;
        this.numberOfRooms = numberOfRooms;
        this.yearOfBuilt = yearOfBuilt;
        this.dateOfPublishing = dateOfPublishing;
        this.condition = condition;
        this.link = link;
    }

    public String getDateOfPublishing() {
        return dateOfPublishing;
    }

    public String getCondition() {
        return condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPriceOfOneMeter() {
        return priceOfOneMeter;
    }

    public void setPriceOfOneMeter(double priceOfOneMeter) {
        this.priceOfOneMeter = priceOfOneMeter;
    }

    public String getFloors() {
        return floors;
    }

    public void setFloors(String floors) {
        this.floors = floors;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getYearOfBuilt() {
        return yearOfBuilt;
    }

    public void setYearOfBuilt(int yearOfBuilt) {
        this.yearOfBuilt = yearOfBuilt;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "House{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", price=" + price +
                ", area=" + area +
                ", priceOfOneMeter=" + priceOfOneMeter +
                ", floors='" + floors + '\'' +
                ", numberOfRooms=" + numberOfRooms +
                ", yearOfBuilt=" + yearOfBuilt +
                ", link='" + link + '\'' +
                '}';
    }
}
