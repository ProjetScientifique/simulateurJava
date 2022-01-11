package EquipeUn.Simulator;

import java.io.IOException;
import java.util.ArrayList;

import model.Coord;
import rest.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        Map test = new Map("VybYldGG1GIV15GOG3meIG9QEil7MxfD");
        ArrayList<Coord> coord = test.getCoords(new Coord(45.7791495, 4.8779960), new Coord(45.7657769, 4.8340385));
        System.out.println(coord);
    }
}
