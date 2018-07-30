/*Implementation from https://github.com/castamir/Mogger/blob/master/src/cz/vutbr/fit/mogger/DTW.java*/

package calculations;

 
import static java.lang.Math.sqrt;

import java.util.LinkedList;
import java.util.List;

public class DTW {

    public DTW() {

    }
    public double calcDTW(double [] frameToCheck, double [] frameFromBase)
    {
        List<Double[]> path= new LinkedList<Double[]>();

    	int sizeM=frameToCheck.length;
    	int sizeN=frameFromBase.length;
        double[][] distances = new double[sizeN][sizeM];
        double[][] accumulated_cost= new double[sizeN][sizeM];

        for (int i=0;i<sizeN;i++)
        {
        	for (int j=0;j<sizeM;j++)
        	{
        		 distances[i][j]=(frameToCheck[j]-frameFromBase[i])*(frameToCheck[j]-frameFromBase[i]);
         	}
         }
        accumulated_cost[0][0]= distances[0][0];
 
        for (int i=1;i<sizeM;i++)
        {
            accumulated_cost[0][i] = distances[0][i] + accumulated_cost[0][i-1];    
 
        }
        for (int i=1;i<sizeN;i++)
        {
        accumulated_cost[i][0] = distances[i][ 0] + accumulated_cost[i-1][ 0] ;   
 
        }
        for (int i=1;i<sizeN;i++)
        {
        	for (int j=1;j<sizeM;j++)
        	{
        	double temp_min=Math.min(accumulated_cost[i-1][ j-1], accumulated_cost[i-1][ j]);
  accumulated_cost[i][j] =  Math.min(accumulated_cost[i][ j-1],temp_min) + distances[i][ j];
        	}
        }
        int i = sizeN-1;
        int j = sizeM-1;
        Double [] firstResult= {(double) j,(double) i};
   	    path.add(firstResult);

        int x=1;
         while (i>0 || j>0)
        {
        	 if (i==0)
                 j = j - 1;
        	 else if (j==0)
                 i = i - 1;
        	 else
        	 { 
        		 double temp_min=Math.min(accumulated_cost[i-1][ j-1], accumulated_cost[i-1][j] );
        		 if (accumulated_cost[i-1][j] == Math.min(temp_min, accumulated_cost[i][ j-1]))
                     i = i - 1;
        		  if (accumulated_cost[i][j-1] == Math.min(temp_min, accumulated_cost[i][ j-1]))
                      j = j - 1;
        		  else
        		  {
                      i = i - 1;
                      j = j - 1;
        		  }

        	 }
             Double[] result=new Double[2];

        	 result[0]=(double) j;
        	 result[1]=(double) i;
        	 path.add(result);
        	 
        	 
        	
 
        	 
        	 x++;
        	 
        }
          
	   double cost=0.0;

	   for (int k=0;k<path.size();k++)
	   {
		   double x_temp=path.get(k)[0];
		   double y_temp=path.get(k)[1];
       	cost=cost+distances[(int) y_temp][(int) x_temp];


	   }
             
             

     	return cost;
    }
    public int dtw_check (int[][] acc_gesture, int[][] gesture) {

        int dtw = 0;

        int size = acc_gesture[0].length;

        int n = size;
        int m = size;

        int[][] local_distance = new int[n][m];
        int cost = 0;

        for (int i = 0; i < n; i++) {
            local_distance[i][0] = 10000000;
        }

        for (int i = 0; i < n; i++) {
            local_distance[0][i] = 10000000;
        }

        local_distance[0][0] = 0;

        for(int i=0; i<n; i++) {

            for(int k=0; k<m; k++) {
                cost = euclidean_distance(gesture[0][i],acc_gesture[0][k],gesture[1][i],acc_gesture[1][k],gesture[2][i],acc_gesture[2][k]);

                local_distance[i][k] = cost;
            }

        }

        int[][] global_distance = new int[n][m];

        global_distance[0][0] = local_distance[0][0];

        for(int i=1; i<n; i++) {
            global_distance[i][0] = local_distance[i][0] + global_distance[i - 1][0];
        }

        for(int k=1; k<m; k++) {
            global_distance[0][k] = local_distance[0][k] + global_distance[0][k-1];
        }


        for(int i=1; i<n; i++) {

            for(int k=1; k<m; k++) {
                global_distance[i][k] = local_distance[i][k] + min(global_distance[i-1][k],global_distance[i-1][k-1],global_distance[i][k-1]);
            }

        }

        dtw = global_distance[n-1][m-1];

        return dtw;
    }

    public double euclidean(double x1,double x2)
    {
    	double result=Math.sqrt((x2 - x1)*(x2 - x1));
    	return result;
    }
    public int euclidean_distance (double x1, double x2, double y1, double y2, double z1, double z2) {
        int ed = 0;

        ed = (int) sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2) + (z1 - z2)*(z1 - z2));

        return ed;
    }

    public int min(int x, int y, int z)
    {
        if(( x <= y ) && ( x <= z )) {
            return x;
        }
        if(( y <= x ) && ( y <= z )) {
            return y;
        }
        return z;
    }

}
