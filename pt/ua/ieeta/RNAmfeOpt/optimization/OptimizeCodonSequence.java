
package pt.ua.ieeta.RNAmfeOpt.optimization;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.ieeta.RNAmfeOpt.sa.*;

/**
 *
 * @author Paulo Gaspar
 */
public class OptimizeCodonSequence extends Thread
{
    public SimulatedAnnealing sa;
    
    public OptimizeCodonSequence(String sequence, IFitnessAssessor fitnessCalculator, int numIterations, int startNuc, int endNuc)
    {
        assert sequence != null;
        assert fitnessCalculator != null;
        assert numIterations > 0;
        assert endNuc > startNuc;
        assert (endNuc  - startNuc + 1)%3 == 0; // number of coding nucleotides is multiple of 3
        assert startNuc > 0;
        assert endNuc > 0;
        
        /* Create seed. */
        List<IOptimizationTarget> featureList = new Vector<IOptimizationTarget>();
        featureList.add(new CodonSequenceOptimizationTarget(sequence, 1, startNuc-1, endNuc-1));
        EvolvingSolution seed = new EvolvingSolution(featureList);
        
        sa = new SimulatedAnnealing(fitnessCalculator, seed, numIterations, 0.85, 0.2, 0.1, 0.3);
    }
    
    public OptimizeCodonSequence(String codonSequence, IFitnessAssessor fitnessCalculator, int numIterations)
    {
        /* Default start and end nucleotides are the first and last ones. */
        this(codonSequence,  fitnessCalculator,  numIterations, 1, codonSequence.length());
    }
    
    @Override
    public void run()
    {
        sa.start();
        try
        {
            sa.join();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(OptimizeCodonSequence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public EvolvingSolution getSolution()
    {
        return sa.getSolution();
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("* Starting codon sequence optimization experiment *");

        String sequence = "AUGGAGGUGGCUGGCUGUUUCUGCAACAUGGAGCUGGGGUGGGGCAUCCCAGUGUCAAAGACUGCAGAGGGGAUUGCUGCACUGCACAGCUUGCAAGCCUUUCCUGAUGACCAGGAGAGUUCCAUAACCAGGUCUGUAGUUCCCACCUUGGCAGACACAGCCAAGCCCUCAGCCCCAGUCACUUCCCACUCCCUGCUCUCCAGGUACCACCCGGGUCAGUGA";
        OptimizeCodonSequence codonSequenceOptimizer = new OptimizeCodonSequence(sequence, new PseudoEnergyFitnessAssessor(), 4000);
        codonSequenceOptimizer.start();
        codonSequenceOptimizer.join();
        
        System.out.println("* Terminated codon sequence optimization experiment *");
        
        System.out.println("Solution: ");
        codonSequenceOptimizer.getSolution().print();
        
        System.out.println("Score: " + codonSequenceOptimizer.sa.getScore());
    }
}
