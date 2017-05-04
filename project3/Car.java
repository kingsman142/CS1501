//James Hahn
//This was created for CS1501 Project 3
//Represents a car with its appropriate properties

public class Car{
    private String VIN = ""; //Identification number
    private String Make = "";
    private String Model = "";
    private String Color = "";
    private int Mileage = -1;
    private int Price = -1;
    private int PricesHeapIndex = -1; //Index in the heap for the priority queue with ALL prices
    private int MileageHeapIndex = -1; //Index in the heap for the priority queue with ALL mileages
    private int MakeModelPriceHeapIndex = -1; //Index in the heap for the priority queue with this car's specific make/model's prices
    private int MakeModelMileageHeapIndex = -1; //Index in the heap for the priority queue with this car's specific make/model's mileages

    public Car(String id, String make, String model, String color, int mileage, int price){
        VIN = id;
        Make = make;
        Model = model;
        Color = color;
        Mileage = mileage;
        Price = price;
    }

    public String getVIN(){
        return VIN;
    }

    public String getMake(){
        return Make;
    }

    public String getModel(){
        return Model;
    }

    public String getColor(){
        return Color;
    }

    public int getMileage(){
        return Mileage;
    }

    public int getPrice(){
        return Price;
    }

    public int getPricesIndex(){
        return PricesHeapIndex;
    }

    public int getMileageIndex(){
        return MileageHeapIndex;
    }

    public int getMMPriceIndex(){
        return MakeModelPriceHeapIndex;
    }

    public int getMMMileageIndex(){
        return MakeModelMileageHeapIndex;
    }

    public void setColor(String newColor){
        Color = newColor;
    }

    public void setMileage(int newMileage){
        Mileage = newMileage;
    }

    public void setPrice(int newPrice){
        Price = newPrice;
    }

    public void setPriceIndex(int index){
        PricesHeapIndex = index;
    }

    public void setMileageIndex(int index){
        MileageHeapIndex = index;
    }

    public void setMMPriceIndex(int index){
        MakeModelPriceHeapIndex = index;
    }

    public void setMMMileageIndex(int index){
        MakeModelMileageHeapIndex = index;
    }

    public String toString(){
        String output = "\tVIN: " + VIN + "\n\tMake: " + Make + "\n\tModel: " + Model + "\n\tColor: " + Color + "\n\tMileage: " + Mileage + "\n\tPrice: $" + Price;
        return output;
    }
}
