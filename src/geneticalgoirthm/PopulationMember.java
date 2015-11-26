package geneticalgoirthm;

import static geneticalgoirthm.GeneticAlgoirthm.knapsackCapacity;
import static geneticalgoirthm.GeneticAlgoirthm.shipmentData;
import static geneticalgoirthm.GeneticAlgoirthm.targetValue;
import static geneticalgoirthm.GeneticAlgoirthm.maxValue;

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
    
    public void fitness(){
        //adds the distance between the best weight and value together for fitness
        //double distanceFromMaxValue = (double) this.totalValue / (double) targetValue;
        double distanceFromMaxWeight = 0;
        double distanceFromMaxValue = 0;
        if((double) this.totalWeight / (double) knapsackCapacity > 1){
            distanceFromMaxWeight = (double) knapsackCapacity / (double) this.totalWeight;
            //penalize it for being above
            distanceFromMaxWeight = distanceFromMaxWeight * distanceFromMaxWeight;
        }else{
            distanceFromMaxWeight = (double) this.totalWeight / (double) knapsackCapacity;
        }
        
        if((double) this.totalValue / (double) targetValue > 1){
            distanceFromMaxValue = (double) targetValue / (double) this.totalValue;
        }else{
            distanceFromMaxValue = (double) this.totalValue / (double) targetValue;
        }
        
        //penalty for having overweight because that cant be a legal solution
        //distanceFromMaxWeight = Math.sqrt(distanceFromMaxWeight);
        
        //average the distance between the best distance from weight and distance from value
        //this.fitness = (distanceFromMaxValue + distanceFromMaxWeight) / 2;
        
        
        //compute the distance and invert it, the values 1 should be what we are striving for
        double distance = Math.sqrt((1-distanceFromMaxValue)*(1-distanceFromMaxValue) + (1-distanceFromMaxWeight)*(1-distanceFromMaxWeight));
        this.fitness = Math.abs(1 - distance);
    }
    
    public String toString(){
        return "Fitness: " + fitness + ", Weight: " + totalWeight + ", Value: " + totalValue; 
    }
}
