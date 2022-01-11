package EquipeUn.Emergency;

import java.util.ArrayList;

import org.json.JSONArray;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ArrayList<String> arr = new ArrayList<String>();
        arr.add("oajzoe");
        arr.add("oazjeozajejoza");
        JSONArray testArr = new JSONArray()
        		.put(arr)
        		.put("hi")
        		.put("nnnn");
        System.out.println(testArr);
    }
}
