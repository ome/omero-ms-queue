package ome.smuggler.config;

import static util.sequence.Streams.emptyIfNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;


/**
 * Enumerates the profile constants used to select configuration profiles.
 */
public class Profiles {
    // NB why not use an enum? Because it won't work with @Profile annotations. 
    // In fact the value of the annotation needs to be a constant, so something
    // like @Profile(ProfilesEnum.SomeEnumValue.name()) will make the compiler
    // puke up.

    /**
     * Configuration for the development environment. 
     */
    public static final String Dev = "dev";
    
    /**
     * Configuration for the QA environment. 
     */
    public static final String QA = "qa";
    
   /**
    * Configuration for the production environment. 
    */
   public static final String Prod = "prod";
   
   /**
    * Finds the strings in the given list that don't match the name of any of
    * our profiles.
    * @param xs a list of candidate profile names.
    * @return a stream with the names that don't match.
    */
   public static Stream<String> findUnknownProfiles(Stream<String> xs) {
       Set<String> knowProfiles = new HashSet<>();
       knowProfiles.addAll(Arrays.asList(Dev, QA, Prod));
       
       return emptyIfNull(xs).filter(x -> !knowProfiles.contains(x));
   }
   
}

