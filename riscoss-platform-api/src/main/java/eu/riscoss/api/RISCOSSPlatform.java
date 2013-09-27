package eu.riscoss.api;

import java.util.List;

import org.xwiki.component.annotation.Role;

import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Answer;

/**
 * RISCOSSPlatform. This interface provides all the methods for accessing all the functionalities provided by the
 * RISCOSS platform. such as methods for storing/retrieving entities stored in the "knowledge base".
 *
 * @version $Id$
 */
@Role
public interface RISCOSSPlatform
{
    /**
     * Retrieve the tool factory for creating instances of a given tool.
     *
     * @param toolId the id of the tool.
     * @return the factory for instantiating the tool.
     */
    ToolFactory getToolFactory(String toolId);

    /**
     * @return all the tool factories registered in the system.
     */
    List<ToolFactory> getToolFactories();

    /**
     * Store a measurement entity object in the platform knowledge base.
     *
     * @param measurement the measurement to be stored.
     */
    void storeMeasurement(Measurement measurement);
    
    /**
     *  The registerQuestion method should propagate the question identified by 
     *  questionId to the user interface. The consumer shall notify the producer 
     *  when the question is answered by the user. It may happen that the question 
     *  was already asked by other component, in this case the consumer should 
     *  directly notify the answer to the producer 
     *  
     *  @param questionId the id of the question to be answered.
     *  @param questionProcessor instance that can "process()" the question once answered.
     */
    void registerQuestion(String questionId, QuestionProcessor questionProcessor);
}
