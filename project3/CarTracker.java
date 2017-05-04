//James Hahn
//Main CarTracker class created for CS1501 Project 3

import java.util.*;

public class CarTracker{
    private static VINdlb Cars; //DLB of the cars that are created for fast access to them later on
    private static CarPQManager HeapManager; //Manages all of the heaps for all prices, all mileages, and the same for all makes and models
    private static Scanner sc; //Scanner for input throughout the program

    public static void main(String[] args){
        sc = new Scanner(System.in);
        Cars = new VINdlb();
        HeapManager = new CarPQManager();
        String input = "";

        do{
            //Main UI
            System.out.println("\n\t1) Add a car");
            System.out.println("\t2) Update a car");
            System.out.println("\t3) Remove a car");
            System.out.println("\t4) Retrieve the lowest priced car");
            System.out.println("\t5) Retrieve the lowest mileage car");
            System.out.println("\t6) Retrieve the lowest priced car by make/model");
            System.out.println("\t7) Retrieve the lowest mileage car by make/model");
            System.out.println("\t8) Quit");
            System.out.print("Choose one (enter number): ");
            input = sc.nextLine();

            if(input.equals("1")){
                addCar();
            } else if(input.equals("2")){
                updateCar();
            } else if(input.equals("3")){
                removeCar();
            } else if(input.equals("4")){
                getLowestPriceCar();
            } else if(input.equals("5")){
                getLowestMileageCar();
            } else if(input.equals("6")){
                getLowestPriceCarByMakeAndModel();
            } else if(input.equals("7")){
                getLowestMileageCarByMakeAndModel();
            } else if(input.equals("8")){
                System.exit(0);
            } else{
                System.out.println("Invalid choice!\n");
            }
        } while(!input.equals("8"));
    }

    //A user adds a car with its necessary information
    public static void addCar(){
        String id = "";
        String make = "";
        String model = "";
        String color = "";
        int mileage = 0;
        int price = 0;

        System.out.print("\nEnter a VIN: ");
        id = sc.nextLine();

        System.out.print("Enter a Make: ");
        make = sc.nextLine();

        System.out.print("Enter a Model: ");
        model = sc.nextLine();

        System.out.print("Enter a Color: ");
        color = sc.nextLine();

        //Ask the user for the mileage of the car
        try{
            System.out.print("Enter a Mileage: ");
            mileage = Integer.parseInt(sc.nextLine());
            while(mileage < 0){
                System.out.print("Enter a VALID Mileage: ");
                mileage = Integer.parseInt(sc.nextLine());
            }
        } catch(NumberFormatException e){ //parseInt() will throw NumberFormatException if the user doesn't enter a number
            System.out.print("Enter a VALID Mileage: ");
            mileage = Integer.parseInt(sc.nextLine());
            while(mileage < 0){
                System.out.print("Enter a VALID Mileage: ");
                mileage = Integer.parseInt(sc.nextLine());
            }
        }

        //Ask the user for the price of the car
        try{
            System.out.print("Enter a Price (whole USD): $");
            price = Integer.parseInt(sc.nextLine());
            while(price < 0){ //Invalid price <0$
                System.out.print("Enter a VALID Price (whole USD): $");
                price = Integer.parseInt(sc.nextLine());
            }
        } catch(NumberFormatException e){ //parseInt() will throw NumberFormatException if the user doesn't enter a number
            System.out.print("Enter a VALID Price (whole USD): $");
            price = Integer.parseInt(sc.nextLine());
            while(price < 0){ //Invalid price <0$
                System.out.print("Enter a VALID Price (whole USD): $");
                price = Integer.parseInt(sc.nextLine());
            }
        }

        if(Cars.exists(id)){ //Checks to see if the car already exists; if so, don't add it
            System.out.println("\n--- A car with that VIN number already exists! ---");
            return;
        }

        Car newCar = new Car(id, make, model, color, mileage, price);

        Cars.insert(newCar); //Insert the car into the symbol table
        HeapManager.insert(newCar); //Insert the car into the appropriate heaps (all prices, all mileages, make/model prices, make/model mileages)
    }

    //Update a car's price, mileage, or color
    public static void updateCar(){
        System.out.print("\nEnter the VIN of the car to update ('q' to Quit): ");
        String id = sc.nextLine();
        if(id.equals("q")) return; //Quit on 'q'

        Car car = Cars.getCar(id); //Grab the car from the symbol table in constant time
        while(car == null){ //Car doesn't exist
            System.out.println("\n=== Invalid VIN! ===");
            System.out.print("Enter the VIN of the car to update ('q' to Quit): "); //Prompt the user to enter a new VIN or quit
            id = sc.nextLine();
            if(id.equals("q")) return;
            car = Cars.getCar(id); //The user entered another VIN, so grab that car
        }

        //Ask the user for which property of the car they want to update
        //1 = Price
        //2 = Mileage
        //3 = Color
        System.out.println("\nWould you like to update (enter number, 'q' to Quit):\n\t1) Price\n\t2) Mileage\n\t3) Color");
        System.out.print("Choose one: ");
        String choice = sc.nextLine();
        while(!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("q")){
            System.out.println("\n=== Invalid Choice! ===");
            System.out.println("Would you like to update (enter number, 'q' to Quit):\n\t1) Price\n\t2) Mileage\n\t3) Color");
            System.out.print("Choose one: ");
            choice = sc.nextLine();
        }

        //Update the price of the car
        if(choice.equals("1")){
            int price = -1;

            try{
                System.out.print("Enter a Price (whole USD): $");
                price = Integer.parseInt(sc.nextLine());
                while(price < 0){
                    System.out.print("Enter a VALID Price (whole USD): $");
                    price = Integer.parseInt(sc.nextLine());
                }
            } catch(NumberFormatException e){
                System.out.print("Enter a VALID Price (whole USD): $");
                price = Integer.parseInt(sc.nextLine());
                while(price < 0){
                    System.out.print("Enter a VALID Price (whole USD): $");
                    price = Integer.parseInt(sc.nextLine());
                }
            }

            //Change the property of the car
            car.setPrice(price);
            HeapManager.update(car, true); //true means the price was updated
        } else if(choice.equals("2")){ //Update the mileage of the car
            int mileage = -1;

            try{
                System.out.print("Enter a Mileage: ");
                mileage = Integer.parseInt(sc.nextLine());
                while(mileage < 0){
                    System.out.print("Enter a VALID Mileage: ");
                    mileage = Integer.parseInt(sc.nextLine());
                }
            } catch(NumberFormatException e){
                System.out.print("Enter a VALID Mileage: ");
                mileage = Integer.parseInt(sc.nextLine());
                while(mileage < 0){
                    System.out.print("Enter a VALID Mileage: ");
                    mileage = Integer.parseInt(sc.nextLine());
                }
            }

            //Change the property of the car
            car.setMileage(mileage);
            HeapManager.update(car, false); //false means the mileage was updated
        } else if(choice.equals("3")){
            System.out.print("Enter a color: ");
            String color = sc.nextLine();

            //Change the property of the car
            //No need to update the heap for a change in color
            car.setColor(color);
        } else if(choice.equals("q")){
            return;
        }
    }

    //Remove the car from the "database", which are the priority queues and symbol table
    public static void removeCar(){
        System.out.print("\nEnter the VIN of the car to remove ('q' to Quit): ");
        String id = sc.nextLine();
        if(id.equals("q")) return;

        Car car = Cars.getCar(id); //Grab the car from the symbol table
        while(car == null){ //Car doesn't exist
            System.out.println("=== Invalid Car! ===");
            System.out.print("\nEnter the VIN of the car to remove ('q' to Quit): "); //Prompt the user to enter another VIN or quit
            id = sc.nextLine();
            if(id.equals("q")) return;
            car = Cars.getCar(id); //The user entered another VIN, so grab that car
        }

        HeapManager.remove(car); //Remove the car from the necessary priority queues
        Cars.remove(id); //Remove the car from the symbol table
    }

    //Retrieve the lowest priced car from all makes and models
    public static void getLowestPriceCar(){
        Car lowest = HeapManager.getLowestPrice();

        if(lowest == null) System.out.println("\n--- No cars available. ---"); //No cars exist
        else System.out.println("\n" + lowest.toString()); //Print the car for the user
    }

    //Retrieve the lowest mileaged car from all makes and models
    public static void getLowestMileageCar(){
        Car lowest = HeapManager.getLowestMileage();

        if(lowest == null) System.out.println("\n--- No cars available. ---"); //No cars exist
        else System.out.println("\n" + lowest.toString()); //Print the car for the user
    }

    //Retrieve the lowest priced car from a specific make and model
    public static void getLowestPriceCarByMakeAndModel(){
        System.out.print("\nEnter a make (Toyota, Ford, Honda, ...): ");
        String make = sc.nextLine();

        System.out.print("Enter a model (Camry, Fiesta, Civic, ...): ");
        String model = sc.nextLine();

        Car lowest = HeapManager.getLowestMMPrice(make, model); //Get Lowest Make Model Price

        if(lowest == null) System.out.println("\n--- No cars available for that make and model. ---"); //No cars exist for this make and model
        else System.out.println("\n" + lowest.toString()); //Print the car for the user
    }

    //Retrieve the lowest mileaged car from a specific make and model
    public static void getLowestMileageCarByMakeAndModel(){
        System.out.print("\nEnter a make (Toyota, Ford, Honda, ...): ");
        String make = sc.nextLine();

        System.out.print("Enter a model (Camry, Fiesta, Civic, ...): ");
        String model = sc.nextLine();

        Car lowest = HeapManager.getLowestMMMileage(make, model); //Get Lowest Make Model Mileage

        if(lowest == null) System.out.println("\n--- No cars available for that make and model. ---"); //No cars exist for this make and model
        else System.out.println("\n" + lowest.toString()); //Print the car for the user
    }
}
