package ch.epfl.sweng.tutosaurus.SearchFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for criteria
 */
public class SearchCriterionFactory {
    Map<String, SearchCriterion> searchNames;

    public SearchCriterionFactory(){
        searchNames = new HashMap<String, SearchCriterion>();
        registerSearches();
    }

    private void registerSearches(){
        searchNames.put("findTutorByName", new SearchByName());
        searchNames.put("findTutorByCourse", new SearchByCourse());
        searchNames.put("showFullList", new ShowFullTutorList());
    }

    public SearchCriterion findSearchCriterionNamed(String operatorName) {
        SearchCriterion searchCriterion = searchNames.get(operatorName);
        if(searchCriterion == null) {
            throw new NoSuchSearchCriterion();
        }
        else{
            return searchCriterion;
        }
    }
}