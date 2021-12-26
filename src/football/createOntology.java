package football;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.XSD;

import java.io.IOException;
import java.util.Vector;

public class createOntology {

    static public void make_dataProperty(Vector<String[]> data, OntModel model, String uri, OntClass dataClass)
    {
        for(int j=0; j<data.get(0).length; j++)
        {
            DatatypeProperty ontologyProperty = model.createDatatypeProperty(uri + data.get(0)[j]);
            ontologyProperty.addDomain(dataClass);
            if(data.get(0)[j].contains("overall") || data.get(0)[j].contains("count"))
                ontologyProperty.addRange(XSD.integer);
            else if(data.get(0)[j].equalsIgnoreCase("points_per_game"))
                ontologyProperty.addRange(XSD.xdouble);
            ontologyProperty.addRange(XSD.xstring);

        }
    }

    /*static public void make_dataProperties_individuals(Vector<String[]> data, OntModel model, String uri, OntClass dataClass)
    {
        for(int i=1; i<data.size(); i++)
        {
            Individual ind = dataClass.createIndividual(data.get(i)[0]);
            for(int j=0; j<data.get(0).length; j++)
            {
                DatatypeProperty ontologyProperty = model.createDatatypeProperty(uri + data.get(0)[j]);
                ontologyProperty.addDomain(dataClass);
                if(data.get(0)[j].contains("overall") || data.get(0)[j].contains("count"))
                    ontologyProperty.addRange(XSD.integer);
                else if(data.get(0)[j].equalsIgnoreCase("points_per_game"))
                    ontologyProperty.addRange(XSD.xdouble);
                ontologyProperty.addRange(XSD.xstring);


            }
        }
    }*/

    public static void main(String[] args) throws IOException {
        readAndConvert csvRead = new readAndConvert();
        Vector<String[]> matchesData = readAndConvert.getFullData("src\\matches.csv");
        Vector<String[]> playersData = readAndConvert.getFullData("src\\players.csv");
        Vector<String[]> teamsData = readAndConvert.getFullData("src\\teams.csv");

        //create ontology
        String baseURI = "http://www.semanticweb.org/nada/ontologies/2021/11/project1#";
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        // create classes
        OntClass matchClass =  model.createClass(baseURI + "match");
        OntClass playerClass = model.createClass(baseURI + "player");
        OntClass teamClass = model.createClass(baseURI + "team");

        // create object Properties
        ObjectProperty playedMatch = model.createObjectProperty( baseURI + "playedMatch" );
        playedMatch.addDomain(playerClass);
        playedMatch.addRange(matchClass);

        ObjectProperty playsInClub = model.createObjectProperty( baseURI + "playsInClub" );
        playedMatch.addDomain(playerClass);
        playedMatch.addRange(teamClass);

        ObjectProperty hasHomeTeam = model.createObjectProperty( baseURI + "hasHomeTeam" );
        playedMatch.addDomain(matchClass);
        playedMatch.addRange(teamClass);

        ObjectProperty hasAwayTeam = model.createObjectProperty( baseURI + "hasAwayTeam" );
        playedMatch.addDomain(matchClass);
        playedMatch.addRange(teamClass);


//        make_dataProperties_individuals(matchesData, model, baseURI, matchClass);
//        make_dataProperties_individuals(playersData, model, baseURI, playerClass);
//        make_dataProperties_individuals(teamsData, model, baseURI, teamClass);



        //create data properties
        make_dataProperty(matchesData, model, baseURI, matchClass);
        make_dataProperty(playersData, model, baseURI, playerClass);
        make_dataProperty(teamsData, model, baseURI, teamClass);


        model.write(System.out);

        // individuals
            // for match making individuals with the timestamp
        //make_individual(matchesData, matchClass);

            // for player making individuals with the full_name
        //make_individual(playersData, playerClass);

            // for teams making individuals with the team_name
        //make_individual(teamsData, teamClass);




















































    }






}
