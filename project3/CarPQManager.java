//James Hahn
//This was created for CS1501 Project 3
//CarPQManager.java allows a user to insert a car and have it be added, removed, or updated across all 4 of its respective priority queues.

public class CarPQManager{
    private static CarPQ Prices; //Priority Queue for ALL car prices
    private static CarPQ Mileages; //Priority Queue for ALL car mileages
    private static PriorQueueDLB MakeModelPrices; //DLB that stores priority queues for all makes and models prices
    private static PriorQueueDLB MakeModelMileages; //DLB that stores priority queues for all makes and models mileages

    public CarPQManager(){
        Prices = new CarPQ('p', false); //false indicates it's for ALL cars
        Mileages = new CarPQ('m', false); //false indicates it's for ALL cars
        MakeModelPrices = new PriorQueueDLB();
        MakeModelMileages = new PriorQueueDLB();
    }

    public void insert(Car car){
        String make = car.getMake();
        String model = car.getModel();
        String makeModelString = make + "%" + model; //Separate the make and model by a '%', so "Ford Fiesta" won't be confused with "For dFiesta"

        Prices.insert(car);
        Mileages.insert(car);
        CarPQ mmprices = MakeModelPrices.getPQ(makeModelString); //Grab the prices heap for that specific make and model
        CarPQ mmmileages = MakeModelMileages.getPQ(makeModelString); //Grab the mileages heap for that specific make and model
        if(mmprices == null){ //Make/Model prices heap doesn't exist
            CarPQ newPQ = new CarPQ('p', true); //true indicates it's for a specific make/model
            MakeModelPrices.insert(makeModelString, newPQ); //Insert this PQ into the priority queue DLB
            mmprices = newPQ;
        }
        if(mmmileages == null){ //Make/Model mileages heap doesn't exist
            CarPQ newPQ = new CarPQ('m', true); //true indicates it's for a specific make/model
            MakeModelMileages.insert(makeModelString, newPQ); //Insert this PQ into the priority queue DLB
            mmmileages = newPQ;
        }

        //Insert the car into its corresponding make/model priority queues
        mmprices.insert(car);
        mmmileages.insert(car);
    }

    //Get the car with the lowest price
    public Car getLowestPrice(){
        return Prices.getMin();
    }

    //Get the car with the lowest mileage
    public Car getLowestMileage(){
        return Mileages.getMin();
    }

    //Get the car with the lowest price for a specific make/model
    public Car getLowestMMPrice(String make, String model){
        String makeModelString = make + "%" + model;
        CarPQ mmprices = MakeModelPrices.getPQ(makeModelString);

        return mmprices != null ? mmprices.getMin() : null; //If the PQ is null, it doesn't exist, so return null; otherwise, return the min
    }

    //Get the car with the lowest mileage for a specific make/model
    public Car getLowestMMMileage(String make, String model){
        String makeModelString = make + "%" + model;
        CarPQ mmmileages = MakeModelMileages.getPQ(makeModelString);

        return mmmileages != null ? mmmileages.getMin() : null; //If the PQ is null, it doesn't exist, so return null; otherwise, return the min
    }

    //Remove a car from its respective priority queues
    public void remove(Car car){
        //Grab a car's appropriate indices for all 4 of its priority queues for easy deletion
        int pricesIndex = car.getPricesIndex();
        int mileagesIndex = car.getMileageIndex();
        int mmpricesIndex = car.getMMPriceIndex();
        int mmmileagesIndex = car.getMMMileageIndex();
        String make = car.getMake();
        String model = car.getModel();

        Prices.delete(pricesIndex);
        Mileages.delete(mileagesIndex);

        String makeModelString = make + "%" + model;
        CarPQ mmprices = MakeModelPrices.getPQ(makeModelString); //Grab the prices heap for this make/model from the DLB
        CarPQ mmmileages = MakeModelMileages.getPQ(makeModelString); //Grab the prices heap for this make/model from the DLB
        if(mmprices != null) mmprices.delete(mmpricesIndex); //Make sure the heap exists first, then delete
        if(mmmileages != null) mmmileages.delete(mmmileagesIndex); //Make sure the heap exists first, then delete
    }

    //Update a car's mileage or price in its heaps
    public void update(Car car, boolean updatedPrice){ //UpdatedPrice == false means they updated mileage, true means they updated price
        String make = car.getMake();
        String model = car.getModel();
        String makeModelString = make + "%" + model;

        if(updatedPrice){ //Updated Price, so only update the car's price heaps
            int pricesIndex = car.getPricesIndex();
            int pricesMMIndex = car.getMMPriceIndex();
            Prices.update(pricesIndex);

            CarPQ mmprices = MakeModelPrices.getPQ(makeModelString);
            if(mmprices != null) mmprices.update(pricesMMIndex); //Make sure the heap exists first, then update
        } else{ //Updated Mileage, so only update the car's mileage heaps
            int mileageIndex = car.getMileageIndex();
            int mileageMMIndex = car.getMMMileageIndex();
            Mileages.update(mileageIndex);

            CarPQ mmmileages = MakeModelMileages.getPQ(makeModelString);
            if(mmmileages != null) mmmileages.update(mileageMMIndex); //Make sure the heap exists first, then update
        }
    }
}
