package com.disneystreaming.i21;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is the core of the test, implement the method {@link #getTopTitles(String, String, String)}.
 *
 * Consider using the utility methods in {@link JsonConverters}.
 */
final class TopTitlesService {

    private TopTitlesService() {}

    /**
     * Implements the top titles selection algorithm, see <code>README.md</code> for details. See
     * <code>test/java/.../TopTitlesServiceTest</code> to verify your implementation.
     *
     * The JSON parsing and conversion is available in {@link JsonConverters#convertContentData(String)} and
     * {@link JsonConverters#convertPreferences(String)}.
     *
     * @param contentDataJson the JSON data of an array of {@link Content}, i.e. <code>"[{...}, {...}, ...]"</code>
     * @param brandPreferencesJson the JSON data of a dictionary, where the keys and values represent the user's <em>brand preferences</em>
     * @param contentTypePreferencesJson the JSON data of a dictionary, where the keys and values represent the user's <em>content type preferences</em>
     * @return the top titles
     */
    static List<String> getTopTitles(String contentDataJson, String brandPreferencesJson, String contentTypePreferencesJson) {
        int dislike = -20;
        int indifferent = +0;
        int like = +10;
        int adore = +30;
        int love = +50;

        String title = "";
        String brand = "";
        String availability = "";
        SimpleDateFormat availableDate = new SimpleDateFormat("yyyy-MM-dd");
        Date givenDate = null;
        Date requiredDate = null;
        Boolean isKidsContent = false;
        int popularity = 0;
        int dateResult = 0;
        String contentType = "";

        HashMap<String, Integer>objectInfo = new HashMap<>();
        ArrayList<Integer> arrylistPopularity = new ArrayList<>();
        ArrayList<String> arrylistTitleNames = new ArrayList<>();




        String contentDataSTR = contentDataJson;
        String[] contentData = contentDataSTR.split("}, \\{");


      for (int i = 0; i < contentData.length; i++){

          boolean dateFlag = false;
          boolean usFlag = false;

          String tempContentDataSTR =  contentData[i].toString();
          tempContentDataSTR = tempContentDataSTR.replace("}]", "");


          tempContentDataSTR = tempContentDataSTR.replace("\", \"", ", ");


          //Splits Each element of Object
          String[] arryContentData = tempContentDataSTR.split(", ");
          ArrayList<String> contentElements = new ArrayList<String>();

          for (int j =0; j < arryContentData.length; j++){
              //Converts the array to a string with everything before the colon removed
              String removeColon = arryContentData[j].substring(arryContentData[j].indexOf(":") + 1);
              //Remove the white space from the front and back
              removeColon = removeColon.trim();
              removeColon = removeColon.replace("\"", "");

              //Add each element to contentElements so data can be extracted
              contentElements.add(removeColon);
          }


          title = contentElements.get(0);
          brand = contentElements.get(1);


          availability = contentElements.get(2);
          availability = availability.replace("[ ]\"", "");
          String[] arryAvailability = availability.split(",");

          for (String s : arryAvailability) {
              if (s.contains("US")) {

                  System.out.println(s);
                  usFlag = true;
                  System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + usFlag);
              }
          }


          String availableDateSTR = contentElements.get(3);
          availableDateSTR = availableDateSTR.split("T")[0];
          System.out.println("LALALALLALALALALALALLALA>" + availableDateSTR + "<");

          try{

              givenDate = availableDate.parse(availableDateSTR);
              requiredDate = availableDate.parse("2020-01-01");
              dateResult = givenDate.compareTo(requiredDate);
              System.out.println("OMGGGGGGGG" + dateResult);



              System.out.println(dateFlag);

              isKidsContent = Boolean.parseBoolean(contentElements.get(4).replace(" ", ""));
              System.out.println("THIS IS KIDS CONTENT " + isKidsContent);
              //Attempt to take the 5th index (Popularity score) from the ArrayList and typecast it to int
              popularity = Integer.parseInt(contentElements.get(5).replace(" ", ""));
          }
          catch (Exception e){
              System.out.println(e);
          }

          if(dateResult <= 0){
              dateFlag = true;
          }

          contentType = contentElements.get(6);

          Map<String, String> brandMap = generateHashmap(brandPreferencesJson);

          if (brandMap.containsKey(brand)) {
              String brandResult = brandMap.get(brand);

              popularity = switchCasePopularity(dislike, indifferent, like, adore, love, popularity, brandResult);
          }

          HashMap<String, String> contentTypeMap = generateHashmap(contentTypePreferencesJson);



          if (contentTypeMap.containsKey(contentType)) {
              String contentTypeResult = contentTypeMap.get(contentType);
              popularity = switchCasePopularity(dislike, indifferent, like, adore, love, popularity, contentTypeResult);
          }


          if(dateFlag && usFlag){
              arrylistPopularity.add(popularity);
              arrylistTitleNames.add(title+"<"+popularity);
              //objectInfo.put(title, popularity);
          }

      }
        Collections.sort(arrylistPopularity, Collections.reverseOrder());
        Collections.sort(arrylistTitleNames);

        System.out.println("FUCKKKKKKKKK "+ arrylistTitleNames);


        ArrayList<String> finalNames = new ArrayList<String>();
        int finalSize = 0;
        if (arrylistPopularity.size() > 5){
            finalSize = 5;
        }
        else
            finalSize = arrylistPopularity.size();

        for (int x =0; x < finalSize; x++){

            System.out.println(arrylistPopularity.get(x));

            for(String names: arrylistTitleNames){

                if (names.contains(String.valueOf(arrylistPopularity.get(x)))){
                    finalNames.add(names.split("<")[0]);
                }

            }





        }


        System.out.println("THIS IS IT !!!!!!! " + finalNames);
        return finalNames;
        //return myArrayList;
    }



    private static HashMap<String, String> generateHashmap(String jsonFile){
        HashMap<String, String> myHashMap = new HashMap<String, String>();
        String jsonFileSTR = jsonFile;

        jsonFileSTR = jsonFileSTR.replace("{", "");
        jsonFileSTR = jsonFileSTR.replace("}", "");
        jsonFileSTR = jsonFileSTR.replace("\"", "");
        String[] contentPairs = jsonFileSTR.split(",");

        for (String pair1 : contentPairs) {
            String[] keyValue1 = pair1.split(":");

            myHashMap.put(keyValue1[0].trim(), (keyValue1[1].trim()));

        }

        return myHashMap;
    }

    private static int switchCasePopularity(int dislike, int indifferent, int like, int adore, int love, int popularity, String stringToCheck) {
        switch (stringToCheck) {
            case "dislike":
                popularity += dislike;

                break;

            case "indifferent":
                popularity += indifferent;
                break;

            case "like":
                popularity += like;

                break;

            case "adore":
                popularity += adore;
                break;

            case "love":
                popularity += love;
                break;
            default:
                System.out.println("NO MATCH FOUND ");
        }
        return popularity;
    }

}






