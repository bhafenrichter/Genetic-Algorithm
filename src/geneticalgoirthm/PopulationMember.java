package geneticalgoirthm;

public class PopulationMember {
    String sequence;
    double fitness;
    
    public PopulationMember(){
        
    }
    
    public PopulationMember(String sequence, double fitness){
        this.sequence = sequence;
        this.fitness = fitness;
    }
}
