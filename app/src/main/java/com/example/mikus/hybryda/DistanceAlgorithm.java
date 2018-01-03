package com.example.mikus.hybryda;

import java.util.ArrayList;
import java.util.List;

public class DistanceAlgorithm {

    public void AvargePoint() {
        double wynik = 0;
        int i = 0;
        while (i != Points.PointsList.count_Points_local()) {
            for (int j = 0; j < 6; j++) {

                wynik = (Points.PointsList.getByIndexPointCollection_Local(i).getRSSI(j)) + (Points.PointsList.getByIndexPointCollection_Local(i + 1).getRSSI(j)) + (Points.PointsList.getByIndexPointCollection_Local(i + 2).getRSSI(j));
                wynik = wynik / 3;
                Points.PointsList.getByIndexPointCollection_Local(i).replaceRSSI(j, Double.toString(wynik));

            }
            Points.PointsList.removeByIndex(i + 1);
            Points.PointsList.removeByIndex(i + 1);

            i++;
        }
    }

    public int Distance() {
        List<Double> DistanceList = new ArrayList<>();
        double wynik;
        for (int i = 0; i < Points.PointsList.count_Points_local(); i++) {
            wynik = (double) 0;
            for (int j = 0; j < Points.PointsList.getByIndexPointCollection_Local(i).CountListRSSI(); j++) {
                for (int l = 0; l < Points.PointsList.getByIndexPointCollection_External().CountListRSSI(); l++) {

                    if ((Points.PointsList.getByIndexPointCollection_External().getMAC(l).equals(Points.PointsList.getByIndexPointCollection_Local(i).getMAC(j)))) {
                        wynik += Math.pow(((Points.PointsList.getByIndexPointCollection_Local(i).getRSSI(j)) - (Points.PointsList.getByIndexPointCollection_External().getRSSI(l))), 2);
                    }
                }
                if (Points.PointsList.getByIndexPointCollection_External().returnBSSID().contains(Points.PointsList.getByIndexPointCollection_Local(i).getMAC(j)) == false) {
                    wynik += Math.pow(Points.PointsList.getByIndexPointCollection_Local(i).getRSSI(j) - (-100), 2);
                }
            }
            wynik = Math.sqrt(wynik);
            DistanceList.add(wynik);
    }

        for (int i = 0; i < DistanceList.size(); i++) {
     //       Log.v("DistanceList", "" + DistanceList.get(i) + " Punkt: " + i);
         //   System.out.println(DistanceList.get(i));
        }

        double min = DistanceList.get(0);
        int index =0;
        for (int i =1; i<DistanceList.size(); i++) {
            if (DistanceList.get(i) < min) {
                min = DistanceList.get(i);
                index = i;
            }
        }
  //      Log.v("Minimum","" + min + " " +index);
        return index;

      //  System.out.println(index +" >>> "+min);
    }

}