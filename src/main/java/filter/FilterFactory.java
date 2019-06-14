package filter;

import calculator.commands.CodingHabitsCommand;
import calculator.commands.OrdonateWordCommand;
import calculator.commands.SimilarCodeCommand;
import calculator.commands.VariableNameCommand;
import com.sun.org.apache.xpath.internal.operations.Mod;
import utils.Logger;
import model.Exam;
import model.Student;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FilterFactory {

    private static FilterFactory instance;
    private final List<java.lang.Class> filtersActivated;
    private final HashMap<Class,String> availableFilters = new HashMap<Class, String>()
    {{
        put(CommonCodeSuppressionFilter.class, "Supprimer le code commun entre les élèves.");
        put(ImportationSuppressionFilter.class, "Supprimer les lignes d'import dans les fichiers.");
        put(KeywordFilter.class, "Remplacer les mots clés du langage java par XXX.");
        put(ModelSuppressionFilter.class, "Supprimer le code commun avec le professeur.");
        put(SpaceSeparatorFilter.class, "Supprimer les espaces / tabulations etc. en trop dans le code.");
    }};

    private FilterFactory() {
        this.filtersActivated = new LinkedList<>();
    }

    public void addFilter(Class filterClass) {
        this.filtersActivated.add(filterClass);
    }

    public void removeFilter(Class filterClass) {
        this.filtersActivated.remove(filterClass);
    }

    public Exam executeFilers(Exam exam, Class filter) {
        if (filter == ModelSuppressionFilter.class) {
            if (exam.getProfessorLines().size() == 0) {
                return exam;
            }
        }
        try {
            exam.accept((FilterVisitor) filter.getConstructor(Exam.class).newInstance(exam));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return exam;
    }

    public Exam executeAllFilters(Exam exam) {
        for (Class filter : filtersActivated) {
            executeFilers(exam,filter);
        }
        return exam;
    }

    public void listFilters() {
        Logger.info("Filters enabled :");
        this.filtersActivated.stream().forEach(System.out::println);
    }

    public List getActivatedFilters() {
        return filtersActivated;
    }

    public HashMap<Class, String> getAvailableFilters() {
        return availableFilters;
    }

    public static FilterFactory getInstance() {
        if (instance == null) {
            instance =  init();
        }
        return instance;
    }

    public static FilterFactory init() {
        FilterFactory filterFactory = new FilterFactory();
        filterFactory.addFilter(ModelSuppressionFilter.class);
        filterFactory.addFilter(SpaceSeparatorFilter.class);
        filterFactory.addFilter(ImportationSuppressionFilter.class);
        filterFactory.addFilter(KeywordFilter.class);
        filterFactory.addFilter(CommonCodeSuppressionFilter.class);
        return filterFactory;
    }
}