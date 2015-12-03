package geneticalgoirthm;

import static geneticalgoirthm.GeneticAlgoirthm.knapsackCapacity;
import static geneticalgoirthm.GeneticAlgoirthm.shipmentData;
import static geneticalgoirthm.GeneticAlgoirthm.maxValue;

//Class PopulationMember.java
//Description: Contains the fitness function and data for members of the population
//***********************************************************************************************************
//***********************************************************************************************************

public class PopulationMember {
    String sequence;
    double fitness;
    int totalWeight;
    int totalValue;
    
    public PopulationMember(){
        
    }
    
    public PopulationMember(String sequence){
        this.sequence = sequence;
        this.fitness = fitness;
        
        //we are including it in the shipment
        for(int i = 0; i < sequence.length(); i++){
            String str = this.sequence.substring(i, i + 1);
            if(str.equals("1")){
                   this.totalValue += shipmentData.get(i).value;
                   this.totalWeight += shipmentData.get(i).weight;
            }
        }

        fitness();
    }
//***********************************************************************************************************
//Method: fitness()
//Description: Evaluates the fitness of the shipment by finding the distance between the optimal weight and value,
//normalizing it, and then taking the distance of that value from the ideal value (1,1).  Subtract it from 1 to 
//get how "close" it is to the ideal function
//Returns: None
//Calls: None    
//Parameters: None
//***********************************************************************************************************
    public void fitness(){
        //adds the distance between the best weight and value together for fitness
        //double distanceFromMaxValue = (double) this.totalValue / (double) targetValue;
        double distanceFromMaxWeight = 0;
        double distanceFromMaxValue = 0;
        if((double) this.totalWeight / (double) knapsackCapacity >= 1){
            distanceFromMaxWeight = (double) knapsackCapacity / (double) this.totalWeight;
            
            //double penalty = Math.log10(distanceFromMaxWeight);
            distanceFromMaxWeight = distanceFromMaxWeight * distanceFromMaxWeight;
        }else{
            distanceFromMaxWeight = (double) this.totalWeight / (double) knapsackCapacity;
        }
        
        distanceFromMaxValue = (double) this.totalValue / (double) maxValue;
        
        //average the distance between the best distance from weight and distance from value
        //this.fitness = (distanceFromMaxValue + distanceFromMaxWeight) / 2;
        
        
        //compute the distance and invert it, the values 1 should be what we are striving for
        double distance = Math.sqrt(((1-distanceFromMaxValue)*(1-distanceFromMaxValue)) + ((1-distanceFromMaxWeight)*(1-distanceFromMaxWeight)));
        this.fitness = 1 - distance;
    }
//***********************************************************************************************************
//Method: toString()
//Description: Prints the PopulationMember nicely
//Returns:              String string       nicely printed string
//Calls: None    
//Parameters: None
//***********************************************************************************************************
    public String toString(){
        return "Fitness: " + fitness + ", Weight: " + totalWeight + ", Value: " + totalValue; 
    }
}
