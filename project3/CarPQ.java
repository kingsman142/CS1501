//James Hahn
//This was created for CS1501 Project 3
//CarPQ.java is a priority queue designed with a min-heap as its underlying data structure.  The user can insert Cars
//  and prioritize them by price or mileage.

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CarPQ{
    private int n;           //number of elements in the priority queue
    private Car[] heap;      //the heap, which will be initialized to 511 items
    private char Mode;       //'m' = mileages, 'p' = prices
    private boolean MakeModelHeap; //true = min heap for specific make/models, false indicates a heap for all makes/models

    //Initializes an empty indexed priority queue with 511 indices
    public CarPQ(char mode, boolean makeModelHeap) {
        n = 0; //Number of items initially in the heap is 0
        Mode = mode;
        MakeModelHeap = makeModelHeap;
        heap = new Car[511]; //Enough to completely fill 9 levels of a tree
    }

    //Returns the number of items on this priority queue
    public int size() {
        return n;
    }

    //Insert a car into the priority queue
    public void insert(Car key){
        if(key == null) return; //Can't insert a null Car
        n++; //Number of items increases by one
        if(n >= heap.length){ //Resize the heap if there's no more room left to insert a new Car
            resizeHeap();
        }
        heap[n] = key; //Insert the car
        swim(n); //Place it at the correct position in the heap based on its appropriate property value (mileage or price)
    }

    //Doubles the size of the heap once it's full
    public void resizeHeap(){
        Car[] newHeap = new Car[n*2]; //Double the size of the heap
        for(int i = 0; i <= n; i++){ //Copy all the items from the old heap over to the new heap
            newHeap[i] = heap[i];
        }
    }

    //Returns the Car with the minimum value (price or mileage) in the heap
    public Car getMin(){
        if (n == 0) return null;
        return heap[1];
    }

    //Change the car property associated with index i to the specified value and update its position in the heap
    public void update(int i){
        if (i < 0) throw new IndexOutOfBoundsException();
        swim(i); //When the Car is updated at index i, swim to the top of the heap
        sink(i); //and then sink it to its appropriate location
    }

    //Remove the car associated with index i
    public void delete(int i){
        if (i < 0) throw new IndexOutOfBoundsException();
        exch(i, n--); //Place the car at the bottom of the heap
        swim(i); //Swim the swapped in car to the top
        sink(i); //and then sink it down to its appropriate location
        heap[n+1] = null; //Make sure the car we deleted is set to null
    }

   /***************************************************************************
    * General helper functions.
    ***************************************************************************/
    private boolean greater(int i, int j){
        if(Mode == 'm') return heap[i].getMileage() > heap[j].getMileage(); //Compare mileages of two cars
        else if(Mode == 'p') return heap[i].getPrice() > heap[j].getPrice(); //Compare prices of two cars
        else return false; //If the heap is in any other mode, return false
    }

    private void exch(int i, int j){
        Car swap = heap[i];
        heap[i] = heap[j];
        heap[j] = swap;

        //Allows the cars to be indexable in this current heap, so update the index variables for these cars
        setNewCarHeapIndex(i);
        setNewCarHeapIndex(j);
    }

    private void setNewCarHeapIndex(int j){
        if(Mode == 'm'){ //Set the mileage index
            if(MakeModelHeap) heap[j].setMMMileageIndex(j); //Set the index for the heap for this make/model
            else heap[j].setMileageIndex(j); //Set the index for the heap for ALL cars
        } else if(Mode == 'p'){ //Set the price index
            if(MakeModelHeap) heap[j].setMMPriceIndex(j); //Set the index for the heap for this make/model
            else heap[j].setPriceIndex(j); //Set the index for the heap for ALL cars
        }
    }

   /***************************************************************************
    * Heap helper functions.
    * These functions were given in the IndexMinPQ.java on Farnan's website
    ***************************************************************************/
    private void swim(int k){
        while (k > 1 && greater(k/2, k)){
            exch(k, k/2);
            k = k/2;
        }

        setNewCarHeapIndex(k);
    }

    private void sink(int k){
        while (2*k <= n){
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }

        setNewCarHeapIndex(k);
    }
}
