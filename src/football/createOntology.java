package football;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.XSD;

import java.io.IOException;
import java.util.Vector;

public class createOntology {


    static public Vector<Individual> make_dataProperties_individuals(Vector<String[]> data, OntModel model,
                                                                     String uri, OntClass dataClass)
    {
        Vector<Individual> individuals = new Vector<Individual>();

        for(int i=1; i<data.size(); i++)
        {
            if(data.get(i)[0].contains(" "))
                data.get(i)[0] = data.get(i)[0].replace(" ", "_");

            Individual instance;
            if(dataClass.getLocalName().contains("match"))
                instance = dataClass.createIndividual(uri + i);
            else
                instance = dataClass.createIndividual(uri + data.get(i)[0]);


            individuals.add(instance);
            for(int j=0; j<data.get(0).length; j++)
            {

                if(data.get(0)[j].contains(" ") || data.get(0)[j].contains("(") || data.get(0)[j].contains(")")) {
                    data.get(0)[j] = data.get(0)[j].replace(" ", "_");
                    data.get(0)[j] = data.get(0)[j].replace("(", "");
                    data.get(0)[j] = data.get(0)[j].replace(")", "");
                }

                DatatypeProperty ontologyProperty = model.createDatatypeProperty(uri + data.get(0)[j]);
                ontologyProperty.addDomain(dataClass);

                if(StringUtils.isNumeric(data.get(1)[j]) && !data.get(0)[j].contains("points_per_game"))
                    ontologyProperty.addRange(XSD.integer);
                else if(data.get(0)[j].contains("points_per_game"))
                    ontologyProperty.addRange(XSD.xdouble);
                else
                    ontologyProperty.addRange(XSD.xstring);

                instance.addProperty(ontologyProperty, data.get(i)[j]);

            }
        }

        return individuals;
    }


    static public void fill_object_properties(Vector<Individual> domain, Vector<Individual> range, Vector<String[]> data,
                                              DatatypeProperty rangeProperty, DatatypeProperty domainProperty,
                                              ObjectProperty property, int index)
    {
        for(int i=0; i<domain.size(); i++)
        {
            if(data.get(i)[index].contains(" "))
                data.get(i)[index] = data.get(i)[index].replace(" ", "_");

            for (Individual individual : range) {
                if (individual.getPropertyValue(rangeProperty).equals(domain.get(i).getPropertyValue(domainProperty))) {
                    domain.get(i).addProperty(property, individual);
                    break;
                }
            }

        }
    }


    public static void main(String[] args) throws IOException {
        readAndConvert csvRead = new readAndConvert();
        Vector<String[]> matchesData = readAndConvert.getFullData("src\\england-premier-league-matches-2018-to-2019-stats.csv");
        Vector<String[]> playersData = readAndConvert.getFullData("src\\england-premier-league-players-2018-to-2019-stats.csv");
        Vector<String[]> teamsData = readAndConvert.getFullData("src\\england-premier-league-teams-2018-to-2019-stats.csv");

        //create ontology
        String baseURI = "http://www.semanticweb.org/nada/ontologies/2021/11/project1#";
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        // create classes
        OntClass matchClass =  model.createClass(baseURI + "match");
        OntClass playerClass = model.createClass(baseURI + "player");
        OntClass teamClass = model.createClass(baseURI + "team");


        // create object Properties
        ObjectProperty playsInClub = model.createObjectProperty( baseURI + "playsInClub" );
        playsInClub.addDomain(playerClass);
        playsInClub.addRange(teamClass);

        ObjectProperty hasHomeTeam = model.createObjectProperty( baseURI + "hasHomeTeam" );
        hasHomeTeam.addDomain(matchClass);
        hasHomeTeam.addRange(teamClass);

        ObjectProperty hasAwayTeam = model.createObjectProperty( baseURI + "hasAwayTeam" );
        hasAwayTeam.addDomain(matchClass);
        hasAwayTeam.addRange(teamClass);


        // create data properties and individuals and filling data properties
        //for matches with individual is sequence id
        Vector<Individual> matchIndividuals =  make_dataProperties_individuals(matchesData, model, baseURI, matchClass);

        //for players with the full_name
        Vector<Individual> playerIndividuals =  make_dataProperties_individuals(playersData, model, baseURI, playerClass);

        //for teams with the team_name
        Vector<Individual> teamIndividuals =  make_dataProperties_individuals(teamsData, model, baseURI, teamClass);


        DatatypeProperty commonNameProperty = model.getDatatypeProperty(baseURI + teamsData.get(0)[1]);

        DatatypeProperty currentClubProperty = model.getDatatypeProperty(baseURI + playersData.get(0)[5]);
        DatatypeProperty homeTeamNameProperty = model.getDatatypeProperty(baseURI + matchesData.get(0)[4]);
        DatatypeProperty awayTeamNameProperty = model.getDatatypeProperty(baseURI + matchesData.get(0)[5]);


        // filling object properties
        // playsInClub (player, team)  with column (current_club)
        fill_object_properties(playerIndividuals, teamIndividuals, playersData, commonNameProperty, currentClubProperty, playsInClub, 5);
        // hasHomeTeam (match, team) with column (home_team_name)
        fill_object_properties(matchIndividuals, teamIndividuals, matchesData, commonNameProperty, homeTeamNameProperty, hasHomeTeam, 4);
        // hasAwayTeam (match, team) with column (away_team_name)
        fill_object_properties(matchIndividuals, teamIndividuals, matchesData, commonNameProperty, awayTeamNameProperty, hasAwayTeam, 5);


        model.write(System.out);
    }


}