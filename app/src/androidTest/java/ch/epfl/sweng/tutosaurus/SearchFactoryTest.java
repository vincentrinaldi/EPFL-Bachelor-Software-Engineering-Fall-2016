package ch.epfl.sweng.tutosaurus;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.sweng.tutosaurus.SearchFactory.NoSuchSearchCriterion;
import ch.epfl.sweng.tutosaurus.SearchFactory.SearchByName;
import ch.epfl.sweng.tutosaurus.SearchFactory.SearchCriterion;
import ch.epfl.sweng.tutosaurus.SearchFactory.SearchCriterionFactory;

import static org.junit.Assert.assertEquals;

/**
 * Created by albertochiappa on 09/12/16.
 */

public class SearchFactoryTest {
    SearchCriterionFactory searchCriterionFactory;

    @Before
    public void createFactory(){
        searchCriterionFactory = new SearchCriterionFactory();
    }

    @Test
    public void returnsKnownCriterion(){
        SearchCriterion searchCriterion = searchCriterionFactory.findSearchCriterionNamed("findTutorByName");
        assertEquals(searchCriterion.getClass(), SearchByName.class);
    }

    @Test (expected = NoSuchSearchCriterion.class)
    public void throwsExceptionWhenUnknownCriterionIsCalled(){
        SearchCriterion searchCriterion = searchCriterionFactory.findSearchCriterionNamed("aRandomSearchCriterionThatDoesNotExist");
    }
}
