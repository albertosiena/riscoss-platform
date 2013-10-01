package eu.riscoss.internal;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import eu.riscoss.api.RISCOSSPlatform;
import eu.riscoss.api.ToolFactory;
import eu.riscoss.api.model.QuestionProcessor;
import eu.riscoss.api.model.Measurement;
import eu.riscoss.api.model.Answer;
import eu.riscoss.api.model.QuestionnaireProcessor;

/**
 * RISCOSSPlatformImpl.
 *
 * @version $Id$
 */
@Component
@Singleton
public class RISCOSSPlatformImpl implements RISCOSSPlatform
{
    @Inject
    private Logger logger;

    @Inject
    private ComponentManager componentManager;

    @Override public ToolFactory getToolFactory(String toolId)
    {
        try {
            return componentManager.getInstance(ToolFactory.class, toolId);
        } catch (ComponentLookupException e) {
            logger.error(String.format("Error while retrieving tool factories", e));
        }

        return null;
    }

    @Override public List<ToolFactory> getToolFactories()
    {
        try {
            return componentManager.getInstanceList(ToolFactory.class);
        } catch (ComponentLookupException e) {
            logger.error(String.format("Error while retrieving tool factories", e));
        }

        return Collections.EMPTY_LIST;
    }

    @Override public void storeMeasurement(Measurement measurement)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    
    @Override public void registerQuestion(String questionId, QuestionProcessor questionProcessor)
    {
        // may be here or in other part of the code, executed once you got the answer
        Answer answer=new Answer();
        answer.addAnswer("my answer");
        answer.addAnswer("my second answer of a multichoice question");
        questionProcessor.setAnswer(answer);
        questionProcessor.process();
        
        // You need to save somewhere the question ID / answer just in case some component 
        // re-register a question (we don't want to ask the same question more than once).
    }
    
    @Override public void registerQuestionnarie(String[] questionIds, QuestionnaireProcessor questionnarieProcessor)
    {
        // may be here or in other part of the code, executed once you got the answer
        Answer answer;
        answer=new Answer();
        answer.addAnswer("my answer");
        answer.addAnswer("my second answer of a multichoice question");
        questionnarieProcessor.addAnswer("SOME ID" /*questionIds[0]*/, answer);
        answer=new Answer();
        answer.addAnswer("my answer");
        questionnarieProcessor.addAnswer("OTHER ID" /*questionIds[1]*/, answer);
        questionnarieProcessor.process();

        // You need to save somewhere the pair {question ID, answer} just in case some component 
        // re-register a question (we don't want to ask the same question more than once).
}
}
