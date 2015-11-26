package geneticalgoirthm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgoirthm {
    
    public static ArrayList<Item> shipmentData;
    public static int knapsackCapacity;
    public static int targetValue;
    public static int numberOfRuns;
    public static double crossoverRate;
    public static double mutationRate;
    public static boolean isSavedBestSolution;
    public static int maxValue;
    public static int populationSize;
    
    public static void main(String[] args) {        
        KeyboardInputClass input = new KeyboardInputClass();
        
        //get the users information for the GA
        shipmentData = readShipmentInfo();
        
        //get the maximum value for the fitness function
        maxValue = 0;
        for (int i = 0; i < shipmentData.size(); i++) {
            maxValue += shipmentData.get(i).value;
        }
        
        //initialize defaults
        populationSize = 20;
        knapsackCapacity = 200;
        crossoverRate = .5;
        mutationRate = .1;
        numberOfRuns = 1;
        targetValue = 1000;
        
        //set the parameters of the run
        setParameters();

        //initializes the first population at complete random without mutation or crossover
        //FIRST POPULATION
        ArrayList<PopulationMember> currentGen = initializeFirstGeneration(populationSize, shipmentData.size());
        //System.out.println(Arrays.toString(currentGen.toArray()));
        
        boolean oneRunCase = false;
        int generationNumber = 0;
        while (true) {
            //iterates the generation
            for (int i = 0; i < numberOfRuns; i++) {
                generationNumber++;
                currentGen = iterateGenerations(generationNumber, currentGen);
            }
            //System.out.println(Arrays.toString(currentGen.toArray()));
            PopulationMember bestMember = currentGen.get(0);
            
            //determine if we need to keep asking for generations
            if (numberOfRuns == 1) {
                oneRunCase = true;
            }
            
            if(oneRunCase){
                boolean isDetails = true;
                String details = input.getKeyboardInput("Show Details? (Y / N)");
                if (details.length() > 0 && details.toLowerCase().equals("y")) {
                    isDetails = true;
                } else {
                    isDetails = false;
                }
                //print the details of the generation
                if (isDetails) {
                    System.out.println("Details of best solution so far as (item, weight, value) triples:");
                    int selectedCount = 0;
                    for (int i = 0; i < bestMember.sequence.length(); i++) {
                        if (bestMember.sequence.charAt(i) == '1') {
                            selectedCount++;
                            System.out.println("(" + i + ", " + shipmentData.get(i).weight + ", " + shipmentData.get(i).value + ")");
                        }
                    }
                    System.out.println(selectedCount
                            + " items selected (with weight of "
                            + bestMember.totalWeight
                            + ", and value of "
                            + bestMember.totalValue
                            + ") out of " + shipmentData.size() + " total items");
                }

                boolean isPopulationDetails = true;
                String populationDetails = input.getKeyboardInput("Show Population Details? (Y / N)");
                if (populationDetails.length() > 0 && populationDetails.toLowerCase().equals("y")) {
                    isPopulationDetails = true;
                } else {
                    isPopulationDetails = false;
                }

                if (isPopulationDetails) {
                    System.out.println("Population members and attributes (fitness, weight, value):");
                    for (int i = 0; i < currentGen.size(); i++) {
                        PopulationMember cur = currentGen.get(i);
                        System.out.println(cur.sequence);
                        System.out.println("(" + ", " + cur.fitness + ", " + cur.totalWeight + ", " + cur.totalValue + ")");

                    }
                }

                boolean isSetParameters = true;
                String parameters = input.getKeyboardInput("Reset Parameters? (Y / N)");
                if (parameters.length() > 0 && parameters.toLowerCase().equals("y")) {
                    isSetParameters = true;
                } else {
                    isSetParameters = false;
                }
                
                //reset the parameters
                if(isSetParameters){
                    setParameters();
                }else{
                    numberOfRuns = Integer.parseInt(input.getKeyboardInput("Number of generations? (default = 10000):"));
                }
                
                
            }else{
                //todo: add multiple run stuff
                oneRunCase = true;
            }
        }
    }
    
    public static void setParameters(){
        KeyboardInputClass input = new KeyboardInputClass();
        try{populationSize = Integer.parseInt(input.getKeyboardInput("Population Size? (Default: 20)"));}catch(Exception e){}
        try{knapsackCapacity = Integer.parseInt(input.getKeyboardInput("Knapsack Capacity? (Default: 200)"));}catch(Exception e){}
        try{crossoverRate = Integer.parseInt(input.getKeyboardInput("Crossover Rate? (0-1: Default = .5)"));}catch(Exception e){}
        try{mutationRate = Integer.parseInt(input.getKeyboardInput("Mutation Rate? (0-1: Default = .1)"));}catch(Exception e){}
        isSavedBestSolution = true;
        String str = input.getKeyboardInput("Save best solution from each generation? (Y / N)");
        if(str.length() > 0 && str.toLowerCase().equals("y")){
            isSavedBestSolution = true;
        }else{
            isSavedBestSolution = false;
        }
        try{targetValue = Integer.parseInt(input.getKeyboardInput("Target value? (default = 1000)"));}catch(Exception e){}
        try{numberOfRuns = Integer.parseInt(input.getKeyboardInput("Number of runs? (default = 1)"));}catch(Exception e){}
    }
    
    public static ArrayList<PopulationMember> iterateGenerations(int generationNumber, ArrayList<PopulationMember> currentGen) {
        //if save the best solution is true, set the last member to the best member
        //if(isSavedBestSolution){
        //currentGen.set(currentGen.size() - 1, bestMember);
        //}

        //generate the next population
        Random rand = new Random();
        ArrayList<PopulationMember> newGen = new ArrayList<PopulationMember>();
        for (int j = 0; j < currentGen.size(); j++) {

            //System.out.println(dad.fitness + ", " + mom.fitness);
            String crossedOverChromosome = "";
            //crossover if it gets good rate
            if (rand.nextDouble() <= crossoverRate) {
                //select two parents
                PopulationMember dad = getBestParentOnWeightedPercentage(currentGen);
                PopulationMember mom = getBestParentOnWeightedPercentage(currentGen);
                crossedOverChromosome = crossover(dad, mom).sequence;
            } else {
                //don't crossover, just add the dad 
                crossedOverChromosome = getBestParentOnWeightedPercentage(currentGen).sequence;
            }

            //check for mutation  
            if (rand.nextDouble() <= mutationRate) {
                crossedOverChromosome = mutate(crossedOverChromosome);
            }
            PopulationMember child = new PopulationMember(crossedOverChromosome);
            newGen.add(child);
        }

            //set the new generation to the newly generated one 
        //System.out.println(Arrays.toString(newGen.toArray()));
        
        //sort the array
        //todo: make a better sort
        Collections.sort(newGen, new Comparator<PopulationMember>() {
            public int compare(PopulationMember result1, PopulationMember result2) {
                return Double.valueOf(result2.fitness).compareTo(Double.valueOf(result1.fitness));
            }
        });
        
        System.out.println("Generation: " 
                + generationNumber
                + ": Best Fitness: " 
                + newGen.get(0).fitness 
                + ", Weight: " 
                + newGen.get(0).totalWeight 
                + ", Value: " 
                + newGen.get(0).totalValue);
        return newGen;

    }

    public static PopulationMember getBestParentOnWeightedPercentage(ArrayList<PopulationMember> gen){
        //find the sum of all the fitnesses
        double sum = 0;
        for (int i = 0; i < gen.size(); i++) {
            sum += gen.get(i).fitness;
        }
        
        //gives us the position we need to pick based on weighted percentages
        Random rand = new Random();
        double index = sum * rand.nextDouble();
        
        double lowerBounds = 0;
        
        //finds the candidate based on weighted percentage
        for(int i = 0; i < gen.size(); i++){
            double upperBounds = lowerBounds + gen.get(i).fitness;
            if(index >= lowerBounds && index <= upperBounds){
                return gen.get(i);
            }else{
                lowerBounds += gen.get(i).fitness;
            }
        }
        //should never reach this code
        return gen.get(0);
    }
    
    public static String mutate(String chromosome){
        //calculate the position of mutation
        Random rand = new Random();
        int position = rand.nextInt(chromosome.length());
        
        //flip the bit
        StringBuilder sequence = new StringBuilder(chromosome);
        if(chromosome.charAt(position) == '0'){
            sequence.setCharAt(position, '1');
        }else{
            sequence.setCharAt(position, '0');
        }
        
        return sequence.toString();
    }
    
    public static PopulationMember crossover(PopulationMember a, PopulationMember b){
        //pick crossover point
        Random rand = new Random();
        int firstCrossoverPoint = rand.nextInt(a.sequence.length() + 1);
        int secondCrossoverPoint = rand.nextInt(a.sequence.length() + 1);
        String sequence = "";
        
        if(firstCrossoverPoint > secondCrossoverPoint){
            sequence += a.sequence.substring(0, secondCrossoverPoint);
            sequence += b.sequence.substring(secondCrossoverPoint, firstCrossoverPoint);
            sequence += a.sequence.substring(firstCrossoverPoint, a.sequence.length());
        }else{
            sequence += a.sequence.substring(0, firstCrossoverPoint);
            sequence += b.sequence.substring(firstCrossoverPoint, secondCrossoverPoint);
            sequence += a.sequence.substring(secondCrossoverPoint, a.sequence.length());
        }
        //sequence += a.sequence.substring(0, crossoverPoint);
        //sequence += b.sequence.substring(crossoverPoint, a.sequence.length());
        
        return new PopulationMember(sequence);
    }
    
    public static ArrayList<Item> readShipmentInfo(){
        TextFileClass textFile = new TextFileClass();
        textFile.getFileName("Specify the text file to be read:");
        textFile.getFileContents();
        String[] textContents = textFile.text;
        
        ArrayList<Item> shipmentData = new ArrayList<Item>();
        int totalWeightOfShipment = 0;
        int totalValueOfShipment = 0;
        //negate the length
        int index = 1;
        while(textContents[index] != null){
            Item cur = new Item(Integer.parseInt(textContents[index++]), Integer.parseInt(textContents[index++]));
            totalValueOfShipment += cur.value;
            totalWeightOfShipment += cur.weight;
            shipmentData.add(cur);
        }
        
        //print the item data
        System.out.println("Item:\tWeight:\tValue:");
        for(int i = 0; i < shipmentData.size(); i++){
            Item cur = shipmentData.get(i);
            System.out.println(i + "\t" + cur.weight + "\t" + cur.value);
        }
        
        System.out.println("Weight of all items: " + totalWeightOfShipment + "; Value of all items: " + totalValueOfShipment);
        return shipmentData;
    }

    private static ArrayList<PopulationMember> initializeFirstGeneration(int populationSize, int chromosomeLength) {
       ArrayList<PopulationMember> generation = new ArrayList<PopulationMember>();
       
       Random rand = new Random();
       for(int i = 0; i < populationSize; i++){
           
           String chromosome = "";
           //generate random chromsome
           for(int j = 0; j < chromosomeLength; j++){
               String str = Integer.toString(rand.nextInt(2));
               chromosome += str;
           }
           
           PopulationMember cur = new PopulationMember(chromosome);
           generation.add(cur);
       }
       return generation;
    }
}
