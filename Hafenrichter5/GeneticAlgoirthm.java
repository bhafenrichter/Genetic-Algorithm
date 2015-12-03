//Program: Hafenrichter.java
//Course: COSC 420
//Description: An implementation of the Genetic Algorithm
//Author: Brandon Hafenrichter
//Revised: December 3, 2015
//Language: Java
//IDE: Netbeans 8.0.2
//***********************************************************************************************************
//***********************************************************************************************************
package geneticalgoirthm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

//Class Main.java
//Description: Contains the essential methods and calls required to run the genetic algorithm minus the fitness function
//***********************************************************************************************************
//***********************************************************************************************************

public class GeneticAlgoirthm {
    
    public static ArrayList<Item> shipmentData; //contains all the information of the shipment item
    public static int knapsackCapacity; //user defined as how much the plane can hold
    public static int targetValue;  //the trigger value, if we reach this value we stop iterating
    public static double targetFitness; //the fitness we would like to reach
    public static int numberOfGenerations;  //number of generations
    public static double crossoverRate; //user defined as the rate at which the parents should crossover
    public static double mutationRate;  //user defined as the rate at which the childrens chromosome mutates
    public static boolean isSavedBestSolution;  //user defined as saving the best member of the previous population
    public static int maxValue; //sum of all items values
    public static int populationSize;   //user defined as the number of members in each generation
    public static int numberOfRuns; //user defined as the number of runs we should do of the GA
    public static int histogramBinSize; //user defined as the size of the histogram bins
    public static ArrayList<PopulationMember> currentGen;

//***********************************************************************************************************
//Method: Main
//Description: Contains the essential methods and calls required to run the algorithms
//Returns: None
//Calls:    readShipmentInfo()
//          setParameters()
//          initializeFirstGeneration()
//          iterateGeneration()
//Parameters: String[] args             standard parameters for the main method
//***********************************************************************************************************
    public static void main(String[] args) {
        KeyboardInputClass input = new KeyboardInputClass();
        boolean hasFinished = true;
        int restartCount = 0;
        ///displayImage(new int[]{500,500,1001,1001,1001,1001,2000,3000,3000,3000}, 1000);

        while (true) {
            if(hasFinished){
                hasFinished = false;
                restartCount = 0;
                numberOfGenerations = 1;
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
            crossoverRate = .75;
            mutationRate = .5;
            targetValue = maxValue;
            targetFitness = 1.0;
            numberOfRuns = 1;
            histogramBinSize = 1000;

            //set the parameters of the run
            setParameters();
            }
            
            boolean oneRunCase = false;
            int generationNumber = 0;

            //determine if we need to keep asking for generations
            if (numberOfRuns == 1) {
                oneRunCase = true;
            }
            
            restartCount++;

            if (oneRunCase) {                
                
                if(restartCount <=1){
                    currentGen = initializeFirstGeneration(populationSize, shipmentData.size());
                }
                //iterates the generation
                for (int i = 0; i < numberOfGenerations; i++) {
                    generationNumber++;
                    currentGen = iterateGenerations(generationNumber, currentGen);

                    System.out.println("Generation: "
                            + generationNumber
                            + ": Best Fitness: "
                            + currentGen.get(0).fitness
                            + ", Weight: "
                            + currentGen.get(0).totalWeight
                            + ", Value: "
                            + currentGen.get(0).totalValue);
                    //if we reach the target fitness, stop iterating and show the details
                    if (currentGen.get(0).fitness >= targetFitness) {
                        hasFinished = true;
                        break;
                    }
                    
                    //we've reached the best combination
                    if(currentGen.get(0).totalValue >= targetValue && currentGen.get(0).totalWeight <= knapsackCapacity){
                        hasFinished = true;
                        break;
                    }
                }
                //System.out.println(Arrays.toString(currentGen.toArray()));
                PopulationMember bestMember = currentGen.get(0);

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

                if (!hasFinished) {
                    boolean isSetParameters = true;
                    String parameters = input.getKeyboardInput("Reset Parameters? (Y / N)");
                    if (parameters.length() > 0 && parameters.toLowerCase().equals("y")) {
                        isSetParameters = true;
                    } else {
                        isSetParameters = false;
                    }

                    //reset the parameters
                    if (isSetParameters && !hasFinished) {
                        setParameters();
                    } else {
                        try {
                            numberOfGenerations = Integer.parseInt(input.getKeyboardInput("Number of generations? (default = 10000):"));
                        } catch (Exception e) {
                            numberOfGenerations = 10000;
                        }
                    }
                }
                

            } else {
                try {
                    histogramBinSize = Integer.parseInt(input.getKeyboardInput("Histogram Bin Size? (default = 1000):"));
                } catch (Exception e) {
                    histogramBinSize = 1000;
                }

                int[] generationCount = new int[numberOfRuns];
                for (int i = 0; i < numberOfRuns; i++) {
                    ArrayList<PopulationMember> currentGen = initializeFirstGeneration(populationSize, shipmentData.size());
                    //subject to change depending on what Dr. Donaldson wants
                    int index = 0;
                    while (currentGen.get(0).fitness < targetFitness) {
                        if(currentGen.get(0).totalValue >= targetValue && currentGen.get(0).totalWeight <= knapsackCapacity){
                            //we've reached the end
                            break;
                        }
                        index++;
                        currentGen = iterateGenerations(index, currentGen);

                        //safeguard against endless loop
                        if (index == 100000) {
                            System.out.println("Reached End of maximum generation count.");
                            break;
                        }
                        
                        
                        //System.out.println(currentGen.get(0).fitness);
                    }

                    System.out.println("Run # "
                            + i
                            + ", Generation "
                            + index
                            + ": Best Fitness: "
                            + currentGen.get(0).fitness
                            + ", Weight: "
                            + currentGen.get(0).totalWeight
                            + ", Value: "
                            + currentGen.get(0).totalValue);
                    generationCount[i] = index;
                }

                System.out.println("Generation Count Summary:");
                System.out.println("-------------------------");
                for (int i = 0; i < generationCount.length; i++) {
                    System.out.println(generationCount[i]);
                }

                System.out.println("Sorted Generation Count Summary:");
                System.out.println("-------------------------");

                Arrays.sort(generationCount);
                for (int i = 0; i < generationCount.length; i++) {
                    System.out.println(generationCount[i]);
                }
                System.out.println("Each Bar Represents 50 Generations.");
                displayImage(generationCount,histogramBinSize);
                    

            }
        }
    }
//***********************************************************************************************************
//Method: setParameters()
//Description: Lets the user select the parameters for the GA
//Returns: None
//Calls: None    
//Parameters: None
//***********************************************************************************************************
    
    public static void setParameters() {
        KeyboardInputClass input = new KeyboardInputClass();
        try {
            populationSize = Integer.parseInt(input.getKeyboardInput("Population Size? (Default: 20)"));
        } catch (Exception e) {
        }
        try {
            knapsackCapacity = Integer.parseInt(input.getKeyboardInput("Knapsack Capacity? (Default: 200)"));
        } catch (Exception e) {
        }
        try {
            crossoverRate = Integer.parseInt(input.getKeyboardInput("Crossover Rate? (0-1: Default = .75)"));
        } catch (Exception e) {
        }
        try {
            mutationRate = Integer.parseInt(input.getKeyboardInput("Mutation Rate? (0-1: Default = .5)"));
        } catch (Exception e) {
        }
        isSavedBestSolution = true;
        String str = input.getKeyboardInput("Save best solution from each generation? (Y / N)");
        if (str.length() > 0 && str.toLowerCase().equals("y")) {
            isSavedBestSolution = true;
        } else {
            isSavedBestSolution = false;
        }
        try {
            targetFitness = Double.parseDouble(input.getKeyboardInput("Target Fitness? (default = 1)"));
        } catch (Exception e) {
        }
        try {
            targetValue = Integer.parseInt(input.getKeyboardInput("Target value? (default = "+ targetValue +")"));
        } catch (Exception e) {
        }
        try {
            numberOfRuns = Integer.parseInt(input.getKeyboardInput("Number of runs? (default = 1)"));
        } catch (Exception e) {
        }
    }
//***********************************************************************************************************
//Method: IterateGeneration()
//Description: Takes the current generation data and iterates to the next using crossover() and mutate()
//Returns:          ArrayList<PopulationMember> nextGeneration
//Calls:            getBestParentOnWeightedPercentage()
//                  mutate()
//                  crossover()
//Parameters:       int generationNumber    what generation we are currently on
//                  ArrayList<PopulationMember> currentGen  the previous generation that'll be iterated over
//***********************************************************************************************************
    public static ArrayList<PopulationMember> iterateGenerations(int generationNumber, ArrayList<PopulationMember> currentGen) {
        //generate the next population
        Random rand = new Random();
        ArrayList<PopulationMember> newGen = new ArrayList<PopulationMember>();
        int startingPoint = 0;

        for (int j = startingPoint; j < currentGen.size(); j++) {

            //System.out.println(dad.fitness + ", " + mom.fitness);
            String crossedOverChromosome = "";
            String crossedOverChromosome2 = "";
            //crossover if it gets good rate
            if (rand.nextDouble() <= crossoverRate) {
                //select two parents
                PopulationMember dad = getBestParentOnWeightedPercentage(currentGen);
                
                PopulationMember mom = dad;
                //addresses duplicates
                while(mom == dad){
                    mom = getBestParentOnWeightedPercentage(currentGen);
                }
                PopulationMember[] chromosomes = crossover(dad,mom);
                crossedOverChromosome = chromosomes[0].sequence;
                crossedOverChromosome2 = chromosomes[1].sequence;
                
            } else {
                //don't crossover, just add the dad 
                crossedOverChromosome = getBestParentOnWeightedPercentage(currentGen).sequence;
                crossedOverChromosome2 = getBestParentOnWeightedPercentage(currentGen).sequence;
            }

            //check for mutation  
            if (rand.nextDouble() <= mutationRate) {
                crossedOverChromosome = mutate(crossedOverChromosome);
                crossedOverChromosome2 = mutate(crossedOverChromosome2);
            }
            PopulationMember child = new PopulationMember(crossedOverChromosome);
            PopulationMember child2 = new PopulationMember(crossedOverChromosome2);
            if(newGen.size() < populationSize){
                newGen.add(child);
            }
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

//        if (isSavedBestSolution) {
//            startingPoint++;
//            //save the best solution to the next generation
//            newGen.add(currentGen.get(0));
//        }
        
        return newGen;

    }
//***********************************************************************************************************
//Method: getBestParentOnWeightedPercentage()
//Description: Returns the best PopulationMember based on their fitness
//Returns:          PopulationMember
//Calls: None
//Parameters:       ArrayList<PopulationMember> gen  the generation that'll be selected from
//***********************************************************************************************************
    public static PopulationMember getBestParentOnWeightedPercentage(ArrayList<PopulationMember> gen) {
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
        for (int i = 0; i < gen.size(); i++) {
            double upperBounds = lowerBounds + gen.get(i).fitness;
            if (index >= lowerBounds && index <= upperBounds) {
                return gen.get(i);
            } else {
                lowerBounds += gen.get(i).fitness;
            }
        }
        //should never reach this code
        return gen.get(0);
    }
//***********************************************************************************************************
//Method: mutate()
//Description: Mutates the chromosome by flipping two bit positions
//Returns:          String              the newly mutated chromosome
//Calls: None
//Parameters:       String chromosome   the chromosome to be mutated
//***********************************************************************************************************
    public static String mutate(String chromosome) {
        //calculate the position of mutation
        Random rand = new Random();
        int position = rand.nextInt(chromosome.length());
        int position2 = rand.nextInt(chromosome.length());
        //flip two bits
        StringBuilder sequence = new StringBuilder(chromosome);
        char temp = chromosome.charAt(position);

        //set first char to second
        sequence.setCharAt(position, sequence.charAt(position2));

        //set second to first
        sequence.setCharAt(position2, temp);
        return sequence.toString();
    }

//***********************************************************************************************************
//Method: crossover()
//Description: crosses the genes of two population members and returns an array of two members to add to the population
//Returns:          PopulationMember[] members  the two members that'll be added to the population
//Calls: None
//Parameters:       PopulationMember a          the dad of the crossover
//                  PopulationMember b          the mom of the crossover
//***********************************************************************************************************
    public static PopulationMember[] crossover(PopulationMember a, PopulationMember b) {
        //pick crossover point
        Random rand = new Random();
        int firstCrossoverPoint = rand.nextInt(a.sequence.length() + 1);
        //int secondCrossoverPoint = rand.nextInt(a.sequence.length() + 1);
        String sequence1 = "";

        sequence1 += a.sequence.substring(0,firstCrossoverPoint);
        sequence1 += b.sequence.substring(firstCrossoverPoint, a.sequence.length());
        
        String sequence2 = "";
        sequence2 += b.sequence.substring(0,firstCrossoverPoint);
        sequence2 += a.sequence.substring(firstCrossoverPoint, a.sequence.length());

        //sequence += a.sequence.substring(0, crossoverPoint);
        //sequence += b.sequence.substring(crossoverPoint, a.sequence.length());
        PopulationMember[] sequences = new PopulationMember[2];
        sequences[0] = new PopulationMember(sequence1);
        sequences[1] = new PopulationMember(sequence2);
        
        return sequences;
    }
//***********************************************************************************************************
//Method: readShipmentInfo()
//Description: Reads the text file with the shipment data
//Returns:          ArrayList<Item>     the item data              
//Calls: None
//Parameters: None
//***********************************************************************************************************
    public static ArrayList<Item> readShipmentInfo() {
        TextFileClass textFile = new TextFileClass();
        textFile.getFileName("Specify the text file to be read:");
        textFile.getFileContents();
        String[] textContents = textFile.text;

        if (textContents[0] != null) {
            ArrayList<Item> shipmentData = new ArrayList<Item>();
            int totalWeightOfShipment = 0;
            int totalValueOfShipment = 0;
            //negate the length
            int index = 1;
            while (textContents[index] != null) {
                Item cur = new Item(Integer.parseInt(textContents[index++]), Integer.parseInt(textContents[index++]));
                totalValueOfShipment += cur.value;
                totalWeightOfShipment += cur.weight;
                shipmentData.add(cur);
            }

            //print the item data
            System.out.println("Item:\tWeight:\tValue:");
            for (int i = 0; i < shipmentData.size(); i++) {
                Item cur = shipmentData.get(i);
                System.out.println(i + "\t" + cur.weight + "\t" + cur.value);
            }

            System.out.println("Weight of all items: " + totalWeightOfShipment + "; Value of all items: " + totalValueOfShipment);
            return shipmentData;
        } else {
            return readShipmentInfo();
        }

    }
//***********************************************************************************************************
//Method: initializeFirstGeneration()
//Description: Creates the first generation for the GA
//Returns:          ArrayList<PopulationMember> generation  the created generation
//Calls: None
//Parameters:       int populationSize          number of members in the population
//                  int chromosomeLength        the size of the chromosome to be used
//***********************************************************************************************************
    private static ArrayList<PopulationMember> initializeFirstGeneration(int populationSize, int chromosomeLength) {
        double factor = .5;
        while (true) {
            ArrayList<PopulationMember> generation = new ArrayList<PopulationMember>();

            Random rand = new Random();
            for (int i = 0; i < populationSize; i++) {

                String chromosome = "";
                //generate random chromsome
                for (int j = 0; j < chromosomeLength; j++) {
                    //have alot of 1s and alot of 0s in other population
                    double random = rand.nextDouble();

                    if (random < .3) {
                        chromosome += '1';
                    } else {
                        chromosome += '0';
                    }
                //String str = Integer.toString(rand.nextInt(2));
                    //chromosome += str;
                }

                PopulationMember cur = new PopulationMember(chromosome);
                generation.add(cur);
            }

               return generation; 
        }
    }
//***********************************************************************************************************
//Method: displayImage()
//Description: Mutates the chromosome by flipping two bit positions
//Returns: None
//Calls:            ImageConstruction           creates the image
//Parameters:       int[] generationCount       the counts of each generation 
//                  int binSize                 the size of each bin
//***********************************************************************************************************
    private static void displayImage(int[] generationCount, int binSize) {
        ImageConstruction myImage = new ImageConstruction(500, 500, 0, 500, 0, 500, 1);
        myImage.displaySetup();
        myImage.displayImage(false, "GA Histogram", true);
        
        int[] counts = new int[25];
        for(int i = 0; i < generationCount.length; i++){
            int count = generationCount[i];
            for (int j = 0; j < counts.length; j++) {
                if(count >= (binSize * j) && count < (binSize * j) + binSize){
                    counts[j]++;
                    break;
                }
                
            }
        }
        
        double barWidth = 495.0 / 30.0;
        boolean trigger = true;
        for(int i = 0; i < counts.length; i++){
            double barHeight = ((495 * counts[i]) / generationCount.length) + 50;
            if(trigger){
                trigger = false;
                myImage.insertBox(((barWidth * i)+ barWidth) + barWidth + barWidth, 50, ((barWidth * i) + (barWidth * 3)) + barWidth, barHeight, 0, 250, 0, true);
            }else{
                trigger = true;
                myImage.insertBox(((barWidth * i)+ barWidth) + barWidth + barWidth, 50, ((barWidth * i) + (barWidth * 3)) + barWidth, barHeight, 0, 0, 250, true);
            }
        }
        //myImage.insertText(20, 50, "Batch", 50);
        //chart lines
        for (int i = 0; i < 10; i++) {
            //myImage.insertLine(50 * i, 50, 50 * i, 55, 250, 0, 0);
           
            myImage.insertLine(50, 50 * i, 450, 50 * i, 250, 0, 0);
        }
        myImage.insertLine(50, 50, 50, 450, 250, 0, 0);
        myImage.insertLine(50, 50, 450, 50, 250, 0, 0);
        
        KeyboardInputClass input = new KeyboardInputClass();
        String batch = input.getKeyboardInput("Go again? (Y/N)");
        if (batch.toLowerCase().equals("n")) {
            System.exit(-1);
        } else {
            myImage.closeDisplay();
        }
    }
}
