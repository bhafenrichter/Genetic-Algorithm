package geneticalgoirthm;

import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgoirthm {
    
    public static void main(String[] args) {
        KeyboardInputClass input = new KeyboardInputClass();
        
        //get the users information for the GA
        ArrayList<Item> shipmentData = readShipmentInfo();
        
        //initialize defaults
        int populationSize = 20;
        int knapsackCapacity = 20;
        double crossoverRate = .5;
        double mutationRate = .1;
        int numberOfRuns = 1;
        double targetValue = 1000;
        
        try{populationSize = Integer.parseInt(input.getKeyboardInput("Population Size? (Default: 20)"));}catch(Exception e){}
        try{knapsackCapacity = Integer.parseInt(input.getKeyboardInput("Knapsack Capacity? (Default: 200)"));}catch(Exception e){}
        try{crossoverRate = Integer.parseInt(input.getKeyboardInput("Crossover Rate? (0-1: Default = .5)"));}catch(Exception e){}
        try{mutationRate = Integer.parseInt(input.getKeyboardInput("Mutation Rate? (0-1: Default = .1)"));}catch(Exception e){}
        boolean isSavedBestSolution = true;
        String str = input.getKeyboardInput("Save best solution from each generation? (Y / N)");
        if(str.length() > 0 && str.toLowerCase().equals("y")){
            isSavedBestSolution = true;
        }else{
            isSavedBestSolution = false;
        }
        try{targetValue = Integer.parseInt(input.getKeyboardInput("Target value? (default = 1000)"));}catch(Exception e){}
        try{numberOfRuns = Integer.parseInt(input.getKeyboardInput("Number of runs? (default = 1)"));}catch(Exception e){}

        //initializes the first population at complete random 
        ArrayList<PopulationMember> currentGen = initializeFirstGeneration(populationSize, shipmentData.size());
        
    }
    
    public static PopulationMember mutate(PopulationMember child){
        //calculate the position of mutation
        Random rand = new Random();
        int position = rand.nextInt(child.sequence.length() + 1);
        
        //flip the bit
        StringBuilder sequence = new StringBuilder(child.sequence);
        if(child.sequence.charAt(position) == '0'){
            sequence.setCharAt(position, '1');
        }else{
            sequence.setCharAt(position, '0');
        }
        child.sequence = sequence.toString();
        
        return child;
    }
    
    public static PopulationMember crossover(PopulationMember a, PopulationMember b){
        PopulationMember child = new PopulationMember();
        
        //pick crossover point
        Random rand = new Random();
        int crossoverPoint = rand.nextInt(a.sequence.length() + 1);
        
        child.sequence = "";
        child.sequence += a.sequence.substring(0, crossoverPoint);
        child.sequence += b.sequence.substring(crossoverPoint, a.sequence.length());
        
        //TODO: evaluate fitness
        
        return child;
    }
    
    public static ArrayList<Item> readShipmentInfo(){
        TextFileClass textFile = new TextFileClass();
        textFile.getFileName("Specify the text file to be read:");
        textFile.getFileContents();
        String[] textContents = textFile.text;
        
        ArrayList<Item> shipmentData = new ArrayList<Item>();
        
        //negate the length
        int index = 1;
        while(textContents[index] != null){
            Item cur = new Item(Integer.parseInt(textContents[index++]), Integer.parseInt(textContents[index++]));
            shipmentData.add(cur);
        }
        
        //print the item data
        System.out.println("Item:\tWeight:\tValue:");
        for(int i = 0; i < shipmentData.size(); i++){
            Item cur = shipmentData.get(i);
            System.out.println(i + "\t" + cur.weight + "\t" + cur.value);
        }
        
        return shipmentData;
    }

    private static ArrayList<PopulationMember> initializeFirstGeneration(int populationSize, int chromosomeLength) {
       ArrayList<PopulationMember> generation = new ArrayList<PopulationMember>();
       
       Random rand = new Random();
       for(int i = 0; i < populationSize; i++){
           PopulationMember cur = new PopulationMember();
           String chromosome = "";
           //generate random chromsome
           for(int j = 0; j < chromosomeLength; j++){
               chromosome += Integer.toString(rand.nextInt(2));
           }
           cur.sequence = chromosome;
           cur.fitness = 0; //todo: implement fitness function
           generation.add(cur);
       }
       return generation;
    }
}
